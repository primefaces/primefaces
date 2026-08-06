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
package org.primefaces.integrationtests.tieredmenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.InputText;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TieredMenu001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("TieredMenu: GitHub #15036 clicking a menuitem of an overlay menu still processes correctly")
    void menuItemActionStillWorks(Page page) {
        // Arrange
        assertEquals("value: null", page.lblValue.getText());
        page.trigger.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(page.item1));

        // Act - clicking a menuitem uses "process=@this" which is resolved server-side and is
        // unaffected by the overlay being relocated in the DOM, so this must keep working
        PrimeSelenium.guardAjax(page.item1).click();

        // Assert
        assertEquals("value: FromMenuItem", page.lblValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("TieredMenu: GitHub #15036 inputText in the 'end' facet of an overlay menu must submit its value via AJAX")
    void endFacetInputSubmitsValue(Page page) {
        // Arrange
        assertEquals("value: null", page.lblValue.getText());
        page.trigger.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(page.menuInput.getInput()));

        // Act - type into the inputText placed in the "end" facet and blur it to trigger AJAX.
        // Since this tieredMenu is overlay="true", its whole markup (including the "end" facet) is
        // relocated by the client-side widget to the document body once shown, which used to detach
        // menuInput from its enclosing <form> and silently drop its value from the AJAX request.
        page.menuInput.setValue("FromFacetInput");

        // Assert - value must have been processed and submitted even though the menu (and its facet)
        // was moved outside of "form" in the DOM
        assertEquals("FromFacetInput", page.menuInput.getValue());
        assertEquals("value: FromFacetInput", page.lblValue.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "trigger")
        WebElement trigger;

        @FindBy(id = "item1")
        WebElement item1;

        @FindBy(id = "form:menuInput")
        InputText menuInput;

        @FindBy(id = "lblValue")
        WebElement lblValue;

        @Override
        public String getLocation() {
            return "tieredmenu/tieredMenu001.xhtml";
        }
    }
}
