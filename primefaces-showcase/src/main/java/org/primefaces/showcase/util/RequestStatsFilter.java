/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.stream.Collectors;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Counts the requests hitting the showcase and breaks them down by user agent, so that it becomes visible how much
 * of the traffic is LLM crawlers and other bots. {@link SessionStatsListener} only shows the session side of that.
 *
 * Nothing is blocked or slowed down here, the filter only counts - {@link BotFilter} does the limiting. This one is
 * mapped first in web.xml, so the numbers below still include everything the BotFilter then rejects.
 *
 * The counters are static as the container - not CDI - instantiates this filter.
 */
public class RequestStatsFilter implements Filter {

    /**
     * Caps how many distinct user agents / paths are tracked, so that randomized values cannot grow the maps forever.
     */
    private static final int MAX_TRACKED = 500;

    private static final int MAX_LENGTH = 200;

    /**
     * Amount of one second buckets used to calculate the current rate.
     */
    private static final int WINDOW_SECONDS = 60;

    private static final AtomicLong TOTAL = new AtomicLong();
    private static final AtomicLong BOTS = new AtomicLong();
    private static final AtomicLong COOKIELESS = new AtomicLong();
    private static final AtomicLong AJAX = new AtomicLong();
    private static final AtomicLong RESOURCES = new AtomicLong();
    private static final AtomicLong BOT_POSTBACKS = new AtomicLong();

    private static final Map<String, AtomicLong> BY_AGENT = new ConcurrentHashMap<>();
    private static final Map<String, AtomicLong> BY_BOT = new ConcurrentHashMap<>();
    private static final Map<String, AtomicLong> BY_PATH = new ConcurrentHashMap<>();

    private static final AtomicLongArray WINDOW_COUNTS = new AtomicLongArray(WINDOW_SECONDS);
    private static final AtomicLongArray WINDOW_SECOND = new AtomicLongArray(WINDOW_SECONDS);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            count((HttpServletRequest) request);
        }

        chain.doFilter(request, response);
    }

    private static void count(HttpServletRequest request) {
        TOTAL.incrementAndGet();
        countInWindow();

        // bots do not send the JSESSIONID cookie back, so these are the requests which allocate a new session
        if (request.getRequestedSessionId() == null) {
            COOKIELESS.incrementAndGet();
        }

        if (request.getHeader("Faces-Request") != null) {
            AJAX.incrementAndGet();
        }

        String path = request.getRequestURI();
        if (path != null && path.contains("jakarta.faces.resource")) {
            RESOURCES.incrementAndGet();
        }
        else {
            track(BY_PATH, path);
        }

        String userAgent = request.getHeader("User-Agent");
        track(BY_AGENT, userAgent);

        String bot = Bots.detect(userAgent);
        if (bot != null) {
            BOTS.incrementAndGet();
            track(BY_BOT, bot);

            if ("POST".equals(request.getMethod())) {
                BOT_POSTBACKS.incrementAndGet();
            }
        }
    }

    private static void countInWindow() {
        long second = System.currentTimeMillis() / 1000;
        int slot = (int) (second % WINDOW_SECONDS);

        // the slot still holds the count of the same second one minute ago, so reset it before reusing it
        if (WINDOW_SECOND.get(slot) != second) {
            WINDOW_SECOND.set(slot, second);
            WINDOW_COUNTS.set(slot, 0);
        }
        WINDOW_COUNTS.incrementAndGet(slot);
    }

    private static void track(Map<String, AtomicLong> counters, String value) {
        String key = value == null || value.isEmpty() ? Bots.NONE : value;
        if (key.length() > MAX_LENGTH) {
            key = key.substring(0, MAX_LENGTH);
        }

        AtomicLong counter = counters.get(key);
        if (counter == null) {
            if (counters.size() >= MAX_TRACKED) {
                return;
            }
            counter = counters.computeIfAbsent(key, k -> new AtomicLong());
        }
        counter.incrementAndGet();
    }

    public static long getTotal() {
        return TOTAL.get();
    }

    public static long getBots() {
        return BOTS.get();
    }

    public static long getCookieless() {
        return COOKIELESS.get();
    }

    public static long getAjax() {
        return AJAX.get();
    }

    public static long getResources() {
        return RESOURCES.get();
    }

    /**
     * @return POSTs of recognized crawlers, so the Faces postbacks which running the demos would cost. Counted
     *         before {@link BotFilter} rejects them, so this stays the measure of what the limiting saves.
     */
    public static long getBotPostbacks() {
        return BOT_POSTBACKS.get();
    }

    /**
     * @return the requests received in the last minute, which - unlike the average since startup - also shows a
     *         burst which just started.
     */
    public static long getLastMinute() {
        long second = System.currentTimeMillis() / 1000;
        long sum = 0;
        for (int i = 0; i < WINDOW_SECONDS; i++) {
            if (second - WINDOW_SECOND.get(i) < WINDOW_SECONDS) {
                sum += WINDOW_COUNTS.get(i);
            }
        }
        return sum;
    }

    /**
     * @param limit maximum amount of user agents to return.
     * @return the user agents with the most requests, highest first.
     */
    public static Map<String, Long> getTopAgents(int limit) {
        return top(BY_AGENT, limit);
    }

    /**
     * @param limit maximum amount of crawlers to return.
     * @return the recognized crawlers with the most requests, highest first.
     */
    public static Map<String, Long> getTopBots(int limit) {
        return top(BY_BOT, limit);
    }

    /**
     * @param limit maximum amount of paths to return.
     * @return the requested paths with the most requests, highest first.
     */
    public static Map<String, Long> getTopPaths(int limit) {
        return top(BY_PATH, limit);
    }

    private static Map<String, Long> top(Map<String, AtomicLong> counters, int limit) {
        return counters.entrySet().stream()
                .sorted(Comparator.comparingLong((Map.Entry<String, AtomicLong> e) -> e.getValue().get()).reversed())
                .limit(limit)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(), (a, b) -> a, LinkedHashMap::new));
    }
}
