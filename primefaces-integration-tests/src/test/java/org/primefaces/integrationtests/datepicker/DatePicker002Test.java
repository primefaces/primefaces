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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;

import java.util.List;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatePicker002Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    void withoutShowOtherMonths(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker1;

        // Act
        datePicker.click(); // focus to bring up panel

        // Assert
        assertNoJavascriptErrors();
        WebElement panel = datePicker.getPanel();
        assertNotNull(panel);
        List<WebElement> elements = panel.findElements(By.cssSelector("table.ui-datepicker-calendar"));
        assertNotNull(elements);
        assertEquals(1, elements.size());
        WebElement table = elements.get(0);

        List<WebElement> days = table.findElements(By.cssSelector("td a"));
        assertNotNull(days);
        assertEquals(28, days.size());

        List<WebElement> daysOtherMonths = table.findElements(By.cssSelector("td.ui-datepicker-other-month"));
        assertNotNull(daysOtherMonths);
        assertEquals(7, daysOtherMonths.size());
        assertEquals(0, daysOtherMonths.stream().filter(dayOtherMonth -> dayOtherMonth.isDisplayed()).count());
    }

    @Test
    @Order(2)
    void withShowOtherMonths(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker2;

        // Act
        datePicker.click(); // focus to bring up panel

        // Assert
        assertNoJavascriptErrors();
        WebElement panel = datePicker.getPanel();
        assertNotNull(panel);
        List<WebElement> elements = panel.findElements(By.cssSelector("table.ui-datepicker-calendar"));
        assertNotNull(elements);
        assertEquals(1, elements.size());
        WebElement table = elements.get(0);

        List<WebElement> days = table.findElements(By.cssSelector("td a"));
        assertNotNull(days);
        assertEquals(28, days.size());

        List<WebElement> daysOtherMonths = table.findElements(By.cssSelector("td.ui-datepicker-other-month"));
        assertNotNull(daysOtherMonths);
        assertEquals(7, daysOtherMonths.size());
        assertEquals(7, daysOtherMonths.stream().filter(dayOtherMonth -> dayOtherMonth.isDisplayed()).count());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker1")
        DatePicker datePicker1;

        @FindBy(id = "form:datepicker2")
        DatePicker datePicker2;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "datepicker/datePicker002.xhtml";
        }
    }
}
