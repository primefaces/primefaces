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
package org.primefaces.showcase.util;

import java.util.Locale;

/**
 * Recognizes crawlers by their User-Agent, shared by {@link RequestStatsFilter} which only counts them and
 * {@link BotFilter} which limits them.
 */
public final class Bots {

    /**
     * Returned for a request which does not send a User-Agent at all.
     */
    public static final String NONE = "<none>";

    /**
     * Returned for an agent which looks like a crawler but is none we know by name.
     */
    public static final String OTHER = "<other bot>";

    /**
     * User agent fragments of well known crawlers, checked lowercase. The showcase is mostly interested in the
     * AI/LLM ones, the classic search engines and generic HTTP clients are listed to keep them out of {@link #OTHER}.
     */
    private static final String[] KNOWN = {
        "gptbot", "oai-searchbot", "chatgpt-user", "claudebot", "claude-user", "claude-searchbot", "anthropic-ai",
        "perplexitybot", "perplexity-user", "google-extended", "meta-externalagent", "bytespider", "ccbot",
        "cohere-ai", "diffbot", "imagesiftbot", "omgili", "timpibot", "youbot", "applebot", "amazonbot",
        "googlebot", "bingbot", "yandexbot", "duckduckbot", "baiduspider", "facebookexternalhit",
        "ahrefsbot", "semrushbot", "mj12bot", "dotbot", "petalbot", "dataforseobot", "screaming frog",
        "python-requests", "scrapy", "go-http-client", "okhttp", "curl/", "wget", "java/", "libwww-perl",
        "headlesschrome", "chrome-lighthouse"
    };

    private Bots() {
    }

    /**
     * @param userAgent the User-Agent header, may be <code>null</code>.
     * @return the name of the recognized crawler, or <code>null</code> if the agent does not look like one.
     */
    public static String detect(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return NONE;
        }

        String lower = userAgent.toLowerCase(Locale.ROOT);
        for (String bot : KNOWN) {
            if (lower.contains(bot)) {
                return bot;
            }
        }

        if (lower.contains("bot") || lower.contains("spider") || lower.contains("crawler")) {
            return OTHER;
        }

        return null;
    }
}
