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
package org.primefaces.integrationtests.spinner;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Spinner;
import org.primefaces.selenium.component.base.ComponentUtils;

public class Spinner001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Spinner: Test integer increment by 1")
    public void testSpinUp(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("", spinner.getValue());

        // Act
        spinner.increment();
        page.button.click();

        // Assert
        Assertions.assertEquals("1", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Spinner: Test integer decrement by 2")
    public void testSpinDown(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("", spinner.getValue());

        // Act
        spinner.decrement();
        spinner.decrement();
        page.button.click();

        // Assert
        Assertions.assertEquals("-2", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Spinner: GitHub #5579 Test integer invalid characters should be ignored")
    public void testInvalidCharacter(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("", spinner.getValue());

        // Act
        ComponentUtils.sendKeys(spinner.getInput(), "abc");
        page.button.click();

        // Assert
        Assertions.assertEquals("", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Spinner: GitHub #5579 Test integer should not allow decimal separator")
    public void testDecimalSeparator(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "3.4");
        page.button.click();

        // Assert
        Assertions.assertEquals("34", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("Spinner: GitHub #5579 Test integer should allow thousand separator")
    public void testThousandSeparator(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "1,456");
        page.button.click();

        // Assert
        Assertions.assertEquals("1,456", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Spinner Config = " + cfg);
        Assertions.assertEquals(1, cfg.getInt("step"));
        Assertions.assertEquals(".", cfg.get("decimalSeparator"));
        Assertions.assertEquals(",", cfg.get("thousandSeparator"));
        if (cfg.has("decimalPlaces")) {
            Assertions.assertEquals("0", cfg.get("decimalPlaces"));
        }
        if (cfg.has("precision")) {
            Assertions.assertEquals(0, cfg.getInt("precision"));
        }
    }

    public void sendKeys(Spinner spinner, CharSequence value) {
        WebElement input = spinner.getInput();
        ComponentUtils.sendKeys(input, value);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:integer")
        Spinner spinner;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "spinner/spinner001.xhtml";
        }
    }
}
