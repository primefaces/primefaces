/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputNumber;

public class InputNumber003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputNumber: GitHub #6387, #6518 test decimal places with padding zeroes")
    public void testPaddingZeroes(Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;
        Assertions.assertEquals("", inputNumber.getValue());

        // Act
        inputNumber.setValue("1.23");
        page.buttonSubmit.click();

        // Assert
        Assertions.assertEquals("1.230000", inputNumber.getValue());
        Assertions.assertEquals("1.230000", inputNumber.getWidgetValue());
        assertConfiguration(inputNumber.getWidgetConfiguration(), true);
    }

    @Test
    @Order(2)
    @DisplayName("InputNumber: GitHub #6387, #6518 test decimal places without padding zeroes")
    public void testNoPadding(Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;
        Assertions.assertEquals("", inputNumber.getValue());

        // Act
        page.buttonPadControl.click();
        inputNumber.setValue("4.56");
        page.buttonSubmit.click();

        // Assert
        Assertions.assertEquals("4.56", inputNumber.getValue());
        Assertions.assertEquals("4.56", inputNumber.getWidgetValue());
        assertConfiguration(inputNumber.getWidgetConfiguration(), false);
    }

    private void assertConfiguration(JSONObject cfg, boolean allowDecimalPadding) {
        assertNoJavascriptErrors();
        System.out.println("InputNumber Config = " + cfg);
        Assertions.assertEquals("6", cfg.get("decimalPlaces"));
        if (cfg.has("allowDecimalPadding")) {
            Assertions.assertEquals(allowDecimalPadding, cfg.getBoolean("allowDecimalPadding"));
        }
        else {
            Assertions.assertTrue(allowDecimalPadding);
        }
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputnumber")
        InputNumber inputnumber;

        @FindBy(id = "form:buttonSubmit")
        CommandButton buttonSubmit;

        @FindBy(id = "form:buttonPadControl")
        CommandButton buttonPadControl;

        @Override
        public String getLocation() {
            return "inputnumber/inputNumber003.xhtml";
        }
    }
}
