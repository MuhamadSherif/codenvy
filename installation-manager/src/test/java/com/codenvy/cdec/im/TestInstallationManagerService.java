/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.cdec.im;

import static com.codenvy.cdec.utils.Commons.getPrettyPrintingJson;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.LinkedHashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.codenvy.cdec.artifacts.Artifact;
import com.codenvy.cdec.artifacts.CDECArtifact;
import com.codenvy.cdec.artifacts.InstallManagerArtifact;
import com.codenvy.cdec.restlet.InstallationManager;
import com.codenvy.cdec.restlet.InstallationManagerService;
import com.codenvy.cdec.utils.HttpException;
import com.codenvy.cdec.utils.HttpTransport;

/**
 * @author Dmytro Nochevnov
 */
public class TestInstallationManagerService {
    private InstallationManagerService installationManagerService;

    private InstallationManager mockInstallationManager;
    private HttpTransport       mockTransport;
    private Artifact            mockInstallManagerArtifact;
    private Artifact            mockCdecArtifact;

    @BeforeMethod
    public void init() {
        initMocks();
        installationManagerService = new InstallationManagerServiceTestImpl(mockInstallationManager);
    }

    public void initMocks() {
        mockTransport = mock(HttpTransport.class);
        mockInstallationManager = mock(InstallationManagerImpl.class);
        mockInstallManagerArtifact = spy(new InstallManagerArtifact());
        mockCdecArtifact = spy(new CDECArtifact("update/endpoint", mockTransport));
    }

    @Test
    public void testGetUpdates() throws Exception {
        when(mockInstallationManager.getUpdates(anyString())).thenReturn(new LinkedHashMap<Artifact, String>() {
            {
                put(mockInstallManagerArtifact, "1.0.1");
                put(mockCdecArtifact, "2.10.5");
            }
        });

        String response = installationManagerService.getUpdates("");
        assertEquals(getPrettyPrintingJson(response), "{\n" +
                                                      "  \"artifacts\": [\n" +
                                                      "    {\n" +
                                                      "      \"artifact\": \"installation-manager\",\n" +
                                                      "      \"version\": \"1.0.1\"\n" +
                                                      "    },\n" +
                                                      "    {\n" +
                                                      "      \"artifact\": \"cdec\",\n" +
                                                      "      \"version\": \"2.10.5\"\n" +
                                                      "    }\n" +
                                                      "  ],\n" +
                                                      "  \"status\": \"OK\"\n" +
                                                      "}");
    }

    @Test
    public void testGetUpdatesCatchesException302() throws Exception {
        when(mockInstallationManager.getUpdates(anyString())).thenThrow(new HttpException(302, ""));
        String response = installationManagerService.getUpdates("incorrect-token");
        assertEquals(getPrettyPrintingJson(response), "{\n" +
                                                      "  \"message\": \"Incorrect login.\",\n" +
                                                      "  \"status\": \"ERROR\"\n" +
                                                      "}");
    }

    @Test
    public void testGetUpdatesCatchesException404() throws Exception {
        when(mockInstallationManager.getUpdates(anyString())).thenThrow(new HttpException(404, "There is no any version of artifact 'cdec'"));
        String response = installationManagerService.getUpdates("incorrect-token");
        assertEquals(getPrettyPrintingJson(response), "{\n" +
                                                      "  \"message\": \"There is no any version of artifact 'cdec'\",\n" +
                                                      "  \"status\": \"OK\"\n" +
                                                      "}");
    }

    @Test
    public void testGetUpdatesCatchesException() throws Exception {
        when(mockInstallationManager.getUpdates(anyString())).thenThrow(new HttpException(500, "Server Error"));
        String response = installationManagerService.getUpdates("incorrect-token");
        assertEquals(getPrettyPrintingJson(response), "{\n" +
                                                      "  \"message\": \"" + InstallationManagerServiceImpl.COMMON_ERROR_MESSAGE + "\",\n" +
                                                      "  \"status\": \"ERROR\"\n" +
                                                      "}");
    }
    
    @Test
    public void testDownloadArtifact() throws Exception {
        doReturn(new LinkedHashMap<Artifact, String>() {
            {
                put(mockCdecArtifact, "2.10.5");
                put(mockInstallManagerArtifact, "1.0.1");
            }
        }).when(mockInstallationManager).getUpdates();

        String response = installationManagerService.download();
        assertEquals(getPrettyPrintingJson(response), "{\n" +
                                                      "  \"artifacts\": [\n" +
                                                      "    {\n" +
                                                      "      \"artifact\": \"cdec\",\n" +
                                                      "      \"status\": \"SUCCESS\",\n" +
                                                      "      \"version\": \"2.10.5\"\n" +
                                                      "    },\n" +
                                                      "    {\n" +
                                                      "      \"artifact\": \"installation-manager\",\n" +
                                                      "      \"status\": \"SUCCESS\",\n" +
                                                      "      \"version\": \"1.0.1\"\n" +
                                                      "    }\n" +
                                                      "  ],\n" +
                                                      "  \"status\": \"OK\"\n" +
                                                      "}");
    }

    public class InstallationManagerServiceTestImpl extends InstallationManagerServiceImpl {
        public InstallationManagerServiceTestImpl(InstallationManager manager) {
            this.manager = manager;
        }
    }
}