package com.codenvy.ldap;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.multibindings.OptionalBinder;
import com.google.inject.name.Names;

/**
 * Created by sj on 12.09.16.
 */
public class LdapModule extends AbstractModule {
    @Override
    protected void configure() {
        OptionalBinder.newOptionalBinder(binder(), Key.get(String.class, Names.named("")))
                      .setDefault().toInstance(null);
//    private int     minPoolSize          = 3;
//    private int     maxPoolSize          = 10;
//    private boolean validateOnCheckout   = true;
//    private boolean validatePeriodically = true;
//    private long    validatePeriod       = 300;
//
//    private boolean failFast      = true;
//    private long    idleTime      = 600;
//    private long    prunePeriod   = 10000;
//    private long    blockWaitTime = 6000;
//
//    private String  ldapUrl = "ldap://localhost:389";
//    private boolean useSsl  = true;
//    private boolean useStartTls;
//    private long connectTimeout = 5000;
    }
}
