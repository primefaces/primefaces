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

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DatePicker003Test extends AbstractDatePickerTest {

    @Test
    @DisplayName("DatePicker: minDate and maxDate; days outside range should be disabled")
    void minMax(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        datePicker.click(); // focus to bring up panel

        // Assert (all days of august active)
        WebElement panel = datePicker.getPanel();
        assertNotNull(panel);
        assertEquals(31, datePicker.getSelectableDays().size());

        // Act - previous month
        datePicker.getPreviousMonthLink().click();

        // Assert (July 1-19 before minDate; the 4 days of June / August filling the weeks are disabled as other months
        // are not selectable)
        assertEquals(19, datePicker.getDisabledDays().size());
        assertEquals(12, datePicker.getSelectableDays().size());
        assertEquals(4, datePicker.getOtherMonthDays().size());
        assertEquals(4, datePicker.getDisabledOtherMonthDays().size());

        // Act - next month
        datePicker.getNextMonthLink().click();
        datePicker.getNextMonthLink().click();

        // Assert (September 21-30 after maxDate, the 5 days of August / October are disabled too)
        assertEquals(10, datePicker.getDisabledDays().size());
        assertEquals(20, datePicker.getSelectableDays().size());
        assertEquals(5, datePicker.getOtherMonthDays().size());
        assertEquals(5, datePicker.getDisabledOtherMonthDays().size());

        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        assertEquals("mm/dd/yy", cfg.getString("dateFormat"));
        assertEquals("single", cfg.getString("selectionMode"));
        assertFalse(cfg.getBoolean("inline"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker")
        DatePicker datePicker;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "datepicker/datePicker003.xhtml";
        }
    }
}
