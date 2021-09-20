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
package org.primefaces.integrationtests.treetable;

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
import org.primefaces.model.TreeNode;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TreeTable;
import org.primefaces.selenium.component.model.datatable.Header;
import org.primefaces.selenium.component.model.treetable.Row;

import java.util.Comparator;
import java.util.List;

public class TreeTable001Test extends AbstractTreeTableTest {

    @Test
    @Order(1)
    @DisplayName("TreeTable: Basic & Toggling")
    public void testBasicAndToggling(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        Assertions.assertNotNull(treeTable);

        // Assert
        Assertions.assertNotNull(treeTable.getHeaderWebElement());

        List<WebElement> rowElts = treeTable.getRowsWebElement();
        Assertions.assertNotNull(rowElts);
        Assertions.assertEquals(root.getChildCount(), rowElts.size());

        List<Row> rows = treeTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(root.getChildCount(), rows.size());

        assertRows(treeTable, root);

        Header header = treeTable.getHeader();
        Assertions.assertNotNull(header);
        Assertions.assertNotNull(header.getCells());
        Assertions.assertEquals(4, header.getCells().size());
        Assertions.assertEquals("Name", header.getCell(0).getColumnTitle().getText());
        Assertions.assertEquals("Size", header.getCell(1).getColumnTitle().getText());
        Assertions.assertEquals("Type", header.getCell(2).getColumnTitle().getText());

        Row firstRow = treeTable.getRow(0);
        Assertions.assertTrue(firstRow.isToggleable());
        Assertions.assertEquals(1, firstRow.getLevel());

        assertConfiguration(treeTable.getWidgetConfiguration());

        // Act
        firstRow.toggle();

        // Assert - Part 2
        rows = treeTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(root.getChildCount() + root.getChildren().get(0).getChildCount(), rows.size());

        Row firstChildRow = treeTable.getRow(1);
        Assertions.assertEquals(2, firstChildRow.getLevel());

        root.getChildren().get(0).setExpanded(true);
        assertRows(treeTable, root);

        assertConfiguration(treeTable.getWidgetConfiguration());

        // Act
        firstRow.toggle();

        // Assert - Part 3
        rows = treeTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(root.getChildCount(), rows.size());

        root.getChildren().get(0).setExpanded(false);
        assertRows(treeTable, root);

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("TreeTable: single sort including recursive subtree sorting")
    public void testSortSingle(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        Assertions.assertNotNull(treeTable);

        treeTable.getRow(0).toggle();
        root.getChildren().get(0).setExpanded(true);

        // Act - ascending
        treeTable.sort("Name");

        // Assert
        TreeNode<Document> treeSorted = sortBy(root, Comparator.comparing((TreeNode<Document> t) -> t.getData().getName().toLowerCase()));
        assertRows(treeTable, treeSorted);

        // Act - descending
        treeTable.sort("Name");

        // Assert
        TreeNode<Document> treeSortedRev = sortBy(treeSorted, Comparator.comparing((TreeNode<Document> t) -> t.getData().getName().toLowerCase()).reversed());
        assertRows(treeTable, treeSortedRev);

        // Act
        page.buttonUpdate.click();

        // Assert - sort must not be lost after update
        assertRows(treeTable, treeSortedRev);

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("TreeTable: filter")
    public void testFilter(Page page) {
        TreeTable treeTable = page.treeTable;
        treeTable.sort("Name");

        // Act (filter on L3)
        treeTable.filter("Name", "mobi");

        // Assert
        // we try to avoid duplicating logic from TreeTableRenderer#filter, so we take the simple/stupid way
        List<Row> rows = treeTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(3, rows.size());
        Assertions.assertEquals("Applications", rows.get(0).getCell(0).getText());
        Assertions.assertEquals("Primefaces", rows.get(1).getCell(0).getText());
        Assertions.assertEquals("mobile.app", rows.get(2).getCell(0).getText());
        Assertions.assertEquals(1, rows.get(0).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());
        Assertions.assertEquals(3, rows.get(2).getLevel());

        // Act (filter on L1)
        treeTable.filter("Name", "down");
        treeTable.getRow(0).toggle();

        // Assert
        rows = treeTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(3, rows.size());
        Assertions.assertEquals("Downloads", rows.get(0).getCell(0).getText());
        Assertions.assertEquals("Spanish", rows.get(1).getCell(0).getText());
        Assertions.assertEquals("Travel", rows.get(2).getCell(0).getText());
        Assertions.assertEquals(1, rows.get(0).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());

        // Act
        page.buttonUpdate.click();

        // Assert - filter must not be lost after update
        rows = treeTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(3, rows.size());
        Assertions.assertEquals("Downloads", rows.get(0).getCell(0).getText());
        Assertions.assertEquals("Spanish", rows.get(1).getCell(0).getText());
        Assertions.assertEquals("Travel", rows.get(2).getCell(0).getText());
        Assertions.assertEquals(1, rows.get(0).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("TreeTable: global filter")
    public void testGlobalFilter(Page page) {
        TreeTable treeTable = page.treeTable;
        treeTable.sort("Name");

        // Act (filter on L3)
        filterGlobal(page.globalFilter, "mobi");

        // Assert
        // we try to avoid duplicating logic from TreeTableRenderer#filter, so we take the simple/stupid way
        List<Row> rows = treeTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(3, rows.size());
        Assertions.assertEquals("Applications", rows.get(0).getCell(0).getText());
        Assertions.assertEquals("Primefaces", rows.get(1).getCell(0).getText());
        Assertions.assertEquals("mobile.app", rows.get(2).getCell(0).getText());
        Assertions.assertEquals(1, rows.get(0).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());
        Assertions.assertEquals(3, rows.get(2).getLevel());

        // Act (filter on L1)
        filterGlobal(page.globalFilter, "down");
        treeTable.getRow(0).toggle();

        // Assert
        rows = treeTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(3, rows.size());
        Assertions.assertEquals("Downloads", rows.get(0).getCell(0).getText());
        Assertions.assertEquals("Spanish", rows.get(1).getCell(0).getText());
        Assertions.assertEquals("Travel", rows.get(2).getCell(0).getText());
        Assertions.assertEquals(1, rows.get(0).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());

        // Act
        PrimeSelenium.guardAjax(page.buttonUpdate).click();

        // Assert - filter must not be lost after update
        rows = treeTable.getRows();
        Assertions.assertNotNull(rows);
        Assertions.assertEquals(3, rows.size());
        Assertions.assertEquals("Downloads", rows.get(0).getCell(0).getText());
        Assertions.assertEquals("Spanish", rows.get(1).getCell(0).getText());
        Assertions.assertEquals("Travel", rows.get(2).getCell(0).getText());
        Assertions.assertEquals(1, rows.get(0).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());
        Assertions.assertEquals(2, rows.get(1).getLevel());

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("TreeTable: show selected document (not a TreeTable feature)")
    public void testShowSelectedDocument(Page page) {
        TreeTable treeTable = page.treeTable;
        treeTable.sort("Name");

        // Act
        PrimeSelenium.guardAjax(treeTable.getRow(0).getCell(3).getWebElement().findElement(By.cssSelector("button"))).click();

        // Assert
        assertMessage(page.messages, 0, "selected document", "Applications");

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("TreeTable: single select (via TreeTable-selection feature)")
    public void testShowSelectedNode(Page page) {
        TreeTable treeTable = page.treeTable;
        treeTable.sort("Name");

        // Act
        PrimeSelenium.guardAjax(treeTable.getRow(0).getCell(1).getWebElement()).click();
        page.buttonShowSelectedNode.click();

        // Assert
        assertMessage(page.messages, 0, "selected node", "Applications");

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("TreeTable: select-event")
    public void testSelectedEvent(Page page) {
        TreeTable treeTable = page.treeTable;
        treeTable.sort("Name");

        // Act
        PrimeSelenium.guardAjax(treeTable.getRow(0).getCell(1).getWebElement()).click();

        // Assert
        assertMessage(page.messages, 0, "select-event", "Applications");

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("TreeTable: unselect-event")
    public void testUnselectedEvent(Page page) {
        TreeTable treeTable = page.treeTable;
        treeTable.sort("Name");

        // Act
        PrimeSelenium.guardAjax(treeTable.getRow(0).getCell(1).getWebElement()).click(); //select
        Actions actions = new Actions(page.getWebDriver());
        Action actionUnselect = actions.keyDown(Keys.META).click(treeTable.getRow(0).getCell(1).getWebElement()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionUnselect).perform();

        // Assert
        assertMessage(page.messages, 0, "unselect-event", "Applications");

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TreeTable Config = " + cfg);
        Assertions.assertEquals("treeTable", cfg.getString("widgetVar"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:treeTable")
        TreeTable treeTable;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:treeTable:globalFilter")
        InputText globalFilter;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @FindBy(id = "form:buttonResetTable")
        CommandButton buttonResetTable;

        @FindBy(id = "form:buttonShowSelectedNode")
        CommandButton buttonShowSelectedNode;

//        @FindBy(id = "form:buttonGlobalFilterOnly")
//        CommandButton buttonGlobalFilterOnly;

        @Override
        public String getLocation() {
            return "treetable/treeTable001.xhtml";
        }
    }
}
