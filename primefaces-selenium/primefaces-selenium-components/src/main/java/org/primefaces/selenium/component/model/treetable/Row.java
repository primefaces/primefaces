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
package org.primefaces.selenium.component.model.treetable;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.model.datatable.Cell;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Row extends org.primefaces.selenium.component.model.datatable.Row {

    public Row(WebElement webElement, List<Cell> cells) {
        super(webElement, cells);
    }

    public boolean isToggleable() {
        return (getToggler() != null);
    }

    public void toggle() {
        if (isToggleable()) {
            PrimeSelenium.guardAjax(getToggler()).click();
        }
    }

    private WebElement getToggler() {
        return getCell(0).getWebElement().findElement(By.className("ui-treetable-toggler"));
    }

    public int getLevel() {
        String cssClasses = getWebElement().getAttribute("class");
        Optional<String> levelClassOpt = Arrays.stream(cssClasses.split(" ")).filter(c -> c.startsWith("ui-node-level")).findFirst();
        if (levelClassOpt.isPresent()) {
            String levelClass = levelClassOpt.get();
            return Integer.parseInt(levelClass.replace("ui-node-level-", ""));
        }
        else {
            return -1;
        }
    }
}
