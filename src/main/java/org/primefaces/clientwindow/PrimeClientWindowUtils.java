/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.clientwindow;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.util.ResourceUtils;

public class PrimeClientWindowUtils {

    public static final String INITIAL_REDIRECT_COOKIE_PREFIX = "pf.initialredirect-";
    public static final int WINDOW_ID_LENGTH = 5;

    private PrimeClientWindowUtils() {
    }

    public static void addInitialRedirectCookie(FacesContext context, String windowId) {
        Map<String, Object> properties = new HashMap<>(4);
        properties.put("path", "/");
        properties.put("maxAge", 30);
        ResourceUtils.addResponseCookie(context, getCookieName(windowId), "true", properties);
    }

    public static Object getInitialRedirectCookie(FacesContext context, String windowId) {
        Map<String, Object> cookieMap = context.getExternalContext().getRequestCookieMap();
        String cookie = getCookieName(windowId);
        if (cookieMap.containsKey(cookie)) {
            return cookieMap.get(cookie);
        }

        return null;
    }

    public static String getCookieName(String windowId) {
        return INITIAL_REDIRECT_COOKIE_PREFIX + windowId;
    }

    public static String secureWindowId(String windowId) {
        //restrict the length to prevent script-injection
        if (windowId != null && windowId.length() > WINDOW_ID_LENGTH) {
            windowId = windowId.substring(0, WINDOW_ID_LENGTH);
        }
        return windowId;
    }

    public static String generateNewWindowId() {
        return UUID.randomUUID().toString().substring(0, WINDOW_ID_LENGTH);
    }

    public static boolean isPost(FacesContext facesContext) {
        if (facesContext.isPostback()) {
            return true;
        }

        Object request = facesContext.getExternalContext().getRequest();
        if (request instanceof HttpServletRequest) {
            if ("POST".equals(((HttpServletRequest) request).getMethod())) {
                return true;
            }
        }

        return false;
    }
}
