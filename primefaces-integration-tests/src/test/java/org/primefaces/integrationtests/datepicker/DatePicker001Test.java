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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatePicker001Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: set date and basic panel validation")
    void basic(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        assertEquals(LocalDate.now(), datePicker.getValue().toLocalDate());
        LocalDate value = LocalDate.of(1978, 2, 19);

        // Act
        datePicker.setValue(value);

        // Assert Panel
        assertDate(datePicker.showPanel(), "February", "1978");

        // Assert Submit Value
        page.button.click();
        LocalDate newValue = datePicker.getValueAsLocalDate();
        assertEquals(value, newValue);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: select date via click on day in the next month")
    void selectDateNextMonth(Page page) {
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
        assertEquals(expectedDate, datePicker.getValueAsLocalDate());

        // Assert Submit Value
        page.button.click();
        LocalDate newValue = datePicker.getValueAsLocalDate();
        assertEquals(expectedDate, newValue);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(3)
    @DisplayName("DatePicker: select date via click on day in the previous month")
    void selectDatePreviousMonth(Page page) {
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
        assertEquals(expectedDate, datePicker.getValueAsLocalDate());

        // Assert Submit Value
        page.button.click();
        LocalDate newValue = datePicker.getValueAsLocalDate();
        assertEquals(expectedDate, newValue);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), newValue.format(dateTimeFormatter));
    }

    @Test
    @Order(4)
    @DisplayName("DatePicker: highlight today and selected")
    void highlight(Page page) {
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
    void disable(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        assertEquals(LocalDate.now(), datePicker.getValue().toLocalDate());

        // Act
        datePicker.disable();
        WebElement panel = datePicker.showPanel();

        // Assert
        assertFalse(datePicker.isEnabled());
        assertNotDisplayed(panel);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), LocalDate.now().format(dateTimeFormatter));
    }

    @Test
    @Order(6)
    @DisplayName("DatePicker: Use enable() widget method to enable DatePicker")
    void enable(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        datePicker.disable();
        assertFalse(datePicker.isEnabled());

        // Act
        datePicker.enable();
        WebElement panel = datePicker.showPanel();

        // Assert
        assertTrue(datePicker.isEnabled());
        assertDisplayed(panel);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        assertConfiguration(datePicker.getWidgetConfiguration(), LocalDate.now().format(dateTimeFormatter));
    }

    @Test
    @Order(7)
    @DisplayName("DatePicker: illegal date")
    void illegalDate(Page page) {
        // Arrange
        DatePicker datePicker = page.datepickerEditable;
        datePicker.clear();
        ComponentUtils.sendKeys(datePicker.getInput(), "02/32/1900");

        // Act
        page.button.click();

        // Assert
        assertTrue(page.messages.isDisplayed());
        assertEquals(1, page.messages.getAllMessages().size());
        assertTrue(page.messages.getAllMessages().get(0).getDetail().contains("could not be understood as a date"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("DatePicker: correct date")
    void correctDate(Page page) {
        // Arrange
        DatePicker datePicker = page.datepickerEditable;
        datePicker.clear();
        ComponentUtils.sendKeys(datePicker.getInput(), "02/28/1900");

        // Act
        page.button.click();

        // Assert
        assertFalse(page.messages.isDisplayed());
        assertEquals(0, page.messages.getAllMessages().size());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(9)
    @DisplayName("DatePicker: Arrow Down should trigger the popup")
    void arrowDownOnInput(Page page) {
        // Arrange
        DatePicker datePicker = page.datepickerEditable;
        datePicker.clear();


        // Act
        ComponentUtils.sendKeys(datePicker.getInput(), Keys.ARROW_DOWN);

        // Assert
        assertTrue(datePicker.getPanel().isDisplayed());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(10)
    @DisplayName("DatePicker: ESCAPE should close the popup")
    void escapeOnPanel(Page page) {
        // Arrange
        DatePicker datePicker = page.datepickerEditable;
        datePicker.clear();
        datePicker.showPanel();
        assertTrue(datePicker.getPanel().isDisplayed());

        // Act
        ComponentUtils.sendKeys(datePicker.getInput(), Keys.ESCAPE);

        // Assert
        assertFalse(datePicker.getPanel().isDisplayed());
        assertNoJavascriptErrors();
    }

    private void assertDay(DatePicker datePicker, String day, String styleClass) {
        PrimeSelenium.wait(100);
        datePicker.showPanel();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.elementToBeClickable(datePicker.getPanel().findElement(By.linkText(day))));
        assertTrue(PrimeSelenium.hasCssClass(datePicker.getPanel().findElement(By.linkText(day)), styleClass));
    }

    private void assertConfiguration(JSONObject cfg, String defaultDate) {
        assertNoJavascriptErrors();
        System.out.println("DatePicker Config = " + cfg);
        assertEquals("mm/dd/yy", cfg.getString("dateFormat"));
        assertEquals(defaultDate, cfg.getString("defaultDate"));
        assertEquals("single", cfg.getString("selectionMode"));
        assertFalse(cfg.getBoolean("inline"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datepicker")
        DatePicker datePicker;

        @FindBy(id = "form:datepickerEditable")
        DatePicker datepickerEditable;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "datepicker/datePicker001.xhtml";
        }
    }
}
