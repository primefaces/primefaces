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

import java.io.Serializable;

import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeSelenium;

/**
 * Component wrapper for the PrimeFaces {@code p:inputMask}.
 */
public abstract class InputMask extends InputText {

    @Override
    public void setValue(Serializable value) {
        WebElement input = getInput();
        input.clear();
        setWidgetValue(value.toString());
    }

    /**
     * Client side widget method to set the current value.
     *
     * @param value the value to set the input to
     */
    public void setWidgetValue(Serializable value) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".setValue('" + value + "');");
        boolean isAjaxified = isOnchangeAjaxified();
        if (isAjaxified) {
            PrimeSelenium.executeScript(isAjaxified, getWidgetByIdScript() + ".jq.trigger('change');");
        }
    }

    /**
     * Client side widget method to get the current value.
     *
     * @return the current value
     */
    public String getWidgetValue() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getValue();");
    }

    /**
     * Client side widget method to get the unmasked value.
     *
     * @return the unmasked value
     * @since 9.0
     */
    public String getWidgetValueUnmasked() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getValueUnmasked();");
    }
}
