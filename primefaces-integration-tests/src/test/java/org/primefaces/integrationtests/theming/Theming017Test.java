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

import org.primefaces.selenium.PrimeSelenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The backdrop classes for the layout of an application: p-backdrop-navigation blurs on a ::before like the toolbar (the
 * bar is no backdrop root, a menu inside it blurs the page), p-backdrop-overlay on the element; the position of the
 * application wins (zero specificity). The tokens are neutral outside of Glass.
 */
class Theming017Test extends AbstractThemingTest {

    private static final String LOCATION = "theming/theming017.xhtml";

    @ParameterizedTest
    @ValueSource(strings = {"aura", "glass"})
    @Order(1)
    @DisplayName("Theming: p-backdrop-navigation / p-backdrop-overlay take the backdrop of the preset")
    void backdropClasses(String preset) {
        // Arrange
        open(LOCATION, preset);
        WebElement topbar = element("topbar");
        WebElement topbarMenu = element("topbarMenu");
        WebElement drawer = element("drawer");
        boolean glass = "glass".equals(preset);

        // Assert
        assertEquals("none", computedStyle(topbar, "backdrop-filter"), preset + ": the bar must not be a backdrop root");
        assertFilter(preset + " bar ::before", PrimeSelenium.getComputedStyle(topbar, "::before", "backdrop-filter"), glass);
        assertFilter(preset + " menu inside the bar", computedStyle(topbarMenu, "backdrop-filter"), glass);
        assertFilter(preset + " drawer", computedStyle(drawer, "backdrop-filter"), glass);
        assertEquals("fixed", computedStyle(topbar, "position"), preset + ": the position of the application must win");
        assertEquals("absolute", computedStyle(topbarMenu, "position"), preset + ": the position of the application must win");
        assertEquals("relative", computedStyle(element("title"), "position"),
                preset + ": the children of the bar are painted above its ::before");
        assertNoJavascriptErrors();
    }

    private static WebElement element(String id) {
        return PrimeSelenium.getWebDriver().findElement(By.id(id));
    }

    private static void assertFilter(String description, String filter, boolean blur) {
        if (blur) {
            assertTrue(filter.contains("blur("), description + " has no backdrop blur: " + filter);
        }
        else {
            assertEquals("none", filter, description);
        }
    }
}
