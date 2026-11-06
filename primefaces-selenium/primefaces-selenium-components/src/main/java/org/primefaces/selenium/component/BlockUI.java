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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:blockUI}.
 */
public abstract class BlockUI extends AbstractComponent {

    /**
     * Finds the overlay elements of this Block UI, one per blocked target. If this Block UI is no longer in the DOM (e.g. destroyed),
     * the overlays of all Block UIs are returned, so leftovers can be detected.
     *
     * @return the overlay elements
     */
    public List<WebElement> getOverlays() {
        return findElements("data-bui-overlay", "p-blockui");
    }

    /**
     * Finds the content elements of this Block UI, one per blocked target if there is more than one. If this Block UI is no longer in
     * the DOM (e.g. destroyed), the contents of all Block UIs are returned, so leftovers can be detected.
     *
     * @return the content elements
     */
    public List<WebElement> getContents() {
        return findElements("data-bui-content", "p-blockui-content");
    }

    private List<WebElement> findElements(String dataAttribute, String fallbackClassName) {
        String id;
        try {
            id = getId();
        }
        catch (NoSuchElementException e) {
            return getWebDriver().findElements(By.className(fallbackClassName));
        }
        return getWebDriver().findElements(By.cssSelector("[" + dataAttribute + "~=\"" + id + "\"]"));
    }

    /**
     * Is the blocker currently blocking.
     *
     * @return true if blocking false if not
     */
    public boolean isBlocking() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".isBlocking();");
    }

    /**
     * Does the block have content.
     *
     * @return true if has content, false if not
     */
    public boolean hasContent() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".hasContent();");
    }

    /**
     * Shows the blocker.
     */
    public void show() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
        List<WebElement> overlays = getOverlays();
        if (overlays.size() > 0) {
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(overlays.get(0)));
        }
    }

    /**
     * Hides the blocker.
     */
    public void hide() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
        List<WebElement> overlays = getOverlays();
        if (overlays.size() > 0) {
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(overlays.get(0)));
        }
    }

}
