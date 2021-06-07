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
package org.primefaces.selenium.component.base;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.findby.FindByParentPartialId;

/**
 * Base class for boolean toggle components.
 */
public abstract class AbstractToggleComponent extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @Override
    public WebElement getInput() {
        return input;
    }

    @Override
    public boolean isSelected() {
        return getValue();
    }

    @Override
    public void click() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getRoot()));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(getRoot()));

        if (isOnChangeAjaxified()) {
            PrimeSelenium.guardAjax(getRoot()).click();
        }
        else {
            getRoot().click();
        }
    }

    /**
     * Is this toggle component AJAX enabled?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isOnChangeAjaxified() {
        return isAjaxified(getInput(), "onchange") || ComponentUtils.hasAjaxBehavior(getRoot(), "change");
    }

    /**
     * Set the value of the the toggle component.
     *
     * @param value true for checked, false for unchecked
     */
    public void setValue(boolean value) {
        if (getValue() != value) {
            click();
        }
    }

    /**
     * Gets the value of the toggle component.
     *
     * @return true for checked, false for unchecked
     */
    public boolean getValue() {
        return getInput().getAttribute("checked") != null;
    }

    /**
     * Turns this switch in case it is off, or turns of off in case it is on.
     */
    public void toggle() {
        PrimeSelenium.executeScript(isOnChangeAjaxified(), getWidgetByIdScript() + ".toggle();");
    }

    /**
     * Turns this switch on if it is not already turned on.
     */
    public void check() {
        PrimeSelenium.executeScript(isOnChangeAjaxified(), getWidgetByIdScript() + ".check();");
    }

    /**
     * Turns this switch off if it is not already turned of.
     */
    public void uncheck() {
        PrimeSelenium.executeScript(isOnChangeAjaxified(), getWidgetByIdScript() + ".uncheck();");
    }

}
