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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:imageSwitch}.
 */
public abstract class ImageSwitch extends AbstractComponent {

    /**
     * Gets the images.
     *
     * @return the images
     */
    public List<WebElement> getImages() {
        return findElements(By.cssSelector(":scope > *"));
    }

    /**
     * Gets the index of the active image, the target image while switching.
     *
     * @return the 0-based index
     */
    public int getActiveIndex() {
        Number index = PrimeSelenium.executeScript("var jq = " + getWidgetByIdScript() + ".jq;"
                    + "var opts = jq.data('cycle.opts');"
                    + "return opts ? opts.currSlide : jq.children(':visible').first().index();");
        return index.intValue();
    }

    /**
     * Gets the active image.
     *
     * @return the active image
     */
    public WebElement getActiveImage() {
        return getImages().get(getActiveIndex());
    }

    /**
     * Switches to the next image.
     */
    public void next() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".next();");
        waitUntilSwitched();
    }

    /**
     * Switches to the previous image.
     */
    public void previous() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".previous();");
        waitUntilSwitched();
    }

    /**
     * Switches to the image with the given index.
     *
     * @param index the 0-based index
     */
    public void switchTo(int index) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".switchTo(" + index + ");");
        waitUntilSwitched();
    }

    /**
     * Is the slideshow switching the images automatically?
     *
     * @return true if the slideshow has a timeout
     */
    public boolean isSlideshowAuto() {
        return getWidgetConfiguration().optInt("timeout", 0) > 0;
    }

    /**
     * Is the slideshow paused?
     *
     * @return true if paused
     */
    public boolean isSlideshowPaused() {
        return PrimeSelenium.executeScript("return (" + getWidgetByIdScript() + ".jq.get(0).cyclePause || 0) > 0;");
    }

    /**
     * Pauses the slideshow.
     */
    public void pauseSlideshow() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".pauseSlideshow();");
    }

    /**
     * Resumes the paused slideshow.
     */
    public void resumeSlideshow() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".resumeSlideshow();");
    }

    /**
     * Waits until the slideshow switched to another image.
     */
    public void waitUntilNextSlide() {
        int index = getActiveIndex();
        PrimeSelenium.waitGui().until(driver -> getActiveIndex() != index);
        waitUntilSwitched();
    }

    private void waitUntilSwitched() {
        PrimeSelenium.waitGui().until(ExpectedConditions.and(
                    PrimeExpectedConditions.script("var opts = " + getWidgetByIdScript() + ".jq.data('cycle.opts'); return !opts || !opts.busy;"),
                    PrimeExpectedConditions.animationNotActive()));
    }
}
