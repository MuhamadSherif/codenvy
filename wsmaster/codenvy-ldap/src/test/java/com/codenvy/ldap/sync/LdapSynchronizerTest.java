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

import com.codenvy.ldap.LdapConnectionFactoryProvider;
import com.codenvy.ldap.MyLdapServer;
import com.codenvy.ldap.sync.LdapSynchronizer.SyncResult;

import org.apache.directory.shared.ldap.entry.ServerEntry;
import org.eclipse.che.api.user.server.model.impl.ProfileImpl;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.api.user.server.spi.ProfileDao;
import org.eclipse.che.api.user.server.spi.UserDao;
import org.eclipse.che.commons.lang.Pair;
import org.ldaptive.ConnectionFactory;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

// TODO:
// cover synchronizer with tests
// separate lookup & membership selector tests
// add integration tests

/**
 * Tests {@link LdapSynchronizer}.
 *
 * @author Yevhenii Voevodin
 */
@Listeners(MockitoTestNGListener.class)
public class LdapSynchronizerTest {

    private static final String BASE_DN = "dc=codenvy,dc=com";

    @Mock
    private UserDao userDao;

    @Mock
    private ProfileDao profileDao;

    @Mock
    private EntityManager entityManager;

    private MyLdapServer      server;
    private ConnectionFactory connFactory;
    private List<ServerEntry> createdEntries;

    @BeforeClass
    public void startServer() throws Exception {
        server = MyLdapServer.builder()
                             .setPartitionId("codenvy")
                             .allowAnonymousAccess()
                             .setPartitionDn(BASE_DN)
                             .useTmpWorkingDir()
                             .setMaxSizeLimit(1000)
                             .build();
        server.start();
        connFactory = new LdapConnectionFactoryProvider(server.getAdminDn(),
                                                        server.getAdminPassword(),
                                                        server.getUrl(),
                                                        30_000,
                                                        120_000).get();

        // create a set of users
        createdEntries = new ArrayList<>(300);

        // first 100 users have additional attributes
        for (int i = 0; i < 100; i++) {
            createdEntries.add(server.addDefaultLdapUser(i,
                                                         Pair.of("givenName", "test-user-first-name" + i),
                                                         Pair.of("sn", "test-user-last-name"),
                                                         Pair.of("telephoneNumber", "00000000" + i)));
        }

        // next 100 users are members of group1
        final List<String> group1Members = new ArrayList<>(100);
        for (int i = 100; i < 200; i++) {
            final ServerEntry entry = server.addDefaultLdapUser(i);
            createdEntries.add(entry);
            group1Members.add(entry.getDn().toString());
        }
        server.addDefaultLdapGroup("group1", group1Members);

        // next 100 users are members of group2
        final List<String> group2Members = new ArrayList<>(100);
        for (int i = 200; i < 300; i++) {
            final ServerEntry entry = server.addDefaultLdapUser(i);
            createdEntries.add(entry);
            group2Members.add(entry.getDn().toString());
        }
        server.addDefaultLdapGroup("group2", group2Members);
    }

    @AfterClass
    public void stopServer() throws Exception {
        server.stop();
    }

    @BeforeMethod
    public void mockEntityManager() {
        final Query q = mock(Query.class);
        when(q.getResultList()).thenReturn(new ArrayList());
        when(entityManager.createNativeQuery(anyString())).thenReturn(q);
    }

    @Test
    public void testLookupSynchronization() throws Exception {
        // mock storage
        final Set<UserImpl> storedUsers = new HashSet<>();
        doAnswer(inv -> storedUsers.add((UserImpl)inv.getArguments()[0])).when(userDao).create(anyObject());
        final Map<String, ProfileImpl> storedProfiles = new HashMap<>();
        doAnswer(inv -> {
            final ProfileImpl profile = (ProfileImpl)inv.getArguments()[0];
            storedProfiles.put(profile.getUserId(), profile);
            return null;
        }).when(profileDao).create(anyObject());

        final LdapSynchronizer synchronizer =
                new LdapSynchronizerBuilder().setConnectionFactory(connFactory)
                                             .setEntityManagerProvider(() -> entityManager)
                                             .setUserDao(userDao)
                                             .setProfileDao(profileDao)
                                             .setBaseDn(BASE_DN)
                                             .setUsersFilter("(&(objectClass=*)(givenName=*))")
                                             .setPageSize(10)
                                             .setUserIdAttr("uid")
                                             .setUserNameAttr("cn")
                                             .setUserEmailAttr("mail")
                                             .addProfileAttr("firstName", "givenName")
                                             .build();

        final SyncResult syncResult = synchronizer.syncAll();

        assertEquals(syncResult.getCreated(), 100);
        assertEquals(syncResult.getRemoved(), 0);
        assertEquals(syncResult.getUpdated(), 0);
        assertEquals(storedUsers, createdEntries.stream()
                                                .filter(entry -> !entry.get("sn")
                                                                       .get(0) // <- sn is a required attribute for inetOrgPerson
                                                                       .toString()
                                                                       .equals("<none>"))
                                                .map(LdapSynchronizerTest::asUser)
                                                .collect(toSet()));
        for (UserImpl storedUser : storedUsers) {
            final ProfileImpl profile = storedProfiles.get(storedUser.getId());
            assertNotNull(profile.getAttributes().get("firstName"));
        }
    }

    @Test
    public void testMembershipSynchronization() throws Exception {
        // mock storage
        final Set<UserImpl> storedUsers = new HashSet<>();
        doAnswer(inv -> storedUsers.add((UserImpl)inv.getArguments()[0])).when(userDao).create(anyObject());

        final LdapSynchronizer synchronizer =
                new LdapSynchronizerBuilder().setConnectionFactory(connFactory)
                                             .setEntityManagerProvider(() -> entityManager)
                                             .setUserDao(userDao)
                                             .setProfileDao(profileDao)
                                             .setBaseDn(BASE_DN)
                                             .setUsersFilter("(&(objectClass=inetOrgPerson)(uid=*))")
                                             .setGroupFilter("(objectClass=groupOfNames)")
                                             .setMembersAttrName("member")
                                             .setUserIdAttr("uid")
                                             .setUserNameAttr("cn")
                                             .setUserEmailAttr("mail")
                                             .addProfileAttr("firstName", "givenName")
                                             .build();

        final SyncResult syncResult = synchronizer.syncAll();

        assertEquals(syncResult.getCreated(), 200);
        assertEquals(syncResult.getRemoved(), 0);
        assertEquals(syncResult.getUpdated(), 0);
        assertEquals(storedUsers, createdEntries.stream()
                                                .filter(entry -> entry.get("sn")
                                                                      .get(0) // <- sn is a required attribute for inetOrgPerson
                                                                      .toString()
                                                                      .equals("<none>"))
                                                .map(LdapSynchronizerTest::asUser)
                                                .collect(toSet()));
    }

    private static UserImpl asUser(ServerEntry entry) {
        return new UserImpl(entry.get("uid").get(0).toString(),
                            entry.get("mail").get(0).toString(),
                            entry.get("cn").get(0).toString());
    }
}
