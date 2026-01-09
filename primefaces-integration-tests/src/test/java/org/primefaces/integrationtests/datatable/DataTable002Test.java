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

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectCheckboxMenu;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

class DataTable002Test extends AbstractDataTableTest {

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(1)
    @DisplayName("DataTable: Lazy: Basic & Paginator")
    void lazyAndPaginator(String xhtml) {
        // Arrange
        goTo(xhtml);
        DataTable dataTable = getDataTable();
        assertNotNull(dataTable);

        // Act
        //page.button.click();
        // Assert
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size());

        Row firstRow = dataTable.getRow(0);
        assertEquals("1", firstRow.getCell(0).getText());
        assertEquals("Language 1", firstRow.getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());

        // Act - second page
        dataTable.selectPage(2);

        // Assert
        rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size());

        firstRow = dataTable.getRow(0);
        assertEquals("11", firstRow.getCell(0).getText());
        assertEquals("Language 11", firstRow.getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());

        // Act - last page
        dataTable.selectPage(8);

        // Assert
        rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(5, rows.size());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(2)
    @DisplayName("DataTable: Lazy: single sort")
    void lazySortSingle(String xhtml) {
        // Arrange
        goTo(xhtml);
        DataTable dataTable = getDataTable();
        assertNotNull(dataTable);
        List<ProgrammingLanguage> langsAsc = model.getLangs().stream().sorted(Comparator.comparing(ProgrammingLanguage::getName)).collect(Collectors.toList());
        List<ProgrammingLanguage> langsDesc = model.getLangs().stream().sorted(Comparator.comparing(ProgrammingLanguage::getName).reversed())
                .collect(Collectors.toList());

        // Act - ascending
        dataTable.selectPage(1);
        dataTable.sort("Name");

        // Assert
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size());

        assertEquals(langsAsc.get(0).getName(), rows.get(0).getCell(1).getText());
        assertEquals(langsAsc.get(1).getName(), rows.get(1).getCell(1).getText());
        assertEquals(langsAsc.get(9).getName(), rows.get(9).getCell(1).getText());

        // Act - descending
        dataTable.sort("Name");

        // Assert
        rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size());

        assertEquals(langsDesc.get(0).getName(), rows.get(0).getCell(1).getText());
        assertEquals(langsDesc.get(1).getName(), rows.get(1).getCell(1).getText());
        assertEquals(langsDesc.get(9).getName(), rows.get(9).getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(3)
    @DisplayName("DataTable: Lazy: filter")
    void lazyFilter(String xhtml) {
        // Arrange
        goTo(xhtml);
        DataTable dataTable = getDataTable();
        assertNotNull(dataTable);
        List<ProgrammingLanguage> langsFiltered = model.getLangs().stream()
                .filter(l -> l.getFirstAppeared() >= 1998)
                .sorted(Comparator.comparingInt(ProgrammingLanguage::getFirstAppeared))
                .collect(Collectors.toList());

        // Act
        dataTable.selectPage(1);
        dataTable.sort("First Appeared");
        dataTable.filter("First Appeared", "1998");

        // Assert
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size()); //one page
        assertEquals(langsFiltered.get(0).getName(), rows.get(0).getCell(1).getText());
        assertEquals(langsFiltered.get(1).getName(), rows.get(1).getCell(1).getText());

        // Act
        getButtonUpdate().click();

        // Assert - filter must not be lost after update
        rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size()); //one page
        assertEquals(langsFiltered.get(0).getName(), rows.get(0).getCell(1).getText());
        assertEquals(langsFiltered.get(1).getName(), rows.get(1).getCell(1).getText());
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(4)
    @DisplayName("DataTable: Lazy: filter - selectCheckboxMenu")
    void lazyFilterSelectCheckboxMenu(String xhtml) {
        // Arrange
        goTo(xhtml);
        DataTable dataTable = getDataTable();
        assertNotNull(dataTable);
        List<ProgrammingLanguage> langsFiltered = model.getLangs().stream()
                .filter(l -> l.getType() == ProgrammingLanguage.ProgrammingLanguageType.COMPILED)
                .sorted(Comparator.comparingInt(ProgrammingLanguage::getFirstAppeared))
                .collect(Collectors.toList());

        // Act
        dataTable.selectPage(1);
        dataTable.sort("First Appeared");
        SelectCheckboxMenu filterType = getFilterType();
        filterType.togglePanel();
        List<WebElement> filterTypeCheckboxes = filterType.getPanel().findElements(By.cssSelector(".ui-chkbox-box"));
        PrimeSelenium.guardAjax(filterTypeCheckboxes.get(1)).click();

        // Assert
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size()); //one page
        for (int row = 0; row < 10; row++) {
            assertEquals(langsFiltered.get(row).getName(), rows.get(row).getCell(1).getText());
        }

        // Act
        getButtonUpdate().click();

        // Assert - filter must not be lost after update
        rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size()); //one page
        for (int row = 0; row < 10; row++) {
            assertEquals(langsFiltered.get(row).getName(), rows.get(row).getCell(1).getText());
        }
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(20)
    @DisplayName("DataTable: Lazy: rowSelect-event")
    void lazyRowSelect(String xhtml) {
        // Arrange
        goTo(xhtml);
        DataTable dataTable = getDataTable();
        assertNotNull(dataTable);

        // Act
        PrimeSelenium.guardAjax(dataTable.getCell(3, 0).getWebElement()).click();

        // Assert
        assertEquals(1, getMessages().getAllMessages().size());
        assertEquals("ProgrammingLanguage Selected", getMessages().getMessage(0).getSummary());
        String row3ProgLang = dataTable.getRow(3).getCell(0).getText() + " - " + dataTable.getCell(3, 1).getText();
        assertEquals(row3ProgLang, getMessages().getMessage(0).getDetail());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(21)
    @DisplayName("DataTable: Lazy: rowSelect-event with filter applied before")
    void lazyRowSelectWithFilterApplied(String xhtml) {
        // Arrange
        goTo(xhtml);
        DataTable dataTable = getDataTable();
        assertNotNull(dataTable);
        dataTable.selectPage(1);
        dataTable.sort("First Appeared");
        dataTable.filter("First Appeared", "1998");

        // Act
        PrimeSelenium.guardAjax(dataTable.getCell(3, 0).getWebElement()).click();

        // Assert
        assertEquals(1, getMessages().getAllMessages().size());
        assertEquals("ProgrammingLanguage Selected", getMessages().getMessage(0).getSummary());
        String row3ProgLang = dataTable.getRow(3).getCell(0).getText() + " - " + dataTable.getCell(3, 1).getText();
        assertEquals(row3ProgLang, getMessages().getMessage(0).getDetail());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(22)
    @DisplayName("DataTable: Lazy: selection with filter applied before")
    void lazySelectionWithFilterApplied(String xhtml) {
        // Arrange
        goTo(xhtml);
        DataTable dataTable = getDataTable();
        assertNotNull(dataTable);
        dataTable.selectPage(1);
        dataTable.sort("First Appeared");
        dataTable.filter("First Appeared", "1998");

        // Act
        PrimeSelenium.guardAjax(dataTable.getCell(3, 0).getWebElement()).click();
        getButtonSubmit().click();

        // Assert
        assertEquals(1, getMessages().getAllMessages().size());
        assertEquals("Selected ProgrammingLanguage", getMessages().getMessage(0).getSummary());
        String row3ProgLang = getDataTable().getRow(3).getCell(0).getText();
        assertEquals(row3ProgLang, getMessages().getMessage(0).getDetail());

        assertConfiguration(getDataTable().getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(30)
    @DisplayName("DataTable: Lazy: delete rows from last page - https://github.com/primefaces/primefaces/issues/1921")
    void lazyRowDeleteFromLastPage(String xhtml) {
        // Arrange
        goTo(xhtml);
        DataTable dataTable = getDataTable();
        assertNotNull(dataTable);
        dataTable.selectPage(dataTable.getPaginator().getPages().size());
        int colDeleteButton = 4;

        // Act & Assert
        for (int row = 5; row > 1; row--) {
            assertEquals(row, getDataTable().getRows().size());
            PrimeSelenium.guardAjax(getDataTable().getCell(0, colDeleteButton).getWebElement().findElement(By.className("ui-button"))).click();
            assertEquals(8, getDataTable().getPaginator().getActivePage().getNumber());
        }

        // Act & Assert - delete last row on page 8
        PrimeSelenium.guardAjax(getDataTable().getCell(0, colDeleteButton).getWebElement().findElement(By.className("ui-button"))).click();
        assertEquals(7, getDataTable().getPaginator().getActivePage().getNumber());
        assertEquals(10, getDataTable().getRows().size());

        // Act & Assert - select first row on page 7
        PrimeSelenium.guardAjax(getDataTable().getCell(0, 0).getWebElement()).click();
        assertEquals(1, getMessages().getAllMessages().size());
        assertEquals("ProgrammingLanguage Selected", getMessages().getMessage(0).getSummary());
        String row0ProgLang = getDataTable().getRow(0).getCell(0).getText() + " - " + getDataTable().getCell(0, 1).getText();
        assertEquals(row0ProgLang, getMessages().getMessage(0).getDetail());

        // Act & Assert - delete first row on page 7
        PrimeSelenium.guardAjax(getDataTable().getCell(0, colDeleteButton).getWebElement().findElement(By.className("ui-button"))).click();
        assertEquals(1, getMessages().getAllMessages().size());
        assertEquals("ProgrammingLanguage Deleted", getMessages().getMessage(0).getSummary());
        assertEquals(row0ProgLang, getMessages().getMessage(0).getDetail());
        assertEquals(7, getDataTable().getPaginator().getActivePage().getNumber());
        assertEquals(9, getDataTable().getRows().size());

        assertConfiguration(getDataTable().getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertTrue(cfg.has("paginator"));
    }

    private static Stream<Arguments> provideXhtmls() {
        return Stream.of(
                Arguments.of("datatable/dataTable002.xhtml"),
                Arguments.of("datatable/dataTable002RowCountOtherImpl.xhtml"),
                Arguments.of("datatable/dataTable002NonLazy.xhtml"),
                Arguments.of("datatable/dataTable002ReflectionLazyDataModel.xhtml"));
    }

    private DataTable getDataTable() {
        return PrimeSelenium.createFragment(DataTable.class, By.id("form:datatable"));
    }

    private Messages getMessages() {
        return PrimeSelenium.createFragment(Messages.class, By.id("form:msgs"));
    }

    private CommandButton getButtonSubmit() {
        return PrimeSelenium.createFragment(CommandButton.class, By.id("form:buttonSubmit"));
    }

    private CommandButton getButtonUpdate() {
        return PrimeSelenium.createFragment(CommandButton.class, By.id("form:buttonUpdate"));
    }

    private SelectCheckboxMenu getFilterType() {
        return PrimeSelenium.createFragment(SelectCheckboxMenu.class, By.id("form:datatable:filterType"));
    }
}
