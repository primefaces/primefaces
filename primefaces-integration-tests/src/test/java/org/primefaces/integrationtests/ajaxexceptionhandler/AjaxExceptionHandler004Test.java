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

class AjaxExceptionHandler004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AjaxExceptionHandler: GitHub #15225 failed update of the handler itself must not end up in an endless loop")
    void noEndlessLoop(Page page) {
        // Act - the response is never received, so the click cannot be guarded
        page.button.clickUnguarded();

        // Assert - the button request and the update request of the handler failed
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.ajaxErrors, "2"));
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.handlerCalls, "1"));

        // Assert - give a possible loop some time, the handler must not be invoked again
        PrimeSelenium.wait(2000);
        assertEquals("2", page.ajaxErrors.getText());
        assertEquals("1", page.handlerCalls.getText());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "handlerCalls")
        WebElement handlerCalls;

        @FindBy(id = "ajaxErrors")
        WebElement ajaxErrors;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "ajaxexceptionhandler/ajaxExceptionHandler004.xhtml";
        }
    }
}
