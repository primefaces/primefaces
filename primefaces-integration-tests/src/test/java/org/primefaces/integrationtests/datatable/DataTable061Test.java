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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

@Tag("DataTable-lazy")
@Tag("DataTable-selection")
@Tag("DataTable-filter")
class DataTable061Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: lazy select all with the default selectAllFilteredOnly selects the filtered rows, not the first unfiltered ones")
    void selectAllFiltered(Page page) {
        // Arrange - "Language 7" matches 7 and 70..75
        DataTable dataTable = page.dataTable;
        dataTable.filterAtOnce("Name", "Language 7");

        // Act
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelections(page.messages, "7,70,71,72,73,74,75");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: lazy select all with an active sort selects the filtered rows")
    void selectAllFilteredAndSorted(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.sort("Name");
        dataTable.filterAtOnce("Name", "Language 7");

        // Act
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelections(page.messages, "7,70,71,72,73,74,75");
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:button")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "datatable/dataTable061.xhtml";
        }
    }
}
