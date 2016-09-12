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
package com.codenvy.ldap;

import org.ldaptive.sasl.Mechanism;
import org.ldaptive.sasl.QualityOfProtection;
import org.ldaptive.sasl.SecurityStrength;

import javax.inject.Named;
import javax.validation.constraints.NotNull;

/**
 * Ldap configuration.
 * Used to organise single place for ldap configuration in container.
 *
 * @author Sergii Kabashniuk
 */
public class LdapConfiguration {


    /**
     * The enum Authentication types.
     */
    public enum AuthenticationType {
        /**
         * Active Directory.
         */
        AD,
        /**
         * Authenticated Search.
         */
        AUTHENTICATED,
        /**
         * Direct Bind.
         */
        DIRECT,
        /**
         * Anonymous Search.
         */
        ANONYMOUS,
        /**
         * SASL bind search.
         */
        SASL
    }


    /**
     *  Url to ldap server. Configured over ldap.url property.
     */
    private final String ldapUrl;
    /**
     * DN to search; An empty value searches the rootDSE;
     * Configured over ldap.basedn property.
     */
    private final String baseDn;

    /**
     * search filter to execute; e.g. (mail={user}) Configured over ldap.userfilter.
     */
    private final String  userFilter;
    /**
     * whether to throw an exception if multiple entries are found with the search filter. Configured over ldap.allowmultipledns
     */
    private final boolean allowMultipleDns;
    /**
     * whether a subtree search should be performed.
     * Configured over ldap.subtreesearch
     */
    private final boolean subtreeSearch;
    /**
     * DN to bind as before performing operations.
     * Configured over ldap.bind.dn
     */
    private final String  bindDn;
    /**
     * Credential for the bind DN.
     * Configured over ldap.bind.password
     */
    private final String  bindCredential;


    /**
     * 	size the pool should be initialized to and pruned to.
     *  Configured over ldap.pool.minsize
     */
    private final int     minPoolSize;
    /**
     * maximum size the pool can grow to.
     * Configured over ldap.pool.maxsize
     */
    private final int     maxPoolSize;
    /**
     * whether connections should be validated when loaned out from the pool.
     * Configured over ldap.pool.validate.oncheckout
     */
    private final boolean validateOnCheckout;
    /**
     * whether connections should be validated when returned to the pool.
     * Configured over ldap.pool.validate.oncheckin
     */
    private final boolean validateOnCheckin;
    /**
     * whether connections should be validated periodically when the pool is idle
     * Configured over ldap.pool.validate.periodically
     */
    private final boolean validatePeriodically;
    /**
     * period in seconds at which pool should be validated.
     * Configured over ldap.pool.validate.period_ms
     */
    private final long    validatePeriod;

    /**
     * Maximum amount of time that connects will block.
     * Configured over ldap.connecttimeout_ms
     */
    private final long connectTimeout;
    /**
     * Sets the maximum amount of time that operations will wait for a response.
     * Configured over ldap.responsetimeout_ms
     */
    private final long responseTimeout;

    /**
     * Resolves an entry DN by using String#format. This resolver is typically used when an entry DN
     * can be formatted directly from the user identifier. For instance, entry DNs of the form
     * uid=dfisher,ou=people,dc=ldaptive,dc=org could be formatted
     * from uid=%s,ou=people,dc=ldaptive,dc=org.
     *
     * Configured over ldap.dnformat
     */
    private final String dnFormat;
    /**
     * Configuration of PooledCompareAuthenticationHandler.
     * Authenticates an entry DN by performing an LDAP compare operation on the userPassword attribute.
     * This authentication handler should be used in cases where you do not have authorization to perform
     * binds, but do have authorization to read the userPassword attribute.
     *
     * Configured over ldap.userpasswordattribute
     */
    private final String userPasswordAttribute;

    private final String trustCertificates;
    private final String keystore;
    private final String keystorePassword;
    private final String keystoreType;


    private final boolean failFast;
    private final long    idleTime;
    private final long    prunePeriod;
    private final long    blockWaitTime;


    private AuthenticationType type;


    private final boolean useStartTls;

    private final String providerClass;

    private final boolean             useSsl;
    private final String              saslRealm;
    private final Mechanism           saslMechanism;
    private final String              saslAuthorizationId;
    private final SecurityStrength    saslSecurityStrength;
    private final Boolean             saslMutualAuth;
    private final QualityOfProtection saslQualityOfProtection;


    public LdapConfiguration(@NotNull @Named("ldap.url") String ldapUrl,
                             @Named("ldap.basedn") String baseDn,
                             @Named("ldap.userfilter") String userFilter,
                             @Named("ldap.allowmultipledns") boolean allowMultipleDns,
                             @Named("ldap.subtreesearch") boolean subtreeSearch,

                             @Named("ldap.bind.dn") String bindDn,
                             @Named("ldap.bind.password") String bindCredential,


                             @Named("ldap.pool.minsize") int minPoolSize,
                             @Named("ldap.pool.maxsize") int maxPoolSize,
                             @Named("ldap.pool.validate.oncheckout") boolean validateOnCheckout,
                             @Named("ldap.pool.validate.oncheckin") boolean validateOnCheckin,
                             @Named("ldap.pool.validate.period_ms") long validatePeriod,
                             @Named("ldap.pool.validate.periodically") boolean validatePeriodically,
                             @Named("ldap.connecttimeout_ms") long connectTimeout,
                             @Named("ldap.responsetimeout_ms") long responseTimeout,


                             @Named("ldap.dnformat") String dnFormat,
                             @Named("ldap.userpasswordattribute") String userPasswordAttribute,

                             @Named("ldap.userfilter") String trustCertificates,
                             @Named("ldap.userfilter") String keystore,
                             @Named("ldap.userfilter") String keystorePassword,
                             @Named("ldap.userfilter") String keystoreType,
                             @Named("ldap.userfilter") boolean failFast,
                             @Named("ldap.userfilter") long idleTime,
                             @Named("ldap.userfilter") long prunePeriod,
                             @Named("ldap.userfilter") long blockWaitTime,
                             @Named("ldap.userfilter") AuthenticationType type,

                             @Named("ldap.userfilter") boolean useSsl,
                             @Named("ldap.userfilter") boolean useStartTls,
                             @Named("ldap.userfilter") String providerClass,
                             @Named("ldap.userfilter") String saslRealm,
                             @Named("ldap.userfilter") Mechanism saslMechanism,
                             @Named("ldap.userfilter") String saslAuthorizationId,
                             @Named("ldap.userfilter") SecurityStrength saslSecurityStrength,
                             @Named("ldap.userfilter") Boolean saslMutualAuth,
                             @Named("ldap.userfilter") QualityOfProtection saslQualityOfProtection) {

        this.ldapUrl = ldapUrl;
        this.baseDn = baseDn;
        this.userFilter = userFilter;
        this.validatePeriodically = validatePeriodically;
        this.responseTimeout = responseTimeout;
        this.bindDn = bindDn;
        this.allowMultipleDns = allowMultipleDns;
        this.dnFormat = dnFormat;
        this.userPasswordAttribute = userPasswordAttribute;
        this.trustCertificates = trustCertificates;
        this.keystore = keystore;
        this.keystorePassword = keystorePassword;
        this.keystoreType = keystoreType;
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.validateOnCheckout = validateOnCheckout;
        this.validateOnCheckin = validateOnCheckin;
        this.validatePeriod = validatePeriod;
        this.failFast = failFast;
        this.idleTime = idleTime;
        this.prunePeriod = prunePeriod;
        this.blockWaitTime = blockWaitTime;
        this.subtreeSearch = subtreeSearch;
        this.type = type;
        this.bindCredential = bindCredential;
        this.useSsl = useSsl;
        this.useStartTls = useStartTls;
        this.connectTimeout = connectTimeout;
        this.providerClass = providerClass;
        this.saslRealm = saslRealm;
        this.saslMechanism = saslMechanism;
        this.saslAuthorizationId = saslAuthorizationId;
        this.saslSecurityStrength = saslSecurityStrength;
        this.saslMutualAuth = saslMutualAuth;
        this.saslQualityOfProtection = saslQualityOfProtection;
    }


    public boolean isAllowMultipleDns() {
        return allowMultipleDns;
    }

    public AuthenticationType getType() {
        return type;
    }

    public String getDnFormat() {
        return dnFormat;
    }

    public String getUserPasswordAttribute() {
        return userPasswordAttribute;
    }

    public boolean isSubtreeSearch() {
        return subtreeSearch;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public String getUserFilter() {
        return userFilter;
    }

    public String getTrustCertificates() {
        return trustCertificates;
    }

    public String getKeystore() {
        return keystore;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public String getKeystoreType() {
        return keystoreType;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public boolean isValidateOnCheckout() {
        return validateOnCheckout;
    }

    public boolean isValidateOnCheckin() {
        return validateOnCheckin;
    }

    public boolean isValidatePeriodically() {
        return validatePeriodically;
    }

    public long getValidatePeriod() {
        return validatePeriod;
    }

    public boolean isFailFast() {
        return failFast;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public long getPrunePeriod() {
        return prunePeriod;
    }

    public long getBlockWaitTime() {
        return blockWaitTime;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public boolean isUseStartTls() {
        return useStartTls;
    }

    public long getConnectTimeout() {
        return connectTimeout;
    }

    public long getResponseTimeout() {
        return responseTimeout;
    }


    public String getProviderClass() {
        return providerClass;
    }

    public String getBindDn() {
        return bindDn;
    }

    public String getBindCredential() {
        return bindCredential;
    }

    public String getSaslRealm() {
        return saslRealm;
    }

    public Mechanism getSaslMechanism() {
        return saslMechanism;
    }

    public String getSaslAuthorizationId() {
        return saslAuthorizationId;
    }

    public SecurityStrength getSaslSecurityStrength() {
        return saslSecurityStrength;
    }

    public Boolean getSaslMutualAuth() {
        return saslMutualAuth;
    }

    public QualityOfProtection getSaslQualityOfProtection() {
        return saslQualityOfProtection;
    }
}
