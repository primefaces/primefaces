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
import org.primefaces.selenium.component.base.AbstractTable;
import org.primefaces.selenium.component.base.ComponentUtils;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class Cell {

    private WebElement webElement;
    private AbstractTable<?> table;

    public Cell(WebElement webElement) {
        this(webElement, null);
    }

    /**
     * Creates a cell which knows its table, which is required to guard the AJAX requests of cell editing.
     *
     * @param webElement the cell (TD) element
     * @param table the table this cell belongs to
     */
    public Cell(WebElement webElement, AbstractTable<?> table) {
        this.webElement = webElement;
        this.table = table;
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    /**
     * Gets the table this cell belongs to.
     *
     * @return the table or {@code null} if the cell was created without it
     */
    public AbstractTable<?> getTable() {
        return table;
    }

    public String getText() {
        if (webElement != null) {
            return webElement.getText();
        }
        return null;
    }

    /**
     * Is this cell in edit mode (cell editing)?
     *
     * @return true if editing
     */
    public boolean isEditing() {
        return PrimeSelenium.hasCssClass(webElement, "p-cell-editing");
    }

    /**
     * Gets the container of the cell editor's input facet, e.g. to wrap a non-text editor in its component wrapper.
     *
     * @return the input facet container
     */
    public WebElement getEditor() {
        return webElement.findElement(By.cssSelector(".p-cell-editor-input"));
    }

    /**
     * Gets the first input of the cell editor's input facet.
     *
     * @return the editor input
     */
    public WebElement getEditorInput() {
        return getEditor().findElement(By.cssSelector("input:not([type='hidden']), select, textarea"));
    }

    /**
     * Switches this cell to edit mode (cell editing) with a click, or a double click if {@code editInitEvent="dblclick"},
     * and waits until the editor is shown. Guards AJAX if {@code cellEditMode="lazy"} or a cellEditInit behavior exists.
     */
    public void startEdit() {
        boolean ajaxified = false;
        boolean dblclick = false;
        if (table != null) {
            JSONObject cfg = table.getWidgetConfiguration();
            ajaxified = "lazy".equals(cfg.optString("cellEditMode")) || ComponentUtils.hasBehavior(table, "cellEditInit");
            dblclick = "dblclick".equals(cfg.optString("editInitEvent"));
        }

        if (dblclick) {
            Action action = new Actions(PrimeSelenium.getWebDriver()).doubleClick(webElement).build();
            if (ajaxified) {
                PrimeSelenium.guardAjax(action).perform();
            }
            else {
                action.perform();
            }
        }
        else if (ajaxified) {
            PrimeSelenium.guardAjax(webElement).click();
        }
        else {
            webElement.click();
        }

        PrimeSelenium.waitGui().until(driver -> isEditing() && getEditor().isDisplayed());
    }

    /**
     * Saves the edited cell by pressing ENTER in the editor input. The widget only fires a request if a value was changed,
     * so instead of guarding a request this waits until no request is pending and no animation is running.
     */
    public void saveEdit() {
        getEditorInput().sendKeys(Keys.ENTER);
        PrimeSelenium.waitAjax().until(ExpectedConditions.and(
                PrimeExpectedConditions.ajaxQueueEmpty(), PrimeExpectedConditions.animationNotActive()));
    }

    /**
     * Cancels editing the cell by pressing ESC in the editor input, which always fires an AJAX request.
     */
    public void cancelEdit() {
        PrimeSelenium.guardAjax(getEditorInput()).sendKeys(Keys.ESCAPE);
    }
}
