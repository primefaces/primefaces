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
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:multiSelectListbox}.
 */
public abstract class MultiSelectListbox extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @Override
    public WebElement getInput() {
        return input;
    }

    /**
     * Gets the selected value.
     *
     * @return the value of the selected item, empty if none is selected
     */
    public String getValue() {
        return input.getDomProperty("value");
    }

    /**
     * Gets the shown lists, the root list and one list per selected group.
     *
     * @return the lists
     */
    public List<WebElement> getLists() {
        return findElements(By.cssSelector(":scope > .p-multiselectlistbox-listcontainer"));
    }

    /**
     * Gets the labels of the highlighted items, from the root list to the selected item.
     *
     * @return the labels
     */
    public List<String> getSelectedPath() {
        return getLists().stream()
                    .flatMap(list -> list.findElements(By.cssSelector(":scope > ul > li.p-state-highlight > span")).stream())
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
    }

    /**
     * Is this component AJAX enabled with "change"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isChangeAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "change");
    }

    /**
     * Selects the items along the given labels, one per list, e.g. a group, a sub group and an item.
     *
     * @param labels the labels from the root list to the item to select
     */
    public void select(String... labels) {
        for (int i = 0; i < labels.length; i++) {
            List<WebElement> lists = getLists();
            if (i >= lists.size()) {
                throw new NoSuchElementException("No list for label: " + labels[i]);
            }
            WebElement item = findItem(lists.get(i), labels[i]);
            if (PrimeSelenium.hasCssClass(item, "p-state-highlight")) {
                // the widget ignores clicks on selected items
                continue;
            }
            boolean leaf = item.findElements(By.tagName("ul")).isEmpty();
            if (leaf && isChangeAjaxified()) {
                PrimeSelenium.guardAjax(item).click();
            }
            else {
                item.click();
                PrimeSelenium.waitGui().until(PrimeExpectedConditions.animationNotActive());
            }
        }
    }

    private WebElement findItem(WebElement list, String label) {
        return list.findElements(By.cssSelector(":scope > ul > li.p-multiselectlistbox-item")).stream()
                    .filter(item -> label.equals(item.findElement(By.cssSelector(":scope > span")).getText()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Item not found: " + label));
    }
}
