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
import org.primefaces.selenium.component.base.ComponentUtils;

import java.io.Serializable;

import org.openqa.selenium.Keys;

/**
 * Component wrapper for the PrimeFaces {@code p:inputText}.
 */
public abstract class InputText extends AbstractInputComponent {

    public String getValue() {
        return getInput().getDomProperty("value");
    }

    public void setValue(Serializable value) {
        boolean ajaxified = isOnchangeAjaxified();
        String oldValue = getValue();
        if (oldValue != null && oldValue.length() > 0) {
            if (ajaxified) {
                PrimeSelenium.guardAjax(getInput()).clear();
            }
            else {
                getInput().clear();
            }
        }

        ComponentUtils.sendKeys(getInput(), value.toString());

        if (ajaxified) {
            PrimeSelenium.guardAjax(getInput()).sendKeys(Keys.TAB);
        }
        else {
            getInput().sendKeys(Keys.TAB);
        }
    }
}
