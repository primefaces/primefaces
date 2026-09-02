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

import java.util.concurrent.atomic.AtomicLong;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Tracks how many HTTP sessions the showcase currently holds and how many were created since startup.
 * Bots do not send cookies back, so every request of theirs creates a new session - which makes the created
 * count and the resulting rate a good indicator for bot traffic.
 *
 * The counters are static as the container - not CDI - instantiates this listener.
 */
@WebListener
public class SessionStatsListener implements HttpSessionListener {

    private static final AtomicLong ACTIVE = new AtomicLong();
    private static final AtomicLong CREATED = new AtomicLong();
    private static final AtomicLong DESTROYED = new AtomicLong();
    private static final AtomicLong PEAK_ACTIVE = new AtomicLong();
    private static final long STARTUP = System.currentTimeMillis();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        CREATED.incrementAndGet();

        long active = ACTIVE.incrementAndGet();
        PEAK_ACTIVE.accumulateAndGet(active, Math::max);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        DESTROYED.incrementAndGet();
        ACTIVE.decrementAndGet();
    }

    public static long getActive() {
        return ACTIVE.get();
    }

    public static long getCreated() {
        return CREATED.get();
    }

    public static long getDestroyed() {
        return DESTROYED.get();
    }

    public static long getPeakActive() {
        return PEAK_ACTIVE.get();
    }

    public static long getUptimeSeconds() {
        return (System.currentTimeMillis() - STARTUP) / 1000;
    }
}
