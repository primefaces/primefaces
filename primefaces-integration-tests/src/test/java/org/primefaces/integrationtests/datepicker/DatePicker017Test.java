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
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DatePicker;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatePicker017Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: mask='true' renders the initial value through the mask")
    void maskInitialValue(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;

        // Assert
        assertNoJavascriptErrors();
        assertEquals(LocalDateTime.of(2026, 6, 5, 0, 0, 0), datePicker.getValue());
        assertEquals("05/06/2026 00:00:00", datePicker.getInputValue());
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: mask='true' accepts manual typed input and submits the value")
    void maskManualTyping(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerEmpty;

        // Act
        datePicker.getInput().click();
        ComponentUtils.sendKeys(datePicker.getInput(), "19021978115519");
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals("19/02/1978 11:55:19", datePicker.getInputValue());
        assertEquals(LocalDateTime.of(1978, 2, 19, 11, 55, 19), datePicker.getValue());
    }

    @Test
    @Order(3)
    @DisplayName("DatePicker: mask='true' keeps input and time picker in sync for every segment")
    void maskTimePickerSync(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        WebElement panel = datePicker.showPanel();

        // Act
        incrementTimeSegment(panel);

        // Assert
        assertTime(panel, "0", "0", "1");
        assertEquals("05/06/2026 00:00:01", datePicker.getInputValue());

        // Act
        datePicker.hidePanel();
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals(LocalDateTime.of(2026, 6, 5, 0, 0, 1), datePicker.getValue());
    }

    @Test
    @Order(4)
    @DisplayName("DatePicker: mask clears incomplete input on blur by default (maskAutoClear)")
    void maskAutoClearIncomplete(Page page) {
        // Arrange
        DatePicker datePicker = page.datePickerEmpty;

        // Act - type incomplete date
        datePicker.getInput().click();
        ComponentUtils.sendKeys(datePicker.getInput(), "1902");

        // Assert - mask partially filled
        assertEquals("19/02/____ __:__:__", datePicker.getInputValue());

        // Act - blur and submit
        page.button.click();

        // Assert - incomplete value cleared
        assertNoJavascriptErrors();
        assertEquals("", datePicker.getInputValue());
    }

    protected void incrementTimeSegment(WebElement panel) {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(panel));
        WebElement picker = panel.findElement(By.className("ui-second-picker"));
        picker.findElement(By.className("ui-picker-up")).click();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datepicker")
        DatePicker datePicker;

        @FindBy(id = "form:datepickerEmpty")
        DatePicker datePickerEmpty;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "datepicker/datePicker017.xhtml";
        }
    }
}
