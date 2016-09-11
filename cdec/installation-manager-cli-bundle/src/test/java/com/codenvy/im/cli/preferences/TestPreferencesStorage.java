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
package com.codenvy.im.cli.preferences;

import com.codenvy.cli.command.builtin.Remote;
import com.codenvy.cli.preferences.Preferences;
import com.codenvy.cli.preferences.PreferencesAPI;
import com.codenvy.cli.security.RemoteCredentials;
import com.google.common.io.Files;

import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNull;

/** @author Dmytro Nochevnov */
public class TestPreferencesStorage {
    private final static String SAAS_SERVER_REMOTE_NAME = "saas-server";
    private final static String TEST_TOKEN              = "authToken";

    private Preferences globalPreferences;
    private final static String DEFAULT_PREFERENCES_FILE          = "default-preferences.json";
    private final static String PREFERENCES_WITH_SAAS_SERVER_FILE = "preferences-with-codenvy-onprem-server-remote.json";


    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    private void testGetAuthToken() {
        globalPreferences = loadPreferences(PREFERENCES_WITH_SAAS_SERVER_FILE);
//        CodenvyOnpremPreferences codenvyOnpremPreferences = new CodenvyOnpremPreferences(globalPreferences);

//        assertEquals(codenvyOnpremPreferences.getAuthToken(), TEST_TOKEN);
    }

    @Test
    private void testGetPreferencesWhenSaasServerRemoteAbsent() {
        globalPreferences = loadPreferences(DEFAULT_PREFERENCES_FILE);
//        CodenvyOnpremPreferences codenvyOnpremPreferences = new CodenvyOnpremPreferences(globalPreferences);
//        assertNull(codenvyOnpremPreferences.getAuthToken());
    }

//    @Test(expectedExceptions = IllegalStateException.class,
//        expectedExceptionsMessageRegExp = "Please log in into 'saas-server' remote.")
//    public void testInitWhenUpdateServerRemoteAbsent() {
//        globalPreferences = loadPreferences(DEFAULT_PREFERENCES_FILE);
//        prepareTestAbstractIMCommand(spyCommand);
//        spyCommand.init();
//        spyCommand.validateIfUserLoggedInCodenvyOnprem();
//    }
//
//    @Test(expectedExceptions = IllegalStateException.class,
//        expectedExceptionsMessageRegExp = "Please log in into 'saas-server' remote.")
//    public void testInitWhenUserDidNotLogin() {
//        globalPreferences = loadPreferences(PREFERENCES_WITH_SAAS_SERVER_WITHOUT_LOGIN_FILE);
//        prepareTestAbstractIMCommand(spyCommand);
//        spyCommand.init();
//        spyCommand.validateIfUserLoggedInCodenvyOnprem();
//    }
//
//    @Test
//    public void testCreateRemote() {
//        doReturn(mockMultiRemoteCodenvy).when(spyCommand).getMultiRemoteCodenvy();
//        doReturn(null).when(mockMultiRemoteCodenvy).getRemote(CodenvyOnpremPreferences.CODENVY_ONPREM_REMOTE_NAME);
//        spyCommand.upsertRemote(CodenvyOnpremPreferences.CODENVY_ONPREM_REMOTE_NAME, CODENVY_ONPREM_SERVER_URL);
//        verify(mockMultiRemoteCodenvy).addRemote(CodenvyOnpremPreferences.CODENVY_ONPREM_REMOTE_NAME, CODENVY_ONPREM_SERVER_URL);
//    }
//
//    @Test
//    public void testCreateRemoteOverExistedOne() {
//        globalPreferences = loadPreferences(PREFERENCES_WITH_SAAS_SERVER_FILE);
//        prepareTestAbstractIMCommand(spyCommand);
//        spyCommand.init();
//
//        RemoteCredentials credentials = globalPreferences.path("remotes").get(CodenvyOnpremPreferences.CODENVY_ONPREM_REMOTE_NAME, RemoteCredentials.class);
//        assertEquals(credentials.getToken(), TEST_TOKEN);
//
//        String newUrl = "new_url";
//        spyCommand.upsertRemote(CodenvyOnpremPreferences.CODENVY_ONPREM_REMOTE_NAME, newUrl);
//
//        Remote remote = globalPreferences.path("remotes").get(CodenvyOnpremPreferences.CODENVY_ONPREM_REMOTE_NAME, Remote.class);
//        assertEquals(remote.getUrl(), newUrl);
//
//        credentials = globalPreferences.path("remotes").get(CodenvyOnpremPreferences.CODENVY_ONPREM_REMOTE_NAME, RemoteCredentials.class);
//        assertEquals(credentials.getToken(), "");
//        assertEquals(credentials.getUsername(), "");
//    }

    private Preferences loadPreferences(String preferencesFileRelativePath) {
        String preferencesFileFullPath = getClass().getClassLoader().getResource(preferencesFileRelativePath).getPath();
        String tempPreferencesFileFullPath = preferencesFileFullPath + ".temp";
        File preferencesFile = new File(preferencesFileFullPath);
        File tempPreferencesFile = new File(tempPreferencesFileFullPath);
        
        try {
            Files.copy(preferencesFile, tempPreferencesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
         
        return PreferencesAPI.getPreferences(tempPreferencesFile.toURI());
    }
}
