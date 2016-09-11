package com.codenvy.ldap.sync;

import org.ldaptive.Connection;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;

/**
 * @author Yevhenii Voevodin
 */
public class MembershipSelector implements LdapEntrySelector {

    public MembershipSelector(String baseDn, String membersAttr) {
    }

    @Override
    public Iterable<LdapEntry> select(Connection connection) throws LdapException {
        return null;
    }
}
