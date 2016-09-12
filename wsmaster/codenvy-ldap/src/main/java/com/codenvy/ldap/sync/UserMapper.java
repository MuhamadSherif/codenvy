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

import org.eclipse.che.api.user.server.model.impl.UserImpl;
import org.ldaptive.LdapEntry;

import java.util.function.Function;

/**
 * @author Yevhenii Voevodin
 */
public class UserMapper implements Function<LdapEntry, UserImpl> {

    private final String idAttr;
    private final String nameAttr;
    private final String mailAttr;

    public UserMapper(String idAttr, String nameAttr, String emailAttr) {
        this.idAttr = idAttr;
        this.nameAttr = nameAttr;
        this.mailAttr = emailAttr;
    }

    @Override
    public UserImpl apply(LdapEntry entry) {
        return null;
    }
}
