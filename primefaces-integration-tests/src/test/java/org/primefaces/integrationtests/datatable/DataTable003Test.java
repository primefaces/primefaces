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
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;

public class DataTable003Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: multisort - firstAppeared desc, name asc")
    public void testMultiSort(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        Actions actions = new Actions(page.getWebDriver());
        WebElement eltSortFirstAppeared = dataTable.getHeader().getCell(2).getWebElement();
        WebElement eltSortName = dataTable.getHeader().getCell(1).getWebElement();

        // 1) sort by firstAppeared desc
        PrimeSelenium.guardAjax(eltSortFirstAppeared).click();
        PrimeSelenium.guardAjax(eltSortFirstAppeared).click();
        assertHeaderSorted(eltSortFirstAppeared, "DESC", 0);

        // 2) additional sort by name asc
        Action actionMetaPlusSortClick = actions.keyDown(Keys.META).click(eltSortName).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionMetaPlusSortClick).perform();

        // Assert
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertHeaderSorted(eltSortFirstAppeared, "DESC", 1);
        assertHeaderSorted(eltSortName, "ASC", 2);

        List<ProgrammingLanguage> langsSorted = languages.stream().sorted(new ProgrammingLanguageSorterFirstAppearedDescNameAsc()).collect(Collectors.toList());
        assertRows(dataTable, langsSorted);
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: multisort - firstAppeared desc, name desc")
    public void testMultiSort2(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        Actions actions = new Actions(page.getWebDriver());
        WebElement eltSortFirstAppeared = dataTable.getHeader().getCell(2).getWebElement();
        WebElement eltSortName = dataTable.getHeader().getCell(1).getWebElement();

        // 1) sort by firstAppeared desc
        PrimeSelenium.guardAjax(eltSortFirstAppeared).click();
        PrimeSelenium.guardAjax(eltSortFirstAppeared).click();
        assertHeaderSorted(eltSortFirstAppeared, "DESC", 0);

        // 2) additional sort by name desc
        Action actionMetaPlusSortClick = actions.keyDown(Keys.META).click(eltSortName).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionMetaPlusSortClick).perform();
        PrimeSelenium.guardAjax(actionMetaPlusSortClick).perform();

        // Assert
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertHeaderSorted(eltSortFirstAppeared, "DESC", 1);
        assertHeaderSorted(eltSortName, "DESC", 2);

        List<ProgrammingLanguage> langsSorted = languages.stream().sorted(new ProgrammingLanguageSorterFirstAppearedDescNameDesc())
                    .collect(Collectors.toList());
        assertRows(dataTable, langsSorted);
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.getBoolean("multiSort"));
        Assertions.assertFalse(cfg.getBoolean("allowUnsorting"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable003.xhtml";
        }
    }
}
