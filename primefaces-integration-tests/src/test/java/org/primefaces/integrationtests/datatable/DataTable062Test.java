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
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("DataTable-rowgroup")
@Tag("DataTable-sort")
@Tag("DataTable-lazy")
@Tag("DataTable-mvs")
class DataTable062Test extends AbstractDataTableTest {

    private static final Comparator<ProgrammingLanguage> BY_YEAR = Comparator.comparing(ProgrammingLanguage::getFirstAppeared);
    private static final Comparator<ProgrammingLanguage> BY_TYPE = Comparator.comparing(ProgrammingLanguage::getType);
    private static final Comparator<ProgrammingLanguage> BY_NAME = Comparator.comparing(ProgrammingLanguage::getName);

    @Test
    @Order(1)
    @DisplayName("DataTable: RowGroup - headerRow initially not rendered groups once it gets rendered; see GitHub #10481")
    void headerRowRenderedLater(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertTrue(getHeaderRows(dataTable).isEmpty());
        assertRows(dataTable, languages);

        // Act
        PrimeSelenium.guardAjax(page.year).click();

        // Assert
        assertEquals(List.of("1990", "1995", "2000", "2012"), getHeaderRows(dataTable));
        assertRows(getDataRows(dataTable), sortByNoLimit(BY_YEAR));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: RowGroup - headerRow no longer rendered does not group the sorting anymore; see GitHub #10481")
    void headerRowNotRenderedAnymore(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        PrimeSelenium.guardAjax(page.year).click();
        assertEquals(4, getHeaderRows(dataTable).size());

        // Act
        PrimeSelenium.guardAjax(page.none).click();
        dataTable.sort("Name");

        // Assert - the sort event does not contain the headerRow which is not rendered either
        assertTrue(getHeaderRows(dataTable).isEmpty());
        assertRows(dataTable, sortByNoLimit(BY_NAME));
        assertEquals("0", page.sortEventHeaderRows.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: RowGroup - switching between two headerRows groups by the rendered one; see GitHub #10481")
    void switchBetweenHeaderRows(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        PrimeSelenium.guardAjax(page.year).click();
        assertEquals(4, getHeaderRows(dataTable).size());

        // Act
        PrimeSelenium.guardAjax(page.type).click();

        // Assert - the in-memory sort is stable on the list it sorted by year before
        assertEquals(List.of("COMPILED", "INTERPRETED"), getHeaderRows(dataTable));
        assertRows(getDataRows(dataTable), sortByNoLimit(BY_TYPE.thenComparing(BY_YEAR)));

        // Act
        dataTable.sort("Name");

        // Assert - grouped by type first, then sorted by name; the sort event only has the rendered headerRow
        assertEquals(List.of("COMPILED", "INTERPRETED"), getHeaderRows(dataTable));
        assertRows(getDataRows(dataTable), sortByNoLimit(BY_TYPE.thenComparing(BY_NAME)));
        assertEquals("1", page.sortEventHeaderRows.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: RowGroup - lazy model only gets the sort of the rendered headerRow; see GitHub #10481")
    void lazyHeaderRows(Page page) {
        // Arrange
        assertEquals("", page.lastSortFields.getText());

        // Act + Assert
        PrimeSelenium.guardAjax(page.year).click();
        assertEquals("firstAppeared", page.lastSortFields.getText());

        PrimeSelenium.guardAjax(page.type).click();
        assertEquals("type", page.lastSortFields.getText());
        // the first page of the 75 rows sorted by type has only compiled ones
        assertEquals(List.of("COMPILED"), getHeaderRows(page.lazyDataTable));

        PrimeSelenium.guardAjax(page.none).click();
        assertEquals("", page.lastSortFields.getText());
        assertTrue(getHeaderRows(page.lazyDataTable).isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: RowGroup - MultiViewState keeps the sort, grouping follows the rendered headerRow; see GitHub #10481")
    void multiViewState(MultiViewStatePage page) {
        // Arrange
        PrimeSelenium.goTo("datatable/dataTable062MultiViewState.xhtml?mode=year");
        PrimeSelenium.guardAjax(page.clearTableState).click();
        page.dataTable.sort("Name");
        assertEquals(List.of("1990", "1995", "2000", "2012"), getHeaderRows(page.dataTable));
        assertRows(getDataRows(page.dataTable), sortByNoLimit(BY_YEAR.thenComparing(BY_NAME)));

        // Act + Assert - the headerRows toggled within the same view
        PrimeSelenium.guardAjax(page.none).click();
        assertTrue(getHeaderRows(page.dataTable).isEmpty());
        assertRows(page.dataTable, sortByNoLimit(BY_NAME));
        PrimeSelenium.guardAjax(page.type).click();
        assertEquals(List.of("COMPILED", "INTERPRETED"), getHeaderRows(page.dataTable));
        assertRows(getDataRows(page.dataTable), sortByNoLimit(BY_TYPE.thenComparing(BY_NAME)));

        // Act - a new view without the headerRow
        PrimeSelenium.goTo("datatable/dataTable062MultiViewState.xhtml?mode=none");

        // Assert
        assertTrue(getHeaderRows(page.dataTable).isEmpty());
        assertRows(page.dataTable, sortByNoLimit(BY_NAME));

        // Act - a new view with the other headerRow
        PrimeSelenium.goTo("datatable/dataTable062MultiViewState.xhtml?mode=type");

        // Assert
        assertEquals(List.of("COMPILED", "INTERPRETED"), getHeaderRows(page.dataTable));
        assertRows(getDataRows(page.dataTable), sortByNoLimit(BY_TYPE.thenComparing(BY_NAME)));

        PrimeSelenium.guardAjax(page.clearTableState).click();
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: RowGroup - headerRow with an EL field set by the action which renders it; see GitHub #10481")
    void headerRowWithElField(Page page) {
        // Arrange - the headerRow which is not rendered is not evaluated, otherwise the page would fail
        assertTrue(getHeaderRows(page.elFieldDataTable).isEmpty());

        // Act
        PrimeSelenium.guardAjax(page.groupFieldType).click();

        // Assert
        assertEquals(List.of("COMPILED", "INTERPRETED"), getHeaderRows(page.elFieldDataTable));
        assertRows(getDataRows(page.elFieldDataTable), sortByNoLimit(BY_TYPE));

        // Act - another field for the same headerRow
        PrimeSelenium.guardAjax(page.groupFieldYear).click();

        // Assert
        assertEquals(List.of("1990", "1995", "2000", "2012"), getHeaderRows(page.elFieldDataTable));
        // the in-memory sort is stable on the list it sorted by type before
        assertRows(getDataRows(page.elFieldDataTable), sortByNoLimit(BY_YEAR.thenComparing(BY_TYPE)));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: RowGroup - headerRow of a table in ui:repeat follows its rendered state per iteration; see GitHub #10481")
    void headerRowInRepeat(Page page) {
        // the headerRow is only rendered for the first iteration
        assertEquals(List.of("1990", "1995", "2000", "2012"), getRepeatedHeaderRows(page, "0"));
        assertTrue(getRepeatedHeaderRows(page, "1").isEmpty());

        // the second iteration (with its own list) is not sorted by the headerRow of the first one
        DataTable second = PrimeSelenium.createFragment(DataTable.class, By.id("form:repeat:1:repeatedDatatable"));
        assertRows(second, languages);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("DataTable: RowGroup - a user sortBy for the field of the headerRow is kept; see GitHub #10481")
    void headerRowWithUserSortBy(Page page) {
        // Arrange
        assertEquals(List.of("2012", "2000", "1995", "1990"), getHeaderRows(page.userSortByDataTable));

        // Act - decodes and encodes the table again
        page.userSortByDataTable.sort("Name");

        // Assert - still grouped by year descending, then sorted by name
        assertEquals(List.of("2012", "2000", "1995", "1990"), getHeaderRows(page.userSortByDataTable));
        assertRows(getDataRows(page.userSortByDataTable), sortByNoLimit(BY_YEAR.reversed().thenComparing(BY_NAME)));
        assertNoJavascriptErrors();
    }

    private List<String> getRepeatedHeaderRows(Page page, String iteration) {
        return page.getWebDriver().findElement(By.id("form:repeat:" + iteration + ":repeatedDatatable"))
                .findElements(By.cssSelector("tbody > tr.p-rowgroup-header")).stream()
                .map(row -> row.getText().trim())
                .collect(Collectors.toList());
    }

    private List<String> getHeaderRows(DataTable dataTable) {
        return dataTable.getRows().stream()
                .filter(this::isHeaderRow)
                .map(row -> row.getCell(0).getText())
                .collect(Collectors.toList());
    }

    private List<Row> getDataRows(DataTable dataTable) {
        return dataTable.getRows().stream()
                .filter(row -> !isHeaderRow(row))
                .collect(Collectors.toList());
    }

    private boolean isHeaderRow(Row row) {
        return PrimeSelenium.hasCssClass(row.getWebElement(), "p-rowgroup-header");
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:lazyDatatable")
        DataTable lazyDataTable;

        @FindBy(id = "form:lastSortFields")
        WebElement lastSortFields;

        @FindBy(id = "form:sortEventHeaderRows")
        WebElement sortEventHeaderRows;

        @FindBy(id = "form:none")
        CommandButton none;

        @FindBy(id = "form:year")
        CommandButton year;

        @FindBy(id = "form:type")
        CommandButton type;

        @FindBy(id = "form:elFieldDatatable")
        DataTable elFieldDataTable;

        @FindBy(id = "form:userSortByDatatable")
        DataTable userSortByDataTable;

        @FindBy(id = "form:groupFieldType")
        CommandButton groupFieldType;

        @FindBy(id = "form:groupFieldYear")
        CommandButton groupFieldYear;

        @Override
        public String getLocation() {
            return "datatable/dataTable062.xhtml";
        }
    }

    public static class MultiViewStatePage extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:none")
        CommandButton none;

        @FindBy(id = "form:type")
        CommandButton type;

        @FindBy(id = "form:clearTableState")
        CommandButton clearTableState;

        @Override
        public String getLocation() {
            return "datatable/dataTable062MultiViewState.xhtml";
        }
    }
}
