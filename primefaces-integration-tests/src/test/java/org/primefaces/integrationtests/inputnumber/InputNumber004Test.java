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

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputNumber;

public class InputNumber004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputNumber: GitHub #6590 Integer bean validation constraints @Positive still ensure decimalPlaces='0'")
    public void testIntegerNoDecimalPlaces(final Page page) {
        // Arrange
        InputNumber inputNumber = page.integer;
        Assertions.assertEquals("66", inputNumber.getValue());

        // Act
        inputNumber.setValue("87.31");
        page.button.click();

        // Assert
        Assertions.assertEquals("87", inputNumber.getValue());
        assertConfiguration(inputNumber.getWidgetConfiguration(), "0", "0", "999999");
    }

    @Test
    @Order(2)
    @DisplayName("InputNumber: GitHub #6590 Integer bean validation constraints @Positive doesn't accept negative number")
    public void testIntegerPositiveConstraint(final Page page) {
        // Arrange
        InputNumber inputNumber = page.integer;
        Assertions.assertEquals("66", inputNumber.getValue());

        // Act
        try {
            inputNumber.setValue("-5");
            Assertions.fail("Should be blocked by AutoNumeric javascript.");
        }
        catch (JavascriptException ex) {
            // Assert
            Assertions.assertEquals(
                        "The value [-5] being set falls outside of the minimumValue [0] and maximumValue [999999] range set for this element",
                        StringUtils.substringBetween(ex.getMessage(), ": ", "\n"));
        }
    }

    @Test
    @Order(3)
    @DisplayName("InputNumber: GitHub #6590 Integer bean validation constraints @Max doesn't accept higher than max value")
    public void testIntegerMaxConstraint(final Page page) {
        // Arrange
        InputNumber inputNumber = page.integer;
        Assertions.assertEquals("66", inputNumber.getValue());

        // Act
        try {
            inputNumber.setValue("23999999");
            Assertions.fail("Should be blocked by AutoNumeric javascript.");
        }
        catch (JavascriptException ex) {
            // Assert
            Assertions.assertEquals(
                        "The value [23999999] being set falls outside of the minimumValue [0] and maximumValue [999999] range set for this element",
                        StringUtils.substringBetween(ex.getMessage(), ": ", "\n"));
        }
    }

    @Test
    @Order(4)
    @DisplayName("InputNumber: GitHub #6590 Integer bean validation constraints @Positive removing value resets component")
    public void testIntegerRemovingValue(final Page page) {
        // Arrange
        InputNumber inputNumber = page.integer;
        Assertions.assertEquals("66", inputNumber.getValue());

        // Act
        inputNumber.setValue("3");
        inputNumber.getInput().sendKeys(Keys.BACK_SPACE);
        inputNumber.getInput().sendKeys(Keys.DELETE);
        Assertions.assertEquals("", inputNumber.getValue());
        page.button.click();

        // Assert
        Assertions.assertEquals("", inputNumber.getValue());

        // NOTE: because its now NULL the default decimal places is set back to 2 because its no longer an Integer
        assertConfiguration(inputNumber.getWidgetConfiguration(), "2", "0.0000001", "999999");
    }

    @Test
    @Order(5)
    @DisplayName("InputNumber: GitHub #6590 Decimal bean validation constraints @Positive still ensure decimalPlaces='2'")
    public void testDecimal(final Page page) {
        // Arrange
        InputNumber inputNumber = page.decimal;
        Assertions.assertEquals("6.78", inputNumber.getValue());

        // Act
        inputNumber.setValue("31.9");
        page.button.click();

        // Assert
        Assertions.assertEquals("31.90", inputNumber.getValue());
        assertConfiguration(inputNumber.getWidgetConfiguration(), "2", "0.0000001", "999999.99");
    }

    @Test
    @Order(6)
    @DisplayName("InputNumber: GitHub #6590 Decimal bean validation constraints @Positive doesn't accept negative number")
    public void testDecimalPositiveConstraint(final Page page) {
        // Arrange
        InputNumber inputNumber = page.decimal;
        Assertions.assertEquals("6.78", inputNumber.getValue());

        // Act
        try {
            inputNumber.setValue("-8.23");
            Assertions.fail("Should be blocked by AutoNumeric javascript.");
        }
        catch (JavascriptException ex) {
            // Assert
            Assertions.assertEquals(
                        "The value [-8.23] being set falls outside of the minimumValue [0.0000001] and maximumValue [999999.99] range set for this element",
                        StringUtils.substringBetween(ex.getMessage(), ": ", "\n"));
        }
    }

    @Test
    @Order(7)
    @DisplayName("InputNumber: GitHub #6590 Decimal bean validation constraints @Max doesn't accept higher than max value")
    public void testDecimalMaxConstraint(final Page page) {
        // Arrange
        InputNumber inputNumber = page.decimal;
        Assertions.assertEquals("6.78", inputNumber.getValue());

        // Act
        try {
            inputNumber.setValue("4599999999999");
            Assertions.fail("Should be blocked by AutoNumeric javascript.");
        }
        catch (JavascriptException ex) {
            // Assert
            Assertions.assertEquals(
                        "The value [4599999999999] being set falls outside of the minimumValue [0.0000001] and maximumValue [999999.99] range set for this element",
                        StringUtils.substringBetween(ex.getMessage(), ": ", "\n"));
        }
    }

    private void assertConfiguration(JSONObject cfg, String decimalPlaces, String minValue, String maxValue) {
        assertNoJavascriptErrors();
        System.out.println("InputNumber Config = " + cfg);
        Assertions.assertEquals(decimalPlaces, cfg.get("decimalPlaces"));
        Assertions.assertEquals(minValue, cfg.get("minimumValue"));
        Assertions.assertEquals(maxValue, cfg.get("maximumValue"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:integer")
        InputNumber integer;

        @FindBy(id = "form:decimal")
        InputNumber decimal;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputnumber/inputNumber004.xhtml";
        }
    }
}
