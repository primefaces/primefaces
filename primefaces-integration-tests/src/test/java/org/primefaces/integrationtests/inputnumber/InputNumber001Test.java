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

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputNumber;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputNumber001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputNumber: Default AJAX event fires on blur")
    void ajaxChangeEvent(final Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;
        assertEquals("50", inputNumber.getValue());

        // Act
        inputNumber.setValue("33");

        // Assert
        assertEquals("33", inputNumber.getWidgetValue());
        assertEquals("33", inputNumber.getValue());
        assertConfiguration(inputNumber.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("InputNumber: Test integer input without decimal places default to 0")
    void integer(Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;
        assertEquals("50", inputNumber.getValue());

        // Act
        inputNumber.setValue("98.54");
        page.button.click();

        // Assert
        assertEquals("99", inputNumber.getValue());
        assertConfiguration(inputNumber.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("InputNumber: Test widget getValue() function returns Integer values with the correct format")
    void integerWidgetValue(Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;
        assertEquals("50", inputNumber.getValue());

        // Act
        inputNumber.setValue("42");

        // Assert
        assertEquals("42", inputNumber.getWidgetValue());
        assertConfiguration(inputNumber.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputNumber Config = " + cfg);
        assertEquals(0, cfg.get("decimalPlaces"));
        assertEquals(0, cfg.get("decimalPlacesRawValue"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputnumber")
        InputNumber inputnumber;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputnumber/inputNumber001.xhtml";
        }
    }
}
