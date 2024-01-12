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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.data.Paginator;
import org.primefaces.selenium.component.model.datatable.Header;
import org.primefaces.selenium.component.model.datatable.Row;

class DataTable018Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: ListModel Basic & Paginator")
    void listModelBasicAndPaginator(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Assert
        assertNotNull(dataTable.getPaginatorWebElement());
        assertNotNull(dataTable.getHeaderWebElement());

        List<WebElement> rowElts = dataTable.getRowsWebElement();
        assertNotNull(rowElts);
        assertEquals(3, rowElts.size());

        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(3, rows.size());

        Row firstRow = dataTable.getRow(0);
        assertEquals("1", firstRow.getCell(0).getText());
        assertEquals("Java", firstRow.getCell(1).getText());

        Header header = dataTable.getHeader();
        assertNotNull(header);
        assertNotNull(header.getCells());
        assertEquals(3, header.getCells().size());
        assertEquals("ID", header.getCell(0).getColumnTitle().getText());
        assertEquals("Name", header.getCell(1).getColumnTitle().getText());

        Paginator paginator = dataTable.getPaginator();
        assertNotNull(paginator);
        assertNotNull(paginator.getPages());
        assertEquals(2, paginator.getPages().size());
        assertEquals(1, paginator.getPage(0).getNumber());
        assertEquals(2, paginator.getPage(1).getNumber());

        assertConfiguration(dataTable.getWidgetConfiguration());

        // Act
        dataTable.selectPage(2);

        // Assert - Part 2
        rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(2, rows.size());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: ListModel single sort")
    void listModelSortSingle(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act - ascending
        dataTable.selectPage(1);
        dataTable.sort("Name");

        // Assert
        List<ProgrammingLanguage> langsSorted = sortBy(Comparator.comparing(ProgrammingLanguage::getName));
        assertRows(dataTable, langsSorted);

        // Act - descending
        dataTable.sort("Name");

        // Assert
        langsSorted = sortBy(Comparator.comparing(ProgrammingLanguage::getName).reversed());
        assertRows(dataTable, langsSorted);

        // Act
        page.buttonUpdate.click();

        // Assert - sort must not be lost after update
        assertRows(dataTable, langsSorted);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: ListModel filter")
    void listModelFilter(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act
        dataTable.selectPage(1);
        dataTable.sort("Name");
        dataTable.filter("Name", "Java");

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByName("Java");
        assertRows(dataTable, langsFiltered);

        // Act
        page.buttonUpdate.click();

        // Assert - filter must not be lost after update
        assertRows(dataTable, langsFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: ListModel rows per page & reset; "
            + "includes https://github.com/primefaces/primefaces/issues/5465 & https://github.com/primefaces/primefaces/issues/5481")
    void listModelRowsPerPageAndReset_5465_5481(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Assert
        Select selectRowsPerPage = new Select(dataTable.getPaginatorWebElement().findElement(By.className("ui-paginator-rpp-options")));
        assertEquals("3", selectRowsPerPage.getFirstSelectedOption().getText());
        assertEquals(3, dataTable.getRows().size());

        // Act
        dataTable.selectPage(1);
        dataTable.sort("Name");
        selectRowsPerPage.selectByVisibleText("10");
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Assert
        assertEquals(languages.size(), dataTable.getRows().size());

        // Act
        dataTable.filter("Name", "Java");
        PrimeSelenium.guardAjax(page.buttonResetTable).click();

        // Assert
        selectRowsPerPage = new Select(dataTable.getPaginatorWebElement().findElement(By.className("ui-paginator-rpp-options")));
        assertEquals("3", selectRowsPerPage.getFirstSelectedOption().getText());
        assertEquals(3, dataTable.getRows().size());
        assertRows(dataTable, languages.stream().limit(3).collect(Collectors.toList())); //implicit checks reset sort & filter

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

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @FindBy(id = "form:buttonResetTable")
        CommandButton buttonResetTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable018.xhtml";
        }
    }
}
