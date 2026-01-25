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
package org.primefaces.integrationtests.clientwindow;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class ClientWindow001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ClientWindow: Basic check - compare URL-Param, SessionStorage and Postback")
    void basic(Page page) {
        // Arrange

        // Act
        page.button.click();

        // Assert
        String url = page.getWebDriver().getCurrentUrl();
        assertTrue(url.contains("jfwid="));

        String pfWindowId = PrimeSelenium.executeScript("return sessionStorage.getItem('pf.windowId');");
        assertNotNull(pfWindowId);
        assertTrue(pfWindowId.length() > 0);
        assertTrue(url.endsWith(pfWindowId));

        assertEquals(pfWindowId, page.messages.getMessage(0).getDetail());

        assertEquals("jfwid", page.messages.getMessage(0).getSummary());
    }

    @Test
    @Order(2)
    @DisplayName("ClientWindow: check rendered URLs")
    void renderedUrls(Page page) {
        // Arrange

        // Act

        // Assert
        String pfWindowId = PrimeSelenium.executeScript("return sessionStorage.getItem('pf.windowId');");
        WebElement eltLink2Anotherpage = page.getWebDriver().findElement(By.className("link2Anotherpage"));
        String href = eltLink2Anotherpage.getAttribute("href");
        assertTrue(href.endsWith(pfWindowId));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputtext")
        InputText inputtext;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "clientwindow/clientWindow001.xhtml";
        }
    }
}
