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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AjaxExceptionHandler005Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AjaxExceptionHandler: GitHub #15225 f:ajax connection error triggers the handler with 'httpError'")
    void httpError(Page page) {
        // Act
        page.httpError.click();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.exceptionName, "httpError"));
        assertTrue(page.exceptionMessage.getText().contains("504"),
                    "Expected the HTTP status in the error message but was: " + page.exceptionMessage.getText());
    }

    @Test
    @Order(2)
    @DisplayName("AjaxExceptionHandler: f:ajax empty response triggers the handler with 'emptyResponse'")
    void emptyResponse(Page page) {
        // Act
        page.emptyResponse.click();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.exceptionName, "emptyResponse"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "exceptionName")
        WebElement exceptionName;

        @FindBy(id = "exceptionMessage")
        WebElement exceptionMessage;

        @FindBy(id = "form:httpError")
        WebElement httpError;

        @FindBy(id = "form:emptyResponse")
        WebElement emptyResponse;

        @Override
        public String getLocation() {
            return "ajaxexceptionhandler/ajaxExceptionHandler005.xhtml";
        }
    }
}
