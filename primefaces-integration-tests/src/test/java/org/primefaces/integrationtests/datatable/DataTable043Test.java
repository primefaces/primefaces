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
package org.primefaces.integrationtests.datatable;

import org.primefaces.integrationtests.datatable.ProgrammingLanguage.ProgrammingLanguageType;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.InputText;

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

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * DataTable #11135: support composite as filter.
 *
 */
class DataTable043Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable #11135: support composite as filter")
    void filter(Page page) {
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
    @Order(2)
    @DisplayName("DataTable #11135: support composite as filter plus paging")
    void filterPlusPaging(Page page) {
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
    @Order(3)
    @DisplayName("DataTable #11135: support composite global filter with globalFilterOnly=false")
    void globalFilter(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        WebElement globalFilter = page.globalFilter;
        assertNotNull(globalFilter);
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
    @Order(4)
    @DisplayName("DataTable #11135: support composite global filter with globalFilterOnly=true")
    void globalFilterIncludeNotDisplayedFilter(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        WebElement globalFilter = page.globalFilter;
        assertNotNull(globalFilter);
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


    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertTrue(cfg.has("paginator"));
        assertEquals("wgtTable", cfg.getString("widgetVar"));
        assertEquals(0, cfg.getInt("tabindex"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;
        @FindBy(id = "form:datatable:globalFilter:filter")
        InputText globalFilter;
        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;
        @FindBy(id = "form:buttonGlobalFilterOnly")
        CommandButton buttonGlobalFilterOnly;

        @Override
        public String getLocation() {
            return "datatable/dataTable043.xhtml";
        }
    }
}
