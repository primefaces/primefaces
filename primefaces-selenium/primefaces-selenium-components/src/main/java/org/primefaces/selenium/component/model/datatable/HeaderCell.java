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
package org.primefaces.selenium.component.model.datatable;

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.List;
import java.util.Locale;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HeaderCell extends Cell {

    public HeaderCell(WebElement webElement) {
        super(webElement);
    }

    /**
     * Gets the title element if it exists for this column.
     *
     * @return the WebElement representing the title
     */
    public WebElement getColumnTitle() {
        if (getWebElement() != null) {
            return getWebElement().findElement(By.className("p-column-title"));
        }

        return null;
    }

    /**
     * Gets the filter element if it exists for this column.
     *
     * @return the WebElement representing the filter
     */
    public WebElement getColumnFilter() {
        if (getWebElement() != null) {
            return getWebElement().findElement(By.className("p-column-filter"));
        }

        return null;
    }

    /**
     * Filter using the Widget configuration "filterDelay" and "filterEvent" values.
     *
     * @param cfg the widget configuration JSON object
     * @param filterValue the value to set the filter
     */
    public void setFilterValue(JSONObject cfg, String filterValue) {
        String filterEvent = cfg.has("filterEvent") ? cfg.getString("filterEvent") : "keyup";
        int filterDelay = cfg.has("filterDelay") ? cfg.getInt("filterDelay") : 0;
        setFilterValue(filterValue, filterEvent, filterDelay);
    }

    /**
     * Filter the column using these values.
     *
     * @param filterValue the value to filter for
     * @param filterEvent the event causing the filter to trigger such as "keyup" or "enter"
     * @param filterDelay the delay in milliseconds if a "keyup" filter
     */
    public void setFilterValue(String filterValue, String filterEvent, int filterDelay) {
        WebElement columnFilter;

        try {
            // default-filter
            columnFilter = getColumnFilter();
        }
        catch (NoSuchElementException ex) {
            // for <f:facet name="filter">
            columnFilter = getWebElement().findElement(By.tagName("input"));
        }

        columnFilter.clear();

        Keys triggerKey = null;
        filterEvent = filterEvent.toLowerCase(Locale.ROOT);
        switch (filterEvent) {
            case "keyup":
            case "keydown":
            case "keypress":
            case "input":
                if (filterDelay == 0) {
                    columnFilter = PrimeSelenium.guardAjax(columnFilter);
                }
                break;
            case "enter":
                triggerKey = Keys.ENTER;
                break;
            case "change":
            case "blur":
                triggerKey = Keys.TAB;
                break;
            default:
                break;
        }

        if (filterValue != null) {
            ComponentUtils.sendKeys(columnFilter, filterValue);
        }
        else {
            // null filter press backspace to trigger the re-filtering
            columnFilter.sendKeys(Keys.BACK_SPACE);
        }

        if (triggerKey != null) {
            PrimeSelenium.guardAjax(columnFilter).sendKeys(triggerKey);
        }
        else if (filterDelay > 0) {
            try {
                // default-filter runs delayed - so wait...
                Thread.sleep(filterDelay * 2);
            }
            catch (InterruptedException ex) {
                System.err.println("AJAX Guard delay was interrupted!");
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }

        // the filter request may still be queued or running, also with a slow server
        PrimeSelenium.waitAjax().until(ExpectedConditions.and(PrimeExpectedConditions.ajaxQueueEmpty(), PrimeExpectedConditions.animationNotActive()));
    }

    /**
     * Gets the current sort order of this column as tracked by the widget, which also works for every column of a
     * multiSort (unlike {@code aria-sort}, which is only set on one column).
     *
     * @return the sort order, {@link SortOrder#UNSORTED} if not sorted or not sortable
     */
    public SortOrder getSortOrder() {
        Object order = PrimeSelenium.executeScript("return jQuery(arguments[0]).data('sortorder');", getWebElement());
        int value = order instanceof Number ? ((Number) order).intValue() : 0;
        if (value > 0) {
            return SortOrder.ASCENDING;
        }
        return value < 0 ? SortOrder.DESCENDING : SortOrder.UNSORTED;
    }

    /**
     * Gets the priority of this column in a multiSort, read from the sort badge.
     *
     * @return the priority starting with 1, 0 if the column has no priority
     */
    public int getSortPriority() {
        List<WebElement> badges = getWebElement().findElements(By.className("p-sortable-column-badge"));
        if (badges.isEmpty()) {
            return 0;
        }
        String text = badges.get(0).getText().trim();
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }

    @Override
    public String toString() {
        return "HeaderCell{text=" + getColumnTitle().getText() + "}";
    }
}
