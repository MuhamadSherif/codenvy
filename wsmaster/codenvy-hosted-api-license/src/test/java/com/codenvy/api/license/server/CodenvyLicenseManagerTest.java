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
package com.codenvy.api.license.server;

import com.codenvy.api.license.CodenvyLicense;
import com.codenvy.api.license.CodenvyLicenseFactory;
import com.codenvy.api.license.LicenseNotFoundException;

import com.codenvy.api.user.server.dao.AdminUserDao;
import com.codenvy.swarm.client.SwarmDockerConnector;
import com.codenvy.swarm.client.model.DockerNode;
import org.eclipse.che.api.core.Page;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.eclipse.che.commons.lang.IoUtil;
import org.eclipse.che.commons.lang.NameGenerator;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Anatoliy Bazko
 * @author Alexander Andrienko
 * @author Dmytro Nochevnov
 */
@Listeners(value = {MockitoTestNGListener.class})
public class CodenvyLicenseManagerTest {

    private static final String TEXT                       = "some test";
    private static final String LICENSE_STORAGE_PREFIX_DIR = "licenseStorage-";
    private static final String LICENSE                    = "license";
    private static final long   USER_NUMBER                = 3;
    private static final int    NODES_NUMBER               = 2;

    @Mock
    private CodenvyLicense        mockCodenvyLicense;
    @Mock
    private CodenvyLicenseFactory licenseFactory;
    @Mock
    private CodenvyLicense        codenvyLicense;
    @Mock
    private SwarmDockerConnector  swarmDockerConnector;
    @Mock
    private AdminUserDao          adminUserDao;
    @Mock
    private Page<UserImpl>        page;
    @Mock
    private List<DockerNode>      dockerNodes;

    private File testDirectory;
    private File licenseFile;

    private CodenvyLicenseManager codenvyLicenseManager;

    @BeforeMethod
    public void setUp() throws IOException, ServerException {
        File targetDir = new File(Thread.currentThread().getContextClassLoader().getResource(".").getPath()).getParentFile();
        testDirectory = new File(targetDir, NameGenerator.generate(LICENSE_STORAGE_PREFIX_DIR, 4));
        licenseFile = new File(testDirectory, LICENSE);
        Files.createDirectories(testDirectory.toPath());
        Mockito.when(codenvyLicense.getLicenseText()).thenReturn(TEXT);

        setAmountOfUsers(USER_NUMBER);
        setSizeOfAdditionalNodes(NODES_NUMBER);

        codenvyLicenseManager = spy(new CodenvyLicenseManager(licenseFile.getAbsolutePath(), licenseFactory, adminUserDao, swarmDockerConnector));
    }

    @AfterMethod
    public void tearDown() throws Exception {
        IoUtil.deleteRecursive(testDirectory);
    }

    @Test
    public void licenseShouldBeStored() throws Exception {
        when(licenseFactory.create(TEXT)).thenReturn(codenvyLicense);
        codenvyLicenseManager.store(TEXT);

        verify(codenvyLicense).getLicenseText();
        assertTrue(Files.exists(licenseFile.toPath()));
        String fileContent = new String(Files.readAllBytes(licenseFile.toPath()), UTF_8);
        assertEquals(TEXT, fileContent);
    }

    @Test
    public void licenseShouldBeDeleted() throws Exception {
        doReturn(codenvyLicense).when(licenseFactory).create(TEXT);
        codenvyLicenseManager.store(TEXT);

        codenvyLicenseManager.delete();
        assertFalse(Files.exists(licenseFile.toPath()));
    }

    @Test(expectedExceptions = LicenseNotFoundException.class)
    public void shouldThrowLicenseNotFoundExceptionIfWeTryToDeleteLicenseFromEmptyStorage() {
        codenvyLicenseManager.delete();
    }

    @Test
    public void licenseShouldBeLoaded() {
        when(licenseFactory.create(TEXT)).thenReturn(codenvyLicense);
        codenvyLicenseManager.store(TEXT);

        CodenvyLicense license = codenvyLicenseManager.load();

        verify(licenseFactory, times(2)).create(TEXT);
        assertEquals(license, codenvyLicense);
    }

    @Test(expectedExceptions = LicenseNotFoundException.class)
    public void shouldThrowLicenseNotFoundExceptionIfWeTryToGetLicenseFromEmptyStorage() {
        codenvyLicenseManager.load();
    }

    @Test
    public void testIsCodenvyLicenseUsageLegal() throws IOException, ServerException {
        doReturn(mockCodenvyLicense).when(codenvyLicenseManager).load();
        doReturn(true).when(mockCodenvyLicense).isLicenseUsageLegal(USER_NUMBER, NODES_NUMBER);

        assertTrue(codenvyLicenseManager.isCodenvyUsageLegal());
    }

    @Test
    public void testIsCodenvyFreeUsageLegal() throws IOException, ServerException {
        setAmountOfUsers(CodenvyLicense.MAX_NUMBER_OF_FREE_USERS);
        setSizeOfAdditionalNodes(CodenvyLicense.MAX_NUMBER_OF_FREE_SERVERS);

        doThrow(LicenseNotFoundException.class).when(codenvyLicenseManager).load();

        assertTrue(codenvyLicenseManager.isCodenvyUsageLegal());
    }

    @Test
    public void testIsCodenvyLicenseUsageNotLegal() throws IOException, ServerException {
        doReturn(mockCodenvyLicense).when(codenvyLicenseManager).load();
        doReturn(false).when(mockCodenvyLicense).isLicenseUsageLegal(USER_NUMBER, NODES_NUMBER);

        assertFalse(codenvyLicenseManager.isCodenvyUsageLegal());
    }

    @Test
    public void testIsCodenvyFreeUsageNotLegal() throws IOException, ServerException {
        setAmountOfUsers(CodenvyLicense.MAX_NUMBER_OF_FREE_USERS + 1);
        setSizeOfAdditionalNodes(CodenvyLicense.MAX_NUMBER_OF_FREE_SERVERS + 1);

        doThrow(LicenseNotFoundException.class).when(codenvyLicenseManager).load();

        assertFalse(codenvyLicenseManager.isCodenvyUsageLegal());
    }

    @Test
    public void testIsCodenvyActualNodesUsageLegal() throws IOException, ServerException {
        doReturn(mockCodenvyLicense).when(codenvyLicenseManager).load();
        doReturn(true).when(mockCodenvyLicense).isLicenseNodesUsageLegal(NODES_NUMBER);

        assertTrue(codenvyLicenseManager.isCodenvyNodesUsageLegal(null));
    }

    @Test
    public void testIsCodenvyGivenNodesUsageLegal() throws IOException, ServerException {
        doReturn(mockCodenvyLicense).when(codenvyLicenseManager).load();
        doReturn(true).when(mockCodenvyLicense).isLicenseNodesUsageLegal(NODES_NUMBER);

        assertTrue(codenvyLicenseManager.isCodenvyNodesUsageLegal(NODES_NUMBER));
        verify(swarmDockerConnector, never()).getAvailableNodes();
    }

    @Test
    public void testIsCodenvyActualNodesUsageNotLegal() throws IOException, ServerException {
        doReturn(mockCodenvyLicense).when(codenvyLicenseManager).load();
        doReturn(false).when(mockCodenvyLicense).isLicenseNodesUsageLegal(NODES_NUMBER);

        assertFalse(codenvyLicenseManager.isCodenvyNodesUsageLegal(null));
    }

    @Test
    public void testIsCodenvyGivenNodesUsageNotLegal() throws IOException, ServerException {
        doReturn(mockCodenvyLicense).when(codenvyLicenseManager).load();
        doReturn(false).when(mockCodenvyLicense).isLicenseNodesUsageLegal(NODES_NUMBER);

        assertFalse(codenvyLicenseManager.isCodenvyNodesUsageLegal(NODES_NUMBER));
        verify(swarmDockerConnector, never()).getAvailableNodes();
    }

    @Test
    public void testIsCodenvyNodesFreeUsageLegal() throws IOException, ServerException {
        setSizeOfAdditionalNodes(CodenvyLicense.MAX_NUMBER_OF_FREE_SERVERS);

        doThrow(LicenseNotFoundException.class).when(codenvyLicenseManager).load();

        assertTrue(codenvyLicenseManager.isCodenvyNodesUsageLegal(null));
    }

    @Test
    public void testIsCodenvyNodesFreeUsageNotLegal() throws IOException, ServerException {
        setSizeOfAdditionalNodes(CodenvyLicense.MAX_NUMBER_OF_FREE_SERVERS + 1);

        doThrow(LicenseNotFoundException.class).when(codenvyLicenseManager).load();

        assertFalse(codenvyLicenseManager.isCodenvyNodesUsageLegal(null));
    }

    private void setAmountOfUsers(long amountOfUsers) throws ServerException {
        when(adminUserDao.getAll(30, 0)).thenReturn(page);   //TODO Replace it with UserManager#getTotalCount when codenvy->jpa-integration branch will be merged to master
        when(page.getTotalItemsCount()).thenReturn(amountOfUsers);
    }

    private void setSizeOfAdditionalNodes(int size) throws IOException {
        when(swarmDockerConnector.getAvailableNodes()).thenReturn(dockerNodes);
        when(dockerNodes.size()).thenReturn(size);
    }

}
