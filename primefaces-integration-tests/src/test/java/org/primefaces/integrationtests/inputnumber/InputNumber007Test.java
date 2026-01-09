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
package org.primefaces.integrationtests.inputnumber;

import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.InputNumber;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InputNumber007Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputNumber: no valueChange event after it has been updated, is blurred and NO inputs were made")
    void noValueChangeEventAfterUpdate(Page page) {
        assertDoesNotThrow(() -> {
            // Arrange
            Messages messages = page.messages;
            InputText otherInput = page.otherInput;

            // Act
            // Sends tab after changing input, focus is on inputNumber afterwards
            otherInput.setValue("asdf");

            // blur inputNumber without sending any inputs. no guardAjax used here, ajax should not be happening
            otherInput.getInput().click();
            TestUtils.wait(100);

            // Assert
            assertNotDisplayed(messages);
        });
    }

    @Test
    @Order(2)
    @DisplayName("InputNumber: valueChange event after it has been updated, is blurred AND inputs were made")
    void valueChangeEventAfterUpdate(Page page) {
        // Arrange
        Messages messages = page.messages;
        InputText otherInput = page.otherInput;
        InputNumber inputNumber = page.inputNumber;

        // Act
        // Sends tab key after changing input, focus is on inputNumber afterwards
        otherInput.setValue("asdf");

        // InputNumber#setValue uses JS to change the value, try to emulate a real user typing here
        inputNumber.getInput().sendKeys("42");
        PrimeSelenium.guardAjax(otherInput.getInput()).click();

        // Assert
        assertDisplayed(messages);
        assertEquals(1, messages.getAllMessages().size());
        assertEquals("42.0", messages.getAllMessages().get(0).getSummary());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:otherInput")
        InputText otherInput;

        @FindBy(id = "form:inputNumber")
        InputNumber inputNumber;

        @Override
        public String getLocation() {
            return "inputnumber/inputNumber007.xhtml";
        }
    }
}