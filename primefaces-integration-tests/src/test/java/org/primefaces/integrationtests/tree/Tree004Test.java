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
import org.primefaces.selenium.component.SelectOneRadio;
import org.primefaces.selenium.component.Tree;
import org.primefaces.selenium.component.model.tree.TreeNode;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Tree004Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: Multiple selection")
    void multipleSelection(Page page) {
        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        TreeNode first = children.get(0);
        assertEquals("Documents", first.getLabelText());

        // Act
        first.select();

        // Assert
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Documents");

        // Act Pt. 2
        children.get(2).toggleSelection();

        // Assert Pt. 2
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Documents,Movies");

        // Act Pt. 3
        first.toggleSelection();

        // Assert Pt. 3
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Movies");

        // Act Pt. 4
        children.get(2).toggleSelection();

        // Assert Pt. 4
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "No node selected!", "");

        assertConfiguration(tree.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Tree: Multiple selection - cross-check without metakay")
    void multipleSelectionWithoutMetakey(Page page) {
        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        TreeNode first = children.get(0);
        assertEquals("Documents", first.getLabelText());

        // Act
        first.select();

        // Assert
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Documents");

        // Act Pt. 2
        children.get(2).select();

        // Assert Pt. 2
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Movies");

        assertConfiguration(tree.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Tree: Filter")
    void filter(Page page) {
        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        TreeNode first = children.get(0);
        assertEquals("Documents", first.getLabelText());

        assertTrue(children.get(0).getWebElement().isDisplayed());
        assertTrue(children.get(1).getWebElement().isDisplayed());
        assertTrue(children.get(2).getWebElement().isDisplayed());

        // Act
        tree.filter("Pro");

        // Assert
        children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        // L1
        assertFalse(children.get(0).getWebElement().isDisplayed());
        assertTrue(children.get(1).getWebElement().isDisplayed());
        assertFalse(children.get(2).getWebElement().isDisplayed());
        // L2
        assertFalse(children.get(1).getChildren().get(0).getWebElement().isDisplayed());
        assertTrue(children.get(1).getChildren().get(1).getWebElement().isDisplayed());
        assertFalse(children.get(1).getChildren().get(2).getWebElement().isDisplayed());

        // Act
        tree.clearFilter();

        // Assert
        children = tree.getChildren();

        assertTrue(children.get(0).getWebElement().isDisplayed());
        assertTrue(children.get(1).getWebElement().isDisplayed());
        assertTrue(children.get(2).getWebElement().isDisplayed());

        assertConfiguration(tree.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Tree: Multiple selection + Filter, https://github.com/primefaces/primefaces/issues/3926")
    void filterWithMultipleSelection(Page page) {
        // Arrange
        Tree tree = page.tree;
        page.selection.select("checkbox");
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        TreeNode first = children.get(0);
        assertEquals("Documents", first.getLabelText());

        // Act
        tree.filter("Pro");
        children = tree.getChildren();
        tree.getChildren().get(1).getChildren().get(1).select();

        // Assert
        page.buttonShowSelectedNodes.click();
        assertMessage(page.messages, 0, "Selected nodes", "Product Launch");
        assertTrue(children.get(1).isPartialSelected());
        assertTrue(children.get(1).getChildren().get(1).isSelected());

        // Act Pt. 2
        tree.clearFilter();

        // Assert Pt. 2
        page.buttonShowSelectedNodes.click();
        assertMessage(page.messages, 0, "Selected nodes", "Product Launch");
        children = tree.getChildren();
        assertBlankCheckbox(children.get(0));
        // TODO: currently broken (2021-09-12)
        // assertTrue(children.get(1).isPartialSelected());
        assertTrue(children.get(1).getChildren().get(1).isSelected());
        assertBlankCheckbox(children.get(2));

        // Act Pt. 3
        children.get(1).getChildren().get(0).select();
        children.get(1).getChildren().get(2).select();

        // Assert Pt. 3
        page.buttonShowSelectedNodes.click();
        assertMessage(page.messages, 0, "Selected nodes", "Product Launch,Meeting,Report Review,Events");
        assertBlankCheckbox(children.get(0));
        assertTrue(children.get(1).isSelected());
        assertTrue(children.get(1).getChildren().get(0).isSelected());
        assertTrue(children.get(1).getChildren().get(1).isSelected());
        assertTrue(children.get(1).getChildren().get(2).isSelected());
        assertBlankCheckbox(children.get(2));

        assertConfiguration(tree.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("Tree: Filter to empty")
    void filterToEmpty(Page page) {
        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        TreeNode first = children.get(0);
        assertEquals("Documents", first.getLabelText());

        assertTrue(children.get(0).getWebElement().isDisplayed());
        assertTrue(children.get(1).getWebElement().isDisplayed());
        assertTrue(children.get(2).getWebElement().isDisplayed());

        // Act
        tree.filter("Blah");

        // Assert
        children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        // L1
        assertFalse(children.get(0).getWebElement().isDisplayed());
        assertFalse(children.get(1).getWebElement().isDisplayed());
        assertFalse(children.get(2).getWebElement().isDisplayed());
        // L2
        assertFalse(children.get(1).getChildren().get(0).getWebElement().isDisplayed());
        assertFalse(children.get(1).getChildren().get(1).getWebElement().isDisplayed());
        assertFalse(children.get(1).getChildren().get(2).getWebElement().isDisplayed());

        // Act
        tree.clearFilter();

        // Assert
        children = tree.getChildren();

        assertTrue(children.get(0).getWebElement().isDisplayed());
        assertTrue(children.get(1).getWebElement().isDisplayed());
        assertTrue(children.get(2).getWebElement().isDisplayed());

        assertConfiguration(tree.getWidgetConfiguration());
    }

    private static void assertBlankCheckbox(TreeNode node) {
        assertFalse(node.isSelected());
        assertFalse(node.isPartialSelected());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:tree")
        Tree tree;

        @FindBy(id = "form:selection")
        SelectOneRadio selection;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:buttonShowSelectedNodes")
        CommandButton buttonShowSelectedNodes;

        @Override
        public String getLocation() {
            return "tree/tree004.xhtml";
        }
    }
}
