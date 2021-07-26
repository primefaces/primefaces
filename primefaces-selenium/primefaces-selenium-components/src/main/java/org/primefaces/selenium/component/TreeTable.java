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
package org.primefaces.selenium.component;

import org.openqa.selenium.By;
import org.primefaces.selenium.component.base.AbstractTable;
import org.primefaces.selenium.component.model.datatable.Cell;
import org.primefaces.selenium.component.model.treetable.Row;

import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

/**
 * Component wrapper for the PrimeFaces {@code p:treeTable}.
 */
public abstract class TreeTable extends AbstractTable<Row> {

    public List<Row> getRows() {
        return getRowsWebElement().stream().map(rowElt -> {
            List<Cell> cells = rowElt.findElements(By.tagName("td")).stream().map(cellElt -> new Cell(cellElt)).collect(Collectors.toList());
            return new Row(rowElt, cells);
        }).collect(Collectors.toList());
    }

    @Override
    public Row getRow(int index) {
        return getRows().get(index);
    }

    public boolean isEmpty() {
        try {
            findElement(By.className("ui-treetable-empty-message"));
            return true;
        }
        catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

}
