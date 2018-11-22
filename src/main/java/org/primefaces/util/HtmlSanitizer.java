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
