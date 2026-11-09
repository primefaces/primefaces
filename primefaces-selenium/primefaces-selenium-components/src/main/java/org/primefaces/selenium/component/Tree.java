/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.selenium.component;

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.model.tree.TreeNode;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:tree}.
 */
public abstract class Tree extends AbstractComponent {

    public static final String CONTAINER_CLASS = "p-tree";
    public static final String ROOT_NODES_CLASS = "p-tree-container";
    public static final String TREE_NODE_CLASS = "p-treenode";
    public static final String PARENT_NODE_CLASS = "p-treenode-parent";
    public static final String CHILDREN_NODES_CLASS = "p-treenode-children";
    public static final String NODE_CONTENT_CLASS = "p-treenode-content";
    public static final String LEAF_NODE_CLASS = "p-treenode-leaf";
    public static final String SELECTED_NODE_CLASS = "p-treenode-selected";
    public static final String PARTIAL_SELECTED_NODE_CLASS = "p-treenode-hasselected";

    public static final String CHILD_SELECTOR = ".p-tree-container>.p-treenode";

    public List<TreeNode> getChildren() {
        return findElements(By.cssSelector(CHILD_SELECTOR)).stream()
                .map(e -> new TreeNode(this, e.getDomAttribute("data-rowkey"), null))
                .collect(Collectors.toList());
    }

    /**
     * Gets the node with the given rowKey, e.g. to access nodes of a horizontal tree. The parent of the returned node is
     * not resolved.
     *
     * @param rowKey the rowKey like "0_1"
     * @return the node
     */
    public TreeNode getNode(String rowKey) {
        return new TreeNode(this, rowKey, null);
    }

    /**
     * Is the node with the given rowKey rendered (e.g. its parent is expanded or the children were loaded)?
     *
     * @param rowKey the rowKey like "0_1"
     * @return true if the node exists
     */
    public boolean hasNode(String rowKey) {
        return !findElements(By.cssSelector("." + TREE_NODE_CLASS + "[data-rowkey='" + rowKey + "']")).isEmpty();
    }

    /**
     * Gets the filter input of the tree.
     *
     * @return the filter input
     */
    public WebElement getFilterInput() {
        return findElement(By.cssSelector("input.p-tree-filter"));
    }

    /**
     * Filters the tree using the widget configuration for "filterEvent", which always fires an AJAX request.
     *
     * @param value the filter value, {@code null} or empty to remove the filter
     */
    public void filter(String value) {
        WebElement input = getFilterInput();
        String filterEvent = getWidgetConfiguration().optString("filterEvent", "keyup").toLowerCase(Locale.ROOT);
        CharSequence keys = (value == null || value.isEmpty()) ? Keys.BACK_SPACE : value;
        switch (filterEvent) {
            case "enter":
            case "keydown":
                // the widget turns filterEvent="enter" into "keydown" (filtering on ENTER only), so the runtime config
                // can't tell both apart: with "enter" the ENTER fires the request, with "keydown" the typed value
                // fires the debounced request while ENTER is ignored
                input.clear();
                input.sendKeys(keys);
                PrimeSelenium.guardAjax(input).sendKeys(Keys.ENTER);
                break;
            case "change":
            case "blur":
                // no clear() as it would already fire the change event
                input.sendKeys(Keys.chord(PrimeSelenium.isMacOs() ? Keys.COMMAND : Keys.CONTROL, "a"));
                input.sendKeys(keys);
                PrimeSelenium.guardAjax(input).sendKeys(Keys.TAB);
                break;
            default:
                // key events trigger the (debounced) filter, an empty filter needs a key event too
                input.clear();
                PrimeSelenium.guardAjax(input).sendKeys(keys);
                break;
        }
    }

    /**
     * Removes the filter of the tree, which always fires an AJAX request.
     */
    public void clearFilter() {
        filter(null);
    }

}
