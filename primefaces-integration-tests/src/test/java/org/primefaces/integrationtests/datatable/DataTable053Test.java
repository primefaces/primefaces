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
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectCheckboxMenu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("DataTable-filter")
class DataTable053Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: GitHub #12885 an empty collection filter must not select the null item of a filter facet")
    void emptyCollectionFilterSelectsNothing(Page page) {
        // Assert - the filter value is an empty list, so no item may be selected, especially not "No Type"
        assertEquals(0, page.filterType.getSelectedCheckboxes().size(), "no item may be preselected");
        assertEquals(3, page.dataTable.getRows().size(), "an empty filter must not filter anything away");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: GitHub #12885 an empty collection filter must survive an AJAX update")
    void emptyCollectionFilterSurvivesUpdate(Page page) {
        // Act
        page.buttonUpdate.click();

        // Assert
        assertEquals(0, page.filterType.getSelectedCheckboxes().size(), "no item may be selected after an update");
        assertEquals(3, page.dataTable.getRows().size());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: GitHub #12885 the backing bean must keep the empty collection, it must not become null")
    void backingBeanKeepsEmptyCollection(Page page) {
        // Act
        page.buttonShowFilter.click();

        // Assert
        assertEquals("[]", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: GitHub #12885 selecting a value in the filter facet still filters")
    void selectingAValueStillFilters(Page page) {
        // Act
        page.filterType.selectValue("COMPILED");
        PrimeSelenium.executeScript(true, "PF('datatable').filter()");

        // Assert
        assertEquals(2, page.dataTable.getRows().size(), "only the COMPILED languages must remain");
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:datatable:filterType")
        SelectCheckboxMenu filterType;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @FindBy(id = "form:buttonShowFilter")
        CommandButton buttonShowFilter;

        @Override
        public String getLocation() {
            return "datatable/dataTable053.xhtml";
        }
    }
}
