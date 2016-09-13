package com.codenvy.ldap.sync;

import com.codenvy.ldap.LdapConnectionFactoryProvider;
import com.codenvy.ldap.MyLdapServer;
import com.codenvy.ldap.TestConnectionFactoryProvider;

import org.apache.directory.shared.ldap.entry.ServerEntry;
import org.eclipse.che.commons.lang.Pair;
import org.ldaptive.Connection;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapEntry;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Tests {@link MembershipSelector};
 *
 * @author Yevhenii Voevodin
 */
public class MembershipSelectorTest {

    private MyLdapServer      server;
    private ConnectionFactory connFactory;

    @BeforeClass
    public void setUpServer() throws Exception {
        (server = MyLdapServer.newDefaultServer()).start();
        connFactory = new TestConnectionFactoryProvider(server).get();

        // first 100 users don't belong to any group
        for (int i = 0; i < 100; i++) {
            server.addDefaultLdapUser(i);
        }

        // next 100 users are members of group1
        final List<String> group1Members = new ArrayList<>(100);
        final List<String> group2Members = new ArrayList<>(100);
        for (int i = 100; i < 300; i++) {
            final ServerEntry entry = server.addDefaultLdapUser(i, Pair.of("givenName", "gn-" + i));
            if (i % 2 == 0) {
                group1Members.add(entry.getDn().toString());
            } else {
                group2Members.add(entry.getDn().toString());
            }
            group1Members.add(entry.getDn().toString());
        }
        server.addDefaultLdapGroup("group1", group1Members);
        server.addDefaultLdapGroup("group2", group2Members);
    }

    @AfterClass
    public void shutdownServer() throws Exception {
        server.shutdown();
    }

    @Test
    public void testMembershipSelection() throws Exception {
        final MembershipSelector selector =
                new MembershipSelector(server.getBaseDn(),
                                       "(objectClass=groupOfNames)",
                                       "(objectClass=inetOrgPerson)",
                                       "member",
                                       "uid",
                                       "givenName");
        try (Connection conn = connFactory.getConnection()) {
            conn.open();
            final Set<LdapEntry> selection = StreamSupport.stream(selector.select(conn).spliterator(), false)
                                                          .collect(toSet());
            assertEquals(selection.size(), 200);
            for (LdapEntry entry : selection) {
                assertNotNull(entry.getAttribute("givenName"));
                assertNotNull(entry.getAttribute("uid"));
            }
        }
    }
}
