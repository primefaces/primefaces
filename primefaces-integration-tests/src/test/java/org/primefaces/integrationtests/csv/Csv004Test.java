/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.integrationtests.csv;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Message;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectBooleanCheckbox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

public class Csv004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("CSV: Non-Ajax Complex validation is not required")
    public void nonAjaxComplexValidationIsNotRequired(Page page) {
        // Arrange
        assertMessage(page.msgName, "");
        page.nameRequired.setValue(false);
        page.name.setValue("");
        // Act
        page.btnNonAjax.click();

        // Assert
        assertMessage(page.msgName, "");
    }

    @Test
    @Order(2)
    @DisplayName("CSV: Ajax Complex validation is not required")
    public void ajaxComplexValidationIsNotRequired(Page page) {
        // Arrange
        assertMessage(page.msgName, "");
        page.nameRequired.setValue(false);
        page.name.setValue("");
        // Act
        page.btnAjax.click();

        // Assert
        assertMessage(page.msgName, "");
    }

    @Test
    @Order(3)
    @DisplayName("CSV: Non-Ajax Complex validation is required")
    public void nonAjaxComplexValidationIsRequired(Page page) {
        // Arrange
        assertMessage(page.msgName, "");
        page.nameRequired.setValue(true);
        page.name.setValue("");
        // Act
        page.btnNonAjax.click();

        // Assert
        assertMessage(page.msgName, "A name is required!");
    }

    @Test
    @Order(4)
    @DisplayName("CSV: Ajax Complex validation is  required")
    public void ajaxComplexValidationIsRequired(Page page) {
        // Arrange
        assertMessage(page.msgName, "");
        page.nameRequired.setValue(true);
        page.name.setValue("");
        // Act
        page.btnAjax.getRoot().click(); // do not guard as it fails validation and never fires AJAX

        // Assert
        assertMessage(page.msgName, "A name is required!");
    }

    @Test
    @Order(5)
    @DisplayName("CSV: Ajax Complex validation is bypassed")
    public void bypassedAjaxComplexValidation(Page page) {
        // Arrange
        assertMessage(page.msgName, "");
        page.nameRequired.setValue(true);
        page.name.setValue("");
        // Act
        page.btnNonAjaxWithoutCsv.click();

        // Assert
        assertMessage(page.msgName, "");
    }

    protected void assertMessage(Message message, String expected) {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(message));
        Assertions.assertEquals(expected, message.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:nameRequired")
        SelectBooleanCheckbox nameRequired;

        @FindBy(id = "form:name")
        InputText name;

        @FindBy(id = "form:msgName")
        Message msgName;

        @FindBy(id = "form:btnNonAjax")
        CommandButton btnNonAjax;

        @FindBy(id = "form:btnAjax")
        CommandButton btnAjax;

        @FindBy(id = "form:btnNonAjaxWithoutCsv")
        CommandButton btnNonAjaxWithoutCsv;

        @Override
        public String getLocation() {
            return "csv/csv004.xhtml";
        }
    }
}
