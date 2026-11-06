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

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractTable;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

public class Row {
    private WebElement webElement;
    private List<Cell> cells;
    private AbstractTable<?> table;

    public Row(WebElement webElement, List<Cell> cells) {
        this(webElement, cells, null);
    }

    /**
     * Creates a row which knows its table, which is required to guard the AJAX requests of selection and editing.
     *
     * @param webElement the row (TR) element
     * @param cells the cells of the row
     * @param table the table this row belongs to
     */
    public Row(WebElement webElement, List<Cell> cells, AbstractTable<?> table) {
        this.webElement = webElement;
        this.cells = cells;
        this.table = table;
    }

    /**
     * Gets the table this row belongs to.
     *
     * @return the table or {@code null} if the row was created without it
     */
    public AbstractTable<?> getTable() {
        return table;
    }

    /**
     * Is this row selected? Reads {@code aria-selected} and falls back to the highlight class.
     *
     * @return true if selected
     */
    public boolean isSelected() {
        String ariaSelected = webElement.getDomAttribute("aria-selected");
        if (ariaSelected != null) {
            return Boolean.parseBoolean(ariaSelected);
        }
        return PrimeSelenium.hasCssClass(webElement, "p-state-highlight");
    }

    /**
     * Selects this row with a plain click, which replaces the current selection (unless {@code selectionRowMode="add"}).
     * Guards AJAX if a select behavior exists.
     */
    public void select() {
        clickForSelection(null, true);
    }

    /**
     * Adds this row to or removes it from the selection with a Cmd (macOS) / Ctrl click.
     * Guards AJAX if a select/unselect behavior exists.
     */
    public void toggleSelection() {
        clickForSelection(PrimeSelenium.isMacOs() ? Keys.COMMAND : Keys.CONTROL, !isSelected());
    }

    /**
     * Selects all rows from the last selected row up to this row with a Shift click (multiple selection).
     * Guards AJAX if a select behavior exists.
     */
    public void selectRange() {
        clickForSelection(Keys.SHIFT, true);
    }

    /**
     * Gets the element which is clicked to select this row.
     *
     * @return the element to click
     */
    protected WebElement getSelectionTarget() {
        return getCell(0).getWebElement();
    }

    /**
     * Does selecting or unselecting this row fire an AJAX request?
     *
     * @param selecting true if the row gets selected, false if it gets unselected
     * @return true if a request is expected
     */
    protected boolean isSelectionAjaxified(boolean selecting) {
        return hasTableBehavior(selecting ? "rowSelect" : "rowUnselect");
    }

    private void clickForSelection(Keys modifier, boolean selecting) {
        WebElement target = getSelectionTarget();
        boolean ajaxified = isSelectionAjaxified(selecting);
        if (modifier == null) {
            if (ajaxified) {
                PrimeSelenium.guardAjax(target).click();
            }
            else {
                target.click();
            }
            return;
        }

        Action action = new Actions(PrimeSelenium.getWebDriver()).keyDown(modifier).click(target).keyUp(modifier).build();
        if (ajaxified) {
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            action.perform();
        }
    }

    /**
     * Does the table of this row have the given AJAX behavior?
     *
     * @param behavior the behavior event like "rowSelect"
     * @return true if the table is known and has the behavior
     */
    protected boolean hasTableBehavior(String behavior) {
        return table != null && ComponentUtils.hasBehavior(table, behavior);
    }

    /**
     * Is this row in edit mode (row editing)?
     *
     * @return true if editing
     */
    public boolean isEditing() {
        return PrimeSelenium.hasCssClass(webElement, "p-row-editing");
    }

    /**
     * Switches this row to edit mode by clicking the row editor's pencil. Guards AJAX if a rowEditInit behavior exists or
     * {@code rowEditMode="lazy"} is used.
     */
    public void startEdit() {
        WebElement pencil = getRowEditorPencil();
        boolean ajaxified = hasTableBehavior("rowEditInit")
                || (table != null && "lazy".equals(table.getWidgetConfiguration().optString("rowEditMode")));
        if (ajaxified) {
            PrimeSelenium.guardAjax(pencil).click();
        }
        else {
            pencil.click();
        }
    }

    /**
     * Saves the edited row by clicking the row editor's check, which always fires an AJAX request.
     */
    public void saveEdit() {
        PrimeSelenium.guardAjax(getRowEditorSave()).click();
    }

    /**
     * Cancels editing the row by clicking the row editor's close, which always fires an AJAX request.
     */
    public void cancelEdit() {
        PrimeSelenium.guardAjax(getRowEditorCancel()).click();
    }

    /**
     * Gets the row editor's pencil, which switches the row to edit mode (shown while not editing).
     *
     * @return the pencil element
     */
    public WebElement getRowEditorPencil() {
        return webElement.findElement(By.className("p-row-editor-pencil"));
    }

    /**
     * Gets the row editor's check, which saves the edited row (shown while editing).
     *
     * @return the check element
     */
    public WebElement getRowEditorSave() {
        return webElement.findElement(By.className("p-row-editor-check"));
    }

    /**
     * Gets the row editor's close, which cancels editing the row (shown while editing).
     *
     * @return the close element
     */
    public WebElement getRowEditorCancel() {
        return webElement.findElement(By.className("p-row-editor-close"));
    }

    /**
     * Gets the checkbox (multiple) or radio button (single) of the selection column ({@code selectionBox="true"} or
     * {@code selectionMode} on the column), e.g. to read its {@code aria-checked} or disabled state. Falls back to the
     * native input with {@code nativeElements="true"}.
     *
     * @return the selection box element
     */
    public WebElement getSelectionBox() {
        List<WebElement> boxes = webElement.findElements(By.cssSelector(".p-selection-column .p-chkbox-box, .p-selection-column .p-radiobutton-box"));
        if (!boxes.isEmpty()) {
            return boxes.get(0);
        }
        return webElement.findElement(By.cssSelector(".p-selection-column input[type='checkbox'], .p-selection-column input[type='radio']"));
    }

    /**
     * Gets the texts of all cells of this row.
     *
     * @return the cell texts in column order
     */
    public List<String> getCellTexts() {
        return cells.stream().map(Cell::getText).collect(Collectors.toList());
    }

    public boolean isToggleable() {
        return getToggler() != null;
    }

    public WebElement getToggler() {
        try {
            return webElement.findElement(By.className("p-row-toggler"));
        }
        catch (NoSuchElementException e) {
            return null; // Ignore, its optional
        }
    }

    public void toggle() {
        if (isToggleable()) {
            PrimeSelenium.guardAjax(getToggler()).click();
        }
    }

    public boolean isExpanded() {
        return Boolean.parseBoolean(getToggler().getDomAttribute("aria-expanded"));
    }

    public void expand() {
        if (!isExpanded()) {
            toggle();
        }
    }

    public void collapse() {
        if (isExpanded()) {
            toggle();
        }
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void setCells(List<Cell> cells) {
        this.cells = cells;
    }

    public Cell getCell(int index) {
        return cells.get(index);
    }
}
