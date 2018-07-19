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

import org.primefaces.application.resource.csp.scripts.ContentSecurityPolicyScripts;
import org.primefaces.application.resource.csp.scripts.ContentSecurityPolicyScriptsResponseWriter;
import org.primefaces.util.OnCommittedResponseWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This servlet filter is responsible for setting appropriate Content-Security-Policy (CSP) response headers that are either whitelisted
 * via {@link Constants.ContextParams#CONTENT_SECURITY_POLICY_HOST_WHITELIST} or determined automatically.
 * Currently the <code>script-src</code> directive is supported only. It is planned to support other directives in the future as well
 * that can then be specified via {@link Constants.ContextParams#CONTENT_SECURITY_POLICY_SUPPORTED_DIRECTIVES}.
 * Per default browsers are instructed to report any CSP violations to the {@link ContentSecurityPolicyReportServlet} endpoint if not
 * provided via {@link Constants.ContextParams#CONTENT_SECURITY_POLICY_REPORT_URI}.
 */
@WebFilter("/*")
public class ContentSecurityPolicyFilter implements Filter {

    ContentSecurityPolicyConfiguration configuration;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        configuration = new ContentSecurityPolicyConfiguration(filterConfig.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (configuration.isEnabled()) {
            HeaderWriterResponse headerWriterResponse = new HeaderWriterResponse((HttpServletRequest) request, (HttpServletResponse) response);
            try {
                chain.doFilter(request, headerWriterResponse);
            }
            finally {
                headerWriterResponse.writeHeader();
            }
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // intentionally kept empty
    }

    /**
     * Response wrapper for writing the Content-Security-Policy header before response is committed
     */
    class HeaderWriterResponse extends OnCommittedResponseWrapper {

        private final HttpServletRequest request;

        HeaderWriterResponse(HttpServletRequest request, HttpServletResponse response) {
            super(response);
            this.request = request;
            configureResponse(10485760); // 10 MiB
        }

        /**
         * Content-Security-Policy header cannot be set before collection of nonce/hash information in {@link ContentSecurityPolicyScriptsResponseWriter}. 
         * Therefore as a workaround we have to increase the response buffer size. 
         * Otherwise, sending headers after the first flush occurred will not have any effects.
         * See {@link javax.servlet.ServletResponse#setBufferSize(int)}. 
         * @param bufferSize the buffer size to use
         */
        private void configureResponse(int bufferSize) {
            setBufferSize(bufferSize);
        }
        
        @Override
        protected void onResponseCommitted() {
            writeHeader();
            this.disableOnResponseCommitted();
        }
        
        protected void writeHeader() {
            if (isDisableOnResponseCommitted()) {
                return;
            }
            ContentSecurityPolicyScripts scripts = (ContentSecurityPolicyScripts) request.getAttribute(ContentSecurityPolicyScripts.class.getName());
            if (scripts != null && (!scripts.getNonces().isEmpty() || !scripts.getSha256Hashes().isEmpty()) || 
                    configuration.getHostWhitelist() != null && !configuration.getHostWhitelist().isEmpty()) {
                ((HttpServletResponse) getResponse()).addHeader(Constants.CSP_HEADER.name, getHeaderValue(request, scripts));
            }
        }
        
    }

    /**
     * Build Content-Security-Policy header based on collected script nonces/hashes and configuration.
     */
    String getHeaderValue(HttpServletRequest request, ContentSecurityPolicyScripts scripts) {
        // TODO use well-tested library for setting CSP headers, e.g. salvation
        StringBuilder headerBuilder = new StringBuilder();
        if (scripts != null) {
            for (String nonce : scripts.getNonces()) {
                headerBuilder.append(" 'nonce-").append(nonce).append('\'');
            }
            for (String hashes : scripts.getSha256Hashes()) {
                // TODO support hashes
            }
        }
        for (String host : configuration.getHostWhitelist()) {
            headerBuilder.append(' ').append(host);
        }
        if (headerBuilder.length() > 0) {
            headerBuilder.insert(0, Constants.SCRIPT_SRC_DIRECTIVE.name);
            headerBuilder.append(';');
        }
        //TODO use report-to since report-uri is deprecated
        String reportUri = configuration.getReportUri();
        if (reportUri == null) {
            reportUri = request.getContextPath() + ContentSecurityPolicyReportServlet.URL;
        }
        headerBuilder.append(' ').append(Constants.REPORT_URI_DIRECTIVE.name).append(' ').append(reportUri).append(';');
        return headerBuilder.toString().trim();
    }
    
}
