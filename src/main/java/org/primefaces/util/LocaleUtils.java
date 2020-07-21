/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class LocaleUtils {

    private LocaleUtils() {
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
     * @param context the {@link FacesContext}
     * @param locale given locale
     * @param clientId the component clientId
     * @return resolved Locale
     */
    public static Locale resolveLocale(FacesContext context, Object locale, String clientId) {
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
            result = context.getViewRoot().getLocale();
        }

        return result;
    }

    /**
     * Some JS libraries like FullCalendar used by Schedule require the locale for "pt_BR" to be "pt-br".
     *
     * @param locale the Locale to convert
     * @return the Javascript string locale
     */
    public static String toJavascriptLocale(Locale locale) {
        return locale.toString().toLowerCase().replace('_', '-');
    }

    public static Locale getCurrentLocale(FacesContext context) {
        Locale locale = null;

        if (context != null) {
            UIViewRoot viewRoot = context.getViewRoot();

            // Prefer the locale set in the view.
            if (viewRoot != null) {
                locale = viewRoot.getLocale();
            }

            // Then the client preferred locale.
            if (locale == null) {
                locale = context.getExternalContext().getRequestLocale();
            }

            // Then the JSF default locale.
            if (locale == null) {
                locale = context.getApplication().getDefaultLocale();
            }
        }

        // Finally the system default locale.
        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }

    public static Locale getCurrentLocale() {
        return getCurrentLocale(FacesContext.getCurrentInstance());
    }

    public static String getDecimalSeparator(FacesContext context) {
        Locale locale = getCurrentLocale(context);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
        return Character.toString(decimalFormatSymbols.getDecimalSeparator());
    }

    public static String getThousandSeparator(FacesContext context) {
        Locale locale = getCurrentLocale(context);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
        return Character.toString(decimalFormatSymbols.getGroupingSeparator());
    }

    /**
     * Gets ISO 639-1 Language Code from current Locale so 'pt_BR' becomes 'pt'.
     *
     * @return the ISO 639-1 Language Code
     * @see https://www.w3schools.com/tags/ref_language_codes.asp
     */
    public static String getCurrentLanguage() {
        return calculateLanguage(getCurrentLocale());
    }

    /**
     * Gets ISO 639-1 Language Code from Locale so 'pt_BR' becomes 'pt'.
     *
     * @param locale the Locale to calculate the language for
     * @return the ISO 639-1 Language Code
     */
    public static String calculateLanguage(Locale locale) {
        return locale.getLanguage();
    }
}
