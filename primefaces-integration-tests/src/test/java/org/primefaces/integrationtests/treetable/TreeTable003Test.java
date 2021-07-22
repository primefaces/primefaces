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
import org.openqa.selenium.support.FindBy;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.TreeTable;

public class TreeTable003Test extends AbstractTreeTableTest {

    @Test
    @Order(1)
    @DisplayName("TreeTable: paginator")
    public void testPaginator(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        Assertions.assertNotNull(treeTable);
        treeTable.selectPage(1);

        // Assert
        int pages = root.getChildren().size() / 3;
        if (root.getChildren().size() % 3 > 0) {
            pages++;
        }
        Assertions.assertEquals(pages, treeTable.getPaginator().getPages().size());

        DefaultTreeNode<Document> rootPage1 = new DefaultTreeNode<>(root.getData());
        rootPage1.setChildren(root.getChildren().subList(0, 3));
        assertRows(treeTable, rootPage1);

        // Act
        treeTable.selectPage(2);

        // Assert
        DefaultTreeNode<Document> rootPage2 = new DefaultTreeNode<>(root.getData());
        rootPage2.setChildren(root.getChildren().subList(0, 3));
        assertRows(treeTable, rootPage2);

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TreeTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("paginator"));
        Assertions.assertEquals("wgtTreeTable", cfg.getString("widgetVar"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:treetable")
        TreeTable treeTable;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @Override
        public String getLocation() {
            return "treetable/treeTable003.xhtml";
        }
    }
}
