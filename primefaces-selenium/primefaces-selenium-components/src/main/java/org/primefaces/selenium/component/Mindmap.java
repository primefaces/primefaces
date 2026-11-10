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

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:mindmap}, drawn as SVG with Raphael. Nodes are identified by their label.
 */
public abstract class Mindmap extends AbstractComponent {

    /**
     * Gets the label of the node in the center: the root or the last selected node.
     *
     * @return the label
     */
    public String getCenterNodeLabel() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".nodes[0].data('model').label;");
    }

    /**
     * Gets the labels of all drawn nodes: the center node, its child nodes and its parent node (if any).
     *
     * @return the labels
     */
    public List<String> getNodeLabels() {
        List<Object> labels = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".nodes.map(function(node) {"
                    + " return node.data('model').label; });");
        return labels.stream().map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * Gets the text element of a node.
     *
     * @param label the label of the node
     * @return the SVG text element
     */
    public WebElement getNode(String label) {
        for (WebElement text : findElements(By.tagName("text"))) {
            if (label.equals(text.getDomProperty("textContent"))) {
                return text;
            }
        }
        throw new NoSuchElementException("Node not found: " + label);
    }

    /**
     * Is this component AJAX enabled with "select"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isSelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "select");
    }

    /**
     * Is this component AJAX enabled with "dblselect"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isDblselectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "dblselect");
    }

    /**
     * Selects a selectable node with a click. With the "select" behavior, the node is loaded from the server and drawn in
     * the center with its child nodes and its parent node.
     *
     * @param label the label of the node
     */
    public void selectNode(String label) {
        WebElement node = getVisibleNode(label);
        if (isSelectAjaxified()) {
            // the click is handled after a delay to detect double clicks, the guard waits for the request
            PrimeSelenium.guardAjax(node).click();
            waitUntilDrawn();
        }
        else {
            node.click();
        }
    }

    /**
     * Double-clicks a selectable node, which fires the "dblselect" behavior.
     *
     * @param label the label of the node
     */
    public void doubleClickNode(String label) {
        Action action = new Actions(getWebDriver()).doubleClick(getVisibleNode(label)).build();
        if (isDblselectAjaxified()) {
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            action.perform();
        }
    }

    private WebElement getVisibleNode(String label) {
        WebElement node = getNode(label);
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(node));
        return node;
    }

    /**
     * Waits until the center node is moved to the center and its child nodes and parent node are drawn.
     */
    private void waitUntilDrawn() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.script("var w = " + getWidgetByIdScript() + ";"
                    + "var model = w.nodes[0].data('model');"
                    + "return w.nodes.length === 1 + (model.children ? model.children.length : 0) + (model.parent ? 1 : 0);"));
    }
}
