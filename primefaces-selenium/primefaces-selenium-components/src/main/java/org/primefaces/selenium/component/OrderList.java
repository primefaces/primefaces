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
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

/**
 * Component wrapper for the PrimeFaces {@code p:orderList}.
 */
public abstract class OrderList extends AbstractComponent {

    private static final String ITEM_VALUE_ATTR = "data-item-value";

    /**
     * Gets the items in their current order.
     *
     * @return the items
     */
    public List<WebElement> getItems() {
        return findElements(By.cssSelector(".p-orderlist-list > .p-orderlist-item"));
    }

    /**
     * Gets the values of the items in their current order.
     *
     * @return the item values
     */
    public List<String> getItemValues() {
        return getItems().stream().map(item -> item.getDomAttribute(ITEM_VALUE_ATTR)).collect(Collectors.toList());
    }

    /**
     * Gets the labels of the items in their current order.
     *
     * @return the item labels
     */
    public List<String> getItemLabels() {
        return getItems().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /**
     * Gets an item by its value or label.
     *
     * @param item the value or label of the item
     * @return the item
     * @throws NoSuchElementException if no item has this value or label
     */
    public WebElement getItem(String item) {
        return getItems().stream()
                    .filter(e -> item.equals(e.getDomAttribute(ITEM_VALUE_ATTR)) || item.equals(e.getText()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Could not find item " + item + " in orderlist " + getId()));
    }

    /**
     * Gets the values of the selected items.
     *
     * @return the selected item values in their current order
     */
    public List<String> getSelectedValues() {
        return getItems().stream()
                    .filter(item -> PrimeSelenium.hasCssClass(item, "p-state-highlight"))
                    .map(item -> item.getDomAttribute(ITEM_VALUE_ATTR))
                    .collect(Collectors.toList());
    }

    /**
     * Is the item selected?
     *
     * @param item the value or label of the item
     * @return true if selected
     */
    public boolean isSelected(String item) {
        return PrimeSelenium.hasCssClass(getItem(item), "p-state-highlight");
    }

    /**
     * Is the orderlist disabled?
     *
     * @return true if disabled
     */
    public boolean isDisabled() {
        return PrimeSelenium.hasCssClass(getRoot(), "p-state-disabled");
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
     * Is this component AJAX enabled with "unselect"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isUnselectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "unselect");
    }

    /**
     * Is this component AJAX enabled with "reorder"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isReorderAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "reorder");
    }

    /**
     * Selects an item with a plain click, which replaces the current selection. Guards AJAX if a "select" behavior
     * exists. Does nothing if the orderlist is disabled, the mouse cannot reach its items (pointer-events: none).
     *
     * @param item the value or label of the item
     */
    public void select(String item) {
        if (isDisabled()) {
            return;
        }
        WebElement element = getItem(item);
        if (isSelectAjaxified()) {
            PrimeSelenium.guardAjax(element).click();
        }
        else {
            element.click();
        }
    }

    /**
     * Adds an item to or removes it from the selection with a Cmd (macOS) / Ctrl click. Guards AJAX if a "select" /
     * "unselect" behavior exists.
     *
     * @param item the value or label of the item
     */
    public void toggleSelection(String item) {
        WebElement element = getItem(item);
        boolean selecting = !PrimeSelenium.hasCssClass(element, "p-state-highlight");
        boolean ajaxified = selecting ? isSelectAjaxified() : isUnselectAjaxified();
        Keys metaKey = PrimeSelenium.isMacOs() ? Keys.COMMAND : Keys.CONTROL;
        Action action = new Actions(getWebDriver()).keyDown(metaKey).click(element).keyUp(metaKey).build();
        if (ajaxified) {
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            action.perform();
        }
    }

    /**
     * Gets the "Move Up" button.
     *
     * @return the button
     */
    public WebElement getMoveUpButton() {
        return findElement(By.className("p-orderlist-button-move-up"));
    }

    /**
     * Gets the "Move Top" button.
     *
     * @return the button
     */
    public WebElement getMoveTopButton() {
        return findElement(By.className("p-orderlist-button-move-top"));
    }

    /**
     * Gets the "Move Down" button.
     *
     * @return the button
     */
    public WebElement getMoveDownButton() {
        return findElement(By.className("p-orderlist-button-move-down"));
    }

    /**
     * Gets the "Move Bottom" button.
     *
     * @return the button
     */
    public WebElement getMoveBottomButton() {
        return findElement(By.className("p-orderlist-button-move-bottom"));
    }

    /**
     * Moves the selected items up by one with the "Move Up" button. Guards AJAX if a "reorder" behavior exists and the
     * items move (nothing moves if nothing or the first item is selected).
     */
    public void moveUp() {
        clickControl(getMoveUpButton(), isSelectionMovable(true));
    }

    /**
     * Moves the selected items to the top with the "Move Top" button. Guards AJAX if a "reorder" behavior exists and
     * the items move (nothing moves if nothing or the first item is selected).
     */
    public void moveTop() {
        clickControl(getMoveTopButton(), isSelectionMovable(true));
    }

    /**
     * Moves the selected items down by one with the "Move Down" button. Guards AJAX if a "reorder" behavior exists and
     * the items move (nothing moves if nothing or the last item is selected).
     */
    public void moveDown() {
        clickControl(getMoveDownButton(), isSelectionMovable(false));
    }

    /**
     * Moves the selected items to the bottom with the "Move Bottom" button. Guards AJAX if a "reorder" behavior exists
     * and the items move (nothing moves if nothing or the last item is selected).
     */
    public void moveBottom() {
        clickControl(getMoveBottomButton(), isSelectionMovable(false));
    }

    private boolean isSelectionMovable(boolean up) {
        List<String> values = getItemValues();
        List<String> selected = getSelectedValues();
        return !selected.isEmpty() && !selected.contains(up ? values.get(0) : values.get(values.size() - 1));
    }

    private void clickControl(WebElement button, boolean moves) {
        if (moves && isReorderAjaxified()) {
            PrimeSelenium.guardAjax(button).click();
        }
        else {
            button.click();
        }
        // the items are moved with an effect, wait until the effect queues of all items are empty
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.script("return " + getWidgetByIdScript()
                    + ".list.children('.p-orderlist-item').filter(function() { return jQuery(this).queue('fx').length > 0; }).length === 0;"));
    }
}
