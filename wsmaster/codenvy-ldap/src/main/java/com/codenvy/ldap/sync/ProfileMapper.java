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

import com.google.common.collect.ImmutableMap;

import org.eclipse.che.api.user.server.model.impl.ProfileImpl;
import org.eclipse.che.commons.lang.Pair;
import org.ldaptive.LdapEntry;

import java.util.Arrays;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * @author Yevhenii Voevodin
 */
public class ProfileMapper implements Function<LdapEntry, ProfileImpl> {

    private final ImmutableMap<String, String> attributes;
    private final String                       idAttr;

    public ProfileMapper(String idAttr, Pair<String, String>[] attributes) {
        this.idAttr = idAttr;
        if (attributes == null) {
            this.attributes = ImmutableMap.of();
        } else {
            this.attributes = ImmutableMap.copyOf(Arrays.stream(attributes).collect(toMap(p -> p.first, p -> p.second)));
        }
    }

    @Override
    public ProfileImpl apply(LdapEntry entry) {
        return null;
    }
}