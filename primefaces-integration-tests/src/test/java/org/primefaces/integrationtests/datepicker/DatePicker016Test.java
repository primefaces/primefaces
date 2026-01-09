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
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatePicker016Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: preselected week")
    void preselectedWeek(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        assertEquals("8/16/2020 - 8/22/2020 (Wk 34)", datePicker.getInputValue());

        // Act
        WebElement panel = datePicker.showPanel(); // focus to bring up panel

        // Assert Panel
        assertNotNull(panel);
        assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("16")), "ui-state-active"));
        assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("17")), "ui-state-active"));
        assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("18")), "ui-state-active"));
        assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("19")), "ui-state-active"));
        assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("20")), "ui-state-active"));
        assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("21")), "ui-state-active"));
        assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("22")), "ui-state-active"));
        assertFalse(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("15")), "ui-state-active"));
        assertFalse(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("23")), "ui-state-active"));
        assertFalse(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("24")), "ui-state-active"));
        assertFalse(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("25")), "ui-state-active"));
        assertEquals(7, panel.findElements(By.className("ui-state-active")).size());

        // Assert Submit Value
        page.button.click();
        assertEquals("08/16/2020 - 08/22/2020", page.messages.getMessage(0).getDetail());
        assertEquals("8/16/2020 - 8/22/2020 (Wk 34)", datePicker.getInputValue());
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: select week")
    void selectWeek(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        WebElement panel = datePicker.showPanel(); // focus to bring up panel
        assertNotNull(panel);
        panel.findElement(By.linkText("3")).click();
        datePicker.hidePanel();
        datePicker.showPanel();

        // Assert Panel
        assertEquals(7, panel.findElements(By.className("ui-state-active")).size());
        assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("2")), "ui-state-active"));
        assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("8")), "ui-state-active"));
        assertFalse(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("9")), "ui-state-active"));

        // Assert Submit Value
        page.button.click();
        assertEquals("08/02/2020 - 08/08/2020", page.messages.getMessage(0).getDetail());
        assertEquals("8/2/2020 - 8/8/2020 (Wk 32)", datePicker.getInputValue());
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        assertEquals("m/d/yy", cfg.getString("dateFormat"));
        assertEquals("range", cfg.getString("selectionMode"));
        assertEquals("week", cfg.getString("view"));
        assertFalse(cfg.getBoolean("inline"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datepicker")
        DatePicker datePicker;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "datepicker/datePicker016.xhtml";
        }
    }
}
