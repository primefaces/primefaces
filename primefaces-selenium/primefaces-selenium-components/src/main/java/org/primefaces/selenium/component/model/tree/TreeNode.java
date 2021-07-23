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
package org.primefaces.selenium.component.model.tree;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Tree;

import java.util.List;
import java.util.stream.Collectors;

public class TreeNode {

    private WebElement webElement;
    private TreeNode parent;
    private Tree tree;
    private String selector;
    private String childSelector;

    public TreeNode(WebElement webElement, String selector, Tree tree) {
        this.webElement = webElement;
        this.selector = selector;
        this.childSelector = selector + ">.ui-treenode-children>.ui-treenode";
        this.tree = tree;
    }

    public TreeNode(WebElement webElement, String selector, TreeNode parent) {
        this.webElement = webElement;
        this.selector = selector;
        this.childSelector = selector + ">.ui-treenode-children>.ui-treenode";
        this.parent = parent;
        this.tree = parent.getTree();
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    public Tree getTree() {
        return tree;
    }

    public TreeNode getParent() {
        return parent;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public void toggle() {
        PrimeSelenium.guardAjax(getTreeToggler()).click();
    }

    public void select() {
        // TODO: we are only allowed to guardAjax if select/unselect is ajaxified!
        PrimeSelenium.guardAjax(getLabel()).click();
    }

    public WebElement getTreeToggler() {
        return webElement.findElement(By.cssSelector(".ui-treenode-content .ui-tree-toggler"));
    }

    public String getLabelText() {
        return getLabel().getText();
    }

    public WebElement getLabel() {
        return webElement.findElement(By.cssSelector(".ui-treenode-content .ui-treenode-label"));
    }

    public List<TreeNode> getChildren() {
        return webElement.findElements(By.cssSelector(childSelector)).stream().map(e -> new TreeNode(e, childSelector, this))
                    .collect(Collectors.toList());
    }

}
