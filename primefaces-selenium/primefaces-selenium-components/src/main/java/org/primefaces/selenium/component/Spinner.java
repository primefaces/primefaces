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
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.findby.FindByParentPartialId;

/**
 * Component wrapper for the PrimeFaces {@code p:spinner}.
 */
public abstract class Spinner extends InputText {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindBy(css = ".ui-spinner-up")
    private WebElement buttonUp;

    @FindBy(css = ".ui-spinner-down")
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

    @Override
    public void setValue(Serializable value) {
        if (value == null) {
            value = "\"\"";
        }

        PrimeSelenium.executeScript(getWidgetByIdScript() + ".setValue(" + value.toString() + ")");
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
