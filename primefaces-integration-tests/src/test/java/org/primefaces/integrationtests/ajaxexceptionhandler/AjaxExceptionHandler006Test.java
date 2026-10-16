/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AjaxExceptionHandler006Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AjaxExceptionHandler: server-side exception triggers update and onexception of the handler registered for its type")
    void specificHandler(Page page) {
        // Act
        page.illegalState.click();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.specificType, "java.lang.IllegalStateException"));
        assertEquals("Simulated IllegalStateException", page.specificMessage.getText());
        assertTrue(page.specificTimestamp.getText().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
                    "Unexpected timestamp: " + page.specificTimestamp.getText());
        assertEquals("Updated: Simulated IllegalStateException", page.specificPanel.getText());
        assertEquals("", page.globalType.getText());
        assertFalse(page.globalPanel.getText().contains("Updated"));
        assertTrue(page.getWebDriver().getCurrentUrl().contains("ajaxExceptionHandler006.xhtml"));
    }

    @Test
    @Order(2)
    @DisplayName("AjaxExceptionHandler: server-side exception without a specific handler triggers update and onexception of the global handler")
    void globalHandler(Page page) {
        // Act
        page.unsupportedOperation.click();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.globalType, "java.lang.UnsupportedOperationException"));
        assertEquals("Simulated UnsupportedOperationException", page.globalMessage.getText());
        assertEquals("Updated: Simulated UnsupportedOperationException", page.globalPanel.getText());
        assertEquals("", page.specificType.getText());
        assertFalse(page.specificPanel.getText().contains("Updated"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "specificType")
        WebElement specificType;

        @FindBy(id = "specificMessage")
        WebElement specificMessage;

        @FindBy(id = "specificTimestamp")
        WebElement specificTimestamp;

        @FindBy(id = "specificPanel")
        WebElement specificPanel;

        @FindBy(id = "globalType")
        WebElement globalType;

        @FindBy(id = "globalMessage")
        WebElement globalMessage;

        @FindBy(id = "globalPanel")
        WebElement globalPanel;

        @FindBy(id = "form:illegalState")
        CommandButton illegalState;

        @FindBy(id = "form:unsupportedOperation")
        CommandButton unsupportedOperation;

        @Override
        public String getLocation() {
            return "ajaxexceptionhandler/ajaxExceptionHandler006.xhtml";
        }
    }
}
