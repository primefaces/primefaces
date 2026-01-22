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
package org.primefaces.integrationtests.messages;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Severity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Messages001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Messages: message and validation")
    void basic(Page page) {
        // Arrange
        Messages messages = page.messages;

        // Act
        page.buttonAction1.click();

        // Assert
        assertDisplayed(messages);
        assertEquals(1, messages.getAllMessages().size());
        assertTrue(messages.getMessage(0).getSummary().contains("not be null")); //Mojarra and MyFaces have slightly different messages
        assertEquals(Severity.ERROR, messages.getMessage(0).getSeverity());
        assertTrue(PrimeSelenium.hasCssClass(page.inputTextVal2, "ui-state-error"));

        // Act
        page.inputTextVal2.setValue("test123");
        page.buttonAction1.click();

        // Assert
        assertDisplayed(messages);
        assertEquals(1, messages.getAllMessages().size());
        assertEquals("Action 1", messages.getMessage(0).getSummary());
        assertEquals(Severity.INFO, messages.getMessage(0).getSeverity());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Messages: error-message with ClientId")
    void errorMessageWithClientId(Page page) {
        // Arrange
        Messages messages = page.messages;
        page.inputTextVal2.setValue("test123");

        // Act
        page.buttonAction2.click();

        // Assert
        assertDisplayed(messages);
        assertEquals(1, messages.getAllMessages().size());
        assertEquals("Action 2", messages.getMessage(0).getSummary());
        assertEquals(Severity.ERROR, messages.getMessage(0).getSeverity());
        assertTrue(PrimeSelenium.hasCssClass(page.inputTextVal1, "ui-state-error"));

        // Act
        page.buttonAction1.click();

        // Assert
        assertDisplayed(messages);
        assertEquals(1, messages.getAllMessages().size());
        assertEquals("Action 1", messages.getMessage(0).getSummary());
        assertEquals(Severity.INFO, messages.getMessage(0).getSeverity());
        assertFalse(PrimeSelenium.hasCssClass(page.inputTextVal1, "ui-state-error"));

        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:val1")
        InputText inputTextVal1;

        @FindBy(id = "form:val2")
        InputText inputTextVal2;

        @FindBy(id = "form:buttonAction1")
        CommandButton buttonAction1;

        @FindBy(id = "form:buttonAction2")
        CommandButton buttonAction2;

        @Override
        public String getLocation() {
            return "messages/messages001.xhtml";
        }
    }
}
