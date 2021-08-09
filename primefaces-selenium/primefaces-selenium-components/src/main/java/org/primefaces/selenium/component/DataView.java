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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractPageableData;

import java.util.List;

/**
 * Component wrapper for the PrimeFaces {@code p:dataView}.
 */
public abstract class DataView extends AbstractPageableData {

    public enum Layout { LIST, GRID };

    @FindBy(className = "ui-dataview")
    private WebElement webElement;

    @FindBy(className = "ui-dataview-header")
    private WebElement header;

    @FindBy(className = "ui-dataview-content")
    private WebElement content;

    @Override
    public List<WebElement> getRowsWebElement() {
        if (getActiveLayout() == Layout.LIST) {
            return content.findElements(By.className("ui-dataview-row"));
        }
        else {
            return content.findElements(By.className("ui-dataview-column"));
        }
    }

    public WebElement getRowWebElement(int index) {
        return getRowsWebElement().get(index);
    }

    public WebElement getLayoutOptionsWebElement() {
        return header.findElement(By.className("ui-dataview-layout-options"));
    }

    public Layout getActiveLayout() {
        List<WebElement> layoutButtons = getLayoutOptionsWebElement().findElements(By.className("ui-button"));
        for (WebElement layoutButton: layoutButtons) {
            WebElement layoutButtonInputHidden = layoutButton.findElement(By.tagName("input"));
            if ("true".equals(layoutButtonInputHidden.getAttribute("checked"))) {
                if ("list".equals(layoutButtonInputHidden.getAttribute("value"))) {
                    return Layout.LIST;
                }
                else {
                    return Layout.GRID;
                }
            }
        }

        return null;
    }

    public void setActiveLayout(Layout layout) {
        List<WebElement> layoutButtons = getLayoutOptionsWebElement().findElements(By.className("ui-button"));
        for (WebElement layoutButton: layoutButtons) {
            WebElement layoutButtonInputHidden = layoutButton.findElement(By.tagName("input"));
            if (layout == Layout.LIST && "list".equals(layoutButtonInputHidden.getAttribute("value"))) {
                PrimeSelenium.guardAjax(layoutButton).click();
            }
            else if (layout == Layout.GRID && "grid".equals(layoutButtonInputHidden.getAttribute("value"))) {
                PrimeSelenium.guardAjax(layoutButton).click();
            }
        }
    }

}
