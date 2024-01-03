/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Tree;
import org.primefaces.selenium.component.model.tree.TreeNode;

class Tree003Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @RepeatedTest(100)
    @DisplayName("Tree: Drag and drop still allows tab and arrow keys to select")
    void tabbing(Page page) throws IOException {
        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        // Act
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(page.tree.getWrappedElement()));
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(page.tree.getChildren().get(0).getWebElement()));
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(page.tree.getChildren().get(1).getWebElement()));
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(page.tree.getChildren().get(2).getWebElement()));
        Actions actions = new Actions(page.getWebDriver());
        Action action = actions.sendKeys(Keys.TAB).build();
        action.perform();
        action = actions.sendKeys(Keys.ARROW_DOWN).build();
        action.perform();
        action = actions.sendKeys(Keys.ARROW_RIGHT).build();
        action.perform();

        // Assert
        // tree = PrimeSelenium.createFragment(Tree.class, By.id("form:tree")); // does this improve stability?
        List<TreeNode> children = tree.getChildren();
        assertNotNull(children);
        assertEquals(3, children.size());

        TreeNode second = children.get(1);
        assertEquals("Events", second.getLabelText());
        assertDisplayed(second.getWebElement());
        System.out.println("Tree - second node, HTML: " + second.getWebElement().getAttribute("innerHTML"));


        try {
            JavascriptExecutor j = (JavascriptExecutor) page.getWebDriver();
            Long verticalScrollPosition = (Long) j.executeScript("return window.pageYOffset;");
            Long horizontalScrollPosition = (Long) j.executeScript("return window.pageXOffset;");
            System.out.println("Vertical scroll position: " + verticalScrollPosition + ", horizontal scroll position: " + horizontalScrollPosition);
            Long windowHeight = (Long) j.executeScript("return window.innerHeight;");
            Long windowWidth = (Long) j.executeScript("return window.innerWidth;");
            System.out.println("Window - height: " + windowHeight + ", width: " + windowWidth);

            List<TreeNode> secondChildren = second.getChildren();
            assertNotNull(secondChildren);
            assertEquals(3, secondChildren.size());

            secondChildren.forEach(t -> {
                System.out.println("Tree - second node - child, HTML: " + t.getWebElement().getAttribute("innerHTML"));
                // stabilize flickering test - be sure keyboard-action was executed
                PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleInViewport(t.getWebElement()));
                assertDisplayed(t.getWebElement());
            });
        }
        catch (Exception ex) {
            File scrFile = ((TakesScreenshot) page.getWebDriver()).getScreenshotAs(OutputType.FILE);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
            FileUtils.copyFile(scrFile, new File("/tmp/pf_it/" + LocalDateTime.now().format(formatter) + "_" + UUID.randomUUID().toString() + ".png"));
            throw ex;
        }

        assertConfiguration(tree.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:tree")
        Tree tree;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "tree/tree003.xhtml";
        }
    }
}
