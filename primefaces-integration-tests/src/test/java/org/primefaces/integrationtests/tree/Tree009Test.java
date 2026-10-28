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
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Tree;
import org.primefaces.selenium.component.model.tree.TreeNode;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Tree009Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: GitHub #14798 checkbox selection must not propagate to a non-selectable child node")
    void checkboxSelectionDoesNotPropagateToUnselectableNode(Page page) {
        // Arrange
        TreeNode documents = page.tree.getChildren().get(0);
        List<TreeNode> children = documents.getChildren();

        // Act
        documents.select();

        // Assert
        assertTrue(documents.isSelected());
        assertTrue(children.get(0).isSelected());
        assertFalse(children.get(1).isSelected());
        assertTrue(children.get(2).isSelected());

        page.showSelected.click();
        String selected = page.messages.getMessage(0).getDetail();
        assertFalse(selected.contains("private.doc"), () -> "non-selectable node was submitted: " + selected);
        assertTrue(selected.contains("work.doc"), () -> "selectable node is missing: " + selected);
        assertTrue(selected.contains("resume.doc"), () -> "selectable node is missing: " + selected);
        assertCheckboxConfiguration(page.tree.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Tree: GitHub #14798 re-checking a child must select its parent despite a non-selectable sibling")
    void recheckingChildSelectsParent(Page page) {
        // Arrange
        TreeNode documents = page.tree.getChildren().get(0);
        TreeNode resume = documents.getChildren().get(2);
        documents.select();

        // Act - uncheck and re-check a selectable child
        resume.select();
        assertTrue(documents.isPartialSelected());
        resume.select();

        // Assert
        assertTrue(documents.isSelected());
        assertFalse(documents.getChildren().get(1).isSelected());

        page.showSelected.click();
        String selected = page.messages.getMessage(0).getDetail();
        assertTrue(selected.contains("Documents"), () -> "parent node is missing: " + selected);
        assertCheckboxConfiguration(page.tree.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Tree: GitHub #14798 toggling the parent must keep a preselected non-selectable node selected")
    void preselectedUnselectableNodeKeepsSelection(Page page) {
        // Arrange
        TreeNode legacy = page.tree.getChildren().get(1);
        TreeNode legacyDoc = legacy.getChildren().get(0);
        TreeNode modernDoc = legacy.getChildren().get(1);
        assertTrue(legacyDoc.isSelected());

        // Act - check and uncheck its parent
        legacy.select();
        assertTrue(modernDoc.isSelected());
        legacy.select();

        // Assert - screen and submitted selection must agree: the non-selectable node is untouched
        assertFalse(legacy.isSelected());
        assertFalse(modernDoc.isSelected());
        assertTrue(legacyDoc.isSelected());

        page.showSelected.click();
        assertEquals("legacy.doc", page.messages.getMessage(0).getDetail());
        assertCheckboxConfiguration(page.tree.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Tree: GitHub #14798 a parent without selectable children must not be selected by a grandchild")
    void parentWithoutSelectableChildrenKeepsState(Page page) {
        // Arrange - "Archive" only has the non-selectable "Locked"
        TreeNode archive = page.tree.getChildren().get(2);
        TreeNode locked = archive.getChildren().get(0);
        TreeNode oldDoc = locked.getChildren().get(0);

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
        assertEquals("legacy.doc", page.messages.getMessage(0).getDetail());
        assertCheckboxConfiguration(page.tree.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("Tree: GitHub #14798 the server must reject a submitted non-selectable node")
    void serverRejectsUnselectableNode(Page page) {
        // Arrange - bypass the widget's propagation rules and select "private.doc"
        PrimeSelenium.executeScript("var tree = PF('treeWidget'); tree.addToSelection('0_1'); tree.writeSelections();");

        // Act
        page.showSelected.click();

        // Assert
        assertEquals("legacy.doc", page.messages.getMessage(0).getDetail());
        assertCheckboxConfiguration(page.tree.getWidgetConfiguration());
    }

    private void assertCheckboxConfiguration(JSONObject cfg) {
        assertConfiguration(cfg, "treeWidget");
        assertEquals("checkbox", cfg.getString("selectionMode"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:tree")
        Tree tree;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:showSelected")
        CommandButton showSelected;

        @Override
        public String getLocation() {
            return "tree/tree009.xhtml";
        }
    }
}
