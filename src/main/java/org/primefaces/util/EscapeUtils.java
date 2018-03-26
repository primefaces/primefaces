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
    
    public static String forHtml(String input) {
        return Encode.forHtml(input);
    }

    public static String forHtmlContent(String input) {
        return Encode.forHtmlContent(input);
    }

    public static String forHtmlAttribute(String input) {
        return Encode.forHtmlAttribute(input);
    }

    public static String forHtmlUnquotedAttribute(String input) {
        return Encode.forHtmlUnquotedAttribute(input);
    }

    public static String forCssString(String input) {
        return Encode.forCssString(input);
    }

    public static String forCssUrl(String input) {
        return Encode.forCssUrl(input);
    }

    public static String forUriComponent(String input) {
        return Encode.forUriComponent(input);
    }

    public static String forXml(String input) {
        return Encode.forXml(input);
    }

    public static String forXmlContent(String input) {
        return Encode.forXmlContent(input);
    }

    public static String forXmlAttribute(String input) {
        return Encode.forXmlAttribute(input);
    }

    public static String forXmlComment(String input) {
        return Encode.forXmlComment(input);
    }

    public static String forCDATA(String input) {
        return Encode.forCDATA(input);
    }

    public static String forJava(String input) {
        return Encode.forJava(input);
    }

    public static String forJavaScript(String input) {
        return Encode.forJavaScript(input);
    }

    public static String forJavaScriptAttribute(String input) {
        return Encode.forJavaScriptAttribute(input);
    }

    public static String forJavaScriptBlock(String input) {
        return Encode.forJavaScriptBlock(input);
    }

    public static String forJavaScriptSource(String input) {
        return Encode.forJavaScriptSource(input);
    }
    
}
