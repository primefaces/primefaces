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

import java.security.Principal;

import javax.faces.context.FacesContext;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static boolean ifGranted(String role) {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role);
    }

    public static boolean ifAllGranted(String value) {
        String[] roles = value.split(",");
        boolean isAuthorized = false;

        for (String role : roles) {
            if (ifGranted(role.trim())) {
                isAuthorized = true;
            }
            else {
                isAuthorized = false;
                break;
            }
        }

        return isAuthorized;
    }

    public static boolean ifAnyGranted(String value) {
        String[] roles = value.split(",");
        boolean isAuthorized = false;

        for (String role : roles) {
            if (ifGranted(role.trim())) {
                isAuthorized = true;
                break;
            }
        }

        return isAuthorized;
    }

    public static boolean ifNoneGranted(String value) {
        String[] roles = value.split(",");
        boolean isAuthorized = false;

        for (String role : roles) {
            if (ifGranted(role.trim())) {
                isAuthorized = false;
                break;
            }
            else {
                isAuthorized = true;
            }
        }

        return isAuthorized;
    }

    public static String remoteUser() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public static Principal userPrincipal() {
        return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
    }
}
