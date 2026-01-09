/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Tree001Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: Basic & Toggling")
    void basicAndToggling(Page page) {
        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        TreeNode first = children.get(0);
        assertEquals("Documents", first.getLabelText());

        List<TreeNode> firstChildren = first.getChildren();
        assertNotNull(firstChildren);
        assertEquals(2, firstChildren.size());

        TreeNode firstOfFirst = firstChildren.get(0);
        assertFalse(firstOfFirst.getWebElement().isDisplayed());

        // Act
        first.toggle();

        // Assert
        assertTrue(firstOfFirst.getWebElement().isDisplayed());
        assertEquals("Work", firstOfFirst.getLabelText());
        assertMessage(page.messages, 0, "Expanded", "Documents");

        // Act Pt. 2
        first.toggle();

        // Assert Pt. 2
        assertFalse(firstOfFirst.getWebElement().isDisplayed());
        assertMessage(page.messages, 0, "Collapsed", "Documents");

        assertConfiguration(tree.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Tree: Ajax update")
    void ajaxUpdate(Page page) {
        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        TreeNode first = children.get(0);
        assertEquals("Documents", first.getLabelText());

        TreeNode second = children.get(1);
        assertEquals("Events", second.getLabelText());

        TreeNode third = children.get(2);
        assertEquals("Movies", third.getLabelText());

        first.toggle();

        List<TreeNode> firstChildren = first.getChildren();
        assertNotNull(firstChildren);
        assertEquals(2, firstChildren.size());

        TreeNode firstOfFirst = firstChildren.get(0);
        assertTrue(firstOfFirst.getWebElement().isDisplayed());

        // Act
        PrimeSelenium.guardAjax(page.buttonUpdate).click();

        // Assert
        children = tree.getChildren();

        assertNotNull(children);
        assertEquals(3, children.size());

        first = children.get(0);
        assertEquals("Documents", first.getLabelText());

        second = children.get(1);
        assertEquals("Events", second.getLabelText());

        third = children.get(2);
        assertEquals("Movies", third.getLabelText());

        assertTrue(tree.isDisplayed());
        assertTrue(first.getWebElement().isDisplayed());
        assertTrue(second.getWebElement().isDisplayed());
        assertTrue(third.getWebElement().isDisplayed());

        firstChildren = first.getChildren();
        assertNotNull(firstChildren);
        assertEquals(2, firstChildren.size());

        firstOfFirst = firstChildren.get(0);
        assertTrue(firstOfFirst.getWebElement().isDisplayed());

        assertConfiguration(tree.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Tree: Tab and arrow keys to select")
    void tabbing(Page page) {
        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        // Act
        Actions actions = new Actions(page.getWebDriver());
        Action actionUnselect = actions.sendKeys(Keys.TAB, Keys.ARROW_DOWN, Keys.ARROW_RIGHT).build();
        PrimeSelenium.guardAjax(actionUnselect).perform();

        // Assert
        List<TreeNode> children = tree.getChildren();
        assertNotNull(children);
        assertEquals(3, children.size());

        TreeNode second = children.get(1);
        assertEquals("Events", second.getLabelText());
        assertTrue(second.getWebElement().isDisplayed());

        List<TreeNode> secondChildren = second.getChildren();
        assertNotNull(secondChildren);
        assertEquals(3, secondChildren.size());

        assertTrue(secondChildren.get(0).getWebElement().isDisplayed());
        assertTrue(secondChildren.get(1).getWebElement().isDisplayed());
        assertTrue(secondChildren.get(2).getWebElement().isDisplayed());

        assertConfiguration(tree.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:tree")
        Tree tree;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @Override
        public String getLocation() {
            return "tree/tree001.xhtml";
        }
    }
}
