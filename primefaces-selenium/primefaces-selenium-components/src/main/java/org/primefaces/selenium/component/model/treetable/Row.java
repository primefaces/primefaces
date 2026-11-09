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
package org.primefaces.selenium.component.model.treetable;

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractTable;
import org.primefaces.selenium.component.model.datatable.Cell;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Row extends org.primefaces.selenium.component.model.datatable.Row {
    public Row(WebElement webElement, List<Cell> cells) {
        super(webElement, cells);
    }

    /**
     * Creates a row which knows its tree table, which is required to guard the AJAX requests of selection and editing.
     *
     * @param webElement the row (TR) element
     * @param cells the cells of the row
     * @param table the tree table this row belongs to
     */
    public Row(WebElement webElement, List<Cell> cells, AbstractTable<?> table) {
        super(webElement, cells, table);
    }

    @Override
    public WebElement getToggler() {
        return getCell(0).getWebElement().findElement(By.className("p-treetable-toggler"));
    }

    public int getLevel() {
        String cssClasses = getWebElement().getDomAttribute("class");
        Optional<String> levelClassOpt = Arrays.stream(cssClasses.split(" ")).filter(c -> c.startsWith("p-node-level")).findFirst();
        if (levelClassOpt.isPresent()) {
            String levelClass = levelClassOpt.get();
            return Integer.parseInt(levelClass.replace("p-node-level-", ""));
        }
        else {
            return -1;
        }
    }

    /**
     * Gets the rowKey of the node rendered by this row.
     *
     * @return the rowKey like "0_1"
     */
    public String getRowKey() {
        return getWebElement().getDomAttribute("data-rk");
    }

    /**
     * Is this row partially selected, i.e. only some of its descendants are selected (checkbox selection)?
     *
     * @return true if partially selected
     */
    public boolean isPartialSelected() {
        return PrimeSelenium.hasCssClass(getWebElement(), "p-treetable-partialselected");
    }

    /**
     * Is the selection checkbox of this row checked (checkbox selection)?
     *
     * @return true if checked, false if unchecked, indeterminate or not available
     */
    public boolean isChecked() {
        List<WebElement> nativeCheckboxes = getCell(0).getWebElement().findElements(By.cssSelector("input[type='checkbox']"));
        if (!nativeCheckboxes.isEmpty()) {
            return nativeCheckboxes.get(0).isSelected();
        }
        return !getWebElement().findElements(By.cssSelector(".p-chkbox-icon.p-checkbox-checked-icon")).isEmpty();
    }

    /**
     * Clicks the selection checkbox of this row (checkbox selection). Checking always fires an AJAX request to resolve
     * the descendants, unchecking only with an unselect behavior.
     */
    public void toggleCheckbox() {
        WebElement checkbox = getSelectionBox();
        if (isSelectionAjaxified(!isSelected())) {
            PrimeSelenium.guardAjax(checkbox).click();
        }
        else {
            checkbox.click();
        }
    }

    /**
     * Gets the selection checkbox of this row (checkbox selection), which is rendered in the first cell. Falls back to the
     * native input with {@code nativeElements="true"}.
     *
     * @return the selection checkbox element
     */
    @Override
    public WebElement getSelectionBox() {
        List<WebElement> boxes = getCell(0).getWebElement().findElements(By.cssSelector("div.p-chkbox-box"));
        if (!boxes.isEmpty()) {
            return boxes.get(0);
        }
        return getCell(0).getWebElement().findElement(By.cssSelector("input[type='checkbox']"));
    }

    /**
     * Clicks the second cell if available, as the first one contains the toggler (and checkbox).
     *
     * @return the element to click
     */
    @Override
    protected WebElement getSelectionTarget() {
        return getCells().size() > 1 ? getCell(1).getWebElement() : getCell(0).getWebElement();
    }

    @Override
    protected boolean isSelectionAjaxified(boolean selecting) {
        if (selecting) {
            return hasTableBehavior("select")
                    || (getTable() != null && "checkbox".equals(getTable().getWidgetConfiguration().optString("selectionMode")));
        }
        return hasTableBehavior("unselect");
    }
}
