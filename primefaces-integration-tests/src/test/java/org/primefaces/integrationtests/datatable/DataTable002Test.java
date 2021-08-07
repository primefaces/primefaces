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
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DataTable002Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: Lazy: Basic & Paginator")
    public void testLazyAndPaginator(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
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

    @Test
    @Order(2)
    @DisplayName("DataTable: Lazy: single sort")
    public void testLazySortSingle(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
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

    @Test
    @Order(3)
    @DisplayName("DataTable: Lazy: filter")
    public void testLazyFilter(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
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

    @Test
    @Order(4)
    @DisplayName("DataTable: Lazy: rowSelect")
    public void testLazyRowSelect(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        PrimeSelenium.guardAjax(dataTable.getCell(3, 0).getWebElement()).click();

        // Assert
        Assertions.assertEquals(1, page.messages.getAllMessages().size());
        Assertions.assertEquals("ProgrammingLanguage Selected", page.messages.getMessage(0).getSummary());
        String row3ProgLang = dataTable.getRow(3).getCell(0).getText() + " - " + dataTable.getCell(3, 1).getText();
        Assertions.assertEquals(row3ProgLang, page.messages.getMessage(0).getDetail());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: Lazy: delete rows from last page - https://github.com/primefaces/primefaces/issues/1921")
    public void testLazyRowDeleteFromLastPage(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);
        dataTable.selectPage(dataTable.getPaginator().getPages().size());

        // Act
        for (int row=5; row>1; row--) {
            Assertions.assertEquals(row, dataTable.getRows().size());
            PrimeSelenium.guardAjax(dataTable.getCell(0, 3).getWebElement().findElement(By.className("ui-button"))).click();
            Assertions.assertEquals(8, dataTable.getPaginator().getActivePage().getNumber());
        }

        // Act - delete last row on page 8
        PrimeSelenium.guardAjax(dataTable.getCell(0, 3).getWebElement().findElement(By.className("ui-button"))).click();
        Assertions.assertEquals(7, dataTable.getPaginator().getActivePage().getNumber());
        Assertions.assertEquals(10, dataTable.getRows().size());

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("paginator"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable002.xhtml";
        }
    }
}
