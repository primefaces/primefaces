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

import org.primefaces.application.resource.csp.scripts.CspScripts;
import org.primefaces.application.resource.csp.scripts.CspScriptsResponseWriter;
import org.primefaces.util.OnCommittedResponseWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * <p>This servlet filter is responsible for setting appropriate Content-Security-Policy (CSP) response headers that are either whitelisted
 * via {@link Constants.ContextParams#CONTENT_SECURITY_POLICY_HOST_WHITELIST} or determined automatically.</p>
 * <p>Currently the <code>script-src</code> directive is supported only. It is planned to support other directives in the future as well
 * that can then be specified via {@link Constants.ContextParams#CONTENT_SECURITY_POLICY_SUPPORTED_DIRECTIVES}.</p>
 * <p>Per default browsers are instructed to report any CSP violations to the {@link CspReportServlet} endpoint if not
 * provided via {@link Constants.ContextParams#CONTENT_SECURITY_POLICY_REPORT_URI}.</p>
 * <p>To not break functionality in production you may also want to set {@link Constants.ContextParams#CONTENT_SECURITY_POLICY_REPORT_ONLY}
 * to just report CSP violations instead of refusing them.</p>
 * <p>If {@link Constants.ContextParams#CONTENT_SECURITY_POLICY_JAVASCRIPT_COOKIE_DEBUGGING} is enabled, it will be possible to read the CSP header
 * in javascript with <code>document.cookie</code>. This is because the JS API unfortunately does not expose response headers. This is not recommended
 * in production systems.</p>
 */
@WebFilter("/*")
public class CspFilter implements Filter {

    CspConfiguration configuration;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        configuration = new CspConfiguration(filterConfig.getServletContext());
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
         * Content-Security-Policy header cannot be set before collection of nonce/hash information in {@link CspScriptsResponseWriter}.
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

        /**
         * Actually write the headers depending on configuration (Content-Security-Policy, Content-Security-Policy-Report-Only, Set-Cookie).
         */
        protected void writeHeader() {
            if (isDisableOnResponseCommitted()) {
                return;
            }
            CspScripts scripts = (CspScripts) request.getSession().getAttribute(CspScripts.class.getName());
            if (scripts != null && (!scripts.getNonces().isEmpty() || !scripts.getSha256Hashes().isEmpty() ||
                    configuration.getHostWhitelist() != null && !configuration.getHostWhitelist().isEmpty())) {
                CspHeader header = configuration.isReportOnly() ? CspHeader.CSP_REPORT_ONLY_HEADER : CspHeader.CSP_HEADER;
                String headerValue = getHeaderValue(request, scripts);
                HttpServletResponse response = (HttpServletResponse) getResponse();
                response.addHeader(header.name, headerValue);
                writeCookie(headerValue);
            }
        }

        private void writeCookie(String headerValue) {
            if (configuration.isJavascriptDebuggingCookie()) {
                try {
                    Cookie cookie = null;
                    String cookieValue = URLEncoder.encode(headerValue, "UTF-8");
                    if (request.getCookies() != null) {
                        for (Cookie c : request.getCookies()) {
                            if (CspHeader.COOKIE_NAME.name.equalsIgnoreCase(c.getName())) {
                                cookie = c;
                                cookie.setValue(cookieValue);
                                break;
                            }
                        }
                    }
                    if (cookie == null) {
                        cookie = new Cookie(CspHeader.COOKIE_NAME.name, cookieValue);
                    }
                    // Explicitly declare cookie as non-HttpOnly since we would like it to be accessible via javascript
                    cookie.setHttpOnly(false);
                    cookie.setPath(request.getContextPath());
                    ((HttpServletResponse) getResponse()).addCookie(cookie);
                }
                catch (UnsupportedEncodingException ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }

    }

    /**
     * Build Content-Security-Policy header based on collected script nonces/hashes and configuration.
     */
    String getHeaderValue(HttpServletRequest request, CspScripts scripts) {
        // TODO use well-tested library for setting CSP headers, e.g. salvation
        StringBuilder headerBuilder = new StringBuilder();
        if (scripts != null) {
            for (String nonce : scripts.getNonces()) {
                headerBuilder.append(" 'nonce-").append(nonce).append('\'');
            }
            for (String hash : scripts.getSha256Hashes()) {
                headerBuilder.append(" 'sha256-").append(hash).append('\'');
            }
        }
        for (String host : configuration.getHostWhitelist()) {
            headerBuilder.append(' ').append(host);
        }
        if (headerBuilder.length() > 0) {
            headerBuilder.insert(0, CspHeader.SCRIPT_SRC_DIRECTIVE.name);
            headerBuilder.append(';');
        }
        //TODO use report-to since report-uri is deprecated
        String reportUri = configuration.getReportUri();
        if (reportUri == null) {
            reportUri = request.getContextPath() + CspReportServlet.URL;
        }
        headerBuilder.append(' ').append(CspHeader.REPORT_URI_DIRECTIVE.name).append(' ').append(reportUri).append(';');
        return headerBuilder.toString().trim();
    }
}
