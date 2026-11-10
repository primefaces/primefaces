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
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:organigram}. Nodes are identified by the text of their content.
 */
public abstract class Organigram extends AbstractComponent {

    /**
     * Gets the drawn nodes, including the ones hidden by a collapsed parent.
     *
     * @return the nodes
     */
    public List<WebElement> getNodes() {
        return findElements(By.cssSelector(":scope > .target .p-organigram-node"));
    }

    /**
     * Gets the texts of all drawn nodes, including the ones hidden by a collapsed parent.
     *
     * @return the node texts
     */
    public List<String> getNodeTexts() {
        return getNodes().stream().map(this::getNodeText).collect(Collectors.toList());
    }

    /**
     * Gets a node by its text.
     *
     * @param text the text of the node content
     * @return the node
     */
    public WebElement getNode(String text) {
        for (WebElement node : getNodes()) {
            if (text.equals(getNodeText(node))) {
                return node;
            }
        }
        throw new NoSuchElementException("Node not found: " + text);
    }

    /**
     * Gets the text of the selected node.
     *
     * @return the text or null if no node is selected
     */
    public String getSelectedNodeText() {
        List<WebElement> selected = findElements(By.cssSelector(":scope > .target .p-organigram-node.selected"));
        return selected.isEmpty() ? null : getNodeText(selected.get(0));
    }

    /**
     * Is the node selected?
     *
     * @param text the text of the node content
     * @return true if selected
     */
    public boolean isSelected(String text) {
        return PrimeSelenium.hasCssClass(getNode(text), "selected");
    }

    /**
     * Selects a node by clicking it, does nothing if it is already selected. Only nodes which are selectable can be
     * selected.
     *
     * @param text the text of the node content
     */
    public void select(String text) {
        WebElement node = getNode(text);
        if (PrimeSelenium.hasCssClass(node, "selected")) {
            return;
        }
        // click the content, not the expander
        WebElement content = node.findElement(By.className("p-organigram-node-content"));
        if (PrimeSelenium.hasCssClass(node, "selectable") && ComponentUtils.hasAjaxBehavior(getRoot(), "select")) {
            PrimeSelenium.guardAjax(content).click();
        }
        else {
            content.click();
        }
    }

    /**
     * Is the node expanded, so its child nodes are displayed?
     *
     * @param text the text of the node content
     * @return true if expanded, false if collapsed or a node without child nodes
     */
    public boolean isExpanded(String text) {
        return PrimeSelenium.hasCssClass(getNode(text), "expanded");
    }

    /**
     * Expands a collapsible node, if collapsed.
     *
     * @param text the text of the node content
     */
    public void expand(String text) {
        WebElement node = getNode(text);
        if (PrimeSelenium.hasCssClass(node, "collapsed")) {
            toggle(node, "expand");
        }
    }

    /**
     * Collapses a collapsible node, if expanded.
     *
     * @param text the text of the node content
     */
    public void collapse(String text) {
        WebElement node = getNode(text);
        if (PrimeSelenium.hasCssClass(node, "collapsible") && PrimeSelenium.hasCssClass(node, "expanded")) {
            toggle(node, "collapse");
        }
    }

    private void toggle(WebElement node, String event) {
        WebElement expander = node.findElement(By.className("expander"));
        if (ComponentUtils.hasAjaxBehavior(getRoot(), event)) {
            PrimeSelenium.guardAjax(expander).click();
        }
        else {
            expander.click();
        }
    }

    private String getNodeText(WebElement node) {
        // textContent, as the text of nodes hidden by a collapsed parent is not visible
        return node.findElement(By.className("p-organigram-node-content")).getDomProperty("textContent").trim();
    }
}
