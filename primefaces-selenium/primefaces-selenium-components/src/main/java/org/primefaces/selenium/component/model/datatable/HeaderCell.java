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
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

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
            return getWebElement().findElement(By.className("ui-column-title"));
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
            return getWebElement().findElement(By.className("ui-column-filter"));
        }

        return null;
    }

    /**
     * Gets the filter match-mode picker trigger icon for this column, if the column defines
     * {@code filterValueType}. Clicking it opens an overlay menu listing the available match modes.
     *
     * @return the WebElement representing the trigger icon button, or {@code null} if not present
     */
    public WebElement getColumnFilterMatchModeIcon() {
        if (getWebElement() != null) {
            try {
                return getWebElement().findElement(By.className("ui-column-filter-mode-icon"));
            }
            catch (NoSuchElementException ex) {
                return null;
            }
        }

        return null;
    }

    /**
     * Gets the currently selected match-mode operator for this column, e.g. "gt" or "equals".
     *
     * @return the current value of the hidden input carrying the selected match mode, or {@code null} if the
     *         column does not define {@code filterValueType}
     */
    public String getColumnFilterMatchModeValue() {
        if (getWebElement() == null) {
            return null;
        }
        try {
            return getWebElement().findElement(By.className("ui-column-filter-mode")).getAttribute("value");
        }
        catch (NoSuchElementException ex) {
            return null;
        }
    }

    /**
     * Opens the filter match-mode overlay menu, collects every option's visible label, then closes it again.
     * {@link WebElement#getText()} returns an empty string for a CSS-hidden element, so the menu must briefly be
     * opened to read its (otherwise correct, always-present) option text.
     *
     * @return the labels of every match mode offered for this column, in declaration order
     */
    public List<String> getFilterMatchModeLabels() {
        WebElement icon = getColumnFilterMatchModeIcon();
        if (icon == null) {
            throw new NoSuchElementException("Column '" + this + "' does not define a filter match-mode picker");
        }

        // opening the menu is a local-only interaction (no AJAX request) - must not be guarded
        icon.click();
        WebElement menu = getFilterMatchModeMenu(icon);
        // scoped to the label span, not the whole link's text - a link's own text would also pick up its
        // symbol glyph (see MatchMode#symbol()); [data-match-mode] excludes the trailing "Clear" action row,
        // which isn't a selectable mode and has no such attribute
        List<String> labels = menu.findElements(By.cssSelector(".ui-menuitem-link[data-match-mode] .ui-column-filter-mode-menuitem-label")).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        // closed via a scripted click, not icon.click(): the menu deliberately has no max-height (see
        // datatable.css#ui-column-filter-mode-menu), so the date/datetime presets' 28-32 items make it taller
        // than the viewport, and jQuery UI's collision:'flipfit' then shifts it back up over its own trigger.
        // A real click would be intercepted by whichever menu item now sits on top of the icon. Dispatching the
        // click on the element itself skips the hit test while still running the widget's own toggle handler.
        PrimeSelenium.executeScript("arguments[0].click();", icon);

        return labels;
    }

    /**
     * Opens the filter match-mode overlay menu, clicks the option matching the given operator, and triggers the
     * filter.
     *
     * @param matchModeOperator the operator value of the match mode option to select, e.g. "gt" or "equals"
     */
    public void setFilterMatchMode(String matchModeOperator) {
        WebElement icon = getColumnFilterMatchModeIcon();
        if (icon == null) {
            throw new NoSuchElementException("Column '" + this + "' does not define a filter match-mode picker");
        }

        // opening the menu is a local-only interaction (no AJAX request) - must not be guarded, or guardAjax()
        // would wait for a request that never arrives
        icon.click();
        WebElement menu = getFilterMatchModeMenu(icon);
        WebElement menuItem = menu.findElement(By.cssSelector("a[data-match-mode='" + matchModeOperator + "']"));

        // selecting the mode is what actually triggers the AJAX filter request
        PrimeSelenium.guardAjax(menuItem).click();
    }

    /**
     * Resolves a filter match-mode trigger icon's overlay menu via its {@code aria-controls} id - the menu is
     * relocated to {@code document.body} client-side (so scrollable/frozen headers don't clip it), so it is no
     * longer a descendant of this header cell and must be looked up from the driver root.
     *
     * @param icon the trigger icon, as returned by {@link #getColumnFilterMatchModeIcon()}
     * @return the WebElement representing the overlay menu ({@code <ul>})
     */
    private WebElement getFilterMatchModeMenu(WebElement icon) {
        return PrimeSelenium.getWebDriver().findElement(By.id(icon.getAttribute("aria-controls")));
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

        if (!columnFilter.isDisplayed()) {
            // the plain value input is intentionally hidden (but still enabled, so it keeps submitting) while a
            // shadow date/date-range DatePicker is the active widget for a "date"/"time"/"datetime" column's
            // current match mode - see DataTableRenderer#encodeDateFilterWidgets() and
            // datatable.widget.js#toggleFilterValueInput(). clear()/sendKeys() would throw
            // ElementNotInteractableException on it, so drive it by value directly instead of through the
            // picker's own calendar UI.
            setHiddenFilterValue(columnFilter, filterValue);
            return;
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
                Thread.sleep(filterDelay * 2L);
            }
            catch (InterruptedException ex) {
                System.err.println("AJAX Guard delay was interrupted!");
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.animationNotActive());
        }
    }

    /**
     * Sets the value of a CSS-hidden (but still enabled) filter input directly and triggers the filter, bypassing
     * the normal keyup-driven path that {@code clear()}/{@code sendKeys()} would otherwise use - fine for a plain
     * {@code display:none} input, but that path is also unavailable to it in the first place.
     *
     * @param columnFilter the (hidden) filter value input
     * @param filterValue the value to filter for, or {@code null} to clear
     */
    private void setHiddenFilterValue(WebElement columnFilter, String filterValue) {
        PrimeSelenium.guardAjax(
                "var input = arguments[0];"
                        + "input.value = arguments[1] || '';"
                        + "var wrapper = input.closest('.ui-datatable, .ui-treetable');"
                        + "var widget = wrapper ? PrimeFaces.getWidgetById(wrapper.id) : null;"
                        + "if (widget && typeof widget.filter === 'function') { widget.filter(); }",
                columnFilter, filterValue);
    }

    @Override
    public String toString() {
        return "HeaderCell{text=" + getColumnTitle().getText() + "}";
    }
}
