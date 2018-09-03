/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.application.resource.csp;

import org.primefaces.util.Constants;

import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Holder for the Content-Security-Policy (CSP) configuration configured via <code>context-param</code>s in the web application's web.xml.
 */
public class CspConfiguration {

    private final boolean enabled;
    private final boolean scripts;
    private final Set<String> hostWhitelist;
    private final String reportUri;
    private final boolean reportOnly;
    private final boolean javascriptDebuggingCookie;

    public CspConfiguration(ExternalContext context) {
        this(context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_ENABLED),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_SUPPORTED_DIRECTIVES),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_HOST_WHITELIST),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_REPORT_URI),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_REPORT_ONLY),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_JAVASCRIPT_DEBUGGING_COOKIE));
    }

    public CspConfiguration(ServletContext context) {
        this(context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_ENABLED),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_SUPPORTED_DIRECTIVES),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_HOST_WHITELIST),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_REPORT_URI),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_REPORT_ONLY),
                context.getInitParameter(Constants.ContextParams.CONTENT_SECURITY_POLICY_JAVASCRIPT_DEBUGGING_COOKIE));
    }

    CspConfiguration(String enabledStr, String supportedDirectives, String hostWhitelistStr, String reportUri, String reportOnlyStr,
            String javascriptDebuggingCookieStr) {
        enabled = Boolean.parseBoolean(enabledStr);

        if (supportedDirectives != null) {
            Set<String> cspSupportedDirectives = new HashSet<>();
            StringTokenizer tok = new StringTokenizer(supportedDirectives, ",");
            while (tok.hasMoreTokens()) {
                cspSupportedDirectives.add(tok.nextToken().trim().toLowerCase());
            }
            scripts = cspSupportedDirectives.contains(CspHeader.SCRIPT_SRC_DIRECTIVE.name);
        }
        else {
            scripts = false;
        }

        Set<String> cspHostWhitelist = new HashSet<>();
        if (hostWhitelistStr != null) {
            StringTokenizer tok = new StringTokenizer(hostWhitelistStr, ",");
            while (tok.hasMoreTokens()) {
                cspHostWhitelist.add(tok.nextToken().trim());
            }
        }
        hostWhitelist = Collections.unmodifiableSet(cspHostWhitelist);

        this.reportUri = reportUri;

        reportOnly = Boolean.parseBoolean(reportOnlyStr);

        javascriptDebuggingCookie = Boolean.parseBoolean(javascriptDebuggingCookieStr);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isScripts() {
        return scripts;
    }

    public Set<String> getHostWhitelist() {
        return hostWhitelist;
    }

    public String getReportUri() {
        return reportUri;
    }

    public boolean isReportOnly() {
        return reportOnly;
    }

    public boolean isJavascriptDebuggingCookie() {
        return javascriptDebuggingCookie;
    }

}
