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

import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:scrollTop}.
 */
public abstract class ScrollTop extends AbstractComponent {

    /**
     * Is the button shown, i.e. is the target scrolled beyond the threshold?
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return PrimeSelenium.isElementDisplayed(getRoot());
    }

    /**
     * Gets the vertical scroll position of the target (window or parent element).
     *
     * @return the scroll position in pixels
     */
    public long getTargetScrollTop() {
        Number scrollTop = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".scrollElement.scrollTop();");
        return Math.round(scrollTop.doubleValue());
    }

    /**
     * Waits until the button is shown after the target was scrolled beyond the threshold.
     */
    public void waitUntilVisible() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getRoot()));
    }

    /**
     * Clicks the button and waits until the target is scrolled to the top and the button is hidden.
     */
    public void scrollToTop() {
        getRoot().click();
        PrimeSelenium.waitGui().until(ExpectedConditions.and(
                    driver -> getTargetScrollTop() == 0,
                    PrimeExpectedConditions.invisibleAndAnimationComplete(getRoot())));
    }
}
