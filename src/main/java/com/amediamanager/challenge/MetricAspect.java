package com.amediamanager.challenge;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amediamanager.metrics.MetricBatcher;

@Aspect
@Component
public class MetricAspect extends com.amediamanager.metrics.MetricAspect {

    @Autowired
    public MetricAspect(MetricBatcher metricBatcher) {
        super(metricBatcher);
    }

    /**
     * Emit metrics for an operation performed against AWS
     * @param service the AWS service being called
     * @param operation the name of the AWS API
     * @param startTime the time the call began
     * @param exception the exception thrown (can be null)
     */
    @Override
    protected void emitMetrics(String service, String operation,
                               long startTime, Throwable exception) {
        super.emitMetrics(service, operation, startTime, exception);
    }

}
