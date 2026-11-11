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
package org.primefaces.integrationtests.selectonemenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.SelectOneMenu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectOneMenu017Test extends AbstractPrimePageTest {

    private static final String FILLED = "ui-inputwrapper-filled";

    SelectOneMenu017Test() {
        super();
    }

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: GitHub #15495 float label is filled when a value is selected")
    void filledOnLoad(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Assert
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());
        assertTrue(PrimeSelenium.hasCssClass(selectOneMenu, FILLED));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: GitHub #15495 float label stays filled after focus and blur")
    void filledAfterBlur(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Act
        selectOneMenu.show();
        selectOneMenu.hide();
        page.outside.click();

        // Assert
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());
        assertTrue(PrimeSelenium.hasCssClass(selectOneMenu, FILLED));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: GitHub #15495 float label filled state follows the selected value")
    void filledFollowsValue(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Act - select the empty option
        selectOneMenu.show();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(selectOneMenu.getPanel()));
        selectOneMenu.getItems().findElements(By.cssSelector("li.ui-selectonemenu-item")).get(0).click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(selectOneMenu.getPanel()));

        // Assert
        assertEquals("", selectOneMenu.getInput().getDomProperty("value"));
        assertFalse(PrimeSelenium.hasCssClass(selectOneMenu, FILLED));

        // Act
        selectOneMenu.select("Max");

        // Assert
        assertEquals("Max", selectOneMenu.getSelectedLabel());
        assertTrue(PrimeSelenium.hasCssClass(selectOneMenu, FILLED));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @FindBy(id = "outside")
        WebElement outside;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu017.xhtml";
        }
    }
}
