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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.findby.FindByParentPartialId;

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

    public void setValue(String value) {
        while (!getValue().equals(value)) {
            click();
        }
    }

    public String getValue() {
        return input.getAttribute("value");
    }

    /**
     * Toggles between its three states. (0, 1, 2)
     */
    public void toggle() {
        PrimeSelenium.executeScript(isOnchangeAjaxified(), getWidgetByIdScript() + ".toggle();");
    }
}
