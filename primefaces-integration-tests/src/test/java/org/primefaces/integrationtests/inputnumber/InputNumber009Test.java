/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputNumber;
import org.primefaces.selenium.component.base.ComponentUtils;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputNumber009Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputNumber: leadingZero=keep preserves leading zeros on initial render")
    void initialRender(Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;

        // Assert
        assertEquals("000050", inputNumber.getValue());
        assertEquals("000050", inputNumber.getHiddenInput().getDomProperty("value"));
        assertEquals("000050", page.output.getText());
        assertConfiguration(inputNumber.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("InputNumber: leadingZero=keep preserves leading zeros after submit round-trip")
    void submitPreservesLeadingZeros(Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;
        assertEquals("000050", inputNumber.getValue());

        // Act - type with leading zeros (setValue(000123) would be parsed as a JS number)
        WebElement input = inputNumber.getInput();
        input.clear();
        ComponentUtils.sendKeys(input, "000123");
        input.sendKeys(Keys.TAB);
        page.button.click();

        // Assert
        assertEquals("000123", inputNumber.getValue());
        assertEquals("000123", inputNumber.getHiddenInput().getDomProperty("value"));
        assertEquals("000123", page.output.getText());
        assertConfiguration(inputNumber.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("InputNumber: leadingZero=keep preserves leading zeros when set via widget API")
    void widgetSetValuePreservesLeadingZeros(Page page) {
        // Arrange
        InputNumber inputNumber = page.inputnumber;

        // Act
        PrimeSelenium.executeScript(inputNumber.getWidgetByIdScript() + ".setValue('000456')");
        page.button.click();

        // Assert
        assertEquals("000456", inputNumber.getValue());
        assertEquals("000456", inputNumber.getHiddenInput().getDomProperty("value"));
        assertEquals("000456", page.output.getText());
        assertConfiguration(inputNumber.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputNumber Config = " + cfg);
        assertEquals("keep", cfg.getString("leadingZero"));
        assertEquals(0, cfg.get("decimalPlaces"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputnumber")
        InputNumber inputnumber;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:output")
        WebElement output;

        @Override
        public String getLocation() {
            return "inputnumber/inputNumber009.xhtml";
        }
    }
}
