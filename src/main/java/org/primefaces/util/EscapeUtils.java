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

import org.apache.xmlbeans.impl.common.XMLChar;
import org.owasp.encoder.Encode;

/**
 * <p>Utility methods contained herein must be used strictly for the appropriate context, e.g. HTML, HTML attribute, JS string.</p>
 * <p>Method calls are delegated to safe and well-tried whitelisting encoders from owasp-java-encoding.</p>
 */
public class EscapeUtils {

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

    /**
     * Ensure a valid XMLElement name is returned.<br>
     * Uses the {@link org.apache.xmlbeans.impl.common.XMLChar}<br>
     * Replaces spaces by underscores, &lt; by .lt, &gt; by .gt. and all other characters by '.X.', where is the output of
     * {@link java.lang.Integer}.toHexString()
     *
     * @param intag the source for the element name
     * @return valid XML element name
     */
    public static String forXmlTag(String intag) {
        if (XMLChar.isValidName(intag) || intag == null || intag.length() == 0) {
            return intag;
        }

        StringBuilder sb = new StringBuilder(intag.length());
        sb.append(intag);

        char c;
        for (int i = sb.length() - 1; i >= 0; i--) {
            c = intag.charAt(i);
            if (!XMLChar.isName(c)) {
                switch (c) {
                    case ' ':
                        sb.setCharAt(i, '_');
                        break;
                    case '<':
                        sb.setCharAt(i, '.');
                        sb.insert(i + 1, "lt.");
                        break;
                    case '>':
                        sb.setCharAt(i, '.');
                        sb.insert(i + 1, "gt.");
                        break;
                    default:
                        sb.setCharAt(i, '.');
                        sb.insert(i + 1, '.');
                        sb.insert(i + 1, Integer.toHexString(c));
                        break;
                }
            }
        }
        // Make sure the first character is an allowed one
        if (!XMLChar.isNameStart(sb.charAt(0))) {
            sb.insert(0, '_');
        }

        return sb.toString();
    }

}
