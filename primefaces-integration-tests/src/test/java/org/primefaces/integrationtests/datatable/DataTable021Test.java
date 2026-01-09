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

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Add and remove rows from filtered DataTable
 * https://github.com/primefaces/primefaces/issues/7336
 */
public class DataTable021Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: filter and add row")
    void filterAndAddRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.sort("Name");

        // Act
        dataTable.filter("Name", "Java");

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByName("Java");
        assertRows(dataTable, langsFiltered);

        for (int cnt = 0; cnt < 3; cnt++) {
            // Act
            int rows = dataTable.getRows().size();
            page.buttonAdd.click();

            // Assert - one row more than before
            assertEquals(rows + 1, dataTable.getRows().size());
        }

        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: filter and remove row (manually remove from filteredValue)")
    void filterAndRemoveRowV1(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.sort("Name");

        // Act
        dataTable.filter("Name", "Script");

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByNameContains("Script");
        assertRows(dataTable, langsFiltered);

        for (int cnt = 0; cnt < 2; cnt++) {
            // Act
            int rows = dataTable.getRows().size();
            WebElement removeButton = dataTable.getRow(0).getCell(3).getWebElement().findElement(By.className("ui-button"));
            PrimeSelenium.guardAjax(removeButton).click();

            // Assert - one row less than before
            assertEquals(rows - 1, dataTable.getRows().size());
        }

        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: filter and remove row (DataTable#filterAndSort)")
    void filterAndRemoveRowV2(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.sort("Name");

        // Act
        dataTable.filter("Name", "Script");

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByNameContains("Script");
        assertRows(dataTable, langsFiltered);

        for (int cnt = 0; cnt < 2; cnt++) {
            // Act
            int rows = dataTable.getRows().size();
            WebElement removeButton = dataTable.getRow(0).getCell(4).getWebElement().findElement(By.className("ui-button"));
            PrimeSelenium.guardAjax(removeButton).click();

            // Assert - one row less than before
            assertEquals(rows - 1, dataTable.getRows().size());
        }

        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: remove row without filter or sort applied (manually remove from filteredValue)")
    void removeRowWithoutFilterOrSortAppliedV1(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        int rows = dataTable.getRows().size();

        // Act
        WebElement removeButton = dataTable.getRow(0).getCell(3).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(removeButton).click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: remove row without filter or sort applied (DataTable#filterAndSort)")
    void removeRowWithoutFilterOrSortAppliedV2(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        int rows = dataTable.getRows().size();

        // Act
        WebElement removeButton = dataTable.getRow(0).getCell(4).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(removeButton).click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: remove row with sort but not filter applied (manually remove from filteredValue)")
    void removeRowWithoutFilterAppliedV1(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.sort("Name");
        int rows = dataTable.getRows().size();

        // Act
        WebElement removeButton = dataTable.getRow(0).getCell(3).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(removeButton).click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: remove row with sort but not filter applied (DataTable#filterAndSort)")
    void removeRowWithoutFilterAppliedV2(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.sort("Name");
        int rows = dataTable.getRows().size();

        // Act
        WebElement removeButton = dataTable.getRow(0).getCell(4).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(removeButton).click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("DataTable: add row without filter or sort applied")
    void addRowWithoutFilterOrSortApplied(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        int rows = dataTable.getRows().size();

        // Act
        page.buttonAdd.click();

        // Assert - one row more than before
        assertEquals(rows + 1, dataTable.getRows().size());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(9)
    @DisplayName("DataTable: globalfilter and remove row (manually remove from filteredValue)")
    void globalFilterAndRemoveRowV1(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.sort("Name");

        // Act
        WebElement globalFilterInput = page.globalFilter.getInput();
        globalFilterInput.clear();
        ComponentUtils.sendKeys(globalFilterInput, "Script");
        PrimeSelenium.guardAjax(globalFilterInput).sendKeys(Keys.TAB);

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByNameContains("Script");
        assertRows(dataTable, langsFiltered);
        int rows = dataTable.getRows().size();

        // Act - remove one row
        WebElement removeButton = dataTable.getRow(0).getCell(3).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(removeButton).click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        // Act - remove another row
        rows = dataTable.getRows().size();
        removeButton = dataTable.getRow(0).getCell(3).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(removeButton).click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        // Arrange (remove both removed script-languages from language-list)
        List<ProgrammingLanguage> languagesWithoutScriptLangs = languages.stream().filter(l -> !l.getName().contains("Script")).collect(Collectors.toList());

        // Act
        globalFilterInput = page.globalFilter.getInput();
        globalFilterInput.clear();
        ComponentUtils.sendKeys(globalFilterInput, "Java");
        PrimeSelenium.guardAjax(globalFilterInput).sendKeys(Keys.TAB);

        // Assert
        langsFiltered = languagesWithoutScriptLangs.stream().filter(l -> l.getName().contains("Java")).collect(Collectors.toList());
        assertRows(dataTable, langsFiltered);
        rows = dataTable.getRows().size();

        // Act
        page.buttonDeleteRow.click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        assertNoJavascriptErrors();
    }

    @Test
    @Order(10)
    @DisplayName("DataTable: globalfilter and remove row (DataTable#filterAndSort)")
    void globalFilterAndRemoveRowV2(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.sort("Name");

        // Act
        WebElement globalFilterInput = page.globalFilter.getInput();
        globalFilterInput.clear();
        ComponentUtils.sendKeys(globalFilterInput, "Script");
        PrimeSelenium.guardAjax(globalFilterInput).sendKeys(Keys.TAB);

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByNameContains("Script");
        assertRows(dataTable, langsFiltered);
        int rows = dataTable.getRows().size();

        // Act - remove one row
        WebElement removeButton = dataTable.getRow(0).getCell(4).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(removeButton).click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        // Act - remove another row
        rows = dataTable.getRows().size();
        removeButton = dataTable.getRow(0).getCell(4).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(removeButton).click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        // Arrange (remove both removed script-languages from language-list)
        List<ProgrammingLanguage> languagesWithoutScriptLangs = languages.stream().filter(l -> !l.getName().contains("Script")).collect(Collectors.toList());

        // Act
        globalFilterInput = page.globalFilter.getInput();
        globalFilterInput.clear();
        ComponentUtils.sendKeys(globalFilterInput, "Java");
        PrimeSelenium.guardAjax(globalFilterInput).sendKeys(Keys.TAB);

        // Assert
        langsFiltered = languagesWithoutScriptLangs.stream().filter(l -> l.getName().contains("Java")).collect(Collectors.toList());
        assertRows(dataTable, langsFiltered);
        rows = dataTable.getRows().size();

        // Act
        page.buttonDeleteRow.click();

        // Assert - one row less than before
        assertEquals(rows - 1, dataTable.getRows().size());

        assertNoJavascriptErrors();
    }

    public List<ProgrammingLanguage> filterByNameContains(final String name) {
        return languages.stream()
                .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                .filter(l -> l.getName().contains(name))
                .limit(3)
                .collect(Collectors.toList());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:datatable:globalFilter")
        InputText globalFilter;

        @FindBy(id = "form:datatable:buttonDeleteRow")
        CommandButton buttonDeleteRow;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @FindBy(id = "form:buttonResetTable")
        CommandButton buttonResetTable;

        @FindBy(id = "form:buttonAdd")
        CommandButton buttonAdd;

        @Override
        public String getLocation() {
            return "datatable/dataTable021.xhtml";
        }
    }
}
