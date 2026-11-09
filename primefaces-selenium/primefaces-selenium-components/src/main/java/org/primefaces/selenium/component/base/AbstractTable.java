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
package org.primefaces.selenium.component.base;

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.model.datatable.Body;
import org.primefaces.selenium.component.model.datatable.Cell;
import org.primefaces.selenium.component.model.datatable.Header;
import org.primefaces.selenium.component.model.datatable.HeaderCell;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

public abstract class AbstractTable<T extends Row> extends AbstractPageableData {

    @Override
    public List<WebElement> getRowsWebElement() {
        return findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
    }

    public abstract T getRow(int index);

    /**
     * Gets the Cell at the row/column coordinates.
     *
     * @param rowIndex the index of the row
     * @param colIndex the index of the column in the row
     * @return the {@link Cell} representing these coordinates
     * @throws IndexOutOfBoundsException if either row or column not found
     */
    public Cell getCell(int rowIndex, int colIndex) throws IndexOutOfBoundsException {
        Row row = getRow(rowIndex);
        if (row == null) {
            throw new IndexOutOfBoundsException("Row " + rowIndex + " was not found in table");
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            throw new IndexOutOfBoundsException("Column " + colIndex + " was not found in Row " + rowIndex + ".");
        }
        return cell;
    }

    public WebElement getHeaderWebElement() {
        return findElement(By.tagName("table")).findElement(By.tagName("thead"));
    }

    public Header getHeader() {
        List<HeaderCell> cells = getHeaderWebElement().findElements(By.tagName("th")).stream()
                .map(HeaderCell::new)
                .collect(Collectors.toList());
        return new Header(getHeaderWebElement(), cells);
    }

    /**
     * Sorts the column found by its header text. It toggles to the next sort direction.
     *
     * @param headerText the header text to look for
     */
    public void sort(String headerText) {
        sort(headerText, false);
    }

    /**
     * Sorts the column found by its header text. It toggles to the next sort direction.
     *
     * @param headerText the header text to look for
     * @param additive true to hold Cmd (macOS) / Ctrl, which adds the column to the current sorting (multiSort)
     */
    public void sort(String headerText, boolean additive) {
        Optional<HeaderCell> cell = getHeader().getCell(headerText);
        if (cell.isPresent()) {
            sort(cell.get(), additive);
        }
        else {
            System.err.println("Header Cell '" + headerText + "' not found.");
        }
    }

    /**
     * Sorts the column found by its index. It toggles to the next sort direction.
     *
     * @param index the index of the column
     */
    public void sort(int index) {
        sort(index, false);
    }

    /**
     * Sorts the column found by its index. It toggles to the next sort direction.
     *
     * @param index the index of the column
     * @param additive true to hold Cmd (macOS) / Ctrl, which adds the column to the current sorting (multiSort)
     */
    public void sort(int index, boolean additive) {
        HeaderCell cell = getHeader().getCell(index);
        if (cell != null) {
            sort(cell, additive);
        }
        else {
            System.err.println("Header Cell '" + index + "' not found.");
        }
    }

    private void sort(HeaderCell cell, boolean additive) {
        WebElement sortIcon = cell.getWebElement().findElement(By.className("p-sortable-column-icon"));
        if (additive) {
            Keys metaKey = PrimeSelenium.isMacOs() ? Keys.COMMAND : Keys.CONTROL;
            Action action = new Actions(getWebDriver()).keyDown(metaKey).click(sortIcon).keyUp(metaKey).build();
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            PrimeSelenium.guardAjax(sortIcon).click();
        }
    }

    /**
     * Gets the global filter input, which is expected to have the id {@code <tableId>:globalFilter}.
     *
     * @return the global filter input
     */
    public WebElement getGlobalFilterWebElement() {
        return findElement(By.id(getId() + ":globalFilter"));
    }

    /**
     * Filters globally: sets the value of the global filter input ({@code <tableId>:globalFilter}) and applies all
     * filters. The value is set without key events, so the page's own wiring of the global filter (onkeyup, onchange, ...)
     * doesn't fire additional requests.
     *
     * @param value the global filter value, {@code null} or empty to remove the global filter
     */
    public void filterGlobal(String value) {
        filterGlobal(getGlobalFilterWebElement(), value);
    }

    /**
     * Filters globally using a custom global filter input, see {@link #filterGlobal(String)}.
     *
     * @param globalFilterInput the global filter input
     * @param value the global filter value, {@code null} or empty to remove the global filter
     */
    public void filterGlobal(WebElement globalFilterInput, String value) {
        PrimeSelenium.executeScript("arguments[0].value = arguments[1];", globalFilterInput, value == null ? "" : value);
        applyFilters();
    }

    /**
     * Filters the column found by its header text like {@link #filter(String, String)}, but sets the value at once instead
     * of typing it and applies the filters with exactly one request. Typing a value on a slow browser can fire a filter
     * request for partial input, which matters e.g. when asserting the requests of a lazy model.
     *
     * @param headerText the header text to look for
     * @param filterValue the value to filter for, {@code null} or empty to remove the filter
     */
    public void filterAtOnce(String headerText, String filterValue) {
        HeaderCell cell = getHeader().getCell(headerText)
                .orElseThrow(() -> new NoSuchElementException("Header Cell '" + headerText + "' not found."));
        PrimeSelenium.executeScript("arguments[0].value = arguments[1];", cell.getColumnFilter(), filterValue == null ? "" : filterValue);
        applyFilters();
    }

    /**
     * Applies the current values of all filters (column and global filters), which always fires an AJAX request.
     */
    public void applyFilters() {
        PrimeSelenium.executeScript(true, getWidgetByIdScript() + ".filter();");
    }

    /**
     * Clears all filters (column and global filters) and re-filters, which always fires an AJAX request.
     */
    public void clearFilters() {
        PrimeSelenium.executeScript(true, getWidgetByIdScript() + ".clearFilters();");
    }

    /**
     * Filter the column by its index.
     *
     * @param cellIndex the index of the column
     * @param filterValue the value to pass to the filter
     */
    public void filter(int cellIndex, String filterValue) {
        filter(getHeader().getCell(cellIndex), filterValue);
    }

    /**
     * Filter the column by its header text.
     *
     * @param headerText the header text to look for
     * @param filterValue the value to pass to the filter
     */
    public void filter(String headerText, String filterValue) {
        Optional<HeaderCell> cell = getHeader().getCell(headerText);
        if (cell.isPresent()) {
            filter(cell.get(), filterValue);
        }
        else {
            System.err.println("Header Cell '" + headerText + "' not found.");
        }
    }

    /**
     * Removes the current filter at this column index.
     *
     * @param cellIndex the index of the column
     */
    public void removeFilter(int cellIndex) {
        filter(cellIndex, null);
    }

    /**
     * Removes the current filter for a column with the header text
     *
     * @param headerText the header text to look for
     */
    public void removeFilter(String headerText) {
        filter(headerText, null);
    }

    /**
     * Filter using the widget configuration for "filterDelay" and "filterEvent".
     *
     * @param cell the cell to filter
     * @param filterValue the value to pass to the filter.
     */
    private void filter(HeaderCell cell, String filterValue) {
        JSONObject cfg = getWidgetConfiguration();
        if (filterValue != null && filterValue.isEmpty()) {
            filterValue = null;
        }
        cell.setFilterValue(cfg, filterValue);
    }

    /**
     * If using multiple checkbox mode this toggles the Select All checkbox in the header.
     */
    public void toggleSelectAllCheckBox() {
        WebElement checkboxAll = getSelectAllCheckBox();
        if (ComponentUtils.hasBehavior(this, "rowSelect") || ComponentUtils.hasBehavior(this, "rowUnselect")) {
            PrimeSelenium.guardAjax(checkboxAll).click();
        }
        else {
            checkboxAll.click();
        }
    }

    /**
     * Gets the Select All checkbox in the header of the table.
     *
     * @return the WebElement representing the checkbox
     */
    public WebElement getSelectAllCheckBox() {
        return getHeader().getCell(0).getWebElement();
    }

    /**
     * If using multiple checkbox mode: is the Select All checkbox in the header checked? Reads {@code aria-checked}.
     *
     * @return true if checked
     */
    public boolean isSelectAllChecked() {
        WebElement header = getSelectAllCheckBox();
        List<WebElement> boxes = header.findElements(By.cssSelector(".p-chkbox-box, input[type='checkbox']"));
        if (boxes.isEmpty()) {
            return false;
        }
        WebElement box = boxes.get(0);
        String ariaChecked = box.getDomAttribute("aria-checked");
        return ariaChecked != null ? Boolean.parseBoolean(ariaChecked) : box.isSelected();
    }

    public WebElement getFrozenHeaderWebElement() {
        return findElement(By.className("p-datatable-frozenlayout-left")).findElement(By.tagName("thead"));
    }

    public Header getFrozenHeader() {
        List<HeaderCell> frozenHeaderCells = getFrozenHeaderWebElement().findElements(By.tagName("th")).stream()
                .map(HeaderCell::new)
                .collect(Collectors.toList());

        return new Header(getFrozenHeaderWebElement(), frozenHeaderCells);
    }

    public WebElement getScrollableHeaderWebElement() {
        return findElement(By.className("p-datatable-frozenlayout-right")).findElement(By.tagName("thead"));
    }

    public Header getScrollableHeader() {
        List<HeaderCell> scrollableHeaderCells = getScrollableHeaderWebElement().findElements(By.tagName("th")).stream()
                .map(HeaderCell::new)
                .collect(Collectors.toList());

        return new Header(getScrollableHeaderWebElement(), scrollableHeaderCells);
    }

    public WebElement getFrozenBodyWebElement() {
        return findElement(By.className("p-datatable-frozenlayout-left")).findElement(By.tagName("tbody"));
    }

    public WebElement getScrollableBodyWebElement() {
        return findElement(By.className("p-datatable-frozenlayout-right")).findElement(By.tagName("tbody"));
    }

    public Body getFrozenBody() {
        List<Row> rows = new ArrayList<>();
        List<WebElement> frozenBodyRows = getFrozenBodyWebElement().findElements(By.tagName("tr"));

        frozenBodyRows.forEach(row -> {
            List<WebElement> rowCellsTmp = row.findElements(By.cssSelector("td, th[role='rowheader']"));
            List<Cell> rowCells = rowCellsTmp.stream()
                    .map(cellElt -> new Cell(cellElt, this))
                    .collect(Collectors.toList());
            rows.add(new Row(row, rowCells, this));
        });

        return new Body(getFrozenBodyWebElement(), rows);
    }

    public Body getScrollableBody() {
        List<Row> rows = new ArrayList<>();
        List<WebElement> scrollableBodyRows = getScrollableBodyWebElement().findElements(By.tagName("tr"));

        scrollableBodyRows.forEach(row -> {
            List<WebElement> rowCellsTmp = row.findElements(By.cssSelector("td, th[role='rowheader']"));
            List<Cell> rowCells = rowCellsTmp.stream()
                    .map(cellElt -> new Cell(cellElt, this))
                    .collect(Collectors.toList());
            rows.add(new Row(row, rowCells, this));
        });

        return new Body(getScrollableBodyWebElement(), rows);
    }
}
