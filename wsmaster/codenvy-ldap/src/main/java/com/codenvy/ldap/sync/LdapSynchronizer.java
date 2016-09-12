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
package com.codenvy.ldap.sync;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.user.server.model.impl.ProfileImpl;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.api.user.server.spi.ProfileDao;
import org.eclipse.che.api.user.server.spi.UserDao;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.commons.lang.Pair;
import org.ldaptive.Connection;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static java.lang.String.format;

/**
 * Synchronizes ldap attributes with a custom storage.
 *
 * @author Yevhenii Voevodin
 */
@Singleton
public class LdapSynchronizer {

    private static final Logger LOG                       = LoggerFactory.getLogger(LdapSynchronizer.class);
    private static final int    DEFAULT_PAGE_SIZE         = 1000;
    private static final long   DEFAULT_PAGE_READ_TIMEOUT = 30_000L;

    private final Long                             syncPeriodMs;
    private final Function<LdapEntry, ProfileImpl> profileMapper;
    private final Function<LdapEntry, UserImpl>    userMapper;
    private final LdapEntrySelector                selector;
    private final ConnectionFactory                connFactory;
    private final UserDao                          userDao;
    private final ProfileDao                       profileDao;
    private final Provider<EntityManager>          emProvider;

    @Inject
    public LdapSynchronizer(ConnectionFactory connFactory,
                            Provider<EntityManager> emProvider,
                            UserDao userDao,
                            ProfileDao profileDao,
                            @Named("ldap.base_dn") String baseDn,
                            @Named("ldap.user.filter") String usersFilter,
                            @Named("ldap.user.additional_dn") @Nullable String additionalUserDn,
                            @Named("ldap.group.filter") @Nullable String groupFilter,
                            @Named("ldap.group.additional_dn") @Nullable String additionalGroupDn,
                            @Named("ldap.group.attr.members") @Nullable String membersAttrName,
                            @Named("ldap.sync.period_ms") long syncPeriodMs,
                            @Named("ldap.sync.page.size") int pageSize,
                            @Named("ldap.sync.page.read_timeout_ms") long pageReadTimeoutMs,
                            @Named("ldap.sync.user.attr.id") String userIdAttr,
                            @Named("ldap.sync.user.attr.name") String userNameAttr,
                            @Named("ldap.sync.user.attr.email") String userEmailAttr,
                            @Named("ldap.sync.profile.attrs") @Nullable Pair<String, String>[] profileAttributes) {
        if (groupFilter != null && membersAttrName == null) {
            throw new NullPointerException(format("Value of 'ldap.group.filter' is set to '%s', which means that groups search " +
                                                  "is enabled that also requires 'ldap.group.attr.members' to be set",
                                                  groupFilter));
        }
        this.connFactory = connFactory;
        this.emProvider = emProvider;
        this.userDao = userDao;
        this.profileDao = profileDao;
        this.syncPeriodMs = syncPeriodMs;

        // getting attribute names which should be synchronized
        final ArrayList<String> attrsList = new ArrayList<>();
        attrsList.add(userIdAttr);
        attrsList.add(userNameAttr);
        attrsList.add(userEmailAttr);
        if (profileAttributes != null) {
            for (Pair<String, String> profileAttribute : profileAttributes) {
                attrsList.add(profileAttribute.second);
            }
        }
        final String[] syncAttributes = attrsList.toArray(new String[attrsList.size()]);

        this.userMapper = new UserMapper(userIdAttr, userNameAttr, userEmailAttr);
        this.profileMapper = new ProfileMapper(userIdAttr, profileAttributes);

        if (groupFilter == null) {
            selector = new LookupSelector(pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize,
                                          pageReadTimeoutMs <= 0 ? DEFAULT_PAGE_READ_TIMEOUT : pageReadTimeoutMs,
                                          normalizeDn(additionalUserDn, baseDn),
                                          usersFilter,
                                          syncAttributes);
        } else {
            selector = new MembershipSelector(normalizeDn(additionalGroupDn, baseDn),
                                              groupFilter,
                                              usersFilter,
                                              membersAttrName,
                                              syncAttributes);
        }
    }

    @Transactional
    public SyncResult syncAll() throws LdapException, ConflictException, ServerException, NotFoundException {
        final SyncResult syncResult = new SyncResult();
        final Set<String> existingIds = getAllUserIds();
        try (Connection connection = connFactory.getConnection()) {
            connection.open();
            for (LdapEntry entry : selector.select(connection)) {
                final UserImpl user = userMapper.apply(entry);
                final ProfileImpl profile = profileMapper.apply(entry);
                if (existingIds.remove(user.getId())) {
                    userDao.update(user);
                    profileDao.update(profile);
                    syncResult.updated++;
                } else {
                    userDao.create(user);
                    profileDao.create(profile);
                    syncResult.created++;
                }
            }
        }
        for (String existingId : existingIds) {
            userDao.remove(existingId);
        }
        syncResult.removed = existingIds.size();
        return syncResult;
    }

    @SuppressWarnings("unchecked")
    private Set<String> getAllUserIds() {
        return new HashSet<>(emProvider.get()
                                       .createNativeQuery("SELECT id from Usr")
                                       .getResultList());
    }

    public static class SyncResult {
        private long created;
        private long updated;
        private long removed;

        public long getRemoved() {
            return removed;
        }

        public long getUpdated() {
            return updated;
        }

        public long getCreated() {
            return created;
        }

        @Override
        public String toString() {
            return "SyncResult{" +
                   "created=" + created +
                   ", updated=" + updated +
                   ", removed=" + removed +
                   '}';
        }
    }

    private static String normalizeDn(String additionalDn, String baseDn) {
        if (additionalDn == null) {
            return baseDn;
        }
        return additionalDn + ',' + baseDn;
    }
}
