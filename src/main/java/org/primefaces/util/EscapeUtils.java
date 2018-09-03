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

import org.owasp.encoder.Encode;

/**
 * <p>Utility methods contained herein must be used strictly for the appropriate context, e.g. HTML, HTML attribute, JS string.</p>
 * <p>Method calls are delegated to safe and well-tried whitelisting encoders from owasp-java-encoding.</p>
 */
public final class EscapeUtils {

    /** no instances */
    private EscapeUtils() {
    }

    /**
     * @see Encode#forHtml(String)
     */
    public static String forHtml(String input) {
        return Encode.forHtml(input);
    }

    /**
     * @see Encode#forHtmlContent(String)
     */
    public static String forHtmlContent(String input) {
        return Encode.forHtmlContent(input);
    }

    /**
     * @see Encode#forHtmlAttribute(String)
     */
    public static String forHtmlAttribute(String input) {
        return Encode.forHtmlAttribute(input);
    }

    /**
     * @see Encode#forHtmlUnquotedAttribute(String)
     */
    public static String forHtmlUnquotedAttribute(String input) {
        return Encode.forHtmlUnquotedAttribute(input);
    }

    /**
     * @see Encode#forCssString(String)
     */
    public static String forCssString(String input) {
        return Encode.forCssString(input);
    }

    /**
     * @see Encode#forCssUrl(String)
     */
    public static String forCssUrl(String input) {
        return Encode.forCssUrl(input);
    }

    /**
     * @see Encode#forUriComponent(String)
     */
    public static String forUriComponent(String input) {
        return Encode.forUriComponent(input);
    }

    /**
     * @see Encode#forXml(String)
     */
    public static String forXml(String input) {
        return Encode.forXml(input);
    }

    /**
     * @see Encode#forXmlContent(String)
     */
    public static String forXmlContent(String input) {
        return Encode.forXmlContent(input);
    }

    /**
     * @see Encode#forXmlAttribute(String)
     */
    public static String forXmlAttribute(String input) {
        return Encode.forXmlAttribute(input);
    }

    /**
     * @see Encode#forXmlComment(String)
     */
    public static String forXmlComment(String input) {
        return Encode.forXmlComment(input);
    }

    /**
     * @see Encode#forCDATA(String)
     */
    public static String forCDATA(String input) {
        return Encode.forCDATA(input);
    }

    /**
     * @see Encode#forJava(String)
     */
    public static String forJava(String input) {
        return Encode.forJava(input);
    }

    /**
     * @see Encode#forJavaScript(String)
     */
    public static String forJavaScript(String input) {
        return Encode.forJavaScript(input);
    }

    /**
     * @see Encode#forJavaScriptAttribute(String)
     */
    public static String forJavaScriptAttribute(String input) {
        return Encode.forJavaScriptAttribute(input);
    }

    /**
     * @see Encode#forJavaScriptBlock(String)
     */
    public static String forJavaScriptBlock(String input) {
        return Encode.forJavaScriptBlock(input);
    }

    /**
     * @see Encode#forJavaScriptSource(String)
     */
    public static String forJavaScriptSource(String input) {
        return Encode.forJavaScriptSource(input);
    }

}
