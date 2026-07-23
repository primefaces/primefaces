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
package org.primefaces.webapp;

import org.primefaces.util.Constants;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Reads ServletContext-level configuration during context initialization and stores it as
 * ServletContext attributes so that other components (e.g. PrimeConfiguration) can safely
 * consume those values later in the lifecycle without triggering the programmatic-listener
 * restriction of the Servlet spec (getSessionCookieConfig() et al. are forbidden when called
 * from a programmatically-added listener).
 */
public class PrimeServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        boolean cookiesSecure = true;
        if (sce.getServletContext().getSessionCookieConfig() != null) {
            cookiesSecure = sce.getServletContext().getSessionCookieConfig().isSecure();
        }
        sce.getServletContext().setAttribute(Constants.ServletContextAttributes.COOKIES_SECURE, cookiesSecure);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // NOOP
    }
}
