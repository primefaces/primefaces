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

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chromium.HasCdp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Behavior of the theme CSS beyond the token values, in every preset: the responsive rules of a narrow viewport, the
 * toggleMask icon of an RTL password, size classes on a scope / density element, print, reduced motion and the hidden
 * helper class.
 */
class Theming016Test extends AbstractThemingTest {

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara", "nora", "metal", "glass"})
    @Order(1)
    @DisplayName("Theming: narrow viewport stacks the fluid selectOneButton and reflows the dataTable")
    void narrowViewport(String preset) {
        // Arrange
        open(preset);
        WebDriver.Window window = PrimeSelenium.getWebDriver().manage().window();
        Dimension size = window.getSize();
        List<WebElement> buttons = PrimeSelenium.getWebDriver().findElements(By.cssSelector("[id='form:fluidButtons'] > .ui-button"));
        WebElement header = PrimeSelenium.getWebDriver().findElement(By.cssSelector("[id='form:reflow'] thead th"));
        assertEquals(buttons.get(0).getLocation().getY(), buttons.get(1).getLocation().getY(), preset + ": wide viewport, one row");
        assertTrue(header.isDisplayed(), preset + ": wide viewport, the table header is shown");

        try {
            // Act
            window.setSize(new Dimension(560, size.getHeight()));
            Number viewport = PrimeSelenium.executeScript("return window.innerWidth;");
            Assumptions.assumeTrue(viewport.intValue() <= 640, "the window cannot be made narrow enough: " + viewport);

            // Assert
            int y0 = buttons.get(0).getLocation().getY();
            int y1 = buttons.get(1).getLocation().getY();
            int y2 = buttons.get(2).getLocation().getY();
            assertTrue(y0 < y1 && y1 < y2, preset + ": the buttons are not stacked: " + y0 + " " + y1 + " " + y2);
            assertEquals(buttons.get(0).getSize().getWidth(), buttons.get(2).getSize().getWidth(), preset + ": stacked buttons differ in width");
            assertFalse(header.isDisplayed(), preset + ": the reflow table still shows its header");
        }
        finally {
            window.setSize(size);
        }
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara", "nora", "metal", "glass"})
    @Order(2)
    @DisplayName("Theming: the toggleMask icon of an RTL password lies on the left, outside of the text")
    void rtlPassword(String preset) {
        // Arrange
        open(preset);
        WebElement input = PrimeSelenium.getWebDriver().findElement(By.id("form:rtlPassword"));
        WebElement icon = input.findElement(By.xpath("following-sibling::*[contains(@class, 'ui-password-icon')]"));

        // Assert
        int inputLeft = input.getLocation().getX();
        int iconLeft = icon.getLocation().getX();
        int iconRight = iconLeft + icon.getSize().getWidth();
        double paddingLeft = pixels(computedStyle(input, "padding-left"));
        assertTrue(iconLeft < inputLeft + input.getSize().getWidth() / 2, preset + ": the icon is not on the left in RTL");
        assertTrue(iconRight <= inputLeft + paddingLeft + 1, preset + ": the icon (right edge " + iconRight + ") overlaps the text "
                + "(starts at " + (inputLeft + paddingLeft) + ")");
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara", "nora", "metal", "glass"})
    @Order(3)
    @DisplayName("Theming: ui-toggleswitch-sm on a scope / density element keeps a valid size (no var() cycle)")
    void toggleSwitchSize(String preset) {
        // Arrange
        open(preset);
        WebElement normal = PrimeSelenium.getWebDriver().findElement(By.id("form:normalSwitch"));
        WebElement small = PrimeSelenium.getWebDriver().findElement(By.id("form:smallSwitch"));

        // Assert
        double normalWidth = pixels(computedStyle(normal, "width"));
        double smallWidth = pixels(computedStyle(small, "width"));
        assertTrue(normalWidth > 0, preset + ": normal switch has no width");
        assertEquals(normalWidth * 0.8, smallWidth, 1, preset + ": small switch is not 80% of the normal one");
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara", "nora", "metal", "glass"})
    @Order(4)
    @DisplayName("Theming: p-hidden hides a component that sets its own display")
    void helperHidden(String preset) {
        // Arrange
        open(preset);

        // Assert
        WebElement button = PrimeSelenium.getWebDriver().findElement(By.id("form:hiddenButton"));
        assertEquals("none", computedStyle(button, "display"), preset + ": the hidden button is displayed");
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @ValueSource(strings = {"aura", "glass"})
    @Order(5)
    @DisplayName("Theming: print is light, also when the application sets color-scheme: dark")
    void print(String preset) {
        // Arrange
        HasCdp cdp = cdp();
        Assumptions.assumeTrue(cdp != null, "the browser cannot emulate print (no Chrome DevTools Protocol)");
        open(preset);
        setColorScheme("dark");
        assertEquals("dark", PrimeSelenium.executeScript("return getComputedStyle(document.documentElement).colorScheme;"));

        try {
            // Act
            cdp.executeCdpCommand("Emulation.setEmulatedMedia", Map.of("media", "print"));

            // Assert
            assertEquals("light", PrimeSelenium.executeScript("return getComputedStyle(document.documentElement).colorScheme;"),
                    preset + ": print is not light");
        }
        finally {
            cdp.executeCdpCommand("Emulation.setEmulatedMedia", Map.of("media", ""));
        }
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara"})
    @Order(6)
    @DisplayName("Theming: with prefers-reduced-motion the indeterminate progressBar shows a full, static bar")
    void reducedMotionProgressBar(String preset) {
        // Arrange
        HasCdp cdp = cdp();
        Assumptions.assumeTrue(cdp != null, "the browser cannot emulate prefers-reduced-motion (no Chrome DevTools Protocol)");
        open(preset);
        WebElement value = PrimeSelenium.getWebDriver().findElement(By.cssSelector("[id='form:progress'] .ui-progressbar-value"));

        try {
            // Act
            emulateMediaFeature(cdp, "prefers-reduced-motion", "reduce");

            // Assert
            String script = "var cs = getComputedStyle(arguments[0], '::before');"
                    + "return [cs.animationName, cs.left, cs.right, cs.opacity];";
            List<Object> before = PrimeSelenium.executeScript(script, value);
            assertEquals(List.of("none", "0px", "0px", "0.5"), before, preset + ": the bar is not full and static");
        }
        finally {
            emulateMediaFeature(cdp, "prefers-reduced-motion", null);
        }
        assertNoJavascriptErrors();
    }

    private void open(String preset) {
        PrimeSelenium.goTo("theming/theming016.xhtml?theme=" + preset);
        setColorScheme("light");
    }
}
