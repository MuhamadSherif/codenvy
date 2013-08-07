/*
 * Copyright (C) 2013 Codenvy.
 */
package com.codenvy.analytics.metrics;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public class UserLoginGooglePercentMetric extends PercentMetric {

    public UserLoginGooglePercentMetric() {
        super(MetricType.USER_LOGIN_GOOGLE_PERCENT,
              MetricFactory.createMetric(MetricType.USER_LOGIN_TOTAL),
              MetricFactory.createMetric(MetricType.USER_LOGIN_GOOGLE));
    }
}
