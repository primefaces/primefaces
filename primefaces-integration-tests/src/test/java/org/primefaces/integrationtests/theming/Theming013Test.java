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
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Glass preset (?theme=glass): the floating layer (dialog, modal mask, toolbar, buttons) blurs what lies behind it, the
 * content layer (panel) and the menubar (backdrop root of its submenus) do not; the page gets a wallpaper; button texts
 * reach 4.5:1 in light and dark mode. The backdrop tokens are neutral in Aura.
 */
class Theming013Test extends AbstractThemingTest {

    @Test
    @Order(1)
    @DisplayName("Theming: Glass blurs the floating layer (dialog, mask, toolbar, buttons), not the content layer and the menubar")
    void backdrop(GlassPage page) {
        // Arrange
        setColorScheme("light");

        // Assert
        List<WebElement> links = PrimeSelenium.getWebDriver().findElements(By.cssSelector("link[href*='ln=primefaces-']"));
        assertTrue(links.stream().anyMatch(l -> l.getDomAttribute("href").contains("ln=primefaces-glass")), "primefaces-glass:theme.css not rendered");
        assertBlur("dialog box", backdropFilter(dialogBox(page.dialog), "::before"));
        assertEquals("none", backdropFilter(page.dialog), "the full viewport dialog container must not blur the page");
        assertBlur("modal mask", backdropFilter(PrimeSelenium.getWebDriver().findElement(By.cssSelector(".p-widget-overlay, .p-dialog-mask"))));
        assertBlur("toolbar", backdropFilter(page.toolbar, "::before"));
        assertEquals("none", backdropFilter(page.toolbar), "the toolbar itself must not be a backdrop root / stacking context");
        assertBlur("button", backdropFilter(page.primary));
        assertEquals("none", backdropFilter(page.panel), "the content layer (panel) must not get a backdrop filter");
        assertEquals("none", backdropFilter(page.menubar), "the menubar must not get a backdrop filter (backdrop root of its submenus)");
        String wallpaper = PrimeSelenium.executeScript("return getComputedStyle(document.documentElement).backgroundImage;");
        assertTrue(wallpaper.contains("radial-gradient"), "no page wallpaper: " + wallpaper);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: Glass button texts reach 4.5:1 on every background stop (light and dark)")
    void contrast(GlassPage page) {
        for (String scheme : List.of("light", "dark")) {
            // Arrange
            setColorScheme(scheme);

            // Assert
            for (WebElement button : List.of(page.primary, page.secondary, page.outlined, page.success, page.danger)) {
                double ratio = minContrast(button.findElement(By.cssSelector(".p-button-text")), button, null);
                assertTrue(ratio >= 4.5, scheme + " " + button.getAttribute("id") + ": " + ratio);
            }
        }
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Theming: the backdrop and document tokens are neutral in Aura")
    void auraUnchanged(AuraPage page) {
        // Arrange
        setColorScheme("light");

        // Assert
        assertEquals("none", backdropFilter(dialogBox(page.dialog), "::before"));
        assertEquals("none", backdropFilter(page.toolbar, "::before"));
        assertEquals("none", backdropFilter(page.primary));
        String background = PrimeSelenium.executeScript("return getComputedStyle(document.documentElement).backgroundImage;");
        assertFalse(background.contains("gradient"), "aura paints a page background: " + background);
        assertNoJavascriptErrors();
    }

    private String backdropFilter(WebElement element) {
        return computedStyle(element, "backdrop-filter");
    }

    private String backdropFilter(WebElement element, String pseudo) {
        return PrimeSelenium.executeScript("return getComputedStyle(arguments[0], arguments[1]).getPropertyValue('backdrop-filter');",
                element, pseudo);
    }

    private static WebElement dialogBox(WebElement dialog) {
        return dialog.findElement(By.cssSelector(".p-dialog-box"));
    }

    private void assertBlur(String description, String filter) {
        assertTrue(filter.contains("blur("), description + " has no backdrop blur: " + filter);
    }

    public static class GlassPage extends AbstractPrimePage {
        @FindBy(id = "form:toolbar")
        WebElement toolbar;

        @FindBy(id = "form:primary")
        WebElement primary;

        @FindBy(id = "form:secondary")
        WebElement secondary;

        @FindBy(id = "form:outlined")
        WebElement outlined;

        @FindBy(id = "form:success")
        WebElement success;

        @FindBy(id = "form:danger")
        WebElement danger;

        @FindBy(id = "form:menubar")
        WebElement menubar;

        @FindBy(id = "form:panel")
        WebElement panel;

        @FindBy(id = "form:dialog")
        WebElement dialog;

        @Override
        public String getLocation() {
            return "theming/theming013.xhtml?theme=glass";
        }
    }

    public static class AuraPage extends AbstractPrimePage {
        @FindBy(id = "form:toolbar")
        WebElement toolbar;

        @FindBy(id = "form:primary")
        WebElement primary;

        @FindBy(id = "form:dialog")
        WebElement dialog;

        @Override
        public String getLocation() {
            return "theming/theming013.xhtml";
        }
    }
}
