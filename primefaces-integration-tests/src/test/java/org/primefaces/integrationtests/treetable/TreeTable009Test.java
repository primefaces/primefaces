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
package org.primefaces.integrationtests.treetable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.TreeTable;
import org.primefaces.selenium.component.model.treetable.Row;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression test for #9254 - TreeTable: clearFilters() breaks the table when filtering
 * is enabled. The client-side clearFilters() API resets the filter inputs and triggers a
 * filter request; the stale filterBy entries then keep null values, so filtering ran
 * against a null root and shadowed the (new) model on the next render, freezing the table.
 */
class TreeTable009Test extends AbstractTreeTableTest {

    @Test
    @Order(1)
    @DisplayName("TreeTable: clearFilters() then switch data - https://github.com/primefaces/primefaces/issues/9254")
    void clearFiltersThenSwitchData(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        assertRows(treeTable, root);

        // Act - apply a column filter so the filterBy map gets populated
        treeTable.filter("Name", "down");

        // Assert - only the matching node is shown
        List<Row> rows = treeTable.getRows();
        assertEquals(1, rows.size());
        assertEquals("Downloads", rows.get(0).getCell(0).getText());

        // Act - clear all filters via the client-side API (sends a filter AJAX request)
        PrimeSelenium.executeScript(true, "PF('treeTable').clearFilters()");

        // Assert - all rows are shown again and the model is restored
        assertRows(treeTable, root);

        // Act - switch to the other root while the stale filterBy entries remain
        page.buttonSwitchRoot.click();

        // Assert - the table must reflect the new model instead of staying frozen
        assertRows(treeTable, rootOtherDocument);

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TreeTable Config = " + cfg);
        assertEquals("treeTable", cfg.getString("widgetVar"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:treeTable")
        TreeTable treeTable;

        @FindBy(id = "form:buttonSwitchRoot")
        CommandButton buttonSwitchRoot;

        @Override
        public String getLocation() {
            return "treetable/treeTable009.xhtml";
        }
    }
}