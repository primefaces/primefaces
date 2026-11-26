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
package org.primefaces.util;

import org.primefaces.cdk.api.Function;

import java.security.Principal;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import jakarta.faces.context.FacesContext;

public final class SecurityUtils {

    private SecurityUtils() {
        // NOOP
    }

    @Function(description = "Returns true if user has the given role, else false.")
    public static boolean ifGranted(String role) {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role);
    }

    @Function(description = "Returns true if user has all of the given roles, else false.")
    public static boolean ifAllGranted(Object value) {
        return convertRoles(value)
                .allMatch(SecurityUtils::ifGranted);
    }

    @Function(description = "Returns true if user has any of the given roles, else false.")
    public static boolean ifAnyGranted(Object value) {
        return convertRoles(value)
                .anyMatch(SecurityUtils::ifGranted);
    }

    @Function(description = "Returns true if user has none of the given roles, else false.")
    public static boolean ifNoneGranted(Object value) {
        return convertRoles(value)
                .noneMatch(SecurityUtils::ifGranted);
    }

    @Function(description = "Returns the name of the logged in user.")
    public static String remoteUser() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    @Function(description = "Returns the principal instance of the logged in user.")
    public static Principal userPrincipal() {
        return FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
    }

    static Stream<String> convertRoles(Object value) {
        Objects.requireNonNull(value, "Roles value can't be null");

        Stream<?> results = null;
        if (value instanceof String string) {
            results = Stream.of(string.split(",")).map(String::trim);
        }
        else if (value instanceof Object[] objects) {
            results = Stream.of(objects);
        }
        else if (value instanceof Collection<?> collection) {
            results = collection.stream();
        }
        else {
            results = Stream.of(value);
        }
        return results.map(Object::toString);
    }
}
