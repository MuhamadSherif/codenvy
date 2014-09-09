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
package com.codenvy.cdec.im.cli.command;

import com.codenvy.cdec.restlet.InstallationManagerService;
import com.codenvy.cdec.user.UserCredentials;
import com.codenvy.cdec.utils.Commons;

import org.apache.felix.service.command.CommandSession;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/** @author Dmytro Nochevnov */
public class VersionCommandTest {
    private AbstractIMCommand spyCommand;

    @Mock
    private InstallationManagerService mockInstallationManagerProxy;
    @Mock
    private CommandSession             commandSession;

    private UserCredentials                        credentials;
    private JacksonRepresentation<UserCredentials> userCredentialsRep;
    private String okServiceResponse = "{"
                                       + "artifact: {"
                                       + "           artifact: any,"
                                       + "           version: any,"
                                       + "           status: SUCCESS"
                                       + "           },"
                                       + "status: \"OK\""
                                       + "}";

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        spyCommand = spy(new VersionCommand());
        spyCommand.installationManagerProxy = mockInstallationManagerProxy;

        doNothing().when(spyCommand).init();

        credentials = new UserCredentials("token", "accountId");
        userCredentialsRep = new JacksonRepresentation<>(credentials);
        doReturn(userCredentialsRep).when(spyCommand).getCredentialsRep();
    }

    @Test
    public void testCommand() throws Exception {
        doReturn(okServiceResponse).when(mockInstallationManagerProxy).getVersions(userCredentialsRep);

        CommandInvoker commandInvoker = new CommandInvoker(spyCommand, commandSession);

        CommandInvoker.Result result = commandInvoker.invoke();
        String output = result.getOutputStream();
        assertEquals(output, Commons.getPrettyPrintingJson(okServiceResponse) + "\n");
    }
}
