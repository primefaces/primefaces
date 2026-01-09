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
package org.primefaces.integrationtests.inputtext;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;
import org.primefaces.selenium.component.model.Severity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputText002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputText: Test floating label empty field does not have class 'ui-state-filled' but has 'ui-state-hover' ")
    void emptyField(Page page) {
        // Arrange
        InputText inputText = page.inputtext;
        Messages messages = page.messages;

        // Act
        inputText.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals("FillMe*", inputText.getAssignedLabelText());
        assertEquals("", inputText.getValue());
        assertCss(inputText, "ui-inputfield", "ui-inputtext", "ui-state-hover", "ui-state-focus");

        assertEquals(0, messages.getAllMessages().size());
    }

    @Test
    @Order(2)
    @DisplayName("InputText: Test input with data has class 'ui-state-filled'")
    void filledField(Page page) {
        // Arrange
        InputText inputText = page.inputtext;
        Messages messages = page.messages;

        // Act
        inputText.setValue("filled");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals("FillMe*", inputText.getAssignedLabelText());
        assertEquals("filled", inputText.getValue());
        assertCss(inputText, "ui-inputfield", "ui-inputtext", "ui-state-filled");
        assertEquals(0, messages.getAllMessages().size());
    }

    @Test
    @Order(3)
    @DisplayName("InputText: Test empty input submission causes required error message")
    void requiredFieldError(Page page) {
        // Arrange
        InputText inputText = page.inputtext;
        Messages messages = page.messages;

        // Act
        inputText.setValue("");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals("", inputText.getValue());
        assertEquals(1, messages.getAllMessages().size());
        Msg msg = messages.getAllMessages().get(0);
        assertEquals(Severity.ERROR, msg.getSeverity());
        assertCss(inputText, "ui-state-error", "ui-inputtext");
        assertEquals("InputText is required!", msg.getSummary());
        assertEquals("InputText is required!", msg.getDetail());
    }

    @Test
    @Order(3)
    @DisplayName("InputText: Test valid input submission does not cause an error.")
    void requiredFieldPass(Page page) {
        // Arrange
        InputText inputText = page.inputtext;
        Messages messages = page.messages;

        // Act
        inputText.setValue("test");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals("test", inputText.getValue());
        assertEquals(0, messages.getAllMessages().size());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputtext")
        InputText inputtext;

        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputtext/inputText002.xhtml";
        }
    }
}
