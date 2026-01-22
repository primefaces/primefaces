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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Spinner005Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Spinner: Test round to the nearest stepFactor")
    void round(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "12");
        page.button.click();

        // Assert
        assertEquals("15", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(1)
    @DisplayName("Spinner: Test round already on stepFactor does not modify value")
    void roundNoChange(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "20");
        page.button.click();

        // Assert
        assertEquals("20", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Spinner: Test entering above max value rounds to max")
    void roundAboveMax(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "1350");
        page.button.click();

        // Assert
        assertEquals("1200", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Spinner: #11660 Rounding above 1000 does not cause NaN")
    void roundAboveOntThousand(Page page) {
        // Arrange
        Spinner spinner = page.spinner;
        assertEquals("", spinner.getValue());

        // Act
        sendKeys(spinner, "995");
        spinner.increment();
        spinner.increment();
        page.button.click();

        // Assert
        assertEquals("1005", spinner.getValue());
        assertConfiguration(spinner.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Spinner Config = " + cfg);
        assertEquals(5, cfg.getInt("step"));
        assertEquals(".", cfg.get("decimalSeparator"));
        assertEquals(",", cfg.get("thousandSeparator"));
        assertEquals(0, cfg.getInt("precision"));
        assertEquals("0", cfg.get("decimalPlaces"));
        assertTrue(cfg.getBoolean("round"));
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
            return "spinner/spinner005.xhtml";
        }
    }
}
