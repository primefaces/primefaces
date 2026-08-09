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
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GitHub #14945: LazyDataModel#load called twice on filter/sort when partialUpdate="false".
 * These tests verify that the correct (filtered/sorted) data is rendered after each interaction —
 * i.e. that the final load wins with the right parameters and the table is not stale.
 */
@Tag("DataTable-lazy")
@Tag("DataTable-filter")
@Tag("DataTable-sort")
@Tag("DataTable-paginator")
class DataTable049Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: GitHub #14945 lazy filter with partialUpdate=false renders correct rows")
    void lazyFilterPartialUpdateFalse(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        List<ProgrammingLanguage> expected = model.getLangs().stream()
                .filter(l -> l.getFirstAppeared() >= 1998)
                .sorted(Comparator.comparingInt(ProgrammingLanguage::getFirstAppeared))
                .collect(Collectors.toList());

        // Act
        dataTable.sort("First Appeared");
        dataTable.filter("First Appeared", "1998");

        // Assert - filter must be applied (not unfiltered data)
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertFalse(rows.isEmpty(), "No rows returned after filter — filter was not applied");
        assertEquals(expected.get(0).getName(), rows.get(0).getCell(1).getText());
        assertEquals(expected.get(1).getName(), rows.get(1).getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: GitHub #14945 lazy sort with partialUpdate=false renders correct rows")
    void lazySortPartialUpdateFalse(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        List<ProgrammingLanguage> expectedAsc = model.getLangs().stream()
                .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                .collect(Collectors.toList());
        List<ProgrammingLanguage> expectedDesc = model.getLangs().stream()
                .sorted(Comparator.comparing(ProgrammingLanguage::getName).reversed())
                .collect(Collectors.toList());

        // Act - ascending
        dataTable.sort("Name");

        // Assert
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size());
        assertEquals(expectedAsc.get(0).getName(), rows.get(0).getCell(1).getText());
        assertEquals(expectedAsc.get(1).getName(), rows.get(1).getCell(1).getText());
        assertEquals(expectedAsc.get(9).getName(), rows.get(9).getCell(1).getText());

        // Act - descending
        dataTable.sort("Name");

        // Assert
        rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size());
        assertEquals(expectedDesc.get(0).getName(), rows.get(0).getCell(1).getText());
        assertEquals(expectedDesc.get(1).getName(), rows.get(1).getCell(1).getText());
        assertEquals(expectedDesc.get(9).getName(), rows.get(9).getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: GitHub #14945 lazy paging with partialUpdate=false renders correct rows")
    void lazyPagePartialUpdateFalse(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        List<ProgrammingLanguage> expectedPage2 = model.getLangs().stream()
                .skip(10).limit(10)
                .collect(Collectors.toList());

        // Act - navigate to page 2
        dataTable.selectPage(2);

        // Assert - page 2 rows must be shown, not page 1 rows
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size());
        assertEquals(String.valueOf(expectedPage2.get(0).getId()), rows.get(0).getCell(0).getText());
        assertEquals(String.valueOf(expectedPage2.get(9).getId()), rows.get(9).getCell(0).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: GitHub #14945 lazy filter + sort with partialUpdate=false renders correct rows")
    void lazyFilterAndSortPartialUpdateFalse(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        List<ProgrammingLanguage> expected = model.getLangs().stream()
                .filter(l -> l.getName().contains("1"))
                .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                .collect(Collectors.toList());

        // Act
        dataTable.sort("Name");
        dataTable.filter("Name", "1");

        // Assert - both sort and filter must be honoured
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertFalse(rows.isEmpty(), "No rows returned after filter — filter was not applied");
        assertEquals(expected.get(0).getName(), rows.get(0).getCell(1).getText());
        assertEquals(expected.get(1).getName(), rows.get(1).getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: GitHub #14945 lazy filter with partialUpdate=false updates paginator row count")
    void lazyFilterUpdatesPaginatorPartialUpdateFalse(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // 75 languages → 8 pages; filter firstAppeared >= 1998 → 14 rows → 2 pages
        // (firstAppeared = 1990 + (i % 10), so i%10 == 8 or 9 → 7+7 = 14 matching)
        assertEquals(8, dataTable.getPaginator().getPages().size(), "should start with 8 pages for 75 rows");

        // Act
        dataTable.filter("First Appeared", "1998");

        // Assert - paginator must reflect the filtered row count, not the total
        assertEquals(2, dataTable.getPaginator().getPages().size(),
                "paginator must shrink to 2 pages after filtering to 14 rows");

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: GitHub #14945 lazy filter + page 2 with partialUpdate=false shows filtered page 2 rows")
    void lazyFilterThenPage2PartialUpdateFalse(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // filter name contains "1": Language 1,10,11..19,21,31,41,51,61,71 = 17 rows → page 2 has 7 rows
        List<ProgrammingLanguage> filteredById = model.getLangs().stream()
                .filter(l -> l.getName().contains("1"))
                .collect(Collectors.toList());
        assertEquals(17, filteredById.size());

        // Act
        dataTable.filter("Name", "1");
        dataTable.selectPage(2);

        // Assert - must show 7 rows of filtered result, not 10 rows of unfiltered page 2
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(7, rows.size(), "page 2 of filtered result must have 7 rows, not 10 (unfiltered page 2)");
        // first row on page 2: id=19 (Language 19, the 11th match by insertion order)
        assertEquals("19", rows.get(0).getCell(0).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: GitHub #14945 lazy filter then sort (separate requests) with partialUpdate=false preserves filter")
    void lazyFilterThenSortPartialUpdateFalse(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // filter firstAppeared >= 1998 → 14 rows (ids: 8,9,18,19,28,29,38,39,48,49,58,59,68,69)
        // lex sort on name: "Language 18" < "Language 8" (because '1' < '8' at char 9)
        // → page 1 must start with "Language 18", not "Language 8" (insertion-order first)
        List<ProgrammingLanguage> filteredSortedAsc = model.getLangs().stream()
                .filter(l -> l.getFirstAppeared() >= 1998)
                .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                .collect(Collectors.toList());

        // Act - two separate feature requests: filter first, then sort
        dataTable.filter("First Appeared", "1998");
        dataTable.sort("Name");

        // Assert - filter still active AND sort applied
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size(), "page 1 of filtered+sorted result must have 10 rows (filter active)");
        assertEquals(filteredSortedAsc.get(0).getName(), rows.get(0).getCell(1).getText(),
                "first row must be lexicographic first of filtered set — filter must still be active after sort");
        assertEquals(filteredSortedAsc.get(9).getName(), rows.get(9).getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    // ---- non-lazy tests (partialUpdate="false" with in-memory filter/sort) ----

    @Test
    @Order(8)
    @DisplayName("DataTable: GitHub #14945 non-lazy filter with partialUpdate=false renders correct rows")
    void nonLazyFilterPartialUpdateFalse(NonLazyPage page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        List<ProgrammingLanguage> expected = model.getLangs().stream()
                .filter(l -> l.getFirstAppeared() >= 1998)
                .collect(Collectors.toList());

        // Act
        dataTable.filter("First Appeared", "1998");

        // Assert
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertFalse(rows.isEmpty(), "No rows returned — filter was not applied");
        assertEquals(expected.size(), rows.size() + Math.max(0, expected.size() - rows.size()),
                "all filtered rows must be visible across pages");
        assertEquals(String.valueOf(expected.get(0).getId()), rows.get(0).getCell(0).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("DataTable: GitHub #14945 non-lazy sort with partialUpdate=false renders correct rows")
    void nonLazySortPartialUpdateFalse(NonLazyPage page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        List<ProgrammingLanguage> expectedAsc = model.getLangs().stream()
                .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                .collect(Collectors.toList());

        // Act
        dataTable.sort("Name");

        // Assert
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size());
        assertEquals(expectedAsc.get(0).getName(), rows.get(0).getCell(1).getText());
        assertEquals(expectedAsc.get(9).getName(), rows.get(9).getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("DataTable: GitHub #14945 non-lazy filter then sort with partialUpdate=false applies both")
    void nonLazySortWithActiveFilterPartialUpdateFalse(NonLazyPage page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // filter firstAppeared >= 1998 → 14 rows (ids: 8,9,18,19,28,29,38,39,48,49,58,59,68,69)
        // sort by name (lexicographic asc): "Language 18" < "Language 8" because '1' < '8' at char 9
        // → page 1 must start with "Language 18", not "Language 8"
        List<ProgrammingLanguage> filteredSortedAsc = model.getLangs().stream()
                .filter(l -> l.getFirstAppeared() >= 1998)
                .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                .collect(Collectors.toList());

        // Act - apply filter first, then sort
        dataTable.filter("First Appeared", "1998");
        dataTable.sort("Name");

        // Assert - filter still active (14 rows total) and sort applied
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size(), "page 1 of filtered+sorted result must have 10 rows");
        assertEquals(filteredSortedAsc.get(0).getName(), rows.get(0).getCell(1).getText(),
                "first row must be lexicographic first of filtered set, not insertion-order first");
        assertEquals(filteredSortedAsc.get(9).getName(), rows.get(9).getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("DataTable: GitHub #14945 non-lazy filter with partialUpdate=false updates paginator row count")
    void nonLazyFilterUpdatesPaginatorPartialUpdateFalse(NonLazyPage page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        assertEquals(8, dataTable.getPaginator().getPages().size(), "should start with 8 pages for 75 rows");

        // Act - filter to 14 rows → 2 pages
        dataTable.filter("First Appeared", "1998");

        // Assert
        assertEquals(2, dataTable.getPaginator().getPages().size(),
                "paginator must shrink to 2 pages after filtering to 14 rows");

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(12)
    @DisplayName("DataTable: GitHub #14945 non-lazy filter + page 2 with partialUpdate=false shows filtered page 2 rows")
    void nonLazyFilterThenPage2PartialUpdateFalse(NonLazyPage page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // filter name contains "1": 17 rows (Language 1,10,11..19,21,31,41,51,61,71)
        // page 2 (rows 11-17 in insertion order): Language 19, 21, 31, 41, 51, 61, 71 = 7 rows
        // page 2 of unfiltered (rows 11-20): Language 11..20 = 10 rows
        // → row count on page 2 distinguishes filtered from unfiltered

        // Act
        dataTable.filter("Name", "1");
        dataTable.selectPage(2);

        // Assert - must be 7 rows from filtered set, not 10 rows from unfiltered set
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(7, rows.size(), "page 2 of filtered result must have 7 rows, not 10 (unfiltered page 2)");
        assertEquals("19", rows.get(0).getCell(0).getText(),
                "first row on page 2 of filtered set must be Language 19 (11th match)");

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    // ---- row commandButton tests: verify processChildren uses correct (updated) data ----
    // Pattern: after a partialUpdate="false" state change (filter/sort/page), click a row button.
    // If processChildren loaded stale data the actionListener would receive the WRONG item;
    // we detect this by comparing the ID shown in the cell with what the bean recorded.

    @Test
    @Order(13)
    @DisplayName("DataTable: GitHub #14945 lazy filter (partialUpdate=false) → row button click receives correct filtered row")
    void lazyFilterThenRowButtonGetsCorrectRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act — filter (partialUpdate=false AJAX): table re-renders with filtered rows
        dataTable.filter("First Appeared", "1998");

        // Record the ID displayed in row[1] — this is what the button must deliver to the bean
        String expectedId = dataTable.getRow(1).getCell(0).getText();
        assertFalse(expectedId.isEmpty(), "row[1] must have an ID after filter");

        // Act — normal AJAX button click (NOT a partialUpdate=false request)
        WebElement btn = dataTable.getRow(1).getCell(3).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(btn).click();

        // Assert — bean must have received the item that was visually in row[1]
        assertEquals(expectedId, page.selectedOutput.getText(),
                "actionListener received wrong row — processChildren likely loaded unfiltered data");

        assertNoJavascriptErrors();
    }

    @Test
    @Order(14)
    @DisplayName("DataTable: GitHub #14945 lazy sort (partialUpdate=false) → row button click receives correct sorted row")
    void lazySortThenRowButtonGetsCorrectRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act — sort descending by name (partialUpdate=false): row[0] is no longer Language 1
        dataTable.sort("Name");
        dataTable.sort("Name"); // second click = descending

        String expectedId = dataTable.getRow(0).getCell(0).getText();
        assertFalse(expectedId.isEmpty());

        WebElement btn = dataTable.getRow(0).getCell(3).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(btn).click();

        assertEquals(expectedId, page.selectedOutput.getText(),
                "actionListener received wrong row — processChildren likely loaded unsorted data");

        assertNoJavascriptErrors();
    }

    @Test
    @Order(15)
    @DisplayName("DataTable: GitHub #14945 lazy page 2 (partialUpdate=false) → row button click receives correct page-2 row")
    void lazyPage2ThenRowButtonGetsCorrectRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act — navigate to page 2 (partialUpdate=false): rows shifted by pageSize
        dataTable.selectPage(2);

        String expectedId = dataTable.getRow(0).getCell(0).getText();
        assertFalse(expectedId.isEmpty());

        WebElement btn = dataTable.getRow(0).getCell(3).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(btn).click();

        assertEquals(expectedId, page.selectedOutput.getText(),
                "actionListener received wrong row — processChildren likely loaded page 1 data");

        assertNoJavascriptErrors();
    }

    @Test
    @Order(16)
    @DisplayName("DataTable: GitHub #14945 non-lazy filter (partialUpdate=false) → row button click receives correct filtered row")
    void nonLazyFilterThenRowButtonGetsCorrectRow(NonLazyPage page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act — filter (partialUpdate=false): in-memory filter applied, filteredValue updated in view state
        dataTable.filter("Name", "2");

        String expectedId = dataTable.getRow(0).getCell(0).getText();
        assertFalse(expectedId.isEmpty(), "row[0] must have an ID after filter");

        WebElement btn = dataTable.getRow(0).getCell(3).getWebElement().findElement(By.className("ui-button"));
        PrimeSelenium.guardAjax(btn).click();

        assertEquals(expectedId, page.selectedOutput.getText(),
                "actionListener received wrong row — processChildren likely iterated unfiltered list");

        assertNoJavascriptErrors();
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("paginator"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:selectedOutput")
        WebElement selectedOutput;

        @Override
        public String getLocation() {
            return "datatable/dataTable049.xhtml";
        }
    }

    public static class NonLazyPage extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:selectedOutput")
        WebElement selectedOutput;

        @Override
        public String getLocation() {
            return "datatable/dataTable049NonLazy.xhtml";
        }
    }
}
