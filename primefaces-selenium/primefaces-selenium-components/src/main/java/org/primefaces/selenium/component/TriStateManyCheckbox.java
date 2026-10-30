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
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:triStateManyCheckbox}. The state of an item is 0 (unchecked), 1 (checked)
 * or 2 (crossed).
 */
public abstract class TriStateManyCheckbox extends AbstractComponent {

    /**
     * The inputs with the state of each item.
     *
     * @return the inputs
     */
    public List<WebElement> getInputs() {
        return getRoot().findElements(By.cssSelector(".p-chkbox .p-hidden-accessible > input"));
    }

    /**
     * The boxes showing the state of each item.
     *
     * @return the boxes
     */
    public List<WebElement> getBoxes() {
        return getRoot().findElements(By.className("p-chkbox-box"));
    }

    /**
     * The labels of the items.
     *
     * @return the labels
     */
    public List<WebElement> getLabelElements() {
        return getRoot().findElements(By.className("p-chkbox-label"));
    }

    public int getItemsSize() {
        return getInputs().size();
    }

    public String getLabel(int index) {
        return getLabelElements().get(index).getText();
    }

    public List<String> getLabels() {
        return getLabelElements().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /**
     * Gets the state of an item.
     *
     * @param index the index of the item
     * @return the state: 0, 1 or 2
     */
    public int getState(int index) {
        return Integer.parseInt(getInputs().get(index).getDomProperty("value"));
    }

    /**
     * Gets the states of all items.
     *
     * @return the state of each item
     */
    public List<Integer> getStates() {
        return getInputs().stream().map(input -> Integer.parseInt(input.getDomProperty("value"))).collect(Collectors.toList());
    }

    /**
     * Is the component using AJAX "change" event?
     *
     * @return true if using AJAX for change
     */
    public boolean isOnchangeAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "change");
    }

    /**
     * Clicks the box of an item like the user, it moves to the next state (unless it is disabled or read only).
     *
     * @param index the index of the item
     */
    public void toggle(int index) {
        WebElement box = getBoxes().get(index);
        if (isOnchangeAjaxified() && isItemEnabled(index) && !isReadonly()) {
            PrimeSelenium.guardAjax(box).click();
        }
        else {
            box.click();
        }
    }

    /**
     * Clicks the box of an item until it has the state.
     *
     * @param index the index of the item
     * @param state the state: 0, 1 or 2
     */
    public void setState(int index, int state) {
        for (int i = 0; i < 3 && getState(index) != state; i++) {
            toggle(index);
        }
    }

    /**
     * Moves an item to the next or previous state with the widget, like the keyboard.
     *
     * @param index the index of the item
     * @param direction 1 for the next state, -1 for the previous state
     */
    public void toggleWithWidget(int index, int direction) {
        PrimeSelenium.executeScript(isOnchangeAjaxified() && isItemEnabled(index) && !isReadonly(),
                getWidgetByIdScript() + ".toggle(" + index + ", " + direction + ");");
    }

    /**
     * Is the item enabled?
     *
     * @param index the index of the item
     * @return true if the item can be changed (it may still be read only)
     */
    public boolean isItemEnabled(int index) {
        return getInputs().get(index).isEnabled();
    }

    /**
     * Is the component read only?
     *
     * @return true if the states can't be changed
     */
    public boolean isReadonly() {
        return getWidgetConfiguration().optBoolean("readonly", false);
    }

    /**
     * Enables the items with the widget, the ones disabled on the server stay disabled.
     */
    public void enable() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".enable();");
    }

    /**
     * Disables the items with the widget.
     */
    public void disable() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".disable();");
    }
}
