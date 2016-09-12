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


import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.Credential;
import org.ldaptive.DefaultConnectionFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.time.Duration;

/**
 * @author Yevhenii Voevodin
 */
@Singleton
public class LdapConnectionFactoryProvider implements Provider<ConnectionFactory> {

    private final ConnectionFactory connFactory;

    @Inject
    public LdapConnectionFactoryProvider(@Named("ldap.principal") String principal,
                                         @Named("ldap.password") String password,
                                         @Named("ldap.url") String url,
                                         @Named("ldap.connection.timeout_ms") int timeoutMs,
                                         @Named("ldap.connection.read_timeout_ms") int readTimeoutMs) {
        final ConnectionConfig connConfig = new ConnectionConfig();
        connConfig.setLdapUrl(url);
        connConfig.setConnectionInitializer(new BindConnectionInitializer(principal, new Credential(password)));
        connConfig.setConnectTimeout(Duration.ofMillis(timeoutMs));
        connConfig.setResponseTimeout(Duration.ofMillis(readTimeoutMs));
        connFactory = new DefaultConnectionFactory(connConfig);
    }

    @Override
    public ConnectionFactory get() {
        return connFactory;
    }
}
