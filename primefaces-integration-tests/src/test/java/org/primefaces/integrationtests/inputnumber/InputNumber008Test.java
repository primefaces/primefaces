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

class InputNumber008Test extends AbstractPrimePageTest {


    @Test
    @Order(1)
    @DisplayName("InputNumber: Test decimal places raw value has 6 digits")
    void integer(Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;
        assertEquals("12.345678", inputNumber.getWidgetValue());
        assertEquals("12.35", inputNumber.getValue());

        // Act
        inputNumber.setValue("561.7891");
        page.button.click();

        // Assert
        assertEquals("561.7891", inputNumber.getWidgetValue());
        assertEquals("561.79", inputNumber.getValue());
        assertConfiguration(inputNumber.getWidgetConfiguration());
    }


    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputNumber Config = " + cfg);
        assertEquals(2, cfg.get("decimalPlaces"));
        assertEquals(6, cfg.get("decimalPlacesRawValue"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputnumber")
        InputNumber inputnumber;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputnumber/inputNumber008.xhtml";
        }
    }
}
