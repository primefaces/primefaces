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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DatePicker009Test extends AbstractDatePickerTest {

    private static final List<String> MONTHS = Arrays.asList(new String[] {
        "January", "February", "March", "April", "May", "June", "July",
        "August", "September", "October", "November", "December"
    });

    @Test
    @Order(1)
    @DisplayName("DatePicker: numberOfMonths greater than 12. See GitHub #7563")
    void withShowNumberOfMonths(Page page) throws Exception {
        // Arrange
        DatePicker datePicker = page.datePicker;
        assertEquals(LocalDateTime.of(2020, 11, 01, 00, 00, 00), datePicker.getValue());

        // Act
        datePicker.click(); // focus to bring up panel

        // Assert
        assertNoJavascriptErrors();
        WebElement panel = datePicker.getPanel();
        assertNotNull(panel);
        List<WebElement> elements = datePicker.getDisplayedMonths();
        assertNotNull(elements);
        assertEquals(27, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            WebElement title = datePicker.getTitleMonth(i);
            assertNotNull(title);
            String monthName = title.getText();
            assertTrue(MONTHS.contains(monthName), "unknown month `" + monthName + "´");
        }

        // the days of all 27 months from November 2020 to January 2023, the value is the 1st of the first month
        assertEquals(30 + 31 + 365 + 365 + 31, datePicker.getDays().size());
        assertTrue(datePicker.isDaySelected(1));
        assertTrue(datePicker.isDaySelected(0, 1));
        assertFalse(datePicker.isDaySelected(1, 1));
        assertEquals(1, datePicker.getSelectedDays().size());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker")
        DatePicker datePicker;

        @Override
        public String getLocation() {
            return "datepicker/datePicker009.xhtml";
        }
    }
}
