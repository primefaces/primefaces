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
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:selectOneListbox}.
 */
public abstract class SelectOneListbox extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindByParentPartialId("_filter")
    private WebElement filterInput;

    @Override
    public WebElement getInput() {
        return input;
    }

    public WebElement getFilterInput() {
        return filterInput;
    }

    /**
     * Gets the items.
     *
     * @return the items
     */
    public List<WebElement> getItems() {
        return findElements(By.cssSelector(".p-selectlistbox-list .p-selectlistbox-item"));
    }

    /**
     * Gets the labels of the visible items.
     *
     * @return the labels
     */
    public List<String> getVisibleLabels() {
        return getItems().stream().filter(WebElement::isDisplayed).map(WebElement::getText).collect(Collectors.toList());
    }

    /**
     * Gets the label of the selected item.
     *
     * @return the label, {@code null} if no item is selected
     */
    public String getSelectedLabel() {
        return getItems().stream()
                    .filter(item -> "true".equals(item.getDomAttribute("aria-selected")))
                    .map(WebElement::getText)
                    .findFirst()
                    .orElse(null);
    }

    /**
     * Is the input using AJAX "change" or "itemSelect" event?
     *
     * @return true if a selection sends a request
     */
    public boolean isSelectAjaxified() {
        return isOnchangeAjaxified() || ComponentUtils.hasAjaxBehavior(getRoot(), "itemSelect");
    }

    /**
     * Selects the item with the given label by clicking it.
     *
     * @param label the label of the item
     */
    public void select(String label) {
        WebElement item = getItems().stream()
                    .filter(it -> label.equals(it.getText()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Item not found: " + label));
        if (label.equals(getSelectedLabel())) {
            return;
        }
        if (isSelectAjaxified()) {
            PrimeSelenium.guardAjax(item).click();
        }
        else {
            item.click();
        }
    }

    /**
     * Filters the items, only with {@code filter="true"}.
     *
     * @param text the filter text
     */
    public void filter(String text) {
        // clears with keys, the filter runs on keyup
        PrimeSelenium.clearInput(filterInput, false);
        ComponentUtils.sendKeys(filterInput, text);
    }
}
