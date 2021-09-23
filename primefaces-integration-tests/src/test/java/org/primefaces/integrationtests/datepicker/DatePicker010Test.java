/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.time.LocalDate;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.DatePicker;

public class DatePicker010Test extends AbstractDatePickerTest {

    @Test
    @DisplayName("DatePicker: minDate and maxDate; See GitHub #7576")
    public void testMinMax(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        datePicker.click(); // focus to bring up panel

        // Assert (some days of june active)
        WebElement panel = datePicker.getPanel();
        Assertions.assertNotNull(panel);
        Assertions.assertEquals(10, panel.findElements(By.cssSelector("a.ui-state-default")).size());
        Assertions.assertEquals(20 + 5, panel.findElements(By.cssSelector("td > span.ui-state-disabled")).size()); //includes invisible days of other months

        // Act - previous month
        panel.findElement(By.className("ui-datepicker-prev")).click();

        // Assert (some days of May active)
        Assertions.assertEquals(26 + 11, panel.findElements(By.cssSelector("td > span.ui-state-disabled")).size()); //includes invisible days of other months
        Assertions.assertEquals(5, panel.findElements(By.cssSelector("td > a.ui-state-default")).size());

        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @DisplayName("DatePicker: minDate and maxDate; See GitHub #7576")
    public void testMinMaxValueInside(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        datePicker.setValue(LocalDate.of(2021, 06, 02));

        // Assert
        WebElement panel = datePicker.getPanel();
        Assertions.assertNotNull(panel);
        Assertions.assertEquals(LocalDate.of(2021, 6, 02), datePicker.getValue().toLocalDate());

        // Act
        datePicker.setValue(LocalDate.of(2021, 05, 29));

        // Assert
        Assertions.assertEquals(LocalDate.of(2021, 5, 29), datePicker.getValue().toLocalDate());

        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @DisplayName("DatePicker: minDate and maxDate; See GitHub #7576")
    public void testMinMaxValueOutside(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        datePicker.setValue(LocalDate.of(2021, 07, 20));

        // Assert
        WebElement panel = datePicker.getPanel();
        Assertions.assertNotNull(panel);
        Assertions.assertEquals(LocalDate.of(2021, 6, 10), datePicker.getValue().toLocalDate());

        // Act
        datePicker.setValue(LocalDate.of(2021, 04, 30));

        // Assert
        Assertions.assertEquals(LocalDate.of(2021, 5, 27), datePicker.getValue().toLocalDate());

        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        Assertions.assertEquals("dd.mm.yy", cfg.getString("dateFormat"));
        Assertions.assertEquals("27.05.2021", cfg.getString("minDate"));
        Assertions.assertEquals("10.06.2021", cfg.getString("maxDate"));
        Assertions.assertEquals("single", cfg.getString("selectionMode"));
        Assertions.assertFalse(cfg.getBoolean("inline"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker")
        DatePicker datePicker;

        @Override
        public String getLocation() {
            return "datepicker/datePicker010.xhtml";
        }
    }
}
