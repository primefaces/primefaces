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

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:deferred}.
 */
public abstract class Deferred extends AbstractComponent {

    /**
     * Is the content loaded?
     *
     * @return true if the content was rendered, false while the placeholder is shown
     */
    public boolean isLoaded() {
        return PrimeSelenium.executeScript("return " + getClientComponentScript() + ".isLoaded();");
    }

    /**
     * Loads the content via the client side API and waits until it was loaded.
     */
    public void load() {
        PrimeSelenium.executeScript(getClientComponentScript() + ".load();");
        waitUntilLoaded();
    }

    /**
     * Waits until the content was loaded.
     */
    public void waitUntilLoaded() {
        // the load request replaces the element, it may become stale while it is checked
        PrimeSelenium.waitAjax().ignoring(StaleElementReferenceException.class).until(ExpectedConditions.and(
                    driver -> isLoaded(),
                    PrimeExpectedConditions.animationNotActive(),
                    PrimeExpectedConditions.ajaxQueueEmpty()));
    }

    /**
     * Scrolls this component into view, which loads the content with {@code mode="visible"}, and waits until it was
     * loaded.
     */
    public void scrollIntoViewAndLoad() {
        // via the client side component: the element may have been replaced by an AJAX update
        PrimeSelenium.executeScript(getClientComponentScript() + ".getJQ()[0].scrollIntoView();");
        waitUntilLoaded();
    }
}
