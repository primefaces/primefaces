/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.util;

import jakarta.faces.context.FacesContext;

public class AgentUtils {

    private AgentUtils() {
    }

    private static String getUserAgent(FacesContext context) {
        return context.getExternalContext().getRequestHeaderMap().get("User-Agent");
    }

    private static boolean userAgentContains(FacesContext context, String fragment) {
        String userAgent = getUserAgent(context);

        if (userAgent == null) {
            return false;
        }
        else {
            return userAgent.contains(fragment);
        }
    }

    public static boolean isFirefox(FacesContext context) {
        return userAgentContains(context, "Firefox");
    }

    public static boolean isMac(FacesContext context) {
        return userAgentContains(context, "Mac");
    }
}
