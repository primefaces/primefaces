/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.model.datatable.Cell;
import org.primefaces.selenium.component.model.datatable.Header;
import org.primefaces.selenium.component.model.datatable.HeaderCell;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractTable<T extends Row> extends AbstractPageableData {

    @Override
    public List<WebElement> getRowsWebElement() {
        return findElement(By.tagName("table")).findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
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
        Optional<HeaderCell> cell = getHeader().getCell(headerText);
        if (cell.isPresent()) {
            PrimeSelenium.guardAjax(cell.get().getWebElement().findElement(By.className("ui-sortable-column-icon"))).click();
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
        HeaderCell cell = getHeader().getCell(index);
        if (cell != null) {
            PrimeSelenium.guardAjax(cell.getWebElement().findElement(By.className("ui-sortable-column-icon"))).click();
        }
        else {
            System.err.println("Header Cell '" + index + "' not found.");
        }
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
}
