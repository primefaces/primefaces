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
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AjaxExceptionHandler007Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AjaxExceptionHandler: server-side exception without any handler redirects to the error-page")
    void redirectToErrorPage(Page page) {
        // Act
        page.button.clickUnguarded();

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.urlContains("error.xhtml"));
        PrimeSelenium.waitGui().until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
        String body = page.getWebDriver().findElement(By.tagName("body")).getText();
        assertTrue(body.contains("Message:Simulated IllegalStateException"), "Unexpected error page content: " + body);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "ajaxexceptionhandler/ajaxExceptionHandler007.xhtml";
        }
    }
}
