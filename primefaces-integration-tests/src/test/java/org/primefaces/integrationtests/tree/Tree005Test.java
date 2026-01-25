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
import org.primefaces.selenium.component.Tree;
import org.primefaces.selenium.component.model.tree.TreeNode;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Tree005Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: FilterFunction")
    void filterFunction(Page page) {
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
        WebElement filter = tree.findElement(By.cssSelector("input.ui-tree-filter"));
        PrimeSelenium.guardAjax(filter).sendKeys("Pro");

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
        filter.clear();
        PrimeSelenium.guardAjax(filter).sendKeys(Keys.BACK_SPACE); // null filter press backspace to trigger the re-filtering

        // Assert
        children = tree.getChildren();

        assertTrue(children.get(0).getWebElement().isDisplayed());
        assertTrue(children.get(1).getWebElement().isDisplayed());
        assertTrue(children.get(2).getWebElement().isDisplayed());

        assertConfiguration(tree.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:tree")
        Tree tree;

        @Override
        public String getLocation() {
            return "tree/tree005.xhtml";
        }
    }
}
