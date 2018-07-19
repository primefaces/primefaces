package org.primefaces.application.resource.csp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.application.resource.csp.scripts.ContentSecurityPolicyScripts;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentSecurityPolicyFilterTest {

    private ContentSecurityPolicyFilter filter;
    
    private HttpServletRequest request;
    private String contextPath;
    
    @Before
    public void setup() {
        filter = new ContentSecurityPolicyFilter();
        request = mock(HttpServletRequest.class);
        contextPath = "/pf";
        when(request.getContextPath()).thenReturn(contextPath);
    }

    @Test
    public void testScriptNonces() {
        filter.configuration = new ContentSecurityPolicyConfiguration("true", "script-src", null, null);
        ContentSecurityPolicyScripts scripts = new ContentSecurityPolicyScripts(new HashSet<>(Arrays.asList("1", "2")),
                Collections.<String>emptySet());
        String headerValue = filter.getHeaderValue(request, scripts);
        Assert.assertEquals(String.format("script-src 'nonce-1' 'nonce-2'; report-uri %s;", contextPath + ContentSecurityPolicyReportServlet.URL), headerValue);
    }
    
    @Test
    public void testScriptNoncesAndHostWhitelist() {
        filter.configuration = new ContentSecurityPolicyConfiguration("true", "script-src", 
                "https://www.google-analytics.com", null);
        ContentSecurityPolicyScripts scripts = new ContentSecurityPolicyScripts(new HashSet<>(Arrays.asList("1", "2")), 
                Collections.<String>emptySet());
        String headerValue = filter.getHeaderValue(request, scripts);
        Assert.assertEquals(String.format("script-src 'nonce-1' 'nonce-2' https://www.google-analytics.com; report-uri %s;", contextPath + 
                ContentSecurityPolicyReportServlet.URL), headerValue);       
    }
    
    @Test
    public void testScriptHostWhitelist() {
        filter.configuration = new ContentSecurityPolicyConfiguration("true", "script-src", 
                "https://www.google-analytics.com", null); 
        String headerValue = filter.getHeaderValue(request, null);
        Assert.assertEquals(String.format("script-src https://www.google-analytics.com; report-uri %s;", contextPath + 
                ContentSecurityPolicyReportServlet.URL), headerValue);
    }
    
    @Test
    public void testReportUri() {
        filter.configuration = new ContentSecurityPolicyConfiguration("true", "script-src",
                "https:", "https://csp-violation");
        String headerValue = filter.getHeaderValue(request, null);
        Assert.assertEquals("script-src https:; report-uri https://csp-violation;", headerValue);
    }
    
}