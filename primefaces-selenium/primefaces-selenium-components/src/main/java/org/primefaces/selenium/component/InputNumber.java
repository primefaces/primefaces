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
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.io.Serializable;

import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:inputNumber}.
 */
public abstract class InputNumber extends InputText {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindByParentPartialId("_hinput")
    private WebElement hiddenInput;

    @Override
    public WebElement getInput() {
        return input;
    }

    public WebElement getHiddenInput() {
        return hiddenInput;
    }

    /**
     * Sets the value with the widget JS method and fires the "change" event (guarded if AJAX). A {@link Number} is passed as JS number, anything else as
     * string, so leading zeros of e.g. {@code "000456"} are kept.
     *
     * @param value the numeric value, {@code null} clears the input
     */
    @Override
    public void setValue(Serializable value) {
        Object newValue = value instanceof Number ? value : (value == null ? "" : value.toString());
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".setValue(arguments[0]);", newValue);
        PrimeSelenium.executeScript(isOnchangeAjaxified(), getWidgetByIdScript() + ".input.trigger('change')");
    }

    /**
     * Gets the value rendered by the server into the widget configuration.
     *
     * @return the rendered value, {@code null} if empty
     */
    public Double getValueToRender() {
        String value = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".cfg.valueToRender;");
        return value == null || value.isEmpty() ? null : Double.valueOf(value);
    }

    /**
     * Gets the value stored using the widget JS method.
     *
     * @return the widget's value
     */
    public String getWidgetValue() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getValue();");
    }
}
