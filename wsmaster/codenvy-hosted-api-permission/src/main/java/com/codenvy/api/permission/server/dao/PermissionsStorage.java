/*
 *  [2012] - [2016] Codenvy, S.A.
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
package com.codenvy.api.permission.server.dao;

import com.codenvy.api.permission.server.AbstractPermissionsDomain;
import com.codenvy.api.permission.server.PermissionsImpl;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;

import java.util.List;
import java.util.Set;

/**
 * General contract of storage for permissions.
 * Single Storage may maintain one or more Domains
 * (it is responsibility of system on top to make the choice consistent)
 * It actually defines CRUD methods with some specific such as:
 * - processing list of permissions
 * - checking for existence but not returning fully qualified stored permission
 *
 * @author gazarenkov
 * @author Sergii Leschenko
 */
public interface PermissionsStorage {
    /**
     * @return store of domains this storage is able to maintain
     */
    Set<AbstractPermissionsDomain> getDomains();

    /**
     * Stores (adds or updates) permissions.
     *
     * @param permissions
     *         permission to store
     * @throws NotFoundException
     *         when given instance was not found
     * @throws ServerException
     *         when any other error occurs during permissions storing
     */
    void store(PermissionsImpl permissions) throws ServerException, NotFoundException;

    /**
     * @param user
     *         user id
     * @param domain
     *         domain id
     * @param instance
     *         instance id
     * @return user's permissions for specified instance
     * @throws NotFoundException
     *         when permissions with given user and domain and instance was not found
     * @throws ServerException
     *         when any other error occurs during permissions fetching
     */
    PermissionsImpl get(String user, String domain, String instance) throws ServerException, NotFoundException;

    /**
     * @param domain
     *         domain id
     * @param instance
     *         instance id
     * @return set of permissions
     * @throws NotFoundException
     *         when given instance was not found
     * @throws ServerException
     *         when any other error occurs during permissions fetching
     */
    List<PermissionsImpl> getByInstance(String domain, String instance) throws ServerException, NotFoundException;

    /**
     * @param user
     *         user id
     * @param domain
     *         domain id
     * @param instance
     *         instance id
     * @param action
     *         action name
     * @return true if the permission exists
     * @throws ServerException
     *         when any other error occurs during permission existence checking
     */
    boolean exists(String user, String domain, String instance, String action) throws ServerException;

    /**
     * Removes permissions of user related to the particular instance of specified domain
     *
     * @param user
     *         user id
     * @param domain
     *         domain id
     * @param instance
     *         instance id
     * @throws NotFoundException
     *         when permissions with given user and domain and instance was not found
     * @throws ServerException
     *         when any other error occurs during permissions removing
     */
    void remove(String user, String domain, String instance) throws ServerException, NotFoundException;
}
