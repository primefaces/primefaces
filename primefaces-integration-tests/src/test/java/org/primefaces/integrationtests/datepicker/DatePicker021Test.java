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
package org.primefaces.integrationtests.datepicker;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.time.temporal.ChronoUnit;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatePicker021Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: GitHub #5551 minTime / maxTime as LocalTime and string are rendered as ISO time")
    void configuration(Page page) {
        // Assert
        assertConfiguration(page.dateTime.getClientConfiguration(), "09:00", "17:00");
        assertConfiguration(page.earlyDateTime.getClientConfiguration(), "09:00", "17:00");
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: GitHub #5551 the time picker does not step beyond maxTime")
    void stepBeyondMaxTime(Page page) {
        // Arrange
        page.dateTime.showPanel();

        // Act
        page.dateTime.incrementTime(ChronoUnit.HOURS);

        // Assert
        assertEquals("08/20/2020 16:30", page.dateTime.getInputValue());

        // Act
        page.dateTime.decrementTime(ChronoUnit.HOURS);

        // Assert
        assertEquals("08/20/2020 15:30", page.dateTime.getInputValue());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("DatePicker: GitHub #5551 selecting a day moves a time before minTime to minTime on every day")
    void selectDayClampsTime(Page page) {
        // Arrange: 07:00 is before minTime, stepping towards it is allowed
        page.earlyDateTime.showPanel();
        page.earlyDateTime.incrementTime(ChronoUnit.HOURS);
        assertEquals("08/20/2020 08:00", page.earlyDateTime.getInputValue());

        // Act
        page.earlyDateTime.selectDay(21);
        page.earlyDateTimeButton.click();

        // Assert
        assertFalse(page.messages.isDisplayed());
        assertEquals("2020-08-21T09:00", page.earlyDateTimeValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("DatePicker: GitHub #5551 a typed time after maxTime fails the validation")
    void typedTimeValidated(Page page) {
        // Act
        page.dateTime.clear();
        ComponentUtils.sendKeys(page.dateTime.getInput(), "08/21/2020 18:00");
        page.dateTimeButton.click();

        // Assert
        assertTrue(page.messages.isDisplayed());
        assertEquals(1, page.messages.getAllMessages().size());
        assertEquals("2020-08-20T16:30", page.dateTimeValue.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("DatePicker: GitHub #5551 without a value the time picker starts at minTime")
    void emptyStartsAtMinTime(Page page) {
        // Act
        page.emptyDateTime.showPanel();

        // Assert
        assertEquals(9, page.emptyDateTime.getHour());
        assertEquals(0, page.emptyDateTime.getMinute());
        assertEquals("", page.emptyDateTime.getInputValue());
        assertNoJavascriptErrors();
    }

    private void assertConfiguration(JSONObject cfg, String minTime, String maxTime) {
        assertNoJavascriptErrors();
        assertEquals(minTime, cfg.getString("minTime"));
        assertEquals(maxTime, cfg.getString("maxTime"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:dateTime")
        DatePicker dateTime;

        @FindBy(id = "form:dateTimeButton")
        CommandButton dateTimeButton;

        @FindBy(id = "form:dateTimeValue")
        WebElement dateTimeValue;

        @FindBy(id = "form:earlyDateTime")
        DatePicker earlyDateTime;

        @FindBy(id = "form:earlyDateTimeButton")
        CommandButton earlyDateTimeButton;

        @FindBy(id = "form:earlyDateTimeValue")
        WebElement earlyDateTimeValue;

        @FindBy(id = "form:emptyDateTime")
        DatePicker emptyDateTime;

        @Override
        public String getLocation() {
            return "datepicker/datePicker021.xhtml";
        }
    }
}
