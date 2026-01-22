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
package org.primefaces.selenium.component.model.tree;

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Tree;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class TreeNode {

    private TreeNode parent;
    private Tree tree;
    private String rowKey;

    public TreeNode(Tree tree, String rowKey, TreeNode parent) {
        this.tree = tree;
        this.rowKey = rowKey;
        this.parent = parent;
    }

    public WebElement getWebElement() {
        return tree.findElement(By.cssSelector(".ui-treenode[data-rowkey='" + rowKey + "']"));
    }

    public Tree getTree() {
        return tree;
    }

    public TreeNode getParent() {
        return parent;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void toggle() {
        PrimeSelenium.guardAjax(getTreeToggler()).click();
    }

    public void select() {
        // TODO: we are only allowed to guardAjax if select/unselect is ajaxified!
        PrimeSelenium.guardAjax(getLabel()).click();
    }

    public WebElement getTreeToggler() {
        return getWebElement().findElement(By.cssSelector(".ui-treenode-content .ui-tree-toggler"));
    }

    public String getLabelText() {
        return getLabel().getText();
    }

    public WebElement getLabel() {
        return getWebElement().findElement(By.cssSelector(".ui-treenode-content .ui-treenode-label"));
    }

    public List<TreeNode> getChildren() {
        // we need a xpath selector as selenium doesn't support direct child selector like #findElements(">...")
        return getWebElement()
                .findElements(By.xpath("./ul/li[contains(@class, 'ui-treenode')]")).stream()
                .map(e -> new TreeNode(tree, e.getDomAttribute("data-rowkey"), this))
                .collect(Collectors.toList());
    }

    public boolean isExpanded() {
        WebElement children = getWebElement().findElement(By.xpath("./ul"));
        return PrimeSelenium.isElementDisplayed(children);
    }

    public boolean isSelected() {
        return PrimeSelenium.hasCssClass(getWebElement(), Tree.SELECTED_NODE_CLASS);
    }

    public boolean isPartialSelected() {
        return PrimeSelenium.hasCssClass(getWebElement(), Tree.PARTIAL_SELECTED_NODE_CLASS);
    }
}
