/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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
package org.primefaces.integrationtests.inputtextarea;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputTextarea;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;
import org.primefaces.selenium.component.model.Severity;

public class InputTextArea002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputTextarea: Test floating label empty field does not have class 'ui-state-filled' but has 'ui-state-hover' ")
    public void testEmptyField(Page page) {
        // Arrange
        InputTextarea inputTextarea = page.inputTextarea;
        Messages messages = page.messages;

        // Act
        inputTextarea.click();

        // Assert
        assertNoJavascriptErrors();
        Assertions.assertEquals("FillMe*", inputTextarea.getAssignedLabelText());
        Assertions.assertEquals("", inputTextarea.getValue());
        assertCss(inputTextarea, "ui-inputfield", "ui-InputTextarea", "ui-state-hover", "ui-state-focus");

        Assertions.assertEquals(0, messages.getAllMessages().size());
    }

    @Test
    @Order(2)
    @DisplayName("InputTextarea: Test input with data has class 'ui-state-filled'")
    public void testFilledField(Page page) {
        // Arrange
        InputTextarea inputTextarea = page.inputTextarea;
        Messages messages = page.messages;

        // Act
        inputTextarea.setValue("filled");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        Assertions.assertEquals("FillMe*", inputTextarea.getAssignedLabelText());
        Assertions.assertEquals("filled", inputTextarea.getValue());
        assertCss(inputTextarea, "ui-inputfield", "ui-InputTextarea", "ui-state-filled");
        Assertions.assertEquals(0, messages.getAllMessages().size());
    }

    @Test
    @Order(3)
    @DisplayName("InputTextarea: Test empty input submission causes required error message")
    public void testRequiredFieldError(Page page) {
        // Arrange
        InputTextarea inputTextarea = page.inputTextarea;
        Messages messages = page.messages;

        // Act
        inputTextarea.setValue("");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        Assertions.assertEquals("", inputTextarea.getValue());
        Assertions.assertEquals(1, messages.getAllMessages().size());
        Msg msg = messages.getAllMessages().get(0);
        Assertions.assertEquals(Severity.ERROR, msg.getSeverity());
        Assertions.assertEquals("InputTextArea is required!", msg.getSummary());
        Assertions.assertEquals("InputTextArea is required!", msg.getDetail());
    }

    @Test
    @Order(3)
    @DisplayName("InputTextarea: Test valid input submission does not cause an error.")
    public void testRequiredFieldPass(Page page) {
        // Arrange
        InputTextarea inputTextarea = page.inputTextarea;
        Messages messages = page.messages;

        // Act
        inputTextarea.setValue("test");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        Assertions.assertEquals("test", inputTextarea.getValue());
        Assertions.assertEquals(0, messages.getAllMessages().size());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputtext")
        InputTextarea inputTextarea;

        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputtextarea/inputTextArea002.xhtml";
        }
    }
}
