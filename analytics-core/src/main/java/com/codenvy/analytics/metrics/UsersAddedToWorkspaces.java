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

/** @author <a href="mailto:abazko@codenvy.com">Anatoliy Bazko</a> */
public class UsersAddedToWorkspaces extends AbstractMapValueResulted {

    public static final String INVITE  = "invite";
    public static final String WEBSITE = "website";

    public UsersAddedToWorkspaces(String metricName) {
        super(metricName);
    }

    public UsersAddedToWorkspaces() {
        super(MetricType.USERS_ADDED_TO_WORKSPACES);
    }

    @Override
    public String[] getTrackedFields() {
        return new String[]{INVITE, WEBSITE};
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "The way how users were added to workspaces";
    }
}
