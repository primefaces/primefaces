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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Theming003Test extends AbstractThemingTest {

    private static final String STYLE_ID = "theming-test-override";

    private static final String CLOSE_ICON_MARKER = "pf-it-close-icon";

    private static final String CLOSE_ICON = "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' id='"
            + CLOSE_ICON_MARKER + "'%3E%3Crect width='24' height='24'/%3E%3C/svg%3E\")";

    @Test
    @Order(1)
    @DisplayName("Theming: --p-primary-base on :root changes the primary button and its contrast color")
    void primaryBase(Page page) {
        // Arrange
        setColorScheme("light");
        String defaultBackground = computedStyle(page.buttonPrimary, "background-color");

        // Act: light primary
        setApplicationStyle(STYLE_ID, ":root { --p-primary-base: #facc15; }");

        // Assert
        String lightBackground = computedStyle(page.buttonPrimary, "background-color");
        String lightText = computedStyle(page.buttonPrimary, "color");
        assertNotEquals(defaultBackground, lightBackground, "primary base does not change the primary button background");
        assertTokenWired("Button primary (light primary)", page.buttonPrimary, "background-color", "--p-button-primary-background");
        assertTokenWired("Button primary (light primary)", page.buttonPrimary, "color", "--p-button-primary-color");
        assertTrue(luminance(lightText) < 0.2, "text on a light primary must be dark: " + lightText + " on " + lightBackground);
        assertTrue(luminance(lightText) < luminance(lightBackground), "text must be darker than a light primary");

        // Act: dark primary
        setApplicationStyle(STYLE_ID, ":root { --p-primary-base: #1e3a8a; }");

        // Assert
        String darkBackground = computedStyle(page.buttonPrimary, "background-color");
        String darkText = computedStyle(page.buttonPrimary, "color");
        assertNotEquals(lightBackground, darkBackground, "primary base does not change the primary button background");
        assertTrue(luminance(darkText) > 0.5, "text on a dark primary must be light: " + darkText + " on " + darkBackground);
        assertTrue(luminance(darkText) > luminance(darkBackground), "text must be lighter than a dark primary");

        // Act: remove the override
        removeApplicationStyle(STYLE_ID);

        // Assert
        assertEquals(defaultBackground, computedStyle(page.buttonPrimary, "background-color"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: --p-icon-close on :root changes the close icon of dialog and messages")
    void iconOverride(Page page) {
        // Arrange
        setColorScheme("light");
        String dialogDefault = pseudoMaskImage(page.dialogCloseIcon, "::before");
        String messagesDefault = pseudoMaskImage(page.messagesCloseIcon, "::before");
        assertIcon("Dialog close icon (default)", dialogDefault);
        assertIcon("Messages close icon (default)", messagesDefault);
        assertFalse(dialogDefault.contains(CLOSE_ICON_MARKER));
        assertFalse(messagesDefault.contains(CLOSE_ICON_MARKER));

        // Act
        setApplicationStyle(STYLE_ID, ":root { --p-icon-close: " + CLOSE_ICON + "; }");

        // Assert
        String dialogIcon = pseudoMaskImage(page.dialogCloseIcon, "::before");
        String messagesIcon = pseudoMaskImage(page.messagesCloseIcon, "::before");
        assertTrue(dialogIcon.contains(CLOSE_ICON_MARKER), "Dialog close icon not overridden: " + dialogIcon);
        assertTrue(messagesIcon.contains(CLOSE_ICON_MARKER), "Messages close icon not overridden: " + messagesIcon);

        // Act: remove the override
        removeApplicationStyle(STYLE_ID);

        // Assert
        assertEquals(dialogDefault, pseudoMaskImage(page.dialogCloseIcon, "::before"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Theming: data-p-density=compact on <html> makes the input padding smaller")
    void density(Page page) {
        // Arrange
        setColorScheme("light");
        double normalPaddingTop = px(computedStyle(page.inputText, "padding-top"));
        double normalPaddingLeft = px(computedStyle(page.inputText, "padding-left"));
        assertTrue(normalPaddingTop > 0);

        // Act
        PrimeSelenium.executeScript("document.documentElement.setAttribute('data-p-density', 'compact');");

        // Assert
        double compactPaddingTop = px(computedStyle(page.inputText, "padding-top"));
        double compactPaddingLeft = px(computedStyle(page.inputText, "padding-left"));
        assertTrue(compactPaddingTop < normalPaddingTop, "compact padding-top " + compactPaddingTop + " not < " + normalPaddingTop);
        assertTrue(compactPaddingLeft < normalPaddingLeft, "compact padding-left " + compactPaddingLeft + " not < " + normalPaddingLeft);

        // Act
        PrimeSelenium.executeScript("document.documentElement.removeAttribute('data-p-density');");

        // Assert
        assertEquals(normalPaddingTop, px(computedStyle(page.inputText, "padding-top")));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Theming: primary contrast color - white on green-500 in light mode like PrimeUIX, dark on lime-500 and in dark mode")
    void primaryContrastThreshold(Page page) {
        // Arrange
        setColorScheme("light");

        // Act: green-500 (OKLCH lightness 0.723) was just above the former threshold 0.72 and got dark text
        setApplicationStyle(STYLE_ID, ":root { --p-primary-base: var(--p-green-500); }");

        // Assert
        String greenText = computedStyle(page.buttonPrimary, "color");
        assertTrue(luminance(greenText) > 0.9, "text on green-500 must be white: " + greenText);

        // Act: lime-500 (0.768) is too light for white text
        setApplicationStyle(STYLE_ID, ":root { --p-primary-base: var(--p-lime-500); }");

        // Assert
        String limeText = computedStyle(page.buttonPrimary, "color");
        assertTrue(luminance(limeText) < 0.2, "text on lime-500 must be dark: " + limeText);

        // Act: dark mode, the primary is a lighter shade with dark text
        setColorScheme("dark");
        setApplicationStyle(STYLE_ID, ":root { --p-primary-base: var(--p-green-500); }");

        // Assert
        String darkModeText = computedStyle(page.buttonPrimary, "color");
        assertTrue(luminance(darkModeText) < 0.2, "text on the dark mode primary must be dark: " + darkModeText);

        // Act
        removeApplicationStyle(STYLE_ID);
        setColorScheme("light");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("Theming: nested data-p-density follows the nearest one (also in a scope) and keeps a global --p-primary-base")
    void nestedDensity(Page page) {
        // Arrange
        setColorScheme("light");
        double normalInput = px(computedStyle(page.inputText, "padding-left"));
        double normalButton = px(computedStyle(page.buttonPrimary, "padding-left"));

        // Assert: the referencing spacing tokens (button padding) and the scope inside follow the compact density
        assertTrue(px(computedStyle(page.buttonCompact, "padding-left")) < normalButton, "button in a compact area is not compact");
        assertTrue(px(computedStyle(page.inputCompactScope, "padding-left")) < normalInput, "a scope inside a compact area resets the density");
        assertEquals(normalInput, px(computedStyle(page.inputNormal, "padding-left")), "data-p-density=normal does not restore the preset");

        // Act: global primary color
        setApplicationStyle(STYLE_ID, ":root { --p-primary-base: #1e3a8a; }");

        // Assert: the density area does not declare the color tokens again
        assertEquals(computedStyle(page.buttonPrimary, "background-color"), computedStyle(page.buttonCompact, "background-color"),
                "a global --p-primary-base does not reach into the compact area");

        // Act: remove the override
        removeApplicationStyle(STYLE_ID);
        assertNoJavascriptErrors();
    }

    private static void assertIcon(String description, String maskImage) {
        assertTrue(maskImage != null && maskImage.startsWith("url("), description + " has no mask-image: " + maskImage);
    }

    private static double px(String value) {
        return Double.parseDouble(value.replace("px", "").trim());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:buttonPrimary")
        WebElement buttonPrimary;

        @FindBy(id = "form:inputText")
        WebElement inputText;

        @FindBy(id = "form:buttonCompact")
        WebElement buttonCompact;

        @FindBy(id = "form:inputCompactScope")
        WebElement inputCompactScope;

        @FindBy(id = "form:inputNormal")
        WebElement inputNormal;

        @FindBy(css = "[id='form:dialog'] .p-dialog-titlebar-close .p-dialog-close-icon")
        WebElement dialogCloseIcon;

        @FindBy(css = "[id='form:messages'] .p-messages-close .p-messages-close-icon")
        WebElement messagesCloseIcon;

        @Override
        public String getLocation() {
            return "theming/theming003.xhtml";
        }
    }
}
