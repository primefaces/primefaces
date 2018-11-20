package org.primefaces.application.resource.csp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.application.resource.csp.scripts.CspScripts;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CspFilterTest {

    private CspFilter filter;
    
    private HttpServletRequest request;
    private String contextPath;
    
    @Before
    public void setup() {
        filter = new CspFilter();
        request = mock(HttpServletRequest.class);
        contextPath = "/pf";
        when(request.getContextPath()).thenReturn(contextPath);
    }

    @Test
    public void testScriptNonces() {
        filter.configuration = new CspConfiguration("true", "script-src", null, null, "false", 
                "false");
        CspScripts scripts = new CspScripts(new HashSet<>(Arrays.asList("1", "2")), Collections.<String>emptySet());
        String headerValue = filter.getHeaderValue(request, scripts);
        Assert.assertEquals(String.format("script-src 'nonce-1' 'nonce-2'; report-uri %s;", contextPath + CspReportServlet.URL), headerValue);
    }
    
    @Test
    public void testScriptSha256Hashes() {
        filter.configuration = new CspConfiguration("true", "script-src", null, null, "false", 
                "false");
        CspScripts scripts = new CspScripts(Collections.<String>emptySet(), new HashSet<>(Arrays.asList("1", "2")));
        String headerValue = filter.getHeaderValue(request, scripts);
        Assert.assertEquals(String.format("script-src 'sha256-1' 'sha256-2'; report-uri %s;", contextPath + CspReportServlet.URL), headerValue);
    }
    
    @Test
    public void testScriptNoncesAndHostWhitelist() {
        filter.configuration = new CspConfiguration("true", "script-src", "https://www.google-analytics.com", 
                null, "false", "false");
        CspScripts scripts = new CspScripts(new HashSet<>(Arrays.asList("1", "2")), 
                Collections.<String>emptySet());
        String headerValue = filter.getHeaderValue(request, scripts);
        Assert.assertEquals(String.format("script-src 'nonce-1' 'nonce-2' https://www.google-analytics.com; report-uri %s;", contextPath + 
                CspReportServlet.URL), headerValue);       
    }
    
    @Test
    public void testScriptHostWhitelist() {
        filter.configuration = new CspConfiguration("true", "script-src", "https://www.google-analytics.com", 
                null, "false", "false"); 
        String headerValue = filter.getHeaderValue(request, null);
        Assert.assertEquals(String.format("script-src https://www.google-analytics.com; report-uri %s;", contextPath + 
                CspReportServlet.URL), headerValue);
    }
    
    @Test
    public void testReportUri() {
        filter.configuration = new CspConfiguration("true", "script-src", "https:", "https://csp-violation", 
                "false", "false");
        String headerValue = filter.getHeaderValue(request, null);
        Assert.assertEquals("script-src https:; report-uri https://csp-violation;", headerValue);
    }
    
}