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

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatePicker013Test extends AbstractDatePickerTest {

    @Test
    @Order(1)
    @DisplayName("DatePicker: f:convertDateTime vs internal conversion")
    void fConvertDateTimeVsInternal(Page page) {
        // Arrange
        DatePicker datePicker1 = page.datePicker1;
        DatePicker datePicker2 = page.datePicker2;
        LocalDateTime expected = LocalDateTime.of(2021, 1, 10, 1, 16, 04);

        // Assert
        assertEquals(expected, datePicker1.getValue());
        assertEquals(expected, datePicker2.getValue());

        // Act
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertEquals(expected, datePicker1.getValue());
        assertEquals(expected, datePicker2.getValue());
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
            return "datepicker/datePicker013.xhtml";
        }
    }
}
