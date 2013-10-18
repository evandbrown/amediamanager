package com.amediamanager.metrics;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class MetricBatcher {

    private static final int BATCH_SIZE = 10;

    private final Multimap<String, MetricDatum> queuedDatums =
            Multimaps.synchronizedListMultimap(LinkedListMultimap.<String, MetricDatum>create());

    protected final AmazonCloudWatchAsync cloudWatch;

    public MetricBatcher(AmazonCloudWatchAsync cloudWatch) {
        this.cloudWatch = cloudWatch;
    }

    public final void addDatum(String namespace, MetricDatum datum) {
        queuedDatums.put(namespace, datum);
    }

    public final void addDatums(String namespace, Collection<MetricDatum> datums) {
        queuedDatums.putAll(namespace, datums);
    }

    @Scheduled(fixedDelay=60000)
    private void send() {
        System.err.println("Sending metric data.");
        synchronized(queuedDatums) {
            sendBatch(LinkedListMultimap.create(queuedDatums).asMap());
            queuedDatums.clear();
        }
    }

    protected void sendBatch(Map<String, Collection<MetricDatum>> datums) {
        for (final Map.Entry<String, Collection<MetricDatum>> e : datums.entrySet()) {
            for (final List<MetricDatum> batch : Lists.partition(Lists.newLinkedList(e.getValue()), BATCH_SIZE)) {
                cloudWatch.putMetricDataAsync(new PutMetricDataRequest().withNamespace(e.getKey())
                                                                        .withMetricData(batch),
                                              new AsyncHandler<PutMetricDataRequest, Void>() {

                                                @Override
                                                public void onError(Exception exception) {
                                                    System.err.println("Caught exception: " + exception.toString());
                                                    exception.printStackTrace();
                                                    System.err.println("Requeueing metric data.");
                                                    queuedDatums.putAll(e.getKey(), batch);
                                                }

                                                @Override
                                                public void onSuccess(PutMetricDataRequest request, Void result) {
                                                    System.err.println("Successfully put " + request.getMetricData().size() +
                                                                            " datums for namespace " + request.getNamespace());
                                                    System.err.println(request);
                                                }

                });
            }
        }
    }
}