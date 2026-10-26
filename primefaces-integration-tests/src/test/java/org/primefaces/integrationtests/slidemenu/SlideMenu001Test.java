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
package org.primefaces.integrationtests.slidemenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.SlideMenu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlideMenu001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SlideMenu: GitHub #15402 overlay stays open when clicking the back button")
    void backButtonKeepsOverlayOpen(Page page) {
        // Arrange
        SlideMenu menu = page.menu;
        page.trigger.click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(menu));
        menu.selectMenuitemByValue("Submenu");
        assertEquals(1, menu.getDepth());
        assertTrue(menu.isBackwardVisible());

        // Act
        menu.back();

        // Assert
        assertTrue(menu.isDisplayed(), "Overlay should stay open after navigating back");
        assertEquals(0, menu.getDepth());
        assertFalse(menu.isBackwardVisible());
        assertTrue(menu.findMenuitemByValue("Item 3").isDisplayed(), "Root list should be visible again");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SlideMenu: GitHub #15402 overlay still closes when clicking a menu item")
    void menuItemClosesOverlay(Page page) {
        // Arrange
        SlideMenu menu = page.menu;
        page.trigger.click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(menu));

        // Act
        menu.selectMenuitemByValue("Item 3");
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(menu));

        // Assert
        assertFalse(menu.isDisplayed(), "Overlay should close after clicking a menu item");
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:menuButton")
        CommandButton trigger;

        @FindBy(id = "form:menu")
        SlideMenu menu;

        @Override
        public String getLocation() {
            return "slidemenu/slideMenu001.xhtml";
        }
    }
}
