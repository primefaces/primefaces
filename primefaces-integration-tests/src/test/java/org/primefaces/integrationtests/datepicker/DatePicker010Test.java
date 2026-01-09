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
import org.primefaces.selenium.component.DatePicker;

import java.time.LocalDate;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DatePicker010Test extends AbstractDatePickerTest {

    @Test
    @DisplayName("DatePicker: minDate and maxDate; See GitHub #7576")
    @Order(1)
    void minMax(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        datePicker.click(); // focus to bring up panel

        // Assert (some days of june active)
        WebElement panel = datePicker.getPanel();
        assertNotNull(panel);
        assertEquals(10, panel.findElements(By.cssSelector("a.ui-state-default")).size());
        assertEquals(20 + 5, panel.findElements(By.cssSelector("td > span.ui-state-disabled")).size()); //includes invisible days of other months

        // Act - previous month
        panel.findElement(By.className("ui-datepicker-prev")).click();

        // Assert (some days of May active)
        assertEquals(26 + 11, panel.findElements(By.cssSelector("td > span.ui-state-disabled")).size()); //includes invisible days of other months
        assertEquals(5, panel.findElements(By.cssSelector("td > a.ui-state-default")).size());

        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @DisplayName("DatePicker: minDate and maxDate; See GitHub #7576")
    @Order(1)
    void minMaxValueInside(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        datePicker.setValue(LocalDate.of(2021, 06, 02));

        // Assert
        WebElement panel = datePicker.getPanel();
        assertNotNull(panel);
        assertEquals(LocalDate.of(2021, 6, 02), datePicker.getValue().toLocalDate());

        // Act
        datePicker.setValue(LocalDate.of(2021, 05, 29));

        // Assert
        assertEquals(LocalDate.of(2021, 5, 29), datePicker.getValue().toLocalDate());

        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @DisplayName("DatePicker: minDate and maxDate; See GitHub #7576")
    @Order(1)
    void minMaxValueOutside(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        datePicker.setValue(LocalDate.of(2021, 07, 20));

        // Assert
        WebElement panel = datePicker.getPanel();
        assertNotNull(panel);
        assertEquals(LocalDate.of(2021, 6, 10), datePicker.getValue().toLocalDate());

        // Act
        datePicker.setValue(LocalDate.of(2021, 04, 30));

        // Assert
        assertEquals(LocalDate.of(2021, 5, 27), datePicker.getValue().toLocalDate());

        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @DisplayName("DatePicker: maxDate after now default view to max date; See GitHub #8912")
    @Order(2)
    void viewDateisMaxDate(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerMaxDate;

        // Act
        datePicker.click(); // focus to bring up panel

        // Assert
        assertDate(datePicker.showPanel(), "October", "2021");
        assertNoJavascriptErrors();
    }

    @Test
    @DisplayName("DatePicker: minDate after now default view to min date; See GitHub #8912")
    @Order(3)
    void viewDateisMinDate(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerMinDate;

        // Act
        datePicker.click(); // focus to bring up panel

        // Assert
        assertDate(datePicker.showPanel(), "August", "2034");
        assertNoJavascriptErrors();
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        assertEquals("dd.mm.yy", cfg.getString("dateFormat"));
        assertEquals("27.05.2021", cfg.getString("minDate"));
        assertEquals("10.06.2021", cfg.getString("maxDate"));
        assertEquals("single", cfg.getString("selectionMode"));
        assertFalse(cfg.getBoolean("inline"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker")
        DatePicker datePicker;

        @FindBy(id = "form:maxdate")
        DatePicker datePickerMaxDate;

        @FindBy(id = "form:mindate")
        DatePicker datePickerMinDate;

        @Override
        public String getLocation() {
            return "datepicker/datePicker010.xhtml";
        }
    }
}
