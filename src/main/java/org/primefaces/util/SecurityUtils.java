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
package org.primefaces.util;

import javax.faces.context.FacesContext;
import java.security.Principal;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public final class SecurityUtils {

    private SecurityUtils() {
        // NOOP
    }

    public static boolean ifGranted(String role) {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role);
    }

    public static boolean ifAllGranted(Object value) {
        return convertRoles(value)
                .allMatch(SecurityUtils::ifGranted);
    }

    public static boolean ifAnyGranted(Object value) {
        return convertRoles(value)
                .anyMatch(SecurityUtils::ifGranted);
    }

    public static boolean ifNoneGranted(Object value) {
        return convertRoles(value)
                .noneMatch(SecurityUtils::ifGranted);
    }

    public static String remoteUser() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public static Principal userPrincipal() {
        return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
    }

    static Stream<String> convertRoles(Object value) {
        Objects.requireNonNull(value, "Roles value can't be null");

        Stream<?> results = null;
        if (value instanceof String) {
            results = Stream.of(((String) value).split(",")).map(String::trim);
        }
        else if (value instanceof Object[]) {
            results = Stream.of((Object[]) value);
        }
        else if (value instanceof Collection) {
            results = ((Collection<?>) value).stream();
        }
        else {
            results = Stream.of(value);
        }
        return results.map(Object::toString);
    }
}
