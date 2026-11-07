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

import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DatePicker;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractDatePickerTest extends AbstractPrimePageTest {

    protected void assertDate(DatePicker datePicker, String month, String year) {
        WebElement panel = datePicker.getPanel();
        assertNotNull(panel);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(panel));
        assertEquals(month.toUpperCase(), datePicker.getTitleMonth().getText().toUpperCase(Locale.ROOT));
        assertEquals(year, datePicker.getTitleYear().getText());
    }

    protected void assertTime(DatePicker datePicker, String hours, String minutes, String seconds) {
        assertTime(datePicker, hours, minutes, seconds, null);
    }

    protected void assertTime(DatePicker datePicker, String hours, String minutes, String seconds, String milliseconds) {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(datePicker.getPanel()));
        if (hours != null) {
            assertEquals(Integer.parseInt(hours), datePicker.getHour());
        }
        if (minutes != null) {
            assertEquals(Integer.parseInt(minutes), datePicker.getMinute());
        }
        if (seconds != null) {
            assertEquals(Integer.parseInt(seconds), datePicker.getSecond());
        }
        if (milliseconds != null) {
            assertEquals(Integer.parseInt(milliseconds), datePicker.getMillisecond());
        }
    }

    /**
     * Gets the days of the displayed month with the style class, e.g. from the date metadata model.
     */
    protected List<WebElement> getDaysWithStyleClass(DatePicker datePicker, String styleClass) {
        return datePicker.getDays().stream()
                    .filter(day -> PrimeSelenium.hasCssClass(day, styleClass))
                    .collect(Collectors.toList());
    }

    /**
     * Asserts that the day is the only day of the displayed month with the style class.
     */
    protected void assertDayStyleClass(DatePicker datePicker, String styleClass, String day) {
        List<WebElement> days = getDaysWithStyleClass(datePicker, styleClass);
        assertEquals(1, days.size(), days.toString());
        assertEquals(day, days.get(0).getText());
    }

    /**
     * Asserts that the day is the only day of the displayed month with the style class and that it is disabled.
     */
    protected void assertDisabledDayStyleClass(DatePicker datePicker, String styleClass, String day) {
        assertDayStyleClass(datePicker, styleClass, day);
        assertTrue(datePicker.isDayDisabled(day));
    }

    /**
     * Asserts that no day of the displayed month has the style class.
     */
    protected void assertNoDayStyleClass(DatePicker datePicker, String styleClass) {
        List<WebElement> days = getDaysWithStyleClass(datePicker, styleClass);
        assertTrue(days.isEmpty(), days.toString());
    }
}
