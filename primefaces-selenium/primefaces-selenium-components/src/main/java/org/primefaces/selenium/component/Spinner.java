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
import java.math.BigDecimal;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:spinner}.
 */
public abstract class Spinner extends InputText {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindBy(css = ".p-spinner-up")
    private WebElement buttonUp;

    @FindBy(css = ".p-spinner-down")
    private WebElement buttonDown;

    @Override
    public WebElement getInput() {
        return input;
    }

    /**
     * Gets the Spinner's Up button.
     *
     * @return the {@link WebElement} representing the up button
     */
    public WebElement getButtonUp() {
        return buttonUp;
    }

    /**
     * Gets the Spinner's Down button.
     *
     * @return the {@link WebElement} representing the down button
     */
    public WebElement getButtonDown() {
        return buttonDown;
    }

    /**
     * Sets the value with the widget JS method (no "change" event is fired, see {@link #change()}). A {@link Number} is passed as JS number, a numeric
     * string like {@code "4.7"} is converted to a JS number, anything else is passed as string.
     *
     * @param value the numeric value, {@code null} clears the input
     */
    @Override
    public void setValue(Serializable value) {
        Object newValue = value;
        if (value == null) {
            newValue = "";
        }
        else if (!(value instanceof Number)) {
            try {
                newValue = new BigDecimal(value.toString().trim()).doubleValue();
            }
            catch (NumberFormatException e) {
                newValue = value.toString();
            }
        }
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".setValue(arguments[0]);", newValue);
    }

    public String getWidgetValue() {
        return PrimeSelenium.executeScript(getWidgetByIdScript() + ".getValue()");
    }

    /**
     * Increments this spinner by one SpinnerCfg.step
     */
    public void increment() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".spin(1);");
    }

    /**
     * Decrements this spinner by one SpinnerCfg.step
     */
    public void decrement() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".spin(-1);");
    }

    /**
     * Fire the change event for the spinner
     */
    public void change() {
        PrimeSelenium.executeScript(isOnchangeAjaxified(), getWidgetByIdScript() + ".input.trigger('change');");
    }
}
