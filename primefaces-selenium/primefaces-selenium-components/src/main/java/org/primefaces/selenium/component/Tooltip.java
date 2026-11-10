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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Component wrapper for the PrimeFaces {@code p:tooltip}. The root element is the tooltip, it is moved to the body by
 * the widget.
 * <p>
 * A global tooltip ({@code p:tooltip} without {@code for}) renders no element with its client id, the widget creates
 * an element with the class {@code p-tooltip-global} instead. Locate it e.g. by its {@code styleClass}; then only the
 * methods taking the target element and {@link #hide()} can be used, as the widget configuration can't be found by the
 * id.
 */
public abstract class Tooltip extends AbstractComponent {

    /**
     * Gets the target element of a tooltip with a single target ({@code for}).
     *
     * @return the target
     */
    public WebElement getTarget() {
        return getWebDriver().findElement(By.id(getWidgetConfiguration().getString("target")));
    }

    /**
     * Gets the text (content) of the tooltip, also while hidden.
     *
     * @return the text
     */
    public String getTooltipText() {
        return findElement(By.className("p-tooltip-text")).getDomProperty("textContent").trim();
    }

    /**
     * Is the tooltip shown?
     *
     * @return true if shown
     */
    public boolean isVisible() {
        return PrimeSelenium.isElementDisplayed(getRoot());
    }

    /**
     * Shows the tooltip of a single target ({@code for}) by moving the mouse over the target, or clicking it if the
     * show event is not a mouse event (e.g. {@code focus}).
     */
    public void show() {
        String showEvent = getWidgetConfiguration().optString("showEvent", "mouseenter");
        WebElement target = getTarget();
        if (showEvent.startsWith("mouse")) {
            show(target);
        }
        else {
            target.click();
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getRoot()));
        }
    }

    /**
     * Shows the tooltip by moving the mouse over the given target, for global tooltips or tooltips with several targets.
     *
     * @param target the target element
     */
    public void show(WebElement target) {
        new Actions(getWebDriver()).moveToElement(target).perform();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getRoot()));
    }

    /**
     * Hides the tooltip by moving the mouse to the top left corner of the viewport, if shown. The page should not have
     * a tooltip target there.
     */
    public void hide() {
        if (isVisible()) {
            new Actions(getWebDriver()).moveToLocation(0, 0).perform();
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(getRoot()));
        }
    }

    /**
     * Hides the tooltip with the Escape key, if shown.
     */
    public void hideWithEscape() {
        if (isVisible()) {
            new Actions(getWebDriver()).sendKeys(Keys.ESCAPE).perform();
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(getRoot()));
        }
    }
}
