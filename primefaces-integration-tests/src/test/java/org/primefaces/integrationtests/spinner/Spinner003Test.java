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
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Spinner;

public class Spinner003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Spinner: Test starting at 0 and decrementing should rotate to max")
    public void testMinRotateAjax(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("0", spinner.getValue());

        // Act
        PrimeSelenium.guardAjax(spinner.getButtonDown()).click();

        // Assert
        Assertions.assertEquals("2", spinner.getValue());
        assertOutputLabel(page, "2");
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Spinner: Test starting at 0 and incrementing past max should rotate to min")
    public void testMaxRotateAjax(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("0", spinner.getValue());

        // Act
        PrimeSelenium.guardAjax(spinner.getButtonUp()).click();
        assertOutputLabel(page, "1");
        PrimeSelenium.guardAjax(spinner.getButtonUp()).click();
        assertOutputLabel(page, "2");
        PrimeSelenium.guardAjax(spinner.getButtonUp()).click();

        // Assert
        Assertions.assertEquals("0", spinner.getValue());
        assertOutputLabel(page, "0");
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Spinner: AJAX setting value with widget method")
    public void testAjaxSetValue(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("0", spinner.getValue());

        // Act
        spinner.setValue("4");
        spinner.change();

        // Assert
        Assertions.assertEquals("4", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Spinner: AJAX Test integer increment by 1")
    public void testAjaxSpinUp(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("0", spinner.getValue());

        // Act
        spinner.increment();
        spinner.change();

        // Assert
        Assertions.assertEquals("1", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("Spinner: AJAX Test integer decrement by 2")
    public void testSpinAjaxDown(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        Assertions.assertEquals("0", spinner.getValue());

        // Act
        spinner.setValue("6");
        spinner.decrement();
        spinner.decrement();
        spinner.change();

        // Assert
        Assertions.assertEquals("2", spinner.getValue());
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

    private void assertOutputLabel(Page page, String value) {
        Assertions.assertEquals(value, page.output.getText());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:integer")
        Spinner spinner;

        @FindBy(id = "form:ajaxSpinnerValue")
        WebElement output;

        @Override
        public String getLocation() {
            return "spinner/spinner003.xhtml";
        }
    }
}
