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
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Tree;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.component.model.tree.TreeNode;

import java.util.List;

public class Tree004Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: Multiple selection")
    public void testMultipleSelection(Page page) {
        // Arrange
        Tree tree = page.tree;
        Assertions.assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        Assertions.assertNotNull(children);
        Assertions.assertEquals(3, children.size());

        TreeNode first = children.get(0);
        Assertions.assertEquals("Documents", first.getLabelText());

        // Act
        first.select();

        // Assert
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Documents");

        // Act Pt. 2
        Actions actions = new Actions(page.getWebDriver());
        Action actionSelect = actions.keyDown(Keys.META).click(children.get(2).getLabel()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionSelect).perform();

        // Assert Pt. 2
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Documents,Movies");

        // Act Pt. 3
        Action actionUnselect = actions.keyDown(Keys.META).click(first.getLabel()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionUnselect).perform();

        // Assert Pt. 3
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Movies");

        // Act Pt. 4
        actionUnselect = actions.keyDown(Keys.META).click(children.get(2).getLabel()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionUnselect).perform();

        // Assert Pt. 4
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "No node selected!", "");

        assertConfiguration(tree.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Tree: Multiple selection - cross-check without metakay")
    public void testMultipleSelectionWithoutMetakey(Page page) {
        // Arrange
        Tree tree = page.tree;
        Assertions.assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        Assertions.assertNotNull(children);
        Assertions.assertEquals(3, children.size());

        TreeNode first = children.get(0);
        Assertions.assertEquals("Documents", first.getLabelText());

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
    public void testFilter(Page page) {
        // Arrange
        Tree tree = page.tree;
        Assertions.assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        Assertions.assertNotNull(children);
        Assertions.assertEquals(3, children.size());

        TreeNode first = children.get(0);
        Assertions.assertEquals("Documents", first.getLabelText());

        Assertions.assertEquals(true, children.get(0).getWebElement().isDisplayed());
        Assertions.assertEquals(true, children.get(1).getWebElement().isDisplayed());
        Assertions.assertEquals(true, children.get(2).getWebElement().isDisplayed());

        // Act
        WebElement filter = tree.findElement(By.cssSelector("input.ui-tree-filter"));
        ComponentUtils.sendKeys(filter, "Pro");
        PrimeSelenium.wait(300 + 100); // 300ms are hardcoded as filterDelay to tree.vertical.js
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(tree.findElement(By.id("form:tree:1_1"))));

        // Assert
        children = tree.getChildren();

        Assertions.assertNotNull(children);
        Assertions.assertEquals(3, children.size());

        // L1
        Assertions.assertEquals(false, children.get(0).getWebElement().isDisplayed());
        Assertions.assertEquals(true, children.get(1).getWebElement().isDisplayed());
        Assertions.assertEquals(false, children.get(2).getWebElement().isDisplayed());
        // L2
        Assertions.assertEquals(false, children.get(1).getChildren().get(0).getWebElement().isDisplayed());
        Assertions.assertEquals(true, children.get(1).getChildren().get(1).getWebElement().isDisplayed());
        Assertions.assertEquals(false, children.get(1).getChildren().get(2).getWebElement().isDisplayed());

        // Act
        filter.clear();
        ComponentUtils.sendKeys(filter, Keys.BACK_SPACE); // null filter press backspace to trigger the re-filtering
        PrimeSelenium.wait(300 + 100); // 300ms are hardcoded as filterDelay to tree.vertical.js
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(tree.findElement(By.id("form:tree:0"))));

        // Assert
        children = tree.getChildren();

        Assertions.assertEquals(true, children.get(0).getWebElement().isDisplayed());
        Assertions.assertEquals(true, children.get(1).getWebElement().isDisplayed());
        Assertions.assertEquals(true, children.get(2).getWebElement().isDisplayed());
    }

    @Test
    @Order(4)
    @DisplayName("Tree: Multiple selection + Filter, https://github.com/primefaces/primefaces/issues/3926")
    public void testFilterWithMultipleSelection(Page page) {
        // TODO: implement

        // Arrange
        Tree tree = page.tree;
        Assertions.assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();

        Assertions.assertNotNull(children);
        Assertions.assertEquals(3, children.size());

        TreeNode first = children.get(0);
        Assertions.assertEquals("Documents", first.getLabelText());

        // Act
        first.select();

        // Assert
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Documents");

        // Act Pt. 2
        Actions actions = new Actions(page.getWebDriver());
        Action actionSelect = actions.keyDown(Keys.META).click(children.get(2).getLabel()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionSelect).perform();

        // Assert Pt. 2
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Documents,Movies");

        // Act Pt. 3
        Action actionUnselect = actions.keyDown(Keys.META).click(first.getLabel()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionUnselect).perform();

        // Assert Pt. 3
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "Selected nodes", "Movies");

        // Act Pt. 4
        actionUnselect = actions.keyDown(Keys.META).click(children.get(2).getLabel()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionUnselect).perform();

        // Assert Pt. 4
        PrimeSelenium.guardAjax(page.buttonShowSelectedNodes).click();
        assertMessage(page.messages, 0, "No node selected!", "");

        assertConfiguration(tree.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:tree")
        Tree tree;

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
