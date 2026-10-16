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

class AjaxExceptionHandler003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AjaxExceptionHandler: empty response is reported as 'emptyResponse'")
    void emptyResponse(Page page) {
        // Act
        page.emptyResponse.clickUnguarded();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.exceptionName, "emptyResponse"));
    }

    @Test
    @Order(2)
    @DisplayName("AjaxExceptionHandler: response without partial-response is reported as 'malformedXML'")
    void noPartialResponse(Page page) {
        // Act
        page.noPartialResponse.clickUnguarded();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.exceptionName, "malformedXML"));
    }

    @Test
    @Order(3)
    @DisplayName("AjaxExceptionHandler: unparseable response is reported as 'malformedXML'")
    void unparseableResponse(Page page) {
        // Act
        page.unparseableResponse.clickUnguarded();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.exceptionName, "malformedXML"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "exceptionName")
        WebElement exceptionName;

        @FindBy(id = "form:emptyResponse")
        CommandButton emptyResponse;

        @FindBy(id = "form:noPartialResponse")
        CommandButton noPartialResponse;

        @FindBy(id = "form:unparseableResponse")
        CommandButton unparseableResponse;

        @Override
        public String getLocation() {
            return "ajaxexceptionhandler/ajaxExceptionHandler003.xhtml";
        }
    }
}
