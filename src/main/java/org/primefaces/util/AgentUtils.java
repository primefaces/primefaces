/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import javax.faces.context.FacesContext;

public class AgentUtils {

    private AgentUtils() {
    }

    public static boolean isEdge(FacesContext context) {
        String userAgent = context.getExternalContext().getRequestHeaderMap().get("User-Agent");

        if (userAgent == null) {
            return false;
        }
        else {
            return userAgent.contains("Edge");
        }
    }

    public static boolean isIE(FacesContext context) {
        String userAgent = context.getExternalContext().getRequestHeaderMap().get("User-Agent");

        if (userAgent == null) {
            return false;
        }
        else {
            return userAgent.contains("MSIE");
        }
    }

    public static boolean isIE(FacesContext context, int value) {
        String userAgent = context.getExternalContext().getRequestHeaderMap().get("User-Agent");

        if (userAgent == null) {
            return false;
        }
        else {
            int index = userAgent.indexOf("MSIE");

            if (index == -1) {
                return false;
            }
            else {
                int version = Double.valueOf(userAgent.substring((index + 5), userAgent.indexOf(';', index))).intValue();

                return version == value;
            }
        }
    }

    public static boolean isLessThanIE(FacesContext context, int value) {
        String userAgent = context.getExternalContext().getRequestHeaderMap().get("User-Agent");

        if (userAgent == null) {
            return false;
        }
        else {
            int index = userAgent.indexOf("MSIE");

            if (index == -1) {
                return false;
            }
            else {
                int version = Double.valueOf(userAgent.substring((index + 5), userAgent.indexOf(';', index))).intValue();

                return version > value;
            }
        }
    }
}
