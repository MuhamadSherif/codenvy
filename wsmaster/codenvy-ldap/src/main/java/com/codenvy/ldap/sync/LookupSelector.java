package com.codenvy.ldap.sync;

import org.ldaptive.Connection;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.Response;
import org.ldaptive.SearchFilter;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.SearchScope;
import org.ldaptive.control.util.CookieManager;
import org.ldaptive.control.util.DefaultCookieManager;
import org.ldaptive.control.util.PagedResultsClient;

import java.util.Iterator;

import static org.ldaptive.ResultCode.SUCCESS;

/**
 * @author Yevhenii Voevodin
 */
public class LookupSelector implements LdapEntrySelector {

    private final String   filter;
    private final String   baseDn;
    private final String[] attributes;
    private final int      pageSize;

    public LookupSelector(int pageSize,
                          String baseDn,
                          String filter,
                          String[] attributes) {
        this.filter = filter;
        this.baseDn = baseDn;
        this.attributes = attributes;
        this.pageSize = pageSize;
    }

    @Override
    public Iterable<LdapEntry> select(Connection connection) throws LdapException {
        final SearchRequest req = new SearchRequest();
        req.setBaseDn(baseDn);
        req.setSearchFilter(new SearchFilter(filter));
        req.setReturnAttributes(attributes);
        req.setSearchScope(SearchScope.SUBTREE);
        return new PagedIterable(new PagedResultsClient(connection, pageSize), req);
    }

    private static class PagedIterable implements Iterable<LdapEntry> {

        private final SearchRequest      request;
        private final PagedResultsClient prClient;

        private PagedIterable(PagedResultsClient prClient, SearchRequest request) {
            this.request = request;
            this.prClient = prClient;
        }

        @Override
        public Iterator<LdapEntry> iterator() {
            return new PagedIterator(prClient, request);
        }
    }

    private static class PagedIterator implements Iterator<LdapEntry> {

        private final SearchRequest      request;
        private final PagedResultsClient prClient;
        private final CookieManager      cm;

        private Response<SearchResult> response;
        private Iterator<LdapEntry>    delegate;

        private PagedIterator(PagedResultsClient prClient, SearchRequest request) {
            this.request = request;
            this.prClient = prClient;
            this.cm = new DefaultCookieManager();
            requestNextPage();
        }

        @Override
        public boolean hasNext() {
            if (delegate.hasNext()) {
                return true;
            }
            if (!prClient.hasMore(response)) {
                return false;
            }
            requestNextPage();
            return hasNext();
        }

        @Override
        public LdapEntry next() {
            return delegate.next();
        }

        private void requestNextPage() {
            try {
                response = prClient.execute(request, cm);
                if (response.getResultCode() != SUCCESS) {
                    throw new SyncException("Couldn't get a next page of entries, result code is " + response.getResultCode());
                }
                delegate = response.getResult().getEntries().iterator();
            } catch (LdapException x) {
                throw new SyncException(x.getLocalizedMessage(), x);
            }
        }
    }
}
