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

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

public class DataTable016Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: RowGroup - header row and summary row")
    public void testRowGroup(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        //page.button.click();

        // Assert
        Assertions.assertNotNull(dataTable.getHeaderWebElement());

        List<WebElement> rowElts = dataTable.getRowsWebElement();
        Assertions.assertNotNull(rowElts);
        Assertions.assertEquals(languages.size() + 2 + 2, rowElts.size()); //plus 2 header-rows plus 2 summary-rows

        List<Row> rows = dataTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(languages.size() + 2 + 2, rows.size()); //plus 2 header-rows plus 2 summary-rows

        //check header-rows
        Assertions.assertEquals("4", dataTable.getCell(0, 0).getWebElement().getAttribute("colspan"));
        Assertions.assertEquals("COMPILED", dataTable.getCell(0, 0).getText());
        Assertions.assertEquals(1, dataTable.getCell(0, 0).getWebElement().findElements(By.className("ui-rowgroup-toggler")).size());
        Assertions.assertEquals("4", dataTable.getCell(4, 0).getWebElement().getAttribute("colspan"));
        Assertions.assertEquals("INTERPRETED", dataTable.getCell(4, 0).getText());
        Assertions.assertEquals(1, dataTable.getCell(4, 0).getWebElement().findElements(By.className("ui-rowgroup-toggler")).size());

        //check summary-rows
        Assertions.assertEquals("3", dataTable.getCell(3, 0).getWebElement().getAttribute("colspan"));
        Assertions.assertEquals("Total programming languages:", dataTable.getCell(3, 0).getText());
        Assertions.assertEquals("2", dataTable.getCell(3, 1).getText());
        Assertions.assertEquals("3", dataTable.getCell(8, 0).getWebElement().getAttribute("colspan"));
        Assertions.assertEquals("Total programming languages:", dataTable.getCell(8, 0).getText());
        Assertions.assertEquals("3", dataTable.getCell(8, 1).getText());

        //remove header- and summary-rows
        rows.remove(8); //second summary-row
        rows.remove(4); //second header-row
        rows.remove(3); //first summary-row
        rows.remove(0); //first header-row

        assertRows(rows, languages);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("groupColumnIndexes"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable016.xhtml";
        }
    }
}
