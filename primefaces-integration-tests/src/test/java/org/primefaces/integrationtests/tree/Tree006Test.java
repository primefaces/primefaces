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
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Tree;
import org.primefaces.selenium.component.model.tree.TreeNode;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Tree006Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: Lazy Loading")
    void singleSelection(Page page) {

        File webappRoot = new File("src/main/webapp");

        // Assert - loading root folder content
        assertEquals(1, page.messages.getAllMessages().size());
        String lazyLoadedPath = page.messages.getMessage(0).getDetail().replace("\\", "/");
        assertTrue(lazyLoadedPath.endsWith("primefaces-integration-tests/src/main/webapp/") //src
            || lazyLoadedPath.endsWith("primefaces-integration-tests/")); //target

        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();
        assertNotNull(children);
        assertEquals(webappRoot.listFiles().length, children.size() - 1); // -1 because of missing META-INF

        TreeNode child = children.stream()
                .filter(node -> "accordionpanel".equals(node.getLabelText())).findFirst().orElse(null);
        assertNotNull(child);
        child = children.stream()
                .filter(node -> "WEB-INF".equals(node.getLabelText())).findFirst().orElse(null);
        assertNotNull(child);

        // Act
        child = children.stream()
                .filter(node -> "tree".equals(node.getLabelText())).findFirst().orElse(null);
        child.toggle();

        // Assert - loading "tree" folder content
        assertEquals(1, page.messages.getAllMessages().size());
        lazyLoadedPath = page.messages.getMessage(0).getDetail().replace("\\", "/");
        assertTrue(lazyLoadedPath.endsWith("primefaces-integration-tests/src/main/webapp/tree") //src
            || lazyLoadedPath.endsWith("primefaces-integration-tests/tree"));  //target
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:tree")
        Tree tree;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "tree/tree006.xhtml";
        }
    }
}
