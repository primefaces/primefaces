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

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.DataTable;

public class DataTable008Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: filter - issue 5481 - https://github.com/primefaces/primefaces/issues/5481")
    @Disabled("Disabled because DataTable-filter does not support @SessionScoped - see https://github.com/primefaces/primefaces/issues/7373")
    public void testFilterIssue_5481(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act - do some filtering
        dataTable.selectPage(1);
        dataTable.sort("Name");
        Objects.requireNonNull(dataTable.getHeader().getCell("First appeared").orElse(null)).setFilterValue("2010", "change", 0);

        // Assert
        Assertions.assertEquals("2010", getFirstAppearedFilterElt(dataTable).getAttribute("value"));
        List<ProgrammingLanguage> langsFiltered = languages.stream()
                    .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                    .filter(l -> l.getFirstAppeared() >= 2010)
                    .limit(3)
                    .collect(Collectors.toList());
        assertRows(dataTable, langsFiltered);

        assertConfiguration(dataTable.getWidgetConfiguration());

        // Act - reload page and go to page 2 (filter is visually removed but to some degree due to SessionScoped-bean still there)
        page.getWebDriver().navigate().refresh();
        dataTable.selectPage(2);

        // Assert
        Assertions.assertEquals("", getFirstAppearedFilterElt(dataTable).getAttribute("value"));
        List<ProgrammingLanguage> langsUnfilteredPage2 = languages.stream()
                    .sorted(Comparator.comparing(ProgrammingLanguage::getName))
                    .skip(3)
                    .limit(3)
                    .collect(Collectors.toList());
        assertRows(dataTable, langsUnfilteredPage2);

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private static WebElement getFirstAppearedFilterElt(DataTable dataTable) {
        return dataTable.getHeader().getCell(2).getWebElement().findElement(By.tagName("input"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("paginator"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable008.xhtml";
        }
    }
}
