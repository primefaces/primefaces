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

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;

/**
 * Component wrapper for the PrimeFaces {@code p:overlayPanel}.
 */
public abstract class OverlayPanel extends AbstractComponent {

    /**
     * Is the overlay currently visible.
     *
     * @return true if visible false if not
     */
    public boolean isVisible() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".isVisible();");
    }

    /**
     * Is the overlay currently visible.
     *
     * @return true if visible false if not
     */
    public int getShowDelay() {
        return getWidgetConfiguration().getInt("showDelay");
    }

    /**
     * Toggle the overlay visibility.
     */
    public void toggle() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".toggle();");
    }

    /**
     * Makes this overlay modal.
     */
    public void enableModality() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".enableModality();");
    }

    /**
     * Makes this overlay non-modal.
     */
    public void disableModality() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".disableModality();");
    }

    /**
     * Shows the overlay.
     */
    public void show() {
        if (isEnabled() && !isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
            waitForDisplay();
        }
    }

    /**
     * Hides the overlay.
     */
    public void hide() {
        if (isEnabled() && isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(this));
        }
    }

    /**
     * Wait for the client side setTimeout when displaying overlay.
     */
    public void waitForDisplay() {
        PrimeSelenium.wait(getShowDelay());
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(this));
    }

}
