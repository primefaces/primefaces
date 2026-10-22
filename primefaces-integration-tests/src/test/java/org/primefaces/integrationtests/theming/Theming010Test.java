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

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Keyboard focus (:focus-visible) of menu items without the widget focus classes, and RTL.
 */
class Theming010Test extends AbstractThemingTest {

    @Test
    @Order(1)
    @DisplayName("Theming: p:tabMenu item shows the focus ring when its link has :focus-visible")
    void tabMenuFocus(Page page) {
        // Arrange
        setColorScheme("light");
        assertEquals("none", computedStyle(page.secondTab, "outline-style"));

        // Act
        focusVisible(page.secondTabLink);

        // Assert
        assertEquals(resolveToken(page.secondTab, "outline-style", "--p-tabmenu-item-focus-ring-style"),
                computedStyle(page.secondTab, "outline-style"), "tab has no focus ring");
        assertTokenWired("Focused tab", page.secondTab, "outline-color", "--p-tabmenu-item-focus-ring-color");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: p:panelMenu item shows the focus background when its link has :focus-visible")
    void panelMenuFocus(Page page) {
        // Arrange
        setColorScheme("light");
        String normal = computedStyle(page.panelMenuLink, "background-color");

        // Act
        focusVisible(page.panelMenuLink);

        // Assert
        assertNotEquals(normal, computedStyle(page.panelMenuLink, "background-color"), "panelMenu item has no focus background");
        assertTokenWired("Focused panelMenu item", page.panelMenuLink, "background-color", "--p-panelmenu-item-focus-background");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Theming: checked p:toggleSwitch handle moves to the end of the line, also in RTL")
    void toggleSwitchRtl(Page page) {
        // Arrange
        setColorScheme("light");

        // Assert: LTR handle on the right half, RTL handle on the left half of the track
        assertTrue(center(page.ltrHandle) > center(page.ltrSlider), "LTR: checked handle is not on the right");
        assertTrue(center(page.rtlHandle) < center(page.rtlSlider), "RTL: checked handle is not on the left");
        assertNoJavascriptErrors();
    }

    /** Focuses the element by script; a script focus without a prior pointer interaction matches :focus-visible. */
    private static void focusVisible(WebElement element) {
        PrimeSelenium.executeScript("arguments[0].focus();", element);
        Assumptions.assumeTrue(PrimeSelenium.<Boolean> executeScript("return arguments[0].matches(':focus-visible');", element),
                "the browser does not report :focus-visible for a script focus");
    }

    private static double center(WebElement element) {
        return element.getLocation().getX() + element.getSize().getWidth() / 2.0;
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(css = "[id='form:tabMenu'] .ui-tabmenuitem:nth-child(2)")
        WebElement secondTab;

        @FindBy(css = "[id='form:tabMenu'] .ui-tabmenuitem:nth-child(2) > a")
        WebElement secondTabLink;

        @FindBy(css = "[id='form:panelMenu'] .ui-panelmenu-content .ui-menuitem-link")
        WebElement panelMenuLink;

        @FindBy(css = "[id='form:toggleLtr'] .ui-toggleswitch-slider")
        WebElement ltrSlider;

        @FindBy(css = "[id='form:toggleLtr'] .ui-toggleswitch-handler")
        WebElement ltrHandle;

        @FindBy(css = "[id='form:toggleRtl'] .ui-toggleswitch-slider")
        WebElement rtlSlider;

        @FindBy(css = "[id='form:toggleRtl'] .ui-toggleswitch-handler")
        WebElement rtlHandle;

        @Override
        public String getLocation() {
            return "theming/theming010.xhtml";
        }
    }
}
