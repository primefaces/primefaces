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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.primefaces.model.TreeNode;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.TreeTable;

public class TreeTable002Test extends AbstractTreeTableTest {

    @Test
    @Order(1)
    @DisplayName("TreeTable: multi sort, predefined sort in markup")
    public void testMultiSortPlusPredefined(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        Assertions.assertNotNull(treeTable);

        // ... expand first node
        treeTable.getRow(0).toggle();
        TreeNode<Document> treeSorted = sortBy(root, new DocumentSorterNameAscSizeDesc());
        treeSorted.getChildren().get(0).setExpanded(true);

        // Assert - predefined sort (column 1 asc, column 2 desc)
        WebElement eltSortName= treeTable.getHeader().getCell(0).getWebElement();
        WebElement eltSortSize = treeTable.getHeader().getCell(1).getWebElement();
        assertHeaderSorted(eltSortName, "ASC", 1);
        assertHeaderSorted(eltSortSize, "DESC", 2);
        assertRows(treeTable, treeSorted);

        // ... undo expand first node
        treeTable.getRow(0).toggle();
        treeSorted.getChildren().get(0).setExpanded(false);

        // Act - sort (column 2 desc, column 1 asc)
        treeTable.sort(1); //asc
        treeTable.sort(1); //desc
        Actions actions = new Actions(page.getWebDriver());
        Action actionUnselect = actions.keyDown(Keys.META).click(treeTable.getHeader().getCell(0).getWebElement()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionUnselect).perform();
        assertHeaderSorted(eltSortName, "ASC", 2);
        assertHeaderSorted(eltSortSize, "DESC", 1);

        // ... expand first node
        treeTable.getRow(0).toggle();
        treeSorted = sortBy(treeSorted, new DocumentSorterSizeDescNameAsc());
        treeSorted.getChildren().get(0).setExpanded(true);
        assertRows(treeTable, treeSorted);

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
