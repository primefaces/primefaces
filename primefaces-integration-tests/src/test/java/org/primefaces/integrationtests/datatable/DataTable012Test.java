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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;

public class DataTable012Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: single sort; sortBy on p:column; initial sort via sortBy on dataTable")
    public void testSortByWithDefault(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable1;
        Assertions.assertNotNull(dataTable);

        // Act

        // Assert - ascending (initial)
        List<ProgrammingLanguage> langsSorted = sortByNoLimit(Comparator.comparing(ProgrammingLanguage::getName));
        assertRows(dataTable, langsSorted);

        // Act - descending
        dataTable.sort("Name");

        // Assert - descending
        Collections.reverse(langsSorted);
        assertRows(dataTable, langsSorted);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: single sort; field on p:column; initial sort via sortBy on dataTable")
    public void testFieldWithDefault(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable2;
        Assertions.assertNotNull(dataTable);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Act

        // Assert - ascending (initial)
        List<ProgrammingLanguage> langsSorted = sortByNoLimit(Comparator.comparing(ProgrammingLanguage::getName));
        assertRows(dataTable, langsSorted);

        // Act - descending
        PrimeSelenium.guardAjax(Objects.requireNonNull(dataTable.getHeader().getCell("Name").orElse(null)).getWebElement()
                    .findElement(By.className("ui-column-title"))).click();

        // Assert - descending
        Collections.reverse(langsSorted);
        assertRows(dataTable, langsSorted);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form1:datatable1")
        DataTable dataTable1;

        @FindBy(id = "form2:datatable2")
        DataTable dataTable2;

        @Override
        public String getLocation() {
            return "datatable/dataTable012.xhtml";
        }
    }
}
