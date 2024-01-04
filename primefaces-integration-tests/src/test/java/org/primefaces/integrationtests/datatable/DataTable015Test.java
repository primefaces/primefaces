/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;

class DataTable015Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: MultiViewState - sort & filter")
    void multiViewStateSortFilter(Page page, OtherPage otherPage) {
        // Arrange
        PrimeSelenium.goTo(page);
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Act
        dataTable.selectPage(1);
        dataTable.sort("Name");
        dataTable.filter("Name", "Java");

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByName("Java");
        assertRows(dataTable, langsFiltered);

        // Act
        PrimeSelenium.goTo(otherPage);
        PrimeSelenium.goTo(page);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Assert - sort and filter must not be lost after navigating to another page and back
        assertRows(dataTable, langsFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: MultiViewState - paginator")
    void multiViewStatePage(Page page, OtherPage otherPage) {
        // Arrange
        PrimeSelenium.goTo(page);
        PrimeSelenium.guardAjax(page.buttonClearTableState).click();
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Act
        dataTable.selectPage(2);
        PrimeSelenium.goTo(otherPage);
        PrimeSelenium.goTo(page);

        // Assert - page must not be lost after navigating to another page and back
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));
        assertEquals(2, dataTable.getPaginator().getActivePage().getNumber());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertTrue(cfg.has("paginator"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonClearTableState")
        CommandButton buttonClearTableState;

        @Override
        public String getLocation() {
            return "datatable/dataTable015.xhtml";
        }
    }

    public static class OtherPage extends AbstractPrimePage {
        @Override
        public String getLocation() {
            return "clientwindow/clientWindow001.xhtml";
        }
    }
}
