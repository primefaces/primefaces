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
package org.primefaces.integrationtests.tree;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Tree;
import org.primefaces.selenium.component.model.tree.TreeNode;

public class Tree001Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: Basic & Toggling")
    public void testBasicAndToggling(Page page) {
        // Arrange
        Tree tree = page.tree;
        Assertions.assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        Assertions.assertNotNull(children);
        Assertions.assertEquals(3, children.size());

        TreeNode first = children.get(0);
        Assertions.assertEquals("Documents", first.getLabelText());

        List<TreeNode> firstChildren = first.getChildren();
        Assertions.assertNotNull(firstChildren);
        Assertions.assertEquals(2, firstChildren.size());

        TreeNode firstOfFirst = firstChildren.get(0);
        Assertions.assertFalse(firstOfFirst.getWebElement().isDisplayed());

        // Act
        first.toggle();

        // Assert
        Assertions.assertTrue(firstOfFirst.getWebElement().isDisplayed());
        Assertions.assertEquals("Work", firstOfFirst.getLabelText());
        assertMessage(page.messages, 0, "Expanded", "Documents");

        // Act Pt. 2
        first.toggle();

        // Assert Pt. 2
        Assertions.assertFalse(firstOfFirst.getWebElement().isDisplayed());
        assertMessage(page.messages, 0, "Collapsed", "Documents");

        assertConfiguration(tree.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Tree: Ajax update")
    public void testAjaxUpdate(Page page) {
        // Arrange
        Tree tree = page.tree;
        Assertions.assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        Assertions.assertNotNull(children);
        Assertions.assertEquals(3, children.size());

        TreeNode first = children.get(0);
        Assertions.assertEquals("Documents", first.getLabelText());

        TreeNode second = children.get(1);
        Assertions.assertEquals("Events", second.getLabelText());

        TreeNode third = children.get(2);
        Assertions.assertEquals("Movies", third.getLabelText());

        first.toggle();

        List<TreeNode> firstChildren = first.getChildren();
        Assertions.assertNotNull(firstChildren);
        Assertions.assertEquals(2, firstChildren.size());

        TreeNode firstOfFirst = firstChildren.get(0);
        Assertions.assertTrue(firstOfFirst.getWebElement().isDisplayed());

        // Act
        PrimeSelenium.guardAjax(page.buttonUpdate).click();

        // Assert
        children = tree.getChildren();

        Assertions.assertNotNull(children);
        Assertions.assertEquals(3, children.size());

        first = children.get(0);
        Assertions.assertEquals("Documents", first.getLabelText());

        second = children.get(1);
        Assertions.assertEquals("Events", second.getLabelText());

        third = children.get(2);
        Assertions.assertEquals("Movies", third.getLabelText());

        Assertions.assertTrue(tree.isDisplayed());
        Assertions.assertTrue(first.getWebElement().isDisplayed());
        Assertions.assertTrue(second.getWebElement().isDisplayed());
        Assertions.assertTrue(third.getWebElement().isDisplayed());

        firstChildren = first.getChildren();
        Assertions.assertNotNull(firstChildren);
        Assertions.assertEquals(2, firstChildren.size());

        firstOfFirst = firstChildren.get(0);
        Assertions.assertTrue(firstOfFirst.getWebElement().isDisplayed());

        assertConfiguration(tree.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Tree: Tab and arrow keys to select")
    public void testTabbing(Page page) {
        // Arrange
        Tree tree = page.tree;
        Assertions.assertNotNull(tree);

        // Act
        Actions actions = new Actions(page.getWebDriver());
        Action actionUnselect = actions.sendKeys(Keys.TAB, Keys.ARROW_DOWN, Keys.ARROW_RIGHT).build();
        PrimeSelenium.guardAjax(actionUnselect).perform();

        // Assert
        List<TreeNode> children = tree.getChildren();
        Assertions.assertNotNull(children);
        Assertions.assertEquals(3, children.size());

        TreeNode second = children.get(1);
        Assertions.assertEquals("Events", second.getLabelText());
        Assertions.assertTrue(second.getWebElement().isDisplayed());

        List<TreeNode> secondChildren = second.getChildren();
        Assertions.assertNotNull(secondChildren);
        Assertions.assertEquals(3, secondChildren.size());

        Assertions.assertTrue(secondChildren.get(0).getWebElement().isDisplayed());
        Assertions.assertTrue(secondChildren.get(1).getWebElement().isDisplayed());
        Assertions.assertTrue(secondChildren.get(2).getWebElement().isDisplayed());

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
