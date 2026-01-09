/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:colorPicker}.
 */
public abstract class ColorPicker extends AbstractInputComponent {

    /**
     * Gets the color pickup popup panel.
     * @return the WebElement for the panel
     */
    public WebElement getPanel() {
        return getWebDriver().findElement(By.id("clr-picker"));
    }

    /**
     * Gets the popup trigger button.
     * @return the WebElement for the trigger button
     */
    public WebElement getTriggerButton() {
        return findElement(By.xpath("preceding-sibling::*"));
    }

    /**
     * Is this component AJAX enabled with "open"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isOpenAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "open");
    }

    /**
     * Is this component AJAX enabled with "close"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isCloseAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "close");
    }

    /**
     * Set the current color using the widget.
     *
     * @param color the Hex/RGB/HSL color
     */
    public void setColor(String color) {
        PrimeSelenium.executeScript(isOnchangeAjaxified(), getWidgetByIdScript() + ".setColor('" + color + "');");
    }

    /**
     * Gets the current color from the widget.
     *
     * @return the Hex/RGB/HSL color or null
     */
    public String getColor() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getColor();");
    }

    /**
     * Shows the overlay panel.
     *
     * @return the panel shown
     */
    public WebElement showPanel() {
        if (isEnabled()) {
            if (!getPanel().isDisplayed()) {
                PrimeSelenium.executeScript(isOpenAjaxified(), getWidgetByIdScript() + ".show()");
            }
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getPanel()));
        }
        return getPanel();
    }

    /**
     * Hides the overlay panel.
     * @param revert true to revert to the default color
     */
    public void hidePanel(boolean revert) {
        if (isEnabled()) {
            if (getPanel().isDisplayed()) {
                PrimeSelenium.executeScript(isCloseAjaxified(), getWidgetByIdScript() + ".hide(" + Boolean.toString(revert) + ");");
            }
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(getPanel()));
        }
    }
}
