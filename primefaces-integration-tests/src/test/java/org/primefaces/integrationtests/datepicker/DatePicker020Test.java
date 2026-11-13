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
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.SelectOneMenu;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatePicker020Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: GitHub #14137 mask fires only one change request when selecting a date and leaving the input")
    void maskSingleChangeRequest(Page page) {
        assertSingleChangeRequest(page, page.date, page.dateCount);
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: GitHub #14137 mask fires only one change request when selecting a date and leaving the input, updating the form")
    void maskSingleChangeRequestFormUpdate(Page page) {
        assertSingleChangeRequest(page, page.dateForm, page.dateFormCount);
    }

    private void assertSingleChangeRequest(Page page, DatePicker datePicker, WebElement count) {
        // Arrange
        datePicker.focus();

        // Act
        datePicker.selectDay(13);
        assertEquals("08/13/2020", datePicker.getInputValue());
        assertEquals("1", count.getText());

        // leave the date picker by clicking the select one menu (blurs the input), then select an item, whose
        // guarded request completes after any request queued by the blur
        page.menu.click();
        page.menu.select("Option 2");

        // Assert
        assertEquals("Option 2", page.menu.getSelectedLabel());
        assertEquals("08/13/2020", datePicker.getInputValue());
        assertEquals("1", count.getText());
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        assertTrue(cfg.has("mask"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:date")
        DatePicker date;

        @FindBy(id = "form:dateCount")
        WebElement dateCount;

        @FindBy(id = "form:dateForm")
        DatePicker dateForm;

        @FindBy(id = "form:dateFormCount")
        WebElement dateFormCount;

        @FindBy(id = "form:menu")
        SelectOneMenu menu;

        @Override
        public String getLocation() {
            return "datepicker/datePicker020.xhtml";
        }
    }
}
