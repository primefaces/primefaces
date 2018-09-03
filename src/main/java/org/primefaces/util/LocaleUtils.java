/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.util;

import java.util.Locale;

import javax.faces.context.FacesContext;

public class LocaleUtils {

    /**
     * Prevent instantiation.
     */
    private LocaleUtils() {
        // prevent instantiation
    }

    /**
     * Implementation from Apache Commons Lang
     */
    public static Locale toLocale(String str) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len != 2 && len != 5 && len < 7) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        char ch0 = str.charAt(0);
        char ch1 = str.charAt(1);
        if (ch0 < 'a' || ch0 > 'z' || ch1 < 'a' || ch1 > 'z') {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        if (len == 2) {
            return new Locale(str, "");
        }
        else {
            if (str.charAt(2) != '_') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            char ch3 = str.charAt(3);
            if (ch3 == '_') {
                return new Locale(str.substring(0, 2), "", str.substring(4));
            }
            char ch4 = str.charAt(4);
            if (ch3 < 'A' || ch3 > 'Z' || ch4 < 'A' || ch4 > 'Z') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (len == 5) {
                return new Locale(str.substring(0, 2), str.substring(3, 5));
            }
            else {
                if (str.charAt(5) != '_') {
                    throw new IllegalArgumentException("Invalid locale format: " + str);
                }
                return new Locale(str.substring(0, 2), str.substring(3, 5), str.substring(6));
            }
        }
    }

    /**
     * Gets a {@link Locale} instance by the value of the component attribute "locale" which can be String or {@link Locale} or null.
     * <p>
     * If NULL is passed the view root default locale is used.
     *
     * @param locale given locale
     * @return resolved Locale
     */
    public static Locale resolveLocale(Object locale, String clientId) {
        Locale result = null;

        if (locale != null) {
            if (locale instanceof String) {
                result = toLocale((String) locale);
            }
            else if (locale instanceof java.util.Locale) {
                result = (java.util.Locale) locale;
            }
            else {
                throw new IllegalArgumentException("Type:" + locale.getClass() + " is not a valid locale type for: " + clientId);
            }
        }
        else {
            // default to the view local
            result = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        }

        return result;
    }

}
