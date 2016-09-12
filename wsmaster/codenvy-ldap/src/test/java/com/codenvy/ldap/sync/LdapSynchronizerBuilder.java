package com.codenvy.ldap.sync;

import com.google.inject.Provider;

import org.eclipse.che.api.user.server.spi.ProfileDao;
import org.eclipse.che.api.user.server.spi.UserDao;
import org.eclipse.che.commons.lang.Pair;
import org.ldaptive.ConnectionFactory;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helps to build a complex instance of {@link LdapSynchronizer} in test purposes.
 *
 * @author Yevhenii Voevodin
 */
public final class LdapSynchronizerBuilder {

    private ConnectionFactory       connFactory;
    private Provider<EntityManager> emProvider;
    private UserDao                 userDao;
    private ProfileDao              profileDao;
    private String                  baseDn;
    private String                  usersFilter;
    private String                  additionalUserDn;
    private String                  groupFilter;
    private String                  additionalGroupDn;
    private String                  membersAttrName;
    private long                    syncPeriodMs;
    private int                     pageSize;
    private long                    pageReadTimeoutMs;
    private String                  userIdAttr;
    private String                  userNameAttr;
    private String                  userEmailAttr;
    private Map<String, String>     returnAttributes;

    public LdapSynchronizerBuilder setConnectionFactory(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
        return this;
    }

    public LdapSynchronizerBuilder setEntityManagerProvider(Provider<EntityManager> emProvider) {
        this.emProvider = emProvider;
        return this;
    }

    public LdapSynchronizerBuilder setUserDao(UserDao userDao) {
        this.userDao = userDao;
        return this;
    }

    public LdapSynchronizerBuilder setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
        return this;
    }

    public LdapSynchronizerBuilder setBaseDn(String baseDn) {
        this.baseDn = baseDn;
        return this;
    }

    public LdapSynchronizerBuilder setUsersFilter(String usersFilter) {
        this.usersFilter = usersFilter;
        return this;
    }

    public LdapSynchronizerBuilder setAdditionalUserDn(String additionalUserDn) {
        this.additionalUserDn = additionalUserDn;
        return this;
    }

    public LdapSynchronizerBuilder setGroupFilter(String groupFilter) {
        this.groupFilter = groupFilter;
        return this;
    }

    public LdapSynchronizerBuilder setAdditionalGroupDn(String additionalGroupDn) {
        this.additionalGroupDn = additionalGroupDn;
        return this;
    }

    public LdapSynchronizerBuilder setMembersAttrName(String membersAttrName) {
        this.membersAttrName = membersAttrName;
        return this;
    }

    public LdapSynchronizerBuilder setSyncPeriodMs(Long syncPeriodMs) {
        this.syncPeriodMs = syncPeriodMs;
        return this;
    }

    public LdapSynchronizerBuilder setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public LdapSynchronizerBuilder setPageReadTimeoutMs(Long pageReadTimeoutMs) {
        this.pageReadTimeoutMs = pageReadTimeoutMs;
        return this;
    }

    public LdapSynchronizerBuilder setUserIdAttr(String userIdAttr) {
        this.userIdAttr = userIdAttr;
        return this;
    }

    public LdapSynchronizerBuilder setUserNameAttr(String userNameAttr) {
        this.userNameAttr = userNameAttr;
        return this;
    }

    public LdapSynchronizerBuilder setUserEmailAttr(String userEmailAttr) {
        this.userEmailAttr = userEmailAttr;
        return this;
    }

    @SuppressWarnings("unchecked")
    public LdapSynchronizerBuilder addProfileAttr(String name, String value) {
        if (returnAttributes == null) {
            returnAttributes = new HashMap<>();
        }
        returnAttributes.put(name, value);
        return this;
    }

    public LdapSynchronizer build() {
        @SuppressWarnings("unchecked")
        final Pair<String, String>[] attrsArr = new Pair[returnAttributes.size()];
        return new LdapSynchronizer(connFactory,
                                    emProvider,
                                    userDao,
                                    profileDao,
                                    baseDn,
                                    usersFilter,
                                    additionalUserDn,
                                    groupFilter,
                                    additionalGroupDn,
                                    membersAttrName,
                                    syncPeriodMs,
                                    pageSize,
                                    pageReadTimeoutMs,
                                    userIdAttr,
                                    userNameAttr,
                                    userEmailAttr,
                                    returnAttributes.entrySet()
                                                    .stream()
                                                    .map(entry -> Pair.of(entry.getKey(), entry.getValue()))
                                                    .collect(Collectors.toList())
                                                    .toArray(attrsArr));
    }
}
