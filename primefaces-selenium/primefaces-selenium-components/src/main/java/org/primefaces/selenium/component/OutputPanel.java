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

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:outputPanel}.
 */
public abstract class OutputPanel extends AbstractComponent {

    /**
     * Is the deferred content not loaded yet?
     *
     * @return true while the loading indicator is shown
     */
    public boolean isLoading() {
        return !findElements(By.className("p-outputpanel-loading")).isEmpty();
    }

    /**
     * Waits until the deferred content was loaded.
     */
    public void waitUntilLoaded() {
        PrimeSelenium.waitAjax().until(ExpectedConditions.and(
                    driver -> !isLoading(),
                    PrimeExpectedConditions.animationNotActive(),
                    PrimeExpectedConditions.ajaxQueueEmpty()));
    }

    /**
     * Scrolls this panel into view, which loads the content with {@code deferredMode="visible"}, and waits until it
     * was loaded.
     */
    public void scrollIntoViewAndLoad() {
        PrimeSelenium.executeScript("arguments[0].scrollIntoView();", getRoot());
        waitUntilLoaded();
    }
}
