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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatePicker019Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: builtInValidation=false accepts dates outside mindate / maxdate, disabled dates and disabled days")
    void restrictionsSkipped(Page page) {
        submit(page, page.date, "07/01/2020", page.dateButton);
        assertValid(page, page.dateValue, "2020-07-01");

        submit(page, page.date, "09/30/2020", page.dateButton);
        assertValid(page, page.dateValue, "2020-09-30");

        // disabled date
        submit(page, page.date, "08/20/2020", page.dateButton);
        assertValid(page, page.dateValue, "2020-08-20");

        // disabled days (Wednesday, Sunday)
        submit(page, page.date, "08/19/2020", page.dateButton);
        assertValid(page, page.dateValue, "2020-08-19");

        submit(page, page.date, "08/16/2020", page.dateButton);
        assertValid(page, page.dateValue, "2020-08-16");
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: builtInValidation defaults to true and rejects dates outside mindate / maxdate")
    void restrictionsValidated(Page page) {
        submit(page, page.validatedDate, "07/01/2020", page.validatedDateButton);
        assertInvalid(page, page.validatedDateValue, "2020-08-12");

        submit(page, page.validatedDate, "08/20/2020", page.validatedDateButton);
        assertInvalid(page, page.validatedDateValue, "2020-08-12");

        // disabled days: Wednesday and Sunday (0, which is 7 in java.time)
        submit(page, page.validatedDate, "08/19/2020", page.validatedDateButton);
        assertInvalid(page, page.validatedDateValue, "2020-08-12");

        submit(page, page.validatedDate, "08/16/2020", page.validatedDateButton);
        assertInvalid(page, page.validatedDateValue, "2020-08-12");

        submit(page, page.validatedDate, "08/14/2020", page.validatedDateButton);
        assertValid(page, page.validatedDateValue, "2020-08-14");
    }

    @Test
    @Order(3)
    @DisplayName("DatePicker: builtInValidation=false accepts dates which are not in enabledDates")
    void enabledDatesSkipped(Page page) {
        submit(page, page.enabledDate, "08/21/2020", page.enabledDateButton);
        assertValid(page, page.enabledDateValue, "2020-08-21");
    }

    @Test
    @Order(4)
    @DisplayName("DatePicker: builtInValidation=false accepts a range outside mindate / maxdate with the start after the end")
    void rangeSkipped(Page page) {
        submit(page, page.range, "09/30/2020 - 07/01/2020", page.rangeButton);
        assertValid(page, page.rangeValue, "[2020-09-30, 2020-07-01]");
    }

    @Test
    @Order(5)
    @DisplayName("DatePicker: builtInValidation=false accepts a time before the time part of mindate")
    void timeSkipped(Page page) {
        submit(page, page.dateTime, "08/20/2020 08:00", page.dateTimeButton);
        assertValid(page, page.dateTimeValue, "2020-08-20T08:00");
    }

    @Test
    @Order(6)
    @DisplayName("DatePicker: builtInValidation=false still executes required and the converter")
    void requiredAndConverterExecuted(Page page) {
        submit(page, page.requiredDate, "", page.requiredDateButton);
        assertInvalid(page, page.requiredDateValue, "2020-08-12");

        submit(page, page.requiredDate, "02/32/2020", page.requiredDateButton);
        assertInvalid(page, page.requiredDateValue, "2020-08-12");
        assertTrue(page.messages.getMessage(0).getDetail().contains("could not be understood as a date"));

        submit(page, page.requiredDate, "07/01/2020", page.requiredDateButton);
        assertValid(page, page.requiredDateValue, "2020-07-01");
    }

    @Test
    @Order(7)
    @DisplayName("DatePicker: week view accepts a week starting with a disabled day like the popup")
    void weekViewDisabledDays(Page page) {
        page.week.showPanel();
        page.week.selectDay(13);
        page.weekButton.click();
        // the week Sunday 9 - Saturday 15, Sunday is disabled
        assertValid(page, page.weekValue, "[2020-08-09, 2020-08-15]");
    }

    @Test
    @Order(8)
    @DisplayName("DatePicker: month view accepts a month starting with a disabled day like the popup")
    void monthViewDisabledDays(Page page) {
        // November 1, 2020 is a Sunday
        submit(page, page.month, "11/2020", page.monthButton);
        assertValid(page, page.monthValue, "2020-11");
    }

    private void submit(Page page, DatePicker datePicker, String value, CommandButton button) {
        datePicker.clear();
        if (!value.isEmpty()) {
            ComponentUtils.sendKeys(datePicker.getInput(), value);
        }
        button.click();
    }

    private void assertValid(Page page, WebElement output, String value) {
        assertFalse(page.messages.isDisplayed());
        assertEquals(value, output.getText());
        assertNoJavascriptErrors();
    }

    private void assertInvalid(Page page, WebElement output, String value) {
        assertTrue(page.messages.isDisplayed());
        assertEquals(1, page.messages.getAllMessages().size());
        assertEquals(value, output.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:date")
        DatePicker date;

        @FindBy(id = "form:dateButton")
        CommandButton dateButton;

        @FindBy(id = "form:dateValue")
        WebElement dateValue;

        @FindBy(id = "form:validatedDate")
        DatePicker validatedDate;

        @FindBy(id = "form:validatedDateButton")
        CommandButton validatedDateButton;

        @FindBy(id = "form:validatedDateValue")
        WebElement validatedDateValue;

        @FindBy(id = "form:enabledDate")
        DatePicker enabledDate;

        @FindBy(id = "form:enabledDateButton")
        CommandButton enabledDateButton;

        @FindBy(id = "form:enabledDateValue")
        WebElement enabledDateValue;

        @FindBy(id = "form:range")
        DatePicker range;

        @FindBy(id = "form:rangeButton")
        CommandButton rangeButton;

        @FindBy(id = "form:rangeValue")
        WebElement rangeValue;

        @FindBy(id = "form:dateTime")
        DatePicker dateTime;

        @FindBy(id = "form:dateTimeButton")
        CommandButton dateTimeButton;

        @FindBy(id = "form:dateTimeValue")
        WebElement dateTimeValue;

        @FindBy(id = "form:requiredDate")
        DatePicker requiredDate;

        @FindBy(id = "form:requiredDateButton")
        CommandButton requiredDateButton;

        @FindBy(id = "form:requiredDateValue")
        WebElement requiredDateValue;

        @FindBy(id = "form:week")
        DatePicker week;

        @FindBy(id = "form:weekButton")
        CommandButton weekButton;

        @FindBy(id = "form:weekValue")
        WebElement weekValue;

        @FindBy(id = "form:month")
        DatePicker month;

        @FindBy(id = "form:monthButton")
        CommandButton monthButton;

        @FindBy(id = "form:monthValue")
        WebElement monthValue;

        @Override
        public String getLocation() {
            return "datepicker/datePicker019.xhtml";
        }
    }
}
