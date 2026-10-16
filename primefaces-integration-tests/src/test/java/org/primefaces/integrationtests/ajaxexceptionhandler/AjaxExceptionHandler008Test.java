/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.integrationtests.ajaxexceptionhandler;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AjaxExceptionHandler008Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AjaxExceptionHandler: GitHub #13073 Faces error response with 'class ' prefix triggers the handler registered for its type")
    void specificHandlerWithClassPrefix(Page page) {
        // Act
        page.mojarraError.clickUnguarded();

        // Assert - onexception is invoked after the update of the handler
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.specificMessage, "Simulated IllegalStateException"));
        assertEquals("class java.lang.IllegalStateException", page.specificName.getText());
        assertTrue(page.specificTimestamp.getText().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
                    "Unexpected timestamp: " + page.specificTimestamp.getText());
        assertEquals("Updated", page.specificPanel.getText());
        assertEquals("", page.globalName.getText());
    }

    @Test
    @Order(2)
    @DisplayName("AjaxExceptionHandler: GitHub #13073 Faces error response without 'class ' prefix triggers the handler registered for its type")
    void specificHandlerWithoutClassPrefix(Page page) {
        // Act
        page.plainError.clickUnguarded();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.specificMessage, "Simulated IllegalStateException"));
        assertEquals("java.lang.IllegalStateException", page.specificName.getText());
        assertEquals("Updated", page.specificPanel.getText());
        assertEquals("", page.globalName.getText());
    }

    @Test
    @Order(3)
    @DisplayName("AjaxExceptionHandler: GitHub #13073 Faces error response without a specific handler triggers the global handler")
    void globalHandler(Page page) {
        // Act
        page.otherError.clickUnguarded();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.globalMessage, "Simulated UnsupportedOperationException"));
        assertEquals("class java.lang.UnsupportedOperationException", page.globalName.getText());
        assertEquals("", page.specificName.getText());
        assertEquals("", page.specificPanel.getText());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "specificName")
        WebElement specificName;

        @FindBy(id = "specificMessage")
        WebElement specificMessage;

        @FindBy(id = "specificTimestamp")
        WebElement specificTimestamp;

        @FindBy(id = "specificPanel")
        WebElement specificPanel;

        @FindBy(id = "globalName")
        WebElement globalName;

        @FindBy(id = "globalMessage")
        WebElement globalMessage;

        @FindBy(id = "form:mojarraError")
        CommandButton mojarraError;

        @FindBy(id = "form:plainError")
        CommandButton plainError;

        @FindBy(id = "form:otherError")
        CommandButton otherError;

        @Override
        public String getLocation() {
            return "ajaxexceptionhandler/ajaxExceptionHandler008.xhtml";
        }
    }
}
