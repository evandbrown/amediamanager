package com.amediamanager.metrics.challenge;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cloudwatch.AmazonCloudWatchAsync;
import com.amazonaws.services.cloudwatch.model.MetricDatum;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MetricBatcher extends com.amediamanager.metrics.MetricBatcher {

    @Autowired
    public MetricBatcher(AmazonCloudWatchAsync cloudWatch) {
        super(cloudWatch);
    }

    /**
     * Send a batch of MetricDatums to CloudWatch
     * @param datums a map of metric namespace to datums
     */
    @Override
    protected void sendBatch(Map<String, Collection<MetricDatum>> datums) {
        super.sendBatch(datums);
    }
}
