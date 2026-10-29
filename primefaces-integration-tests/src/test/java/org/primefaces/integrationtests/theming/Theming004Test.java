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
package org.primefaces.integrationtests.theming;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Theming004Test extends AbstractThemingTest {

    /**
     * Every ::before / ::after with content "\2003" is an icon drawn by the theme (mask of a --p-icon-* token).
     * Returns [number of icons, descriptions of the icons without mask-image url].
     */
    private static final String ICONS_SCRIPT = ""
            + "var count = 0, missing = [];"
            + "document.querySelectorAll('body *').forEach(function(el) {"
            + "  ['::before', '::after'].forEach(function(pseudo) {"
            + "    var cs = getComputedStyle(el, pseudo);"
            + "    if (cs.getPropertyValue('content') !== '\"\\u2003\"') { return; }"
            + "    var mask = cs.getPropertyValue('mask-image') || cs.getPropertyValue('-webkit-mask-image');"
            // an unchecked checkbox draws nothing on purpose (no --_icon, transparent mask); every other .p-icon must have one
            + "    var blank = '.p-checkbox-icon:not(.p-checkbox-checked-icon, .p-checkbox-indeterminate-icon, .p-checkbox-crossed-icon)';"
            + "    if (el.matches(blank) && mask.indexOf('linear-gradient(rgba(0, 0, 0, 0)') === 0) { return; }"
            + "    count++;"
            + "    if (!mask || mask.indexOf('url(') < 0) {"
            + "      var parent = el.closest('[id]');"
            + "      missing.push(el.tagName.toLowerCase() + '.' + String(el.className).trim().split(/\\s+/).join('.') + pseudo"
            + "          + ' in #' + (parent ? parent.id : '') + ' mask-image: ' + mask);"
            + "    }"
            + "  });"
            + "});"
            + "return [count, missing];";

    @Test
    @Order(1)
    @DisplayName("Theming: every theme icon (content \\2003) has a mask-image url")
    void icons(Page page) {
        // Arrange
        setColorScheme("light");

        // Act
        List<Object> result = PrimeSelenium.executeScript(ICONS_SCRIPT);
        long count = ((Number) result.get(0)).longValue();
        @SuppressWarnings("unchecked")
        List<String> missing = (List<String>) result.get(1);

        // Assert
        assertTrue(count >= 20, "expected many theme icons on the page, found " + count);
        assertTrue(missing.isEmpty(), missing.size() + " of " + count + " icons without mask-image url:\n" + String.join("\n", missing));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @Override
        public String getLocation() {
            return "theming/theming004.xhtml";
        }
    }
}
