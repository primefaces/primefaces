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
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:inputOtp}.
 */
public abstract class InputOtp extends AbstractInputComponent {

    @FindByParentPartialId("_hidden")
    private WebElement hiddenInput;

    /**
     * The input of the first character, a label references it.
     *
     * @return the first input
     */
    @Override
    public WebElement getInput() {
        return getInputs().get(0);
    }

    /**
     * The inputs, one per character.
     *
     * @return the inputs
     */
    public List<WebElement> getInputs() {
        return getRoot().findElements(By.cssSelector("input.p-inputotp-input"));
    }

    /**
     * The hidden input with the submitted value.
     *
     * @return the hidden input
     */
    public WebElement getHiddenInput() {
        return hiddenInput;
    }

    /**
     * Is the component using AJAX "change" event?
     *
     * @return true if using AJAX for change
     */
    @Override
    public boolean isOnchangeAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "change");
    }

    /**
     * Gets the value, the characters of all inputs.
     *
     * @return the value
     */
    public String getValue() {
        return hiddenInput.getDomProperty("value");
    }

    /**
     * Types a code like the user, one character per input, and leaves the component (the "change" event).
     *
     * @param value the code
     */
    public void setValue(String value) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".clear();");

        List<WebElement> inputs = getInputs();
        int[] chars = value.codePoints().toArray();
        for (int i = 0; i < chars.length && i < inputs.size(); i++) {
            inputs.get(i).sendKeys(Character.toString(chars[i]));
        }

        // the change event is called when the focus leaves the component
        PrimeSelenium.executeScript(isOnchangeAjaxified(), "document.activeElement.blur();");
    }

    /**
     * Clears all inputs with the widget, no event is called.
     */
    @Override
    public void clear() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".clear();");
    }

    /**
     * Gets the value with the widget JS method.
     *
     * @return the widget's value
     */
    public String getWidgetValue() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getValue();");
    }
}
