/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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
package org.primefaces.integrationtests.datatable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.ToggleSwitch;

public class DataTable032Test extends AbstractDataTableTest {

    @Test
    @DisplayName("DataTable: disableSelection")
    public void testDisabledRowSelection(Page page) {

        // rows are always "ui-selection-column", indepdent of if the current row is selection disabled or not
        Assertions.assertTrue(PrimeSelenium.hasCssClass(page.dataTable.getCell(0, 0).getWebElement(), "ui-selection-column"));
        Assertions.assertTrue(PrimeSelenium.hasCssClass(page.dataTable.getCell(1, 0).getWebElement(), "ui-selection-column"));

        // not selectable yet, try to trigger it
        page.dataTable.getCell(0, 0).getWebElement().click();
        Assertions.assertTrue(page.messages.isEmpty());

        // make it selectable now
        ToggleSwitch toggleSwitch = PrimeSelenium.createFragment(ToggleSwitch.class, By.id("form:datatable:0:toggle"));
        toggleSwitch.click();

        // selectable yet, try to trigger it
        PrimeSelenium.guardAjax(page.dataTable.getCell(0, 0).getWebElement()).click();

        Assertions.assertEquals("ProgrammingLanguage Selected", page.messages.getMessage(0).getSummary());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonSubmit")
        CommandButton buttonSubmit;

        @Override
        public String getLocation() {
            return "datatable/dataTable032.xhtml";
        }
    }
}
