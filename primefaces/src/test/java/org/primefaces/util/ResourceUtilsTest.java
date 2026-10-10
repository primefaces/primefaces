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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceUtilsTest {

    @Test
    void appendCacheBuster_cached() {
        String url = "http://primefaces.org";
        String result = ResourceUtils.appendCacheBuster(url, true);
        assertEquals("http://primefaces.org?pfdrid_c=true", result);

    }

    @Test
    void appendCacheBuster_notCached() {
        String url = "http://primefaces.org";
        String result = ResourceUtils.appendCacheBuster(url, false);
        assertTrue(result.matches("http:\\/\\/primefaces.org\\?pfdrid_c=false&uid=.*"));
    }

    /**
     * The expected values are what js-cookie 3.0.8, which sets, reads and removes the cookies on the client side,
     * makes of the same names: a cookie the server sets under another encoding of its name is never found nor removed
     * by the client.
     */
    @Test
    void encodeCookieName_printableAscii() {
        StringBuilder printableAscii = new StringBuilder();
        for (char c = ' '; c <= '~'; c++) {
            printableAscii.append(c);
        }
        assertEquals("%20!%22#$%25&'%28%29*+%2C-.%2F0123456789%3A%3B%3C%3D%3E%3F%40ABCDEFGHIJKLMNOPQRSTUVWXYZ%5B%5C%5D^_`abcdefghijklmnopqrstuvwxyz%7B|%7D~",
                ResourceUtils.encodeCookieName(printableAscii.toString()));
    }

    @Test
    void encodeCookieName_controlCharacters() {
        assertEquals("a%09b%7Fc", ResourceUtils.encodeCookieName("a\tb\u007Fc"));
    }

    @Test
    void encodeCookieName_nonAscii() {
        assertEquals("caf%C3%A9", ResourceUtils.encodeCookieName("caf\u00E9"));
        assertEquals("%E6%97%A5%E6%9C%AC", ResourceUtils.encodeCookieName("\u65E5\u672C"));
        assertEquals("%F0%9F%98%80", ResourceUtils.encodeCookieName("\uD83D\uDE00"));
    }

    @Test
    void encodeCookieName_dynamicRouteViewId() {
        assertEquals("primefaces.download_organizations_%5Bid%5D_members",
                ResourceUtils.encodeCookieName("primefaces.download_organizations_[id]_members"));
        assertEquals("primefaces.download_%5Blocale%5D_products_%5Bsku%5D_reviews_orderExport",
                ResourceUtils.encodeCookieName("primefaces.download_[locale]_products_[sku]_reviews_orderExport"));
    }

    @Test
    void encodeCookieName_validNameUnchanged() {
        assertEquals("primefaces.download_orders", ResourceUtils.encodeCookieName("primefaces.download_orders"));
        assertEquals("pf.initialredirect-1a2b3c4d", ResourceUtils.encodeCookieName("pf.initialredirect-1a2b3c4d"));
        assertEquals("", ResourceUtils.encodeCookieName(""));
    }

}
