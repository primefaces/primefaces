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
package org.primefaces.integrationtests.message;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Message001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Message: basic error message validation")
    void basic(Page page) {
        // Arrange
        Message message = page.message;

        // Act
        page.buttonAction1.click();

        // Assert
        assertDisplayed(message);
        assertPresent(message.getIconError());
        assertEquals("Action 1", message.getSummaryError().getText());
        assertEquals("Action 1 - some error", message.getDetailError().getText());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Message: #11148 if severities are filtered, message may not be shown")
    void errorMessageWithClientId(Page page) {
        // Arrange
        Message message = page.message;

        // Act
        page.buttonAction2.click();

        // Assert
        assertDisplayed(message);
        assertNotPresent(message.getIconInfo());
        assertNotPresent(message.getSummaryInfo());
        assertNotPresent(message.getDetailInfo());
        assertPresent(message.getIconError());
        assertEquals("error summary", message.getSummaryError().getText());
        assertEquals("error detail", message.getDetailError().getText());

        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:message")
        Message message;

        @FindBy(id = "form:val1")
        InputText inputTextVal1;

        @FindBy(id = "form:buttonAction1")
        CommandButton buttonAction1;

        @FindBy(id = "form:buttonAction2")
        CommandButton buttonAction2;

        @Override
        public String getLocation() {
            return "message/message001.xhtml";
        }
    }
}
