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

import java.util.regex.Pattern;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlSanitizer {

    private static final PolicyFactory HTML_IMAGES_SANITIZER = new HtmlPolicyBuilder()
            .allowUrlProtocols("data", "http", "https")
            .allowElements("img")
            .allowAttributes("src")
            .matching(Pattern.compile("^(data:image/(gif|png|jpeg)[,;]|http|https|mailto|//).+", Pattern.CASE_INSENSITIVE))
            .onElements("img")
            .toFactory();

    private static final PolicyFactory HTML_LINKS_SANITIZER = Sanitizers.LINKS
            .and(new HtmlPolicyBuilder()
            .allowElements("a")
            .allowAttributes("target")
            .onElements("a")
            .toFactory());

    private static final PolicyFactory HTML_STYLES_SANITIZER = Sanitizers.STYLES
            .and(new HtmlPolicyBuilder()
            .allowElements("span")
            .allowAttributes("class")
            .onElements("span")
            .toFactory());

    private static final PolicyFactory HTML_DENY_ALL_SANITIZER = new HtmlPolicyBuilder().toFactory();

    private HtmlSanitizer() {

    }

    public static String sanitizeHtml(String value,
            boolean allowBlocks, boolean allowFormatting, boolean allowLinks, boolean allowStyles, boolean allowImages) {

        if (LangUtils.isValueBlank(value)) {
            return value;
        }

        PolicyFactory sanitizer = HTML_DENY_ALL_SANITIZER;
        if (allowBlocks) {
            sanitizer = sanitizer.and(Sanitizers.BLOCKS);
        }
        if (allowFormatting) {
            sanitizer = sanitizer.and(Sanitizers.FORMATTING);
        }
        if (allowLinks) {
            sanitizer = sanitizer.and(HTML_LINKS_SANITIZER);
        }
        if (allowStyles) {
            sanitizer = sanitizer.and(HTML_STYLES_SANITIZER);
        }
        if (allowImages) {
            sanitizer = sanitizer.and(HTML_IMAGES_SANITIZER);
        }

        return sanitizer.sanitize(value);
    }


}
