/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Tree;
import org.primefaces.selenium.component.model.tree.TreeNode;

import java.util.List;

public class Tree005Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: FilterFunction")
    public void testFilterFunction(Page page) {
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
        PrimeSelenium.guardAjax(filter).sendKeys("Pro");

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
        PrimeSelenium.guardAjax(filter).sendKeys(Keys.BACK_SPACE); // null filter press backspace to trigger the re-filtering

        // Assert
        children = tree.getChildren();

        Assertions.assertEquals(true, children.get(0).getWebElement().isDisplayed());
        Assertions.assertEquals(true, children.get(1).getWebElement().isDisplayed());
        Assertions.assertEquals(true, children.get(2).getWebElement().isDisplayed());

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
