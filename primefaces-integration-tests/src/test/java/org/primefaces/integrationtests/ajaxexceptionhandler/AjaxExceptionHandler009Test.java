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
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AjaxExceptionHandler009Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AjaxExceptionHandler: GitHub #13071 web.xml error-pages are passed to the client")
    void errorPagesRendered(Page page) {
        // Assert - java.lang.Throwable is registered as the default error page (empty key), the URL is encoded (e.g. jsessionid)
        Object errorPage = PrimeSelenium.executeScript("return PrimeFaces.settings.errorPages[''];");
        assertTrue(errorPage != null && errorPage.toString().contains("/error.xhtml"), "Unexpected error page: " + errorPage);
    }

    @Test
    @Order(2)
    @DisplayName("AjaxExceptionHandler: GitHub #15225 connection error without any handler redirects to the error-page")
    void connectionErrorRedirectsToErrorPage(Page page) {
        // Act
        page.connectionError.clickUnguarded();

        // Assert
        assertErrorPage(page);
    }

    @Test
    @Order(3)
    @DisplayName("AjaxExceptionHandler: GitHub #13071 Faces error response without any handler redirects to the error-page")
    void facesErrorRedirectsToErrorPage(Page page) {
        // Act
        page.facesError.clickUnguarded();

        // Assert
        assertErrorPage(page);
    }

    private void assertErrorPage(Page page) {
        PrimeSelenium.waitGui().until(ExpectedConditions.urlContains("error.xhtml"));
        PrimeSelenium.waitGui().until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
        assertTrue(page.getWebDriver().findElement(By.tagName("h1")).getText().contains("Error"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:connectionError")
        CommandButton connectionError;

        @FindBy(id = "form:facesError")
        CommandButton facesError;

        @Override
        public String getLocation() {
            return "ajaxexceptionhandler/ajaxExceptionHandler009.xhtml";
        }
    }
}
