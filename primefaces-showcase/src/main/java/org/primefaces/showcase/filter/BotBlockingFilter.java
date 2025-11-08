package org.primefaces.showcase.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@WebFilter(filterName = "BotBlockingFilter", urlPatterns = "/*")
public class BotBlockingFilter implements Filter {

    private static final Pattern BOT_PATTERN = Pattern.compile(
            ".*(bot|crawler|spider|scrape|curl|wget|python|java\\/|apache|http|scan|" +
                    "AhrefsBot|SemrushBot|DotBot|MJ12bot|BLEXBot|archive\\.org_bot).*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String userAgent = httpRequest.getHeader("User-Agent");
        String requestUri = httpRequest.getRequestURI();

        // Allow robots.txt
        if (requestUri.endsWith("/robots.txt")) {
            chain.doFilter(request, response);
            return;
        }

        // Block known bots
        if (userAgent != null && BOT_PATTERN.matcher(userAgent).matches()) {
            httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpResponse.getWriter().write("Bot access denied");
            return;
        }

        chain.doFilter(request, response);
    }
}