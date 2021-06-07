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
package org.primefaces.selenium.component.model.datatable;

import java.util.List;
import java.util.Optional;

import org.openqa.selenium.WebElement;

public class Header {

    private WebElement webElement;
    private List<HeaderCell> cells;

    public Header(WebElement webElement, List<HeaderCell> cells) {
        this.webElement = webElement;
        this.cells = cells;
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
    }

    public List<HeaderCell> getCells() {
        return cells;
    }

    public void setCells(List<HeaderCell> cells) {
        this.cells = cells;
    }

    public HeaderCell getCell(int index) {
        return getCells().get(index);
    }

    public Optional<HeaderCell> getCell(String headerText) {
        return getCells().stream()
                    .filter(cell -> headerText.equals(cell.getColumnTitle().getText()))
                    .findFirst();
    }
}
