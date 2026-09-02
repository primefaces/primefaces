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
package org.primefaces.showcase.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Keeps crawlers welcome to read every page, but from being expensive about it:
 *
 * <ul>
 *   <li>Their POSTs are answered with 403. A crawler needs the rendered HTML, not the demos, and running the demos
 *       is what costs a Faces postback with server side state saving. Several demos - deferred outputPanel, lazy
 *       timeline, schedule - even fire those postbacks on render without any interaction at all.</li>
 *   <li>Their GETs are capped at {@value #REQUESTS_PER_MINUTE} per minute and crawler, the excess is answered with
 *       429 plus Retry-After. The showcase is ~330 pages, so a full crawl still finishes in ~11 minutes.</li>
 * </ul>
 *
 * Mapped after {@link RequestStatsFilter} in web.xml, so that the statistics still see everything which is
 * rejected here.
 *
 * The counters are static as the container - not CDI - instantiates this filter.
 */
public class BotFilter implements Filter {

    /**
     * Requests per minute one recognized crawler may spend, 0 would disable the limit.
     */
    private static final int REQUESTS_PER_MINUTE = 30;

    /**
     * Crawlers which are recognized for the statistics, but never limited here: headless browsers are also how our
     * own Selenium tests and Lighthouse audits hit the showcase, and an agent without any User-Agent header is not
     * necessarily a crawler.
     */
    private static final Set<String> NEVER_LIMITED = new HashSet<>(Arrays.asList(
        "headlesschrome", "chrome-lighthouse", Bots.NONE));

    private static final AtomicLong DENIED = new AtomicLong();
    private static final AtomicLong THROTTLED = new AtomicLong();

    /**
     * One budget per recognized crawler. Bounded by construction, as {@link Bots#detect(String)} only ever returns
     * one of its known names or one of the two placeholders.
     */
    private static final Map<String, Budget> BUDGETS = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            String bot = Bots.detect(httpRequest.getHeader("User-Agent"));

            if (bot != null && !NEVER_LIMITED.contains(bot)) {
                // a crawler has no reason to submit a form, so this only cuts the demos, never the content
                if ("POST".equals(httpRequest.getMethod())) {
                    DENIED.incrementAndGet();
                    httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                // over budget: ask it to come back later instead of turning it away for good
                if (REQUESTS_PER_MINUTE > 0 && isOverBudget(bot)) {
                    THROTTLED.incrementAndGet();
                    httpResponse.setHeader("Retry-After", "60");
                    httpResponse.sendError(429);
                    return;
                }
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Fixed window per minute - a crawler which overruns its budget is paused until the next one.
     *
     * @param bot the recognized crawler.
     * @return whether this request is beyond the budget.
     */
    private static boolean isOverBudget(String bot) {
        long minute = System.currentTimeMillis() / 60000;
        Budget budget = BUDGETS.computeIfAbsent(bot, k -> new Budget());

        // only requests of the very same crawler ever contend here
        synchronized (budget) {
            if (budget.minute != minute) {
                budget.minute = minute;
                budget.count = 0;
            }
            return ++budget.count > REQUESTS_PER_MINUTE;
        }
    }

    /**
     * @return crawler postbacks answered with 403.
     */
    public static long getDenied() {
        return DENIED.get();
    }

    /**
     * @return crawler requests answered with 429, because the crawler was over its budget.
     */
    public static long getThrottled() {
        return THROTTLED.get();
    }

    private static final class Budget {

        private long minute;
        private int count;
    }
}
