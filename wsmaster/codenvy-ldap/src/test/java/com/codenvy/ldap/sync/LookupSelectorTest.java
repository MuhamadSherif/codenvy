package com.codenvy.ldap.sync;

import com.codenvy.ldap.LdapConnectionFactoryProvider;
import com.codenvy.ldap.MyLdapServer;

import org.apache.directory.shared.ldap.entry.ServerEntry;
import org.eclipse.che.api.user.server.spi.ProfileDao;
import org.eclipse.che.api.user.server.spi.UserDao;
import org.eclipse.che.commons.lang.Pair;
import org.ldaptive.ConnectionFactory;
import org.mockito.Mock;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link LookupSelector}.
 *
 * @author Yevhenii Voevodin
 */
public class LookupSelectorTest {

//
//    @Mock
//    private UserDao userDao;
//
//    @Mock
//    private ProfileDao profileDao;
//
//    @Mock
//    private EntityManager entityManager;
//
//    private MyLdapServer      server;
//    private ConnectionFactory connFactory;
//    private List<ServerEntry> createdEntries;
//
//    @BeforeClass
//    public void startServer() throws Exception {
//        server.start();
//        connFactory = new LdapConnectionFactoryProvider(server.getAdminDn(),
//                                                        server.getAdminPassword(),
//                                                        server.getUrl(),
//                                                        30_000,
//                                                        120_000).get();
//
//        // create a set of users
//        createdEntries = new ArrayList<>(300);
//
//        // first 100 users have additional attributes
//        for (int i = 0; i < 100; i++) {
//            createdEntries.add(addLdapUser(i,
//                                           Pair.of("givenName", "test-user-first-name" + i),
//                                           Pair.of("sn", "test-user-last-name"),
//                                           Pair.of("telephoneNumber", "00000000" + i)));
//        }
//
//        // next 100 users are members of group1
//        final ServerEntry group1 = createLdapGroup("group1");
//        for (int i = 100; i < 200; i++) {
//            final ServerEntry entry = addLdapUser(i);
//            group1.add("member", entry.getDn().toString());
//            createdEntries.add(entry);
//        }
//        server.addEntry(group1);
//
//        // next 100 users are members of group2
//        final ServerEntry group2 = createLdapGroup("group2");
//        for (int i = 200; i < 300; i++) {
//            final ServerEntry entry = addLdapUser(i);
//            group2.add("member", entry.getDn().toString());
//            createdEntries.add(entry);
//        }
//        server.addEntry(group2);
//    }
//
//    @AfterClass
//    public void stopServer() throws Exception {
//        server.stop();
//    }
//
//    @BeforeMethod
//    public void mockEntityManager() {
//        final Query q = mock(Query.class);
//        when(q.getResultList()).thenReturn(new ArrayList());
//        when(entityManager.createNativeQuery(anyString())).thenReturn(q);
//    }
}
