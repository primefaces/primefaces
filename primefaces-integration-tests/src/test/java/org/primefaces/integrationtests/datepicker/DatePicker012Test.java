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
import org.primefaces.selenium.component.DatePicker;

import java.time.LocalDate;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DatePicker012Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: refresh lazy meta data model on show panel; see #7457")
    void refreshLazyModel(Page page) {
        // Assert initial state
        assertNull(page.datePicker1.getValue());
        assertNull(page.datePicker2.getValue());

        // Act - 1st show panel
        page.datePicker1.click();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.datePicker1.getPanel()));
        assertNoDisablebCalendarDates(page.datePicker1);

        // Act - 2nd show other panel
        page.datePicker1.hidePanel(); // #12009 - close panel of datePicker1 to ensure it does not overlap datePicker2-input-field
        page.datePicker2.click();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.datePicker2.getPanel()));
        assertNoDisablebCalendarDates(page.datePicker2);

        // Act - 3rd show panel and select 5th of current month
        page.datePicker2.hidePanel(); // #12009 - close panel of datePicker2 to ensure it does not overlap datePicker1-input-field
        page.datePicker1.click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.datePicker1.getPanel()));
        page.datePicker1.getPanel().findElement(By.linkText("5")).click();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.datePicker1));
        assertEquals(LocalDate.now().withDayOfMonth(5).atStartOfDay(), page.datePicker1.getValue());

        // Act - 4th show other panel
        page.datePicker1.hidePanel(); // #12009 - close panel of datePicker1 to ensure it does not overlap datePicker2-input-field
        page.datePicker2.click();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.datePicker2.getPanel()));
        assertDisabledCalendarDate(page.datePicker2, "5");
        JSONObject cfg2 = page.datePicker2.getWidgetConfiguration();

        // Act - 5th elect 6th of current month
        page.datePicker2.getPanel().findElement(By.linkText("6")).click();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.datePicker2));
        assertEquals(LocalDate.now().withDayOfMonth(6).atStartOfDay(), page.datePicker2.getValue());

        // Act - 6th show panel
        page.datePicker2.hidePanel(); // #12009 - close panel of datePicker2 to ensure it does not overlap datePicker1-input-field
        page.datePicker1.click();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(page.datePicker1.getPanel()));
        assertDisabledCalendarDate(page.datePicker1, "6");
        JSONObject cfg1 = page.datePicker2.getWidgetConfiguration();

        assertConfiguration(cfg1);
        assertConfiguration(cfg2);
    }

    private void assertNoDisablebCalendarDates(DatePicker datePicker) {
        List<WebElement> days = datePicker.getPanel().findElements(
                    By.cssSelector(".ui-datepicker-calendar td:not(.ui-datepicker-other-month) .tst-disabled"));
        assertTrue(days.isEmpty(), days.toString());
    }

    private void assertDisabledCalendarDate(DatePicker datePicker, String day) {
        List<WebElement> days = datePicker.getPanel().findElements(
                    By.cssSelector(".ui-datepicker-calendar td:not(.ui-datepicker-other-month) .tst-disabled"));
        assertEquals(1, days.size(), days.toString());
        assertEquals(day, days.get(0).getText());
        assertTrue(days.get(0).getAttribute("class").contains("ui-state-disabled"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        assertFalse(cfg.getBoolean("inline"));
        assertTrue(cfg.getJSONObject("behaviors").getString("dateSelect").contains("dateSelect"),
                    "missing behaviour dateSelect");
        assertTrue(cfg.getJSONObject("behaviors").getString("viewChange").contains("viewChange"),
                    "missing behaviour viewChange");
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker1")
        DatePicker datePicker1;
        @FindBy(id = "form:datepicker2")
        DatePicker datePicker2;

        @Override
        public String getLocation() {
            return "datepicker/datePicker012.xhtml";
        }
    }
}