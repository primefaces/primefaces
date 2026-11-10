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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:columnToggler}. The root element is the overlay panel with the
 * checkboxes of the columns, it is moved to the body by the widget.
 */
public abstract class ColumnToggler extends AbstractComponent {

    private static final By ITEM_CHECKBOX = By.cssSelector(":scope > .p-chkbox > .p-chkbox-box");

    /**
     * Gets the trigger which shows and hides the overlay panel.
     *
     * @return the trigger
     */
    public WebElement getTrigger() {
        return getWebDriver().findElement(By.id(getWidgetConfiguration().getString("trigger")));
    }

    /**
     * Is the overlay panel with the columns shown?
     *
     * @return true if shown
     */
    public boolean isPanelVisible() {
        return PrimeSelenium.isElementDisplayed(getRoot());
    }

    /**
     * Shows the overlay panel by clicking the trigger, if hidden.
     */
    public void show() {
        if (!isPanelVisible()) {
            getTrigger().click();
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getRoot()));
        }
    }

    /**
     * Hides the overlay panel via the widget API, if shown (the close icon is hidden by the theme and the panel may cover
     * the trigger). Guards AJAX if a "close" behavior exists.
     */
    public void hide() {
        if (isPanelVisible()) {
            // widget API: the panel may cover the trigger, e.g. after a column was hidden and the table got narrower
            PrimeSelenium.executeScript(isCloseAjaxified(), getWidgetByIdScript() + ".hide();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(getRoot()));
        }
    }

    /**
     * Gets the items of the columns, without the select all item.
     *
     * @return the items
     */
    public List<WebElement> getItems() {
        return findElements(By.cssSelector(".p-columntoggler-items > .p-columntoggler-item"));
    }

    /**
     * Gets the labels of the columns.
     *
     * @return the labels in column order
     */
    public List<String> getColumnLabels() {
        return getItems().stream().map(item -> item.findElement(By.tagName("label")).getDomProperty("textContent"))
                    .collect(Collectors.toList());
    }

    /**
     * Gets the checkbox of a column.
     *
     * @param label the label of the column
     * @return the checkbox
     * @throws NoSuchElementException if no column has this label
     */
    public WebElement getCheckbox(String label) {
        return getItems().stream()
                    .filter(item -> label.equals(item.findElement(By.tagName("label")).getDomProperty("textContent")))
                    .map(item -> item.findElement(ITEM_CHECKBOX))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Could not find column " + label + " in columntoggler " + getId()));
    }

    /**
     * Is the column checked, i.e. visible in the table?
     *
     * @param label the label of the column
     * @return true if checked
     */
    public boolean isChecked(String label) {
        return Boolean.parseBoolean(getCheckbox(label).getDomAttribute("aria-checked"));
    }

    /**
     * Checks or unchecks a column, which shows or hides it in the table. Shows the overlay panel first, if hidden. Guards
     * AJAX if a "toggle" behavior exists.
     *
     * @param label the label of the column
     */
    public void toggle(String label) {
        show();
        WebElement checkbox = getCheckbox(label);
        if (isToggleAjaxified()) {
            PrimeSelenium.guardAjax(checkbox).click();
        }
        else {
            checkbox.click();
        }
    }

    /**
     * Checks a column, if unchecked.
     *
     * @param label the label of the column
     */
    public void check(String label) {
        if (!isChecked(label)) {
            toggle(label);
        }
    }

    /**
     * Unchecks a column, if checked.
     *
     * @param label the label of the column
     */
    public void uncheck(String label) {
        if (isChecked(label)) {
            toggle(label);
        }
    }

    /**
     * Gets the select all checkbox, only rendered with {@code showSelectAll="true"}.
     *
     * @return the select all checkbox
     */
    public WebElement getSelectAllCheckbox() {
        return findElement(By.cssSelector(".p-columntoggler-all > .p-chkbox > .p-chkbox-box"));
    }

    /**
     * Is the select all checkbox checked, i.e. are all columns visible?
     *
     * @return true if checked
     */
    public boolean isSelectAllChecked() {
        return Boolean.parseBoolean(getSelectAllCheckbox().getDomAttribute("aria-checked"));
    }

    /**
     * Checks all columns, or unchecks all if all are checked, with the select all checkbox. Shows the overlay panel first,
     * if hidden. Guards AJAX if a "toggle" behavior exists.
     */
    public void toggleAll() {
        show();
        WebElement checkbox = getSelectAllCheckbox();
        if (isToggleAjaxified()) {
            PrimeSelenium.guardAjax(checkbox).click();
        }
        else {
            checkbox.click();
        }
    }

    /**
     * Is this component AJAX enabled with "toggle"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isToggleAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "toggle");
    }

    /**
     * Is this component AJAX enabled with "close"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isCloseAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "close");
    }
}
