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

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Component wrapper for the PrimeFaces {@code p:dock}.
 */
public abstract class Dock extends AbstractComponent {

    /**
     * Gets the position of the dock.
     *
     * @return "bottom" or "top"
     */
    public String getPosition() {
        return getWidgetConfiguration().optString("position", "bottom");
    }

    /**
     * Gets the horizontal alignment of the items.
     *
     * @return "left", "center" or "right"
     */
    public String getHalign() {
        return getWidgetConfiguration().optString("halign", "center");
    }

    /**
     * Gets the items of the dock.
     *
     * @return the {@code li.p-dock-item} elements
     */
    public List<WebElement> getItems() {
        return findElements(By.cssSelector("li.p-dock-item"));
    }

    /**
     * Gets the label of the given item, also if it is hidden because the item is not hovered.
     *
     * @param item the {@code li.p-dock-item} element
     * @return the label
     */
    public String getItemLabel(WebElement item) {
        return item.findElement(By.cssSelector("em > span")).getDomProperty("textContent");
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
     * @return the {@code li.p-dock-item} element
     */
    public WebElement findItemByValue(String value) {
        return getItems().stream()
                .filter(item -> value.equals(getItemLabel(item)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no dock item with value '" + value + "'"));
    }

    /**
     * Moves the mouse over the item, which magnifies it and shows its label.
     *
     * @param value the label of the item
     */
    public void hoverItem(String value) {
        new Actions(getWebDriver()).moveToElement(findItemByValue(value)).perform();
    }

    /**
     * Moves the mouse away from the items to the empty edge of the dock (the right edge if the items are aligned left,
     * otherwise the left edge).
     */
    public void unhoverItems() {
        int edge = getRoot().getSize().getWidth() / 2 - 1;
        new Actions(getWebDriver()).moveToElement(getRoot(), "left".equals(getHalign()) ? edge : -edge, 0).perform();
    }

    /**
     * Is the item hovered, so it is magnified?
     *
     * @param value the label of the item
     * @return true if active
     */
    public boolean isItemActive(String value) {
        return PrimeSelenium.hasCssClass(findItemByValue(value), "active");
    }

    /**
     * Clicks an item by its label, guarded if it is AJAXified.
     *
     * @param value the label of the item
     */
    public void clickItem(String value) {
        WebElement link = findItemByValue(value).findElement(By.tagName("a"));
        if (isAjaxified(link, "onclick")) {
            PrimeSelenium.guardAjax(link).click();
        }
        else {
            link.click();
        }
    }
}
