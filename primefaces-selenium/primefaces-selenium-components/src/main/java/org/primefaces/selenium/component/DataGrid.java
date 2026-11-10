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
import org.primefaces.selenium.component.base.AbstractPageableData;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:dataGrid}. Paging is inherited from {@link AbstractPageableData}, e.g.
 * {@link #selectPage(int)}, {@link #selectRowsPerPage(int)} and {@link #getPaginator()}.
 */
public abstract class DataGrid extends AbstractPageableData {

    @FindBy(className = "p-datagrid-content")
    private WebElement content;

    public WebElement getContent() {
        return content;
    }

    /**
     * Gets the cells of the displayed items, one per item. The empty cells the renderer adds to fill up the last row of
     * a page are skipped.
     *
     * @return the item cells
     */
    @Override
    public List<WebElement> getRowsWebElement() {
        return content.findElements(By.cssSelector(".p-datagrid-column:not(:empty)"));
    }

    /**
     * Gets the cell of a displayed item.
     *
     * @param index index of the item on the current page
     * @return the item cell
     */
    public WebElement getRowWebElement(int index) {
        return getRowsWebElement().get(index);
    }

    /**
     * Gets the texts of the displayed items.
     *
     * @return the texts of the item cells
     */
    public List<String> getRowTexts() {
        return getRowsWebElement().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /**
     * Is no item displayed, e.g. the empty message is shown instead?
     *
     * @return true if empty
     */
    public boolean isEmpty() {
        return PrimeSelenium.hasCssClass(content, "p-datagrid-content-empty");
    }

    /**
     * Is this component AJAX enabled with "page"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isPageAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "page");
    }
}
