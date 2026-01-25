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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Tree007Test extends AbstractTreeTest {

    @Test
    @Order(1)
    @DisplayName("Tree: Partial/Selection with preselection")
    void preselection(Page page) {

        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();
        assertNotNull(children);

        TreeNode documentsNode = children.get(0);
        assertTrue(documentsNode.isPartialSelected());
        assertFalse(documentsNode.isExpanded());

        TreeNode eventsNode = children.get(1);
        assertTrue(eventsNode.isSelected());
        assertFalse(eventsNode.isExpanded());
        eventsNode.toggle();
        assertTrue(eventsNode.isExpanded());

        TreeNode meetingNode = eventsNode.getChildren().get(0);
        assertTrue(meetingNode.isSelected());
        meetingNode.select();
        assertFalse(meetingNode.isSelected());

        assertTrue(eventsNode.isPartialSelected());
    }

    @Test
    @Order(1)
    @DisplayName("Tree: Partial/Selection Without preselection")
    void withoutPreselection(PageWithoutPreselection page) {

        // Arrange
        Tree tree = page.tree;
        assertNotNull(tree);

        List<TreeNode> children = tree.getChildren();
        assertNotNull(children);

        TreeNode documentsNode = children.get(0);
        assertFalse(documentsNode.isSelected());
        assertFalse(documentsNode.isExpanded());

        TreeNode eventsNode = children.get(1);
        assertFalse(eventsNode.isSelected());
        assertFalse(eventsNode.isExpanded());
        eventsNode.toggle();
        assertTrue(eventsNode.isExpanded());

        eventsNode.select();

        assertTrue(eventsNode.isSelected());

        TreeNode meetingNode = eventsNode.getChildren().get(0);
        assertTrue(meetingNode.isSelected());
        meetingNode.select();

        assertFalse(meetingNode.isSelected());
        assertTrue(eventsNode.isPartialSelected());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:tree")
        Tree tree;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "tree/tree007.xhtml";
        }
    }

    public static class PageWithoutPreselection extends AbstractPrimePage {
        @FindBy(id = "form:tree")
        Tree tree;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "tree/tree007_withoutPreselection.xhtml";
        }
    }
}
