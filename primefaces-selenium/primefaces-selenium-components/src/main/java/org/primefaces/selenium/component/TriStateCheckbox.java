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
package org.primefaces.selenium.component;

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:triStateCheckbox}.
 */
public abstract class TriStateCheckbox extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @Override
    public WebElement getInput() {
        return input;
    }

    public WebElement getBox() {
        return findElement(By.className("ui-chkbox-box"));
    }

    @Override
    public void click() {
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(getRoot()));

        if (isOnchangeAjaxified()) {
            PrimeSelenium.guardAjax(getRoot()).click();
        }
        else {
            getRoot().click();
        }
    }

    public void setValue(Boolean value) {
        while (!Objects.equals(getValue(), value)) {
            click();
        }
    }

    public Boolean getValue() {
        String value = input.getDomProperty("value");
        if ("0".equals(value)) {
            return null;
        }
        else if ("1".equals(value)) {
            return Boolean.TRUE;
        }
        else if ("2".equals(value)) {
            return Boolean.FALSE;
        }
        throw new IllegalStateException();
    }

    /**
     * Toggles between its three states. (null, true, false)
     */
    public void toggle() {
        PrimeSelenium.executeScript(isOnchangeAjaxified(), getWidgetByIdScript() + ".toggle();");
    }
}
