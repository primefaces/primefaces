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

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.base.ComponentUtils;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:progressBar}.
 */
public abstract class ProgressBar extends AbstractComponent {

    @FindBy(className = "p-progressbar-label")
    private WebElement label;

    /**
     * Gets the label, only filled with a {@code labelTemplate}.
     *
     * @return the label element
     */
    public WebElement getLabel() {
        return label;
    }

    /**
     * Gets the current value from {@code aria-valuenow}.
     *
     * @return the value between 0 and 100, or null if none was set
     */
    public Integer getValue() {
        String value = getDomAttribute("aria-valuenow");
        return value == null || value.isEmpty() ? null : Integer.valueOf(value);
    }

    /**
     * Sets the value on the client, values outside of 0 to 100 are ignored.
     *
     * @param value the new value
     */
    public void setValue(int value) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".setValue(" + value + ");");
    }

    /**
     * Is the value polled from the server?
     *
     * @return true in AJAX mode
     */
    public boolean isAjax() {
        return getWidgetConfiguration().optBoolean("ajax", false);
    }

    /**
     * Is this component AJAX enabled with "start"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isStartAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "start");
    }

    /**
     * Starts polling the value in AJAX mode.
     */
    public void start() {
        PrimeSelenium.executeScript(isStartAjaxified(), getWidgetByIdScript() + ".start();");
    }

    /**
     * Stops polling and resets the value to 0.
     */
    public void cancel() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".cancel();");
    }

    /**
     * Waits until the polling reached 100 and the "complete" behavior has finished.
     */
    public void waitUntilComplete() {
        PrimeSelenium.waitAjax().until(ExpectedConditions.and(
                    driver -> Integer.valueOf(100).equals(getValue()),
                    PrimeExpectedConditions.animationNotActive(),
                    PrimeExpectedConditions.ajaxQueueEmpty()));
    }
}
