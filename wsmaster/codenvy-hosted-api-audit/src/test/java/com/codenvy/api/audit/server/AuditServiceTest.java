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
package com.codenvy.api.audit.server;

import com.codenvy.api.license.server.license.CodenvyLicense;
import com.codenvy.api.license.server.license.CodenvyLicenseManager;
import com.codenvy.api.permission.server.PermissionManager;
import com.codenvy.api.permission.server.PermissionsImpl;
import com.codenvy.api.user.server.dao.AdminUserDao;
import com.jayway.restassured.response.Response;

import org.eclipse.che.api.core.Page;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.api.workspace.server.WorkspaceManager;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceConfigImpl;
import org.eclipse.che.api.workspace.server.model.impl.WorkspaceImpl;
import org.everrest.assured.EverrestJetty;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.everrest.assured.JettyHttpServer.ADMIN_USER_NAME;
import static org.everrest.assured.JettyHttpServer.ADMIN_USER_PASSWORD;
import static org.everrest.assured.JettyHttpServer.SECURE_PATH;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Tests for {@link AuditService}.
 *
 * @author Igor Vinokur
 */
@Listeners(value = {EverrestJetty.class, MockitoTestNGListener.class})
public class AuditServiceTest {

    @Mock
    private AdminUserDao      adminUserDao;
    @Mock
    private WorkspaceManager  workspaceManager;
    @Mock
    private PermissionManager permissionManager;
    @Mock
    private CodenvyLicenseManager licenseManager;
    @InjectMocks
    private AuditService      service;

    @Test
    public void shouldReturnAuditReport() throws Exception {
        //License
        CodenvyLicense license = mock(CodenvyLicense.class);
        when(license.getNumberOfUsers()).thenReturn(15);
        when(license.getExpirationDate()).thenReturn(new SimpleDateFormat("dd MMMM yyyy").parse("01 January 2016"));
        when(licenseManager.load()).thenReturn(license);
        //User
        UserImpl user1 = mock(UserImpl.class);
        UserImpl user2 = mock(UserImpl.class);
        when(user1.getEmail()).thenReturn("user1@email.com");
        when(user2.getEmail()).thenReturn("user2@email.com");
        when(user1.getId()).thenReturn("User1Id");
        when(user2.getId()).thenReturn("User2Id");
        when(user1.getName()).thenReturn("User1");
        when(user2.getName()).thenReturn("User2");
        //Workspace config
        WorkspaceConfigImpl ws1config = mock(WorkspaceConfigImpl.class);
        WorkspaceConfigImpl ws2config = mock(WorkspaceConfigImpl.class);
        WorkspaceConfigImpl ws3config = mock(WorkspaceConfigImpl.class);
        when(ws1config.getName()).thenReturn("Workspace1Name");
        when(ws2config.getName()).thenReturn("Workspace2Name");
        when(ws3config.getName()).thenReturn("Workspace3Name");
        //Workspace
        WorkspaceImpl workspace1 = mock(WorkspaceImpl.class);
        WorkspaceImpl workspace2 = mock(WorkspaceImpl.class);
        WorkspaceImpl workspace3 = mock(WorkspaceImpl.class);
        when(workspace1.getNamespace()).thenReturn("User1");
        when(workspace2.getNamespace()).thenReturn("User2");
        when(workspace3.getNamespace()).thenReturn("User2");
        when(workspace1.getId()).thenReturn("Workspace1Id");
        when(workspace2.getId()).thenReturn("Workspace2Id");
        when(workspace3.getId()).thenReturn("Workspace3Id");
        when(workspace1.getConfig()).thenReturn(ws1config);
        when(workspace2.getConfig()).thenReturn(ws2config);
        when(workspace3.getConfig()).thenReturn(ws3config);
        when(workspaceManager.getWorkspaces("User1Id")).thenReturn(asList(workspace1, workspace2));
        when(workspaceManager.getWorkspaces("User2Id")).thenReturn(singletonList(workspace3));
        //Permissions
        PermissionsImpl ws1User1Permissions = mock(PermissionsImpl.class);
        PermissionsImpl ws2User1Permissions = mock(PermissionsImpl.class);
        PermissionsImpl ws2User2Permissions = mock(PermissionsImpl.class);
        PermissionsImpl ws3User2Permissions = mock(PermissionsImpl.class);
        when(ws1User1Permissions.getUser()).thenReturn("User1Id");
        when(ws2User1Permissions.getUser()).thenReturn("User1Id");
        when(ws2User2Permissions.getUser()).thenReturn("User2Id");
        when(ws3User2Permissions.getUser()).thenReturn("User2Id");
        when(ws1User1Permissions.getActions()).thenReturn(asList("read", "use", "run", "configure", "setPermissions", "delete"));
        when(ws2User1Permissions.getActions()).thenReturn(asList("read", "use", "run", "configure", "setPermissions"));
        when(ws3User2Permissions.getActions()).thenReturn(asList("read", "use", "run", "configure", "setPermissions", "delete"));
        when(permissionManager.getByInstance(anyString(), eq("Workspace1Id"))).thenReturn(singletonList(ws1User1Permissions));
        when(permissionManager.getByInstance(anyString(), eq("Workspace2Id"))).thenReturn(asList(ws2User1Permissions, ws2User2Permissions));
        when(permissionManager.getByInstance(anyString(), eq("Workspace3Id"))).thenReturn(singletonList(ws3User2Permissions));
        //Page
        Page page = mock(Page.class);
        Page emptyPage = mock(Page.class);
        when(page.getItems()).thenReturn(asList(user1, user2));
        when(page.getTotalItemsCount()).thenReturn(2L);
        when(emptyPage.getItems()).thenReturn(emptyList());
        when(adminUserDao.getAll(1, 0)).thenReturn(page);
        when(adminUserDao.getAll(20, 0)).thenReturn(page);
        when(adminUserDao.getAll(20, 2)).thenReturn(emptyPage);
        final Response response = given().auth()
                                         .basic(ADMIN_USER_NAME, ADMIN_USER_PASSWORD)
                                         .when()
                                         .get(SECURE_PATH + "/audit");

        assertEquals(response.getStatusCode(), 200);

        response.getBody().print();
    }
}
