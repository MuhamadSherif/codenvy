package com.codenvy.ldap;

/**
 * @author Yevhenii Voevodin
 */
public class TestConnectionFactoryProvider extends LdapConnectionFactoryProvider {

    private static final long DEFAULT_READ_TIMEOUT    = 30_000L;
    private static final long DEFAULT_CONNECT_TIMEOUT = 30_000L;

    public TestConnectionFactoryProvider(MyLdapServer server) {
        super(server.getAdminDn(),
              server.getAdminPassword(),
              server.getUrl(),
              DEFAULT_CONNECT_TIMEOUT,
              DEFAULT_READ_TIMEOUT);
    }
}
