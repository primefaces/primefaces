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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.model.TreeNode;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.model.TreeTable;

public class TreeTable002Test extends AbstractTreeTableTest {

    @Test
    @Order(1)
    @DisplayName("TreeTable: multi sort, predefined sort in markup")
    public void testMultiSortPlusPredefined(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        Assertions.assertNotNull(treeTable);

        treeTable.getRow(0).toggle();
        TreeNode<Document> treeSorted = sortBy(root, new DocumentSorterNameAscSizeDesc());
        treeSorted.getChildren().get(0).setExpanded(true);

        // Assert - predefined sort
        WebElement eltSortName= treeTable.getHeader().getCell(0).getWebElement();
        WebElement eltSortSize = treeTable.getHeader().getCell(1).getWebElement();
        assertHeaderSorted(eltSortName, "ASC", 1);
        assertHeaderSorted(eltSortSize, "DESC", 2);

        //assertRows(treeTable, treeSorted); // TODO: activate after https://github.com/primefaces/primefaces/issues/7537 is fixed

        assertConfiguration(treeTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TreeTable Config = " + cfg);
        Assertions.assertEquals("wgtTreeTable", cfg.getString("widgetVar"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:treetable")
        TreeTable treeTable;

        @Override
        public String getLocation() {
            return "treetable/treeTable002.xhtml";
        }
    }
}
