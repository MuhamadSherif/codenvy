/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2015] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.api.metrics.server;

import org.eclipse.che.api.core.ServerException;

/**
 * Allow to inform @MeterBasedStorage about state of resource
 *
 * @author Sergii Kabashniuk
 */
public interface UsageInformer {
    /**
     * Inform about the fact that resource is still in use.
     */
    void resourceInUse() throws ServerException;

    /**
     * Inform about the fact that resource usage are stopped.
     */
    void resourceUsageStopped() throws ServerException;

}