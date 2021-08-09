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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataTable002Test extends AbstractDataTableTest {

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(1)
    @DisplayName("DataTable: Lazy: Basic & Paginator")
    public void testLazyAndPaginator(String xhtml) {
        // Arrange
        getWebDriver().get(PrimeSelenium.getUrl(xhtml));
        DataTable dataTable = getDataTable();
        Assertions.assertNotNull(dataTable);

        // Act
        //page.button.click();

        // Assert
        List<Row> rows = dataTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(10, rows.size());

        Row firstRow = dataTable.getRow(0);
        Assertions.assertEquals("1", firstRow.getCell(0).getText());
        Assertions.assertEquals("Language 1", firstRow.getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());

        // Act - second page
        dataTable.selectPage(2);

        // Assert
        rows = dataTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(10, rows.size());

        firstRow = dataTable.getRow(0);
        Assertions.assertEquals("11", firstRow.getCell(0).getText());
        Assertions.assertEquals("Language 11", firstRow.getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());

        // Act - last page
        dataTable.selectPage(8);

        // Assert
        rows = dataTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(5, rows.size());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(2)
    @DisplayName("DataTable: Lazy: single sort")
    public void testLazySortSingle(String xhtml) {
        // Arrange
        getWebDriver().get(PrimeSelenium.getUrl(xhtml));
        DataTable dataTable = getDataTable();
        Assertions.assertNotNull(dataTable);
        List<ProgrammingLanguage> langsAsc = model.getLangs().stream().sorted(Comparator.comparing(ProgrammingLanguage::getName)).collect(Collectors.toList());
        List<ProgrammingLanguage> langsDesc = model.getLangs().stream().sorted(Comparator.comparing(ProgrammingLanguage::getName).reversed())
                    .collect(Collectors.toList());

        // Act - ascending
        dataTable.selectPage(1);
        dataTable.sort("Name");

        // Assert
        List<Row> rows = dataTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(10, rows.size());

        Assertions.assertEquals(langsAsc.get(0).getName(), rows.get(0).getCell(1).getText());
        Assertions.assertEquals(langsAsc.get(1).getName(), rows.get(1).getCell(1).getText());
        Assertions.assertEquals(langsAsc.get(9).getName(), rows.get(9).getCell(1).getText());

        // Act - descending
        dataTable.sort("Name");

        // Assert
        rows = dataTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(10, rows.size());

        Assertions.assertEquals(langsDesc.get(0).getName(), rows.get(0).getCell(1).getText());
        Assertions.assertEquals(langsDesc.get(1).getName(), rows.get(1).getCell(1).getText());
        Assertions.assertEquals(langsDesc.get(9).getName(), rows.get(9).getCell(1).getText());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(3)
    @DisplayName("DataTable: Lazy: filter")
    public void testLazyFilter(String xhtml) {
        // Arrange
        getWebDriver().get(PrimeSelenium.getUrl(xhtml));
        DataTable dataTable = getDataTable();
        Assertions.assertNotNull(dataTable);
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
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(10, rows.size()); //one page
        Assertions.assertEquals(langsFiltered.get(0).getName(), rows.get(0).getCell(1).getText());
        Assertions.assertEquals(langsFiltered.get(1).getName(), rows.get(1).getCell(1).getText());
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(4)
    @DisplayName("DataTable: Lazy: rowSelect")
    public void testLazyRowSelect(String xhtml) {
        // Arrange
        getWebDriver().get(PrimeSelenium.getUrl(xhtml));
        DataTable dataTable = getDataTable();
        Assertions.assertNotNull(dataTable);

        // Act
        PrimeSelenium.guardAjax(dataTable.getCell(3, 0).getWebElement()).click();

        // Assert
        Assertions.assertEquals(1, getMessages().getAllMessages().size());
        Assertions.assertEquals("ProgrammingLanguage Selected", getMessages().getMessage(0).getSummary());
        String row3ProgLang = dataTable.getRow(3).getCell(0).getText() + " - " + dataTable.getCell(3, 1).getText();
        Assertions.assertEquals(row3ProgLang, getMessages().getMessage(0).getDetail());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(5)
    @DisplayName("DataTable: Lazy: delete rows from last page - https://github.com/primefaces/primefaces/issues/1921")
    public void testLazyRowDeleteFromLastPage(String xhtml) {
        // Arrange
        getWebDriver().get(PrimeSelenium.getUrl(xhtml));
        DataTable dataTable = getDataTable();
        Assertions.assertNotNull(dataTable);
        dataTable.selectPage(dataTable.getPaginator().getPages().size());

        // Act & Assert
        for (int row=5; row>1; row--) {
            Assertions.assertEquals(row, getDataTable().getRows().size());
            PrimeSelenium.guardAjax(getDataTable().getCell(0, 3).getWebElement().findElement(By.className("ui-button"))).click();
            Assertions.assertEquals(8, getDataTable().getPaginator().getActivePage().getNumber());
        }

        // Act & Assert - delete last row on page 8
        PrimeSelenium.guardAjax(getDataTable().getCell(0, 3).getWebElement().findElement(By.className("ui-button"))).click();
        Assertions.assertEquals(7, getDataTable().getPaginator().getActivePage().getNumber());
        Assertions.assertEquals(10, getDataTable().getRows().size());

        // Act & Assert - select first row on page 7
        PrimeSelenium.guardAjax(getDataTable().getCell(0, 0).getWebElement()).click();
        Assertions.assertEquals(1, getMessages().getAllMessages().size());
        Assertions.assertEquals("ProgrammingLanguage Selected", getMessages().getMessage(0).getSummary());
        String row0ProgLang = getDataTable().getRow(0).getCell(0).getText() + " - " + getDataTable().getCell(0, 1).getText();
        Assertions.assertEquals(row0ProgLang, getMessages().getMessage(0).getDetail());

        // Act & Assert - delete first row on page 7
        PrimeSelenium.guardAjax(getDataTable().getCell(0, 3).getWebElement().findElement(By.className("ui-button"))).click();
        Assertions.assertEquals(1, getMessages().getAllMessages().size());
        Assertions.assertEquals("ProgrammingLanguage Deleted", getMessages().getMessage(0).getSummary());
        Assertions.assertEquals(row0ProgLang, getMessages().getMessage(0).getDetail());
        Assertions.assertEquals(7, getDataTable().getPaginator().getActivePage().getNumber());
        Assertions.assertEquals(9, getDataTable().getRows().size());

        assertConfiguration(getDataTable().getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("paginator"));
    }

    private static Stream<Arguments> provideXhtmls() {
        return Stream.of(
                Arguments.of("datatable/dataTable002.xhtml"),
                Arguments.of("datatable/dataTable002RowCountOtherImpl.xhtml"));
    }

    private DataTable getDataTable() {
        return PrimeSelenium.createFragment(DataTable.class, By.id("form:datatable"));
    }

    private Messages getMessages() {
        return PrimeSelenium.createFragment(Messages.class, By.id("form:msgs"));
    }
}
