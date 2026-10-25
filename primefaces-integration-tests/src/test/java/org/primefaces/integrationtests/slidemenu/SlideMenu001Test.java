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
import org.primefaces.selenium.PrimeSelenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlideMenu001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SlideMenu: GitHub #15402 overlay stays open when clicking the back button")
    void backButtonKeepsOverlayOpen(Page page) {
        // Arrange
        page.trigger.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(page.menu));
        page.submenuLink.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(page.backward));

        // Act
        page.backward.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.invisibilityOf(page.backward));

        // Assert
        assertTrue(page.menu.isDisplayed(), "Overlay should stay open after navigating back");
        assertTrue(page.item3.isDisplayed(), "Root list should be visible again");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SlideMenu: GitHub #15402 overlay still closes when clicking a menu item")
    void menuItemClosesOverlay(Page page) {
        // Arrange
        page.trigger.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(page.menu));

        // Act
        page.item3.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.invisibilityOf(page.menu));

        // Assert
        assertFalse(page.menu.isDisplayed(), "Overlay should close after clicking a menu item");
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:menuButton")
        WebElement trigger;

        @FindBy(id = "form:menu")
        WebElement menu;

        @FindBy(css = "[id='form:submenu'] > a")
        WebElement submenuLink;

        @FindBy(css = "[id='form:menu'] .ui-slidemenu-backward a")
        WebElement backward;

        @FindBy(id = "form:item3")
        WebElement item3;

        @Override
        public String getLocation() {
            return "slidemenu/slideMenu001.xhtml";
        }
    }
}
