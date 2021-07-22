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

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.Messages;

public class DatePicker005Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: preselected range")
    public void testPreselectedRange(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        WebElement panel = datePicker.showPanel(); // focus to bring up panel

        // Assert Panel
        Assertions.assertNotNull(panel);
        Assertions.assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("20")), "ui-state-active"));
        Assertions.assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("25")), "ui-state-active"));
        Assertions.assertFalse(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("19")), "ui-state-active"));
        Assertions.assertFalse(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("26")), "ui-state-active"));
        Assertions.assertEquals(6, panel.findElements(By.className("ui-state-active")).size());

        // Assert Submit Value
        page.button.click();
        Assertions.assertEquals("08/20/2020 - 08/25/2020", page.messages.getMessage(0).getDetail());
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: select range")
    public void testSelectRange(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Act
        WebElement panel = datePicker.showPanel(); // focus to bring up panel
        Assertions.assertNotNull(panel);
        panel.findElement(By.linkText("3")).click();
        panel.findElement(By.linkText("5")).click();

        // Assert Panel
        Assertions.assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("3")), "ui-state-active"));
        Assertions.assertTrue(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("5")), "ui-state-active"));
        Assertions.assertFalse(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("2")), "ui-state-active"));
        Assertions.assertFalse(PrimeSelenium.hasCssClass(panel.findElement(By.linkText("6")), "ui-state-active"));
        Assertions.assertEquals(3, panel.findElements(By.className("ui-state-active")).size());

        // Assert Submit Value
        page.button.click();
        Assertions.assertEquals("08/03/2020 - 08/05/2020", page.messages.getMessage(0).getDetail());
        assertConfiguration(datePicker.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        Assertions.assertEquals("mm/dd/yy", cfg.getString("dateFormat"));
        Assertions.assertEquals("range", cfg.getString("selectionMode"));
        Assertions.assertFalse(cfg.getBoolean("inline"));
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
            return "datepicker/datePicker005.xhtml";
        }
    }
}
