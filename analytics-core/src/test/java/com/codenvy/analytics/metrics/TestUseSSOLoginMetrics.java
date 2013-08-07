/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */


package com.codenvy.analytics.metrics;

import com.codenvy.analytics.metrics.value.DoubleValueData;
import com.codenvy.analytics.metrics.value.LongValueData;
import com.codenvy.analytics.scripts.executor.pig.PigScriptExecutor;
import com.codenvy.analytics.scripts.util.Event;
import com.codenvy.analytics.scripts.util.LogGenerator;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public class TestUseSSOLoginMetrics {

    private HashMap<String, String> context;

    @BeforeMethod
    public void setUp() throws Exception {
        List<Event> events = new ArrayList<>();
        events.add(
                Event.Builder.createUserSSOLoggedInEvent("user1@gmail.com", "google").withDate("2010-10-09").build());
        events.add(
                Event.Builder.createUserSSOLoggedInEvent("user1@gmail.com", "github").withDate("2010-10-09").build());
        events.add(
                Event.Builder.createUserSSOLoggedInEvent("user2@gmail.com", "google").withDate("2010-10-09").build());
        events.add(Event.Builder.createUserSSOLoggedInEvent("user3@gmail.com", "jaas").withDate("2010-10-09").build());
        File log = LogGenerator.generateLog(events);

        context = new HashMap<>();
        context.put(PigScriptExecutor.LOG, log.getAbsolutePath());
        Utils.putFromDate(context, "20101009");
        Utils.putToDate(context, "20101009");

        MetricType.USER_SSO_LOGGED_IN.process(context);
    }

    @Test
    public void testGetValues() throws Exception {
        Metric metric = MetricFactory.createMetric(MetricType.USER_SSO_LOGGED_IN);

        Utils.putParam(context, "google");
        LongValueData value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 2);

        Utils.putParam(context, "github");
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        Utils.putParam(context, "jaas");
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_TOTAL);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 4);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_GOOGLE);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 2);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_FORM);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_GITHUB);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        metric =
                MetricFactory.createMetric(MetricType.USER_LOGIN_GOOGLE_PERCENT);
        DoubleValueData doubleValueData = (DoubleValueData)metric.getValue(context);
        assertEquals(doubleValueData.getAsDouble(), 50.);

        metric =
                MetricFactory.createMetric(MetricType.USER_LOGIN_FORM_PERCENT);
        doubleValueData = (DoubleValueData)metric.getValue(context);
        assertEquals(doubleValueData.getAsDouble(), 25.);

        metric =
                MetricFactory.createMetric(MetricType.USER_LOGIN_GITHUB_PERCENT);
        doubleValueData = (DoubleValueData)metric.getValue(context);
        assertEquals(doubleValueData.getAsDouble(), 25.);
    }

    @Test
    public void testGetValuesWithUserFilters() throws Exception {
        Metric metric = MetricFactory.createMetric(MetricType.USER_SSO_LOGGED_IN);
        context.put(MetricFilter.FILTER_USER.name(), "user1@gmail.com");

        Utils.putParam(context, "google");
        LongValueData value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        Utils.putParam(context, "github");
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        Utils.putParam(context, "jaas");
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 0);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_TOTAL);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 2);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_GOOGLE);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_FORM);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 0);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_GITHUB);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        metric =
                MetricFactory.createMetric(MetricType.USER_LOGIN_GOOGLE_PERCENT);
        DoubleValueData doubleValueData = (DoubleValueData)metric.getValue(context);
        assertEquals(doubleValueData.getAsDouble(), 50.);

        metric =
                MetricFactory.createMetric(MetricType.USER_LOGIN_FORM_PERCENT);
        doubleValueData = (DoubleValueData)metric.getValue(context);
        assertEquals(doubleValueData.getAsDouble(), 0.);

        metric =
                MetricFactory.createMetric(MetricType.USER_LOGIN_GITHUB_PERCENT);
        doubleValueData = (DoubleValueData)metric.getValue(context);
        assertEquals(doubleValueData.getAsDouble(), 50.);
    }

    @Test
    public void testGetValuesWithDomainsFilters() throws Exception {
        Metric metric = MetricFactory.createMetric(MetricType.USER_SSO_LOGGED_IN);
        context.put(MetricFilter.FILTER_USER.name(), "@gmail.com");

        Utils.putParam(context, "google");
        LongValueData value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 2);

        Utils.putParam(context, "github");
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        Utils.putParam(context, "jaas");
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_TOTAL);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 4);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_GOOGLE);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 2);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_FORM);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        metric = MetricFactory.createMetric(MetricType.USER_LOGIN_GITHUB);
        value = (LongValueData)metric.getValue(context);
        assertEquals(value.getAsLong(), 1);

        metric =
                MetricFactory.createMetric(MetricType.USER_LOGIN_GOOGLE_PERCENT);
        DoubleValueData doubleValueData = (DoubleValueData)metric.getValue(context);
        assertEquals(doubleValueData.getAsDouble(), 50.);

        metric =
                MetricFactory.createMetric(MetricType.USER_LOGIN_FORM_PERCENT);
        doubleValueData = (DoubleValueData)metric.getValue(context);
        assertEquals(doubleValueData.getAsDouble(), 25.);

        metric =
                MetricFactory.createMetric(MetricType.USER_LOGIN_GITHUB_PERCENT);
        doubleValueData = (DoubleValueData)metric.getValue(context);
        assertEquals(doubleValueData.getAsDouble(), 25.);
    }
}
