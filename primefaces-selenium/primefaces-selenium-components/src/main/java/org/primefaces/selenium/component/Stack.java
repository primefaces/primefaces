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

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:stack}.
 */
public abstract class Stack extends AbstractComponent {

    /**
     * Gets the image which expands and collapses the stack.
     *
     * @return the toggler {@code img} element
     */
    public WebElement getToggler() {
        return findElement(By.xpath("./img"));
    }

    /**
     * Gets the list containing the items.
     *
     * @return the {@code ul} element
     */
    public WebElement getList() {
        return findElement(By.xpath("./ul"));
    }

    /**
     * Gets the items of the stack.
     *
     * @return the {@code li} elements
     */
    public List<WebElement> getItems() {
        return findElements(By.xpath("./ul/li"));
    }

    /**
     * Gets the label of the given item, also if it is hidden because the stack is collapsed.
     *
     * @param item the {@code li} element
     * @return the label
     */
    public String getItemLabel(WebElement item) {
        return item.findElement(By.cssSelector("a > span")).getDomProperty("textContent");
    }

    /**
     * Gets the labels of all items.
     *
     * @return the labels in rendered order
     */
    public List<String> getItemLabels() {
        return getItems().stream().map(this::getItemLabel).collect(Collectors.toList());
    }

    /**
     * Finds an item by its label.
     *
     * @param value the label of the item
     * @return the {@code li} element
     */
    public WebElement findItemByValue(String value) {
        return getItems().stream()
                .filter(item -> value.equals(getItemLabel(item)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no stack item with value '" + value + "'"));
    }

    /**
     * Is the stack expanded, so the items and their labels are shown?
     *
     * @return true if expanded
     */
    public boolean isExpanded() {
        return PrimeSelenium.hasCssClass(getList(), "openStack");
    }

    /**
     * Expands or collapses the stack by clicking the toggler and waits until the items are moved.
     */
    public void toggle() {
        getToggler().click();
        waitForAnimation();
    }

    /**
     * Expands the stack, if collapsed.
     */
    public void expand() {
        if (!isExpanded()) {
            toggle();
        }
    }

    /**
     * Collapses the stack, if expanded.
     */
    public void collapse() {
        if (isExpanded()) {
            toggle();
        }
    }

    /**
     * Expands the stack if needed and clicks an item by its label, guarded if it is AJAXified.
     *
     * @param value the label of the item
     */
    public void clickItem(String value) {
        expand();
        waitForAnimation();
        WebElement link = findItemByValue(value).findElement(By.tagName("a"));
        if (isAjaxified(link, "onclick")) {
            PrimeSelenium.guardAjax(link).click();
        }
        else {
            link.click();
        }
    }

    private void waitForAnimation() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.script(
                "return " + getWidgetByIdScript() + ".jq.find(':animated').length === 0;"));
    }
}
