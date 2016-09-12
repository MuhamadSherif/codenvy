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
 * Created by sj on 08.09.16.
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


    //    private int     minPoolSize          = 3;
//    private int     maxPoolSize          = 10;
//    private boolean validateOnCheckout   = true;
//    private boolean validatePeriodically = true;
//    private long    validatePeriod       = 300;
//
//    private boolean failFast      = true;
//    private long    idleTime      = 600;
//    private long    prunePeriod   = 10000;
//    private long    blockWaitTime = 6000;
//
//    private String  ldapUrl = "ldap://localhost:389";
//    private boolean useSsl  = true;
//    private boolean useStartTls;
//    private long connectTimeout = 5000;
//
//

    /**
     *  Url to ldap server. Configured over ldap.url property.
     */
    private final String ldapUrl;
    /**
     * baseDn to search on. Configured over ldap.basedn property.
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

    private final String  dnFormat;
    private final String  principalAttributePassword;
    private final boolean allowMultiplePrincipalAttributeValues;

    private final String  trustCertificates;
    private final String  keystore;
    private final String  keystorePassword;
    private final String  keystoreType;
    private final int     minPoolSize;
    private final int     maxPoolSize;
    private final boolean validateOnCheckout;
    private final boolean validatePeriodically;
    private final long    validatePeriod;
    private final boolean failFast;
    private final long    idleTime;
    private final long    prunePeriod;
    private final long    blockWaitTime;
    private final boolean subtreeSearch; //true

    private AuthenticationType type;


    private final int     timeoutMs;
    private final int     readTimeoutMs;
    private final boolean useSsl;
    private final boolean useStartTls;
    private final long    connectTimeout;
    private final String  providerClass;

    /**
     *
     */
    private final String              bindDn;
    private final String              bindCredential;
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
                             @Named("ldap.userfilter") boolean validatePeriodically,
                             @Named("ldap.bind.dn") String bindDn,
                             @Named("ldap.userfilter") String dnFormat,
                             @Named("ldap.userfilter") String principalAttributePassword,
                             @Named("ldap.userfilter") boolean allowMultiplePrincipalAttributeValues,
                             @Named("ldap.userfilter") String trustCertificates,
                             @Named("ldap.userfilter") String keystore,
                             @Named("ldap.userfilter") String keystorePassword,
                             @Named("ldap.userfilter") String keystoreType,
                             @Named("ldap.userfilter") int minPoolSize,
                             @Named("ldap.userfilter") int maxPoolSize,
                             @Named("ldap.userfilter") boolean validateOnCheckout,
                             @Named("ldap.userfilter") long validatePeriod,
                             @Named("ldap.userfilter") boolean failFast,
                             @Named("ldap.userfilter") long idleTime,
                             @Named("ldap.userfilter") long prunePeriod,
                             @Named("ldap.userfilter") long blockWaitTime,
                             @Named("ldap.userfilter") boolean subtreeSearch,
                             @Named("ldap.userfilter") AuthenticationType type,
                             @Named("ldap.bind.credential") String bindCredential,
                             @Named("ldap.connection.timeout_ms") int timeoutMs,
                             @Named("ldap.connection.read_timeout_ms") int readTimeoutMs,
                             @Named("ldap.userfilter") boolean useSsl,
                             @Named("ldap.userfilter") boolean useStartTls,
                             @Named("ldap.userfilter") long connectTimeout,
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
        this.bindDn = bindDn;
        this.allowMultipleDns = allowMultipleDns;
        this.dnFormat = dnFormat;
        this.principalAttributePassword = principalAttributePassword;
        this.allowMultiplePrincipalAttributeValues = allowMultiplePrincipalAttributeValues;
        this.trustCertificates = trustCertificates;
        this.keystore = keystore;
        this.keystorePassword = keystorePassword;
        this.keystoreType = keystoreType;
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.validateOnCheckout = validateOnCheckout;
        this.validatePeriod = validatePeriod;
        this.failFast = failFast;
        this.idleTime = idleTime;
        this.prunePeriod = prunePeriod;
        this.blockWaitTime = blockWaitTime;
        this.subtreeSearch = subtreeSearch;
        this.type = type;
        this.bindCredential = bindCredential;
        this.timeoutMs = timeoutMs;
        this.readTimeoutMs = readTimeoutMs;
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

    public String getPrincipalAttributePassword() {
        return principalAttributePassword;
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
