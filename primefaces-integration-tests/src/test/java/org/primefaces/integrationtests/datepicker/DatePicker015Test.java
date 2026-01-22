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
package org.primefaces.integrationtests.datepicker;


import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.Messages;
import org.primefaces.util.Constants;

import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatePicker015Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: German AM / PM")
    void germanAmPm(Page page) {
        // Arrange
        DatePicker datePicker = page.german;
        LocalDateTime expected = LocalDateTime.of(2020, 2, 10, 1, 16, 0);
        String expectedString = "02/10/2020 01:16 ";
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.GERMANY);
        String[] ampm = symbols.getAmPmStrings();
        String am = ampm[0];
        String pm = ampm[1];

        // Assert
        assertEquals(expected, datePicker.getValue());
        assertDateEquals(expectedString + am, datePicker.getInput().getAttribute("value"));

        // Act
        WebElement panel = datePicker.showPanel();
        assertAmPm(panel, am);
        toggleAmPm(panel);
        datePicker.hidePanel();
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertDateEquals(expectedString + pm, datePicker.getInput().getAttribute("value"));
        assertDateEquals(expectedString + pm, page.messages.getMessage(0).getDetail());
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: Spanish AM / PM")
    void spanishAmPm(Page page) {
        // Arrange
        DatePicker datePicker = page.spanish;
        LocalDateTime expected = LocalDateTime.of(2021, 4, 13, 5, 21, 0);
        String expectedString = "04/13/2021 05:21 ";
        DateFormatSymbols symbols = new DateFormatSymbols(new Locale("es"));
        String[] ampm = symbols.getAmPmStrings();
        String am = ampm[0];
        String pm = ampm[1];

        // Assert
        assertEquals(expected, datePicker.getValue());
        assertDateEquals(expectedString + am, datePicker.getInput().getAttribute("value"));

        // Act
        WebElement panel = datePicker.showPanel();
        assertAmPm(panel, am);
        toggleAmPm(panel);
        datePicker.hidePanel();
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertDateEquals(expectedString + pm, datePicker.getInput().getAttribute("value"));
        assertDateEquals(expectedString + pm, page.messages.getMessage(1).getDetail());
    }

    @Test
    @Order(3)
    @DisplayName("DatePicker: English AM / PM Defaults")
    void englishAmPm(Page page) {
        // Arrange
        DatePicker datePicker = page.english;
        LocalDateTime expected = LocalDateTime.of(2022, 5, 30, 17, 07, 0);
        String expectedString = "05/30/2022 05:07 ";
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.ENGLISH);
        String[] ampm = symbols.getAmPmStrings();
        String am = ampm[0];
        String pm = ampm[1];

        // Assert
        assertEquals(expected, datePicker.getValue());
        assertDateEquals(expectedString + pm, datePicker.getInput().getAttribute("value"));

        // Act
        WebElement panel = datePicker.showPanel();
        assertAmPm(panel, pm);
        toggleAmPm(panel);
        datePicker.hidePanel();
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertDateEquals(expectedString + am, datePicker.getInput().getAttribute("value"));
        assertDateEquals(expectedString + am, page.messages.getMessage(2).getDetail());
    }

    protected void assertAmPm(WebElement panel, String ampm) {
        assertNotNull(panel);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(panel));
        WebElement timePicker = panel.findElement(By.className("ui-timepicker"));
        assertDateEquals(ampm, timePicker.findElement(By.cssSelector("div.ui-ampm-picker > span")).getText());
    }

    protected void toggleAmPm(WebElement panel) {
        assertNotNull(panel);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(panel));
        WebElement amPmPicker = panel.findElement(By.className("ui-ampm-picker"));
        amPmPicker.findElement(By.className("ui-picker-down")).click();
    }

    protected void assertDateEquals(String expected, String actual) {
        // between JDK8 and 11 some space characters became non breaking space '\u00A0'
        expected = expected.replaceAll("\\p{Z}", Constants.SPACE);
        actual = actual.replaceAll("\\p{Z}", Constants.SPACE);
        assertEquals(expected, actual);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:german")
        DatePicker german;

        @FindBy(id = "form:spanish")
        DatePicker spanish;

        @FindBy(id = "form:english")
        DatePicker english;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "datepicker/datePicker015.xhtml";
        }
    }
}
