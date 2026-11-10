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
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:notificationBar}. The bar is moved to the body by the widget.
 */
public abstract class NotificationBar extends AbstractComponent {

    /**
     * Is the bar shown?
     *
     * @return true if shown
     */
    public boolean isVisible() {
        return PrimeSelenium.isElementDisplayed(getRoot());
    }

    /**
     * Gets the position of the bar.
     *
     * @return "top" or "bottom"
     */
    public String getPosition() {
        return getWidgetConfiguration().optString("position", "top");
    }

    /**
     * Waits until the bar is shown, e.g. after a click on a button which shows it.
     */
    public void waitUntilVisible() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getRoot()));
    }

    /**
     * Waits until the bar is hidden, e.g. after a click on a button which hides it.
     */
    public void waitUntilHidden() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(getRoot()));
    }

    /**
     * Shows the bar via the widget API, if hidden.
     */
    public void show() {
        if (!isVisible()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
            waitUntilVisible();
        }
    }

    /**
     * Hides the bar via the widget API, if shown.
     */
    public void hide() {
        if (isVisible()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
            waitUntilHidden();
        }
    }

    /**
     * Gets the close element, the child of the bar with the style class {@code p-notificationbar-close}.
     *
     * @return the close element
     */
    public WebElement getCloseElement() {
        return findElement(By.cssSelector(":scope > .p-notificationbar-close"));
    }

    /**
     * Hides the bar by clicking its close element, if shown.
     */
    public void close() {
        if (isVisible()) {
            getCloseElement().click();
            waitUntilHidden();
        }
    }
}
