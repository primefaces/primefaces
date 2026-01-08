/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
            .matching(Pattern.compile("^(data:image/(gif|png|jpeg|webp)[,;]|http|https|mailto|//).+", Pattern.CASE_INSENSITIVE))
            .onElements("img")
            .toFactory();

    private static final PolicyFactory HTML_MEDIA_SANITIZER = new HtmlPolicyBuilder()
            .allowUrlProtocols("data", "http", "https")
            .allowElements("video", "audio", "source", "iframe", "figure")
            .allowAttributes("controls", "width", "height", "origin-size", "src", "allowfullscreen", "class", "style", "data-proportion", "data-align",
                        "data-percentage", " data-size", "data-file-name", "data-file-size", "data-origin", "data-rotate", "data-index")
            .onElements("video", "audio", "source", "iframe", "figure")
            .toFactory();

    private static final PolicyFactory HTML_LINKS_SANITIZER = Sanitizers.LINKS
            .and(new HtmlPolicyBuilder()
            .allowElements("a")
            .allowAttributes("id", "target", "class", "style")
            .onElements("a")
            .toFactory());

    private static final PolicyFactory HTML_STYLES_SANITIZER = Sanitizers.STYLES
            .and(new HtmlPolicyBuilder()
            .allowElements("span", "li", "ol", "ul", "p", "u", "strong", "em", "s", "h1", "h1", "h2", "h3", "h4", "h5", "h6")
            .allowAttributes("class", "data-list", "contenteditable")
            .onElements("span", "li", "ol", "ul", "p", "u", "strong", "em", "s", "h1", "h1", "h2", "h3", "h4", "h5", "h6")
            .toFactory());

    private static final PolicyFactory HTML_DENY_ALL_SANITIZER = new HtmlPolicyBuilder().toFactory();

    private HtmlSanitizer() {

    }

    public static String sanitizeHtml(String value,
            boolean allowBlocks, boolean allowFormatting, boolean allowLinks, boolean allowStyles, boolean allowMedia) {

        if (LangUtils.isBlank(value)) {
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
        if (allowMedia) {
            sanitizer = sanitizer.and(HTML_IMAGES_SANITIZER);
            sanitizer = sanitizer.and(HTML_MEDIA_SANITIZER);
        }

        return sanitizer.sanitize(value);
    }


}
