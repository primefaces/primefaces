/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.showcase.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter(filterName = "BotBlockingFilter", urlPatterns = "/*")
public class BotBlockingFilter implements Filter {

    private static final Pattern BOT_PATTERN = Pattern.compile(
            ".*(bot|crawl|spider|scrapy|curl|wget|httpclient|python|java|ruby|go-http|okhttp|" +
                    "node|axios|postman|Apache-HttpClient|libwww|pingdom|uptime|monitor|slurp|" +
                    "AhrefsBot|SemrushBot|Baiduspider|MJ12bot|DotBot|BLEXBot|YandexBot|" +
                    "PetalBot|archive\\.org_bot|facebookexternalhit|twitterbot|linkedinbot).*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String userAgent = httpRequest.getHeader("User-Agent");
        String requestUri = httpRequest.getRequestURI();

        // Always allow robots.txt
        if (requestUri != null && requestUri.endsWith("/robots.txt")) {
            chain.doFilter(request, response);
            return;
        }

        // Missing User-Agent (very common in malicious bots)
        if (userAgent == null || userAgent.isBlank()) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setHeader("X-Bot-Blocked", "true");
            httpResponse.getWriter().write("Access denied: missing User-Agent");
            return;
        }

        // Known bot signatures
        if (BOT_PATTERN.matcher(userAgent).matches()) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.setHeader("X-Bot-Blocked", "true");
            httpResponse.getWriter().write("Bot access denied");
            return;
        }

        // Continue for normal traffic
        chain.doFilter(request, response);
    }
}
