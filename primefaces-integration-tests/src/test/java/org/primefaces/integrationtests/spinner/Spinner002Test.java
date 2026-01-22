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
package org.primefaces.integrationtests.spinner;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Spinner;
import org.primefaces.selenium.component.base.ComponentUtils;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Spinner002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Spinner: Test decimal increment by 0.2")
    void spinUp(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        spinner.increment();
        spinner.increment();
        page.button.click();

        // Assert
        assertEquals("0.20", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Spinner: Test decimal decrement by 0.3")
    void spinDown(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        spinner.setValue("4.7");
        spinner.decrement();
        spinner.decrement();
        spinner.decrement();
        page.button.click();

        // Assert
        assertEquals("4.40", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Spinner: GitHub #5579 Test decimal invalid characters should be ignored")
    void invalidCharacter(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "abc");
        page.button.click();

        // Assert
        assertEquals("", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Spinner: GitHub #5579 Test decimal should allow decimal separator")
    void decimalSeparator(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "3.4");
        page.button.click();

        // Assert
        assertEquals("3.40", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("Spinner: GitHub #5579 Test decimal should allow thousand separator")
    void thousandSeparator(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "1,456.89");
        page.button.click();

        // Assert
        assertEquals("1,456.89", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("Spinner: Negative number not allowed when min = 0")
    void negativeNumber(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "-2.8");
        page.button.click();

        // Assert
        assertEquals("2.80", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("Spinner: GitHub #9442 Test decimal increment by 0.7")
    void spinUp7(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        spinner.increment();
        spinner.increment();
        spinner.increment();
        spinner.increment();
        spinner.increment();
        spinner.increment();
        spinner.increment();
        page.button.click();

        // Assert
        assertEquals("0.70", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Spinner Config = " + cfg);
        assertEquals(0.1, cfg.getDouble("step"));
        assertEquals(".", cfg.get("decimalSeparator"));
        assertEquals(",", cfg.get("thousandSeparator"));
        if (cfg.has("decimalPlaces")) {
            assertEquals("2", cfg.get("decimalPlaces"));
        }
        if (cfg.has("precision")) {
            assertEquals(2, cfg.getInt("precision"));
        }
    }

    public void sendKeys(Spinner spinner, CharSequence value) {
        WebElement input = spinner.getInput();
        ComponentUtils.sendKeys(input, value);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:decimal")
        Spinner spinner;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "spinner/spinner002.xhtml";
        }
    }
}
