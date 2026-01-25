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
import org.primefaces.selenium.component.Messages;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatePicker014Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: f:convertDateTime with own converter")
    void ownConverterBasic(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        LocalDateTime expected = LocalDateTime.of(2021, 1, 10, 1, 16, 04);

        // Assert
        assertEquals(expected, datePicker.getValue());

        // Act
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals(expected, datePicker.getValue());
    }

    @Test
    @Order(2)
    @DisplayName("DatePicker: f:convertDateTime with own converter; modify date on client")
    void ownConverterModify(Page page) {
        // Arrange
        DatePicker datePicker = page.datePicker;
        LocalDateTime expected = LocalDateTime.of(2021, 1, 10, 1, 16, 04);

        // Assert
        assertEquals(expected, datePicker.getValue());

        // Act
        LocalDateTime newDateTime = LocalDateTime.of(2022, 6, 17, 20, 0, 0);
        page.datePicker.setDate(newDateTime);
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals(newDateTime, datePicker.getValue());
        assertEquals("2022-6-17 20:0:0", page.messages.getMessage(0).getDetail());
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
            return "datepicker/datePicker014.xhtml";
        }
    }
}
