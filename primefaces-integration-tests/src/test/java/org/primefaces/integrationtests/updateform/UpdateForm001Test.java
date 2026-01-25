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
package org.primefaces.integrationtests.updateform;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Calendar;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputNumber;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.InputTextarea;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpdateForm001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    void updateForm(Page page) {

        // Act
        page.calendar.setValue(System.currentTimeMillis());
        page.inputnumber0.setValue(0);
        page.inputtext1.setValue("Text 1");
        page.inputnumber1.setValue(1);
        page.inputtext2.setValue("Text 2");
        page.inputnumber2.setValue(2);
        page.inputtext3.setValue("Text 3");
        page.inputnumber3.setValue(3);
        page.inputtext4.setValue("Text 4");
        page.inputnumber4.setValue(4);
        page.textarea1.setValue("This is a long text");
        page.button.click();

        // Assert
        assertNotNull(page.calendar.getValue());
        assertEquals("0.00", page.inputnumber0.getWidgetValue());
        assertEquals("Text 1", page.inputtext1.getValue());
        assertEquals("1.00", page.inputnumber1.getWidgetValue());
        assertEquals("Text 2", page.inputtext2.getValue());
        assertEquals("2.00", page.inputnumber2.getWidgetValue());
        assertEquals("Text 3", page.inputtext3.getValue());
        assertEquals("3.00", page.inputnumber3.getWidgetValue());
        assertEquals("Text 4", page.inputtext4.getValue());
        assertEquals("4.00", page.inputnumber4.getWidgetValue());
        assertEquals("This is a long text", page.textarea1.getValue());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:calendar")
        Calendar calendar;

        @FindBy(id = "form:inputnumber0")
        InputNumber inputnumber0;

        @FindBy(id = "form:inputtext1")
        InputText inputtext1;

        @FindBy(id = "form:inputnumber1")
        InputNumber inputnumber1;

        @FindBy(id = "form:inputtext2")
        InputText inputtext2;

        @FindBy(id = "form:inputnumber2")
        InputNumber inputnumber2;

        @FindBy(id = "form:inputtext3")
        InputText inputtext3;

        @FindBy(id = "form:inputnumber3")
        InputNumber inputnumber3;

        @FindBy(id = "form:inputtext4")
        InputText inputtext4;

        @FindBy(id = "form:inputnumber4")
        InputNumber inputnumber4;

        @FindBy(id = "form:inputtextarea1")
        InputTextarea textarea1;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "updateform/updateform001.xhtml";
        }
    }
}
