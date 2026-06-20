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
package org.primefaces.integrationtests.commandlink;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandLink;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandLink001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("CommandLink: AJAX submit fires the action and updates messages + counter")
    void ajaxSubmit(Page page) {
        // Arrange
        assertNoJavascriptErrors();

        // Act
        page.linkAjax.click();

        // Assert
        assertEquals(1, page.messages.getAllMessages().size());
        assertEquals("Submit", page.messages.getMessage(0).getSummary());
        assertEquals("Counter: 1", page.messages.getMessage(0).getDetail());
        assertEquals("1", page.counter.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("CommandLink: repeated AJAX submit keeps firing the action")
    void ajaxSubmitMultiple(Page page) {
        // Arrange
        assertNoJavascriptErrors();

        // Act - let each submit fully settle before firing the next; firing back-to-back
        // is racy on Firefox where the second click can be dropped during request teardown
        page.linkAjax.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.counter, "1"));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(page.linkAjax));
        page.linkAjax.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.counter, "2"));

        // Assert
        assertEquals("Counter: 2", page.messages.getMessage(0).getDetail());
        assertEquals("2", page.counter.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("CommandLink: non-AJAX submit performs a full page submit")
    void nonAjaxSubmit(Page page) {
        // Arrange
        assertNoJavascriptErrors();

        // Act - the CommandLink wrapper only guards AJAX, so guard the full submit ourselves
        PrimeSelenium.guardHttp(page.linkNonAjax).click();

        // Assert - full submit re-renders the page; counter reflects the single submit
        assertEquals("Submit", page.messages.getMessage(0).getSummary());
        assertEquals("Counter: 1", page.messages.getMessage(0).getDetail());
        assertEquals("1", page.counter.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:linkAjax")
        CommandLink linkAjax;

        @FindBy(id = "form:linkNonAjax")
        CommandLink linkNonAjax;

        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:counter")
        WebElement counter;

        @Override
        public String getLocation() {
            return "commandlink/commandLink001.xhtml";
        }
    }

}
