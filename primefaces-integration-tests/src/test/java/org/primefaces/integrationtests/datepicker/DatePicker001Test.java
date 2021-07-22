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
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.base.ComponentUtils;

public class DatePicker001Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: set date and basic panel validation")
    public void testBasic(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        Assertions.assertEquals(LocalDate.now(), datePicker.getValue().toLocalDate());
        LocalDate value = LocalDate.of(1978, 2, 19);

        // Act
        datePicker.setValue(value);

        // Assert Panel
        assertDate(datePicker.showPanel(), "February", "1978");

        // Assert Submit Value
        page.button.click();
        LocalDate newValue = datePicker.getValueAsLocalDate();
        Assertions.assertEquals(value, newValue);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: select date via click on day in the next month")
    public void testSelectDateNextMonth(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        LocalDate value = LocalDate.of(1978, 2, 19);
        datePicker.setValue(value);
        datePicker.showPanel();

        // Act
        datePicker.getNextMonthLink().click();
        datePicker.selectDay("25");

        // Assert selected value
        LocalDate expectedDate = LocalDate.of(1978, 3, 25);
        Assertions.assertEquals(expectedDate, datePicker.getValueAsLocalDate());

        // Assert Submit Value
        page.button.click();
        LocalDate newValue = datePicker.getValueAsLocalDate();
        Assertions.assertEquals(expectedDate, newValue);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(3)
    @DisplayName("DatePicker: select date via click on day in the previous month")
    public void testSelectDatePreviousMonth(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        LocalDate value = LocalDate.of(1978, 2, 19);
        datePicker.setValue(value);
        datePicker.showPanel();

        // Act
        datePicker.getPreviousMonthLink().click();
        datePicker.selectDay("8");

        // Assert selected value
        LocalDate expectedDate = LocalDate.of(1978, 1, 8);
        Assertions.assertEquals(expectedDate, datePicker.getValueAsLocalDate());

        // Assert Submit Value
        page.button.click();
        LocalDate newValue = datePicker.getValueAsLocalDate();
        Assertions.assertEquals(expectedDate, newValue);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(4)
    @DisplayName("DatePicker: highlight today and selected")
    public void testHighlight(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        LocalDate selectedDate = LocalDate.now();
        if (selectedDate.getDayOfMonth() == 1) {
            selectedDate = selectedDate.plusMonths(1).minusDays(1);
        }
        else {
            selectedDate = selectedDate.minusDays(1);
        }

        // Act
        datePicker.setValue(selectedDate);

        //Assert panel
        String currentDayOfMonth = ((Integer) LocalDate.now().getDayOfMonth()).toString();
        assertDay(datePicker, currentDayOfMonth, "ui-state-highlight");
        String selectedDayOfMonth = ((Integer) selectedDate.getDayOfMonth()).toString();
        assertDay(datePicker, selectedDayOfMonth, "ui-state-active");
    }

    @Test
    @Order(5)
    @DisplayName("DatePicker: Use disable() widget method to disable DatePicker")
    public void testDisable(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        Assertions.assertEquals(LocalDate.now(), datePicker.getValue().toLocalDate());

        // Act
        datePicker.disable();
        WebElement panel = datePicker.showPanel();

        // Assert
        Assertions.assertFalse(datePicker.isEnabled());
        assertNotDisplayed(panel);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), LocalDate.now().format(dateTimeFormatter));
    }

    @Test
    @Order(6)
    @DisplayName("DatePicker: Use enable() widget method to enable DatePicker")
    public void testEnable(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        datePicker.disable();
        Assertions.assertFalse(datePicker.isEnabled());

        // Act
        datePicker.enable();
        WebElement panel = datePicker.showPanel();

        // Assert
        Assertions.assertTrue(datePicker.isEnabled());
        assertDisplayed(panel);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), LocalDate.now().format(dateTimeFormatter));
    }

    @Test
    @Order(7)
    @DisplayName("DatePicker: illegal date")
    public void testIllegalDate(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        datePicker.clear();
        ComponentUtils.sendKeys(datePicker.getInput(), "02/32/1900");

        // Act
        page.button.click();

        // Assert
        Assertions.assertTrue(page.messages.isDisplayed());
        Assertions.assertEquals(1, page.messages.getAllMessages().size());
        Assertions.assertTrue(page.messages.getAllMessages().get(0).getDetail().contains("could not be understood as a date"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("DatePicker: correct date")
    public void testCorrectDate(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        datePicker.clear();
        ComponentUtils.sendKeys(datePicker.getInput(), "02/28/1900");

        // Act
        page.button.click();

        // Assert
        Assertions.assertFalse(page.messages.isDisplayed());
        Assertions.assertEquals(0, page.messages.getAllMessages().size());
        assertNoJavascriptErrors();
    }

    private void assertDay(DatePicker datePicker, String day, String styleClass) {
        PrimeSelenium.wait(100);
        datePicker.showPanel();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.elementToBeClickable(datePicker.getPanel().findElement(By.linkText(day))));
        Assertions.assertTrue(PrimeSelenium.hasCssClass(datePicker.getPanel().findElement(By.linkText(day)), styleClass));
    }

    private void assertConfiguration(JSONObject cfg, String defaultDate) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        Assertions.assertEquals("mm/dd/yy", cfg.getString("dateFormat"));
        Assertions.assertEquals(defaultDate, cfg.getString("defaultDate"));
        Assertions.assertEquals("single", cfg.getString("selectionMode"));
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
            return "datepicker/datePicker001.xhtml";
        }
    }
}
