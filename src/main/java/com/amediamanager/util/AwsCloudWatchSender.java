package com.amediamanager.util;
/*
 * Copyright 2011 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not 
 * use this file except in compliance with the License. A copy of the License 
 * is located at
 * 
 *      http://aws.amazon.com/apache2.0/
 * 
 * or in the "LICENSE" file accompanying this file. This file is distributed 
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
 * express or implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StatisticSet;

/**
 * A helper class that simplifies sending custom metrics to AWS CloudWatch.
 * 
 * This class has global context for entire application and can be used safely from multiple threads.
 * Different code blocks can use method AwsCloudWatchSender.submit to report metrics to CloudWatch.
 * However the data is not immediately sent but all metrics are being accumulated and then sent only
 * once for a given time interval. Use AwsCloudWatchSender.setSendIntervalSeconds to control how often
 * metrics should be reported to CloudWatch. In case when a single remote call to CloudWatch fails, the
 * CloudWatchClient will retry the same call again (up to 3 times by default). Use ClientConfiguration
 * options to set desired number of retries.
 * 
 * Pattern of use:
 *  
 * 1. In the beginning of application lifecycle, call AwsCloudWatchSender.init method. It will create
 *    a dedicated CloudWatch communication thread for periodical shipping of metrics in batches. 
 * 
 * 2. To report a metric sample, create an instance of MetricDatum class, fill in required fields and
 *    pass to AwsCloudWatchSender.submit call.
 *
 * 3. In the end of of application lifecycle, call AwsCloudWatchSender.destroy method. It will stop
 *    the communication thread and report the last chunk of accumulated metrics to CloudWatch. 
 */
@Component
public class AwsCloudWatchSender {

	@Autowired
	private AmazonCloudWatch cloudwatchClient;
	
    /**
     * Internal class that uniquely represents a metric definition by its name and dimensions.
     */
    public static class MetricSignature {
        
        private final String metricName;
        
        private final List<Dimension> dimensions;
        
        /**
         * Creates a metric signature based on metric datum.
         */
        public MetricSignature(MetricDatum datum) {
            
            if (datum.getMetricName() == null) {
                throw new IllegalArgumentException("Metric name not initialized.");
            }
            
            this.metricName = datum.getMetricName();
            this.dimensions = datum.getDimensions();
        }
        
        /**
         * Compares to metric signatures for equality (will be used by hash map).
         */
        @Override
		public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            
            MetricSignature other = (MetricSignature)obj;
            if (!this.metricName.equals(other.metricName)) {
                return false;
            }
            
            if (this.dimensions == null && other.dimensions == null) {
                return true;
            }            
            if (this.dimensions == null || other.dimensions == null) {
                return false;
            }
            if (this.dimensions.size() != other.dimensions.size()) {
                return false;
            }
            
            for (int i = 0; i < this.dimensions.size(); ++i) {
                Dimension thisDim = this.dimensions.get(i);
                Dimension otherDim = other.dimensions.get(i);
                if (!thisDim.getName().equals(otherDim.getName())) {
                    return false;
                }
                if (!thisDim.getValue().equals(otherDim.getValue())) {
                    return false;
                }
            }
            
            return true;
        }
        
        /**
         * Generates a hash code for metric signature (will be used by hash map).
         */
        @Override
		public int hashCode() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.metricName);
            if (this.dimensions != null) {
                for (Dimension dim : this.dimensions) {
                    sb.append(dim.getName());
                    sb.append(dim.getValue());
                }
            }
            return sb.toString().hashCode();
        }
    }
    
    /**
     * Controls how often to call remote CloudWatch operation PutMetricData. 
     */
    private static int sendIntervalSeconds = 60;

    /**
     * Specifies how many metrics will be grouped together for a single PutMetricData call.
     */
    private static int batchSize = 10;
    
    /**
     * CloudWatch namespace for all metrics being reported.
     */
    private static String namespace = "aws-demo/aMediaManager";
    
    /**
     * Sets how often to send metrics to CloudWatch. Recommended values are 60 and 300 to report
     * 1-minute or 5-minute metrics which will be in-line with AWS console viewing options. 
     * 
     * @param maxRetryAttempts
     */
    public static void setSendIntervalSeconds(int sendIntervalSeconds) {
        AwsCloudWatchSender.sendIntervalSeconds = sendIntervalSeconds;
    }
    
    /**
     * Obtains the current initialization state. 
     * @return true if already initialized, false otherwise
     */
    public static boolean isInitialized() {
        return initialized.get();
    }
    
    /**
     * Accepts metric data that needs to be reported to CloudWatch.
     */
    public static void submit(MetricDatum datum) {
                
        if (!initialized.get()) {
            System.err.println("CW: client is not initialized");
            throw new IllegalStateException("AWS CloudWatch client is not initialized.");
        }

        normalizeMetricDatum(datum);        
        MetricSignature msign = new MetricSignature(datum);
        
        synchronized(syncMetrics) {
            MetricDatum stored = metrics.get(msign);
            if (stored == null) {
                metrics.put(msign, datum);
            } else {
                mergeStatisticSets(datum.getStatisticValues(), stored.getStatisticValues());
            }            
        }
        
    }
    
    /**
     * Verifies metric datum and makes it use statistic set instead of single value.
     */
    static void normalizeMetricDatum(MetricDatum datum) {
        
        if (datum.getValue() == null && datum.getStatisticValues() == null) {
            throw new IllegalArgumentException("Missing metric values.");
        }
        if (datum.getValue() != null && datum.getStatisticValues() != null) {
            throw new IllegalArgumentException("Ambiguous metric values.");
        }
        
        if (datum.getValue() != null) {
            StatisticSet stats = new StatisticSet();
            stats.setMaximum(datum.getValue());
            stats.setMinimum(datum.getValue());
            stats.setSum(datum.getValue());
            stats.setSampleCount(1.0);
            datum.setStatisticValues(stats);
            datum.setValue(null);
        }
        
        if (datum.getStatisticValues() != null) {
            StatisticSet stats = datum.getStatisticValues();
            if (stats.getMaximum() == null || stats.getMinimum() == null
                    || stats.getSampleCount() == null || stats.getSum() == null) {
                throw new IllegalArgumentException("Uninitialized statistic values.");
            }
        }
    }
    
    /**
     * Combines two statistic sets aggregating the values in the second set.
     */
    static void mergeStatisticSets(StatisticSet from, StatisticSet to) {
        if (to.getMaximum() < from.getMaximum()) {
            to.setMaximum(from.getMaximum());
        }
        if (to.getMinimum() > from.getMinimum()) {
            to.setMinimum(from.getMinimum());
        }
        to.setSum(to.getSum() + from.getSum());
        to.setSampleCount(to.getSampleCount() + from.getSampleCount());
    }
    
    /**
     * Executes the continuous loop of communication thread:
     *   - wait until send interval time elapsed or destroy signal received
     *   - send the metrics only in valid state (initialized and not destroying)
     *   - exit when received a destroying signal 
     */
    static void run() {
        
        System.err.println("CW: communication thread started");
        log.info("CloudWatchSender communication thread started");
        
        while (true) {
        
            syncSending.lock();
            try {
                System.err.println("CW: waiting to send metrics for " + sendIntervalSeconds + " seconds");
                syncWaiting.await(sendIntervalSeconds, TimeUnit.SECONDS);
            } catch (InterruptedException ie) {
                System.err.println("CW: communication thread interrupted");
                log.warn("CloudWatchSender communication thread interrupted");
                return;
            } finally {
                syncSending.unlock();
            }
            
            if (destroyed.get()) {
                System.err.println("CW: communication thread exited");
                log.info("CloudWatchSender communication thread exited");
                return;
            }
            
            if (initialized.get()) {
                System.err.println("CW: schedule to send metrics");
                sendMetrics();
            }
        }
    }
    
    /**
     * Grabs accumulated metrics, splits them into batches, and then sends batches to CloudWatch. 
     */
    static void sendMetrics() {
        
        Collection<MetricDatum> snapshot = null;
        
        synchronized(syncMetrics) {
            snapshot = new ArrayList<MetricDatum>(metrics.values());
            metrics.clear();
        }

        System.err.println("CW: found " + snapshot.size() + " metrics to send");
        Iterator<MetricDatum> iter = snapshot.iterator();
        
        while (iter.hasNext())
        {
            Collection<MetricDatum> batch = new ArrayList<MetricDatum>(batchSize);
            while (iter.hasNext() && batch.size() < batchSize) {
                batch.add(iter.next());
            }
            
            PutMetricDataRequest request = new PutMetricDataRequest();
            request.setNamespace(namespace);
            request.setMetricData(batch);
            
            try {
                System.err.println("CW: Sending request: " + request.toString());
                client.putMetricData(request);
            } catch (Exception e) {
                log.error("Failed calling CloudWatch PutMetricData operation", e);
                System.err.println("CW: PUT call failed: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
        
    }

    /**
     * Creates the CloudWatch communication thread and initializes CloudWatch client.
     * 
     * This method must be called at the beginning of the application lifecycle. For servlets, call it
     * inside the Servlet.init method. Multiple simultaneous calls to this method from different threads
     * will be synchronized but not blocked. Only one thread will perform the actual initialization.
     * 
     * @param creds AWS credentials
     * @param clientConfig optional AWS client configuration (can be null)
     * @param namespace CloudWatch namespace for all metrics that will be reported
     */
    @PostConstruct
    public void init(/*AWSCredentials creds, ClientConfiguration clientConfig, String namespace*/) {
        
        if (initialized.get()) {
            System.err.println("CW: Already initialized 1, exit");
            return;
        }
        
        synchronized(syncState) {

            if (initialized.get()) {
                System.err.println("CW: Already initialized 2, exit");
                return;
            }
            
/*			AwsCloudWatchSender.namespace = namespace;

			if (clientConfig != null) {
				client = new AmazonCloudWatchClient(creds, clientConfig);
			} else {
				client = new AmazonCloudWatchClient(creds);
			}*/
            
            client = cloudwatchClient;
            
            initialized.set(true);
        }
        
        // Start the CloudWatch communication thread.
        
        new Thread() {
            @Override
            public void run() {
                System.err.println("CW: thread starting");
                AwsCloudWatchSender.run();
                System.err.println("CW: thread exiting");
            }
        }.start();
        
        System.err.println("CW: exit init method");
    }

    /**
     * Stops the CloudWatch communication thread and submits the last chunk of accumulated metrics.
     * 
     * This method must be called at the end of application lifecycle. For servlets, call it inside
     * the Servlet.destroy method. Multiple simultaneous calls to this method from different threads
     * will be synchronized but not blocked. Only one thread that actually sent the signal to stop
     * the communication thread will block until last chunk of metric data is sent.
     */
    @PreDestroy
    public static void destroy() {
        
        System.err.println("CW: enter destroy method");

        if (destroyed.get()) {
            return;
        }
    
        synchronized(syncState) {

            if (destroyed.get()) {
                return;
            }

            destroyed.set(true);

            try {
                syncSending.lock();
                syncWaiting.signal();                
            } finally {
                syncSending.unlock();
            }
        }

        sendMetrics();
        
        System.err.println("CW: exit destroy method");
    }

    private static AmazonCloudWatch client;
    
    private static final HashMap<MetricSignature, MetricDatum> metrics = new HashMap<MetricSignature, MetricDatum>();
    
    private static final Object syncMetrics = new Object();
    
    private static final Object syncState = new Object();
    
    private static final ReentrantLock syncSending = new ReentrantLock();
    
    private static final Condition syncWaiting = syncSending.newCondition();
    
    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    
    private static final AtomicBoolean destroyed = new AtomicBoolean(false);
    
    private static final Log log = LogFactory.getLog(AwsCloudWatchSender.class);
}
