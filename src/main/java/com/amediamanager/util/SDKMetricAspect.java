package com.amediamanager.util;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import com.google.common.base.Preconditions;

@Aspect
@Component
public class SDKMetricAspect {

    private final MetricBatcher metricBatcher;

    @Autowired
    public SDKMetricAspect(final MetricBatcher metricBatcher) {
        this.metricBatcher = metricBatcher;
    }

    @Pointcut("execution(public * com.amazonaws.services..*.*(..))")
    public void sdkClients() {
    }

    @Around("sdkClients()")
    public Object logMetrics(ProceedingJoinPoint pjp) throws Throwable {
        final String service = pjp.getSignature().getDeclaringType().getSimpleName();
        final String operation = pjp.getSignature().getName();

        final MetricBuilder builder = new MetricBuilder(service, operation);

        builder.start();
        try {
            return pjp.proceed();
        } catch (Exception e) {
            builder.exception(e);
            throw e;
        } finally {
            builder.end();
        }
    }

    private class MetricBuilder {

        private final String service;
        private final String operation;
        private long startTime = -1;
        private Throwable exception = null;

        public MetricBuilder(String srv, String op) {
            service = srv;
            operation = op;
        }

        private MetricDatum newDatum() {
            return new MetricDatum().withDimensions(new Dimension().withName("Service").withValue(service),
                                                    new Dimension().withName("Operation").withValue(operation))
                                    .withTimestamp(new Date(startTime));
        }

        private MetricBuilder start() {
            Preconditions.checkState(startTime == -1, "Cannot start multiple times");
            startTime = System.currentTimeMillis();
            return this;
        }

        private MetricBuilder exception(Exception e) {
            exception = e;
            return this;
        }

        private void end() {
            Preconditions.checkState(startTime != -1, "Cannot end without starting");
            long time = System.currentTimeMillis() - startTime;

            metricBatcher.addDatum("AMM", newDatum().withMetricName("Latency").withUnit(StandardUnit.Milliseconds).withValue((double)time));
            metricBatcher.addDatum("AMM", newDatum().withMetricName("Success").withValue(exception == null ? 1.0 : 0.0).withUnit(StandardUnit.Count));
        }
    }
}
