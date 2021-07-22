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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TreeTable;

public class TreeTable004Test extends AbstractTreeTableTest {

    @Test
    @Order(6)
    @DisplayName("TreeTable: multiple select")
    public void testShowSelectedNodes(Page page) {
        TreeTable treeTable = page.treeTable;
        treeTable.sort("Name");

        // Act - select 3 nodes
        Actions actions = new Actions(page.getWebDriver());
        actions.keyDown(Keys.META).click(treeTable.getRow(2).getCell(1).getWebElement()).keyUp(Keys.META).perform();
        treeTable.getRow(0).toggle();
        actions.keyDown(Keys.META).click(treeTable.getRow(1).getCell(1).getWebElement()).keyUp(Keys.META).perform();
        actions.keyDown(Keys.META).click(treeTable.getRow(3).getCell(1).getWebElement()).keyUp(Keys.META).perform();
        page.buttonShowSelectedNodes.click();

        // Assert
        assertMessage(page.messages, 0, "selected nodes", "Desktop,editor.app,settings.app");

        // Act - unselect 1 node
        actions.keyDown(Keys.META).click(treeTable.getRow(3).getCell(1).getWebElement()).keyUp(Keys.META).perform();
        page.buttonShowSelectedNodes.click();

        // Assert
        assertMessage(page.messages, 0, "selected nodes", "Desktop,editor.app");

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

        @FindBy(id = "form:buttonShowSelectedNodes")
        CommandButton buttonShowSelectedNodes;

//        @FindBy(id = "form:buttonGlobalFilterOnly")
//        CommandButton buttonGlobalFilterOnly;

        @Override
        public String getLocation() {
            return "treetable/treeTable004.xhtml";
        }
    }
}
