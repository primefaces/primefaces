/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class Row {
    private WebElement webElement;
    private List<Cell> cells;

    public Row(WebElement webElement, List<Cell> cells) {
        this.webElement = webElement;
        this.cells = cells;
    }

    public boolean isToggleable() {
        return getToggler() != null;
    }

    public WebElement getToggler() {
        try {
            return webElement.findElement(By.className("ui-row-toggler"));
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
