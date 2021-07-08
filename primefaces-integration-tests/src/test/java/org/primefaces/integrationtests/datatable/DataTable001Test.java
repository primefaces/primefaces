/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.primefaces.integrationtests.datatable.ProgrammingLanguage.ProgrammingLanguageType;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.model.data.Paginator;
import org.primefaces.selenium.component.model.datatable.Header;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DataTable001Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: Basic & Paginator")
    public void testBasicAndPaginator(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        //page.button.click();

        // Assert
        Assertions.assertNotNull(dataTable.getPaginatorWebElement());
        Assertions.assertNotNull(dataTable.getHeaderWebElement());

        List<WebElement> rowElts = dataTable.getRowsWebElement();
        Assertions.assertNotNull(rowElts);
        Assertions.assertEquals(3, rowElts.size());

        List<Row> rows = dataTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(3, rows.size());

        Row firstRow = dataTable.getRow(0);
        Assertions.assertEquals("1", firstRow.getCell(0).getText());
        Assertions.assertEquals("Java", firstRow.getCell(2).getText());

        Header header = dataTable.getHeader();
        Assertions.assertNotNull(header);
        Assertions.assertNotNull(header.getCells());
        Assertions.assertEquals(4, header.getCells().size());
        Assertions.assertEquals("ID", header.getCell(0).getColumnTitle().getText());
        Assertions.assertEquals("Type", header.getCell(1).getColumnTitle().getText());
        Assertions.assertEquals("Name", header.getCell(2).getColumnTitle().getText());

        Paginator paginator = dataTable.getPaginator();
        Assertions.assertNotNull(paginator);
        Assertions.assertNotNull(paginator.getPages());
        Assertions.assertEquals(2, paginator.getPages().size());
        Assertions.assertEquals(1, paginator.getPage(0).getNumber());
        Assertions.assertEquals(2, paginator.getPage(1).getNumber());

        assertConfiguration(dataTable.getWidgetConfiguration());

        // Act
        dataTable.selectPage(2);

        // Assert - Part 2
        rows = dataTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(2, rows.size());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: single sort")
    public void testSortSingle(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);
        dataTable.selectPage(1);

        // Act - ascending
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
    @DisplayName("DataTable: filter")
    public void testFilter(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.selectPage(1);
        dataTable.sort("Name");

        // Act
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
    @DisplayName("DataTable: filter plus paging")
    public void testFilterPlusPaging(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.selectPage(1);
        dataTable.sort("Name");
        Select selectRowsPerPage = new Select(dataTable.getPaginatorWebElement().findElement(By.className("ui-paginator-rpp-options")));
        PrimeSelenium.guardAjax(selectRowsPerPage).selectByValue("2");

        // Act
        dataTable.filter("Name", "t");

        // Assert
        List<ProgrammingLanguage> langsFiltered = languages.stream()
                .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                .filter(l -> l.getName().toLowerCase().contains("t"))
                .collect(Collectors.toList());
        assertRows(dataTable, langsFiltered.stream().limit(2).collect(Collectors.toList()));

        // Act
        dataTable.selectPage(2);

        // Assert - filter must not be lost after update
        assertRows(dataTable, langsFiltered.stream().skip(2).limit(2).collect(Collectors.toList()));

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: global filter with globalFilterOnly=false")
    public void testGlobalFilter(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        InputText globalFilter = page.globalFilter;
        Assertions.assertNotNull(globalFilter);
        dataTable.selectPage(1);
        dataTable.sort("Name");

        // Act
        filterGlobal(globalFilter, "Python");

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByName("Python");
        assertRows(dataTable, langsFiltered);
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: GitHub #7193 global filter with globalFilterOnly=true")
    public void testGlobalFilterIncludeNotDisplayedFilter(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        InputText globalFilter = page.globalFilter;
        Assertions.assertNotNull(globalFilter);
        dataTable.selectPage(1);
        dataTable.sort("Type");

        // Act
        page.buttonGlobalFilterOnly.click();
        filterGlobal(globalFilter, ProgrammingLanguageType.INTERPRETED.name());

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByType(ProgrammingLanguageType.INTERPRETED);
        assertRows(dataTable, langsFiltered);
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: rows per page & reset; includes https://github.com/primefaces/primefaces/issues/5465 & https://github.com/primefaces/primefaces/issues/5481")
    public void testRowsPerPageAndReset_5465_5481(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Assert
        Select selectRowsPerPage = new Select(dataTable.getPaginatorWebElement().findElement(By.className("ui-paginator-rpp-options")));
        Assertions.assertEquals("3", selectRowsPerPage.getFirstSelectedOption().getText());
        Assertions.assertEquals(3, dataTable.getRows().size());

        // Act
        dataTable.selectPage(1);
        dataTable.sort("Name");
        selectRowsPerPage.selectByVisibleText("10");
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Assert
        Assertions.assertEquals(languages.size(), dataTable.getRows().size());

        // Act
        dataTable.filter("Name", "Java");
        PrimeSelenium.guardAjax(page.buttonResetTable).click();

        // Assert
        selectRowsPerPage = new Select(dataTable.getPaginatorWebElement().findElement(By.className("ui-paginator-rpp-options")));
        Assertions.assertEquals("3", selectRowsPerPage.getFirstSelectedOption().getText());
        Assertions.assertEquals(3, dataTable.getRows().size());
        assertRows(dataTable, languages.stream().limit(3).collect(Collectors.toList())); //implicit checks reset sort & filter

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("paginator"));
        Assertions.assertEquals("wgtTable", cfg.getString("widgetVar"));
        Assertions.assertEquals(0, cfg.getInt("tabindex"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:datatable:globalFilter")
        InputText globalFilter;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @FindBy(id = "form:buttonResetTable")
        CommandButton buttonResetTable;
        
        @FindBy(id = "form:buttonGlobalFilterOnly")
        CommandButton buttonGlobalFilterOnly;

        @Override
        public String getLocation() {
            return "datatable/dataTable001.xhtml";
        }
    }
}
