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
package org.primefaces.integrationtests.tree;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Tree;
import org.primefaces.selenium.component.model.tree.TreeNode;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Tree010Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: GitHub #14798 horizontal checkbox selection must not propagate to a non-selectable child node")
    void horizontalCheckboxSelectionDoesNotPropagateToUnselectableNode(Page page) {
        // Arrange - the selenium TreeNode only resolves the children of a vertical tree, so use the rowKeys
        TreeNode documents = node(page.horizontalTree, "0");
        TreeNode workDoc = node(page.horizontalTree, "0_0");
        TreeNode privateDoc = node(page.horizontalTree, "0_1");
        TreeNode resumeDoc = node(page.horizontalTree, "0_2");

        // Act
        documents.select();

        // Assert
        assertTrue(documents.isSelected());
        assertTrue(workDoc.isSelected());
        assertFalse(privateDoc.isSelected());
        assertTrue(resumeDoc.isSelected());

        // Act - uncheck and re-check a selectable child
        resumeDoc.select();
        assertTrue(documents.isPartialSelected());
        resumeDoc.select();

        // Assert - all selectable children are selected again, so the parent must be fully selected
        assertTrue(documents.isSelected());
        assertFalse(privateDoc.isSelected());

        page.showSelected.click();
        assertEquals("Documents,resume.doc,snapshot.doc,work.doc", page.messages.getMessage(0).getDetail());
        assertCheckboxConfiguration(page.horizontalTree.getWidgetConfiguration(), "horizontalTreeWidget");
    }

    @Test
    @Order(2)
    @DisplayName("Tree: GitHub #14798 horizontal parent without selectable children must not be selected by a grandchild")
    void horizontalParentWithoutSelectableChildrenKeepsState(Page page) {
        // Arrange - "Archive" only has the non-selectable "Locked"
        TreeNode archive = node(page.horizontalTree, "1");
        TreeNode locked = node(page.horizontalTree, "1_0");
        TreeNode oldDoc = node(page.horizontalTree, "1_0_0");

        // Act & Assert
        oldDoc.select();
        assertTrue(oldDoc.isSelected());
        assertFalse(locked.isSelected());
        assertFalse(archive.isSelected());
        assertFalse(archive.isPartialSelected());

        oldDoc.select();
        assertFalse(oldDoc.isSelected());
        assertFalse(archive.isSelected());
        assertFalse(archive.isPartialSelected());

        page.showSelected.click();
        assertEquals("snapshot.doc", page.messages.getMessage(0).getDetail());
        assertCheckboxConfiguration(page.horizontalTree.getWidgetConfiguration(), "horizontalTreeWidget");
    }

    @Test
    @Order(3)
    @DisplayName("Tree: GitHub #14798 dynamic checkbox selection must not propagate to a non-selectable child node")
    void dynamicCheckboxSelectionDoesNotPropagateToUnselectableNode(Page page) {
        // Arrange
        TreeNode documents = page.dynamicTree.getChildren().get(0);

        // Act
        documents.select();

        // Assert
        assertTrue(documents.isSelected());
        assertTrue(documents.getChildren().get(0).isSelected());
        assertFalse(documents.getChildren().get(1).isSelected());
        assertTrue(documents.getChildren().get(2).isSelected());

        page.showSelected.click();
        assertEquals("Documents,resume.doc,snapshot.doc,work.doc", page.messages.getMessage(1).getDetail());
        assertCheckboxConfiguration(page.dynamicTree.getWidgetConfiguration(), "dynamicTreeWidget");
    }

    @Test
    @Order(4)
    @DisplayName("Tree: GitHub #14798 dynamic unchecking a collapsed parent must keep its preselected non-selectable child")
    void dynamicCollapsedPreselectedUnselectableNodeKeepsSelection(Page page) {
        // Arrange - the children of the collapsed "Backup" are not rendered
        TreeNode backup = page.dynamicTree.getChildren().get(2);
        assertTrue(page.dynamicTree.findElements(By.cssSelector(".p-treenode[data-rowkey='2_0']")).isEmpty());

        // Act
        backup.select();
        assertTrue(backup.isSelected());
        backup.select();

        // Assert
        assertFalse(backup.isSelected());

        page.showSelected.click();
        assertEquals("snapshot.doc", page.messages.getMessage(1).getDetail());
        assertCheckboxConfiguration(page.dynamicTree.getWidgetConfiguration(), "dynamicTreeWidget");
    }

    private TreeNode node(Tree tree, String rowKey) {
        return new TreeNode(tree, rowKey, null);
    }

    private void assertCheckboxConfiguration(JSONObject cfg, String widgetVar) {
        assertConfiguration(cfg, widgetVar);
        assertEquals("checkbox", cfg.getString("selectionMode"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:horizontalTree")
        Tree horizontalTree;

        @FindBy(id = "form:dynamicTree")
        Tree dynamicTree;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:showSelected")
        CommandButton showSelected;

        @Override
        public String getLocation() {
            return "tree/tree010.xhtml";
        }
    }
}
