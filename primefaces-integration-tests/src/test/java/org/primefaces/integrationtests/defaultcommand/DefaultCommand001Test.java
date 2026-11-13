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
package org.primefaces.integrationtests.defaultcommand;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCommand001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DefaultCommand: Enter clicks the target, not the first button of the form")
    void enter(Page page) {
        // Arrange
        page.name.setValue("PrimeFaces");

        // Act
        PrimeSelenium.guardAjax(page.name.getInput()).sendKeys(Keys.ENTER);

        // Assert
        assertEquals("save", page.messages.getMessage(0).getSummary());
        assertEquals("PrimeFaces", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DefaultCommand: Enter within the scope clicks the scoped target")
    void scope(Page page) {
        // Arrange
        page.scoped.setValue("Scoped");

        // Act
        PrimeSelenium.guardAjax(page.scoped.getInput()).sendKeys(Keys.ENTER);

        // Assert
        assertEquals("scopedSave", page.messages.getMessage(0).getSummary());
        assertEquals("Scoped", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:name")
        InputText name;

        @FindBy(id = "form:scoped")
        InputText scoped;

        @Override
        public String getLocation() {
            return "defaultcommand/defaultCommand001.xhtml";
        }
    }
}
