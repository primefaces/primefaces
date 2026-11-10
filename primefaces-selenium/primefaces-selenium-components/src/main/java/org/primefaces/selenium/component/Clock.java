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

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;

import java.util.List;
import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:clock}.
 */
public abstract class Clock extends AbstractComponent {

    /**
     * Is the time taken from the browser?
     *
     * @return true in client mode, false in server mode
     */
    public boolean isClient() {
        return "client".equals(getWidgetConfiguration().optString("mode"));
    }

    /**
     * Is the clock drawn as an analog clock?
     *
     * @return true in analog display mode
     */
    public boolean isAnalog() {
        return "analog".equals(getWidgetConfiguration().optString("displayMode"));
    }

    /**
     * Gets the formatted date time of a digital clock. It is empty until the first tick.
     *
     * @return the formatted date time
     */
    public String getDateTime() {
        return getText();
    }

    /**
     * Gets the SVG elements of an analog clock: the face and pin circles, 12 hour marks and the 3 hands.
     *
     * @return the SVG circles and paths
     */
    public List<WebElement> getAnalogElements() {
        return findElements(By.cssSelector("svg > circle, svg > path"));
    }

    /**
     * Waits until the clock ticked (once per second): the text of a digital clock or the second hand of an analog clock
     * changed.
     */
    public void waitForTick() {
        if (isAnalog()) {
            String transform = getSecondHandTransform();
            PrimeSelenium.waitAjax().until(driver -> !Objects.equals(transform, getSecondHandTransform()));
        }
        else {
            String text = getDateTime();
            PrimeSelenium.waitAjax().until(driver -> {
                String current = getDateTime();
                return !current.isEmpty() && !current.equals(text);
            });
        }
    }

    /**
     * Starts the ticking of a digital clock.
     */
    public void start() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".start();");
    }

    /**
     * Stops the ticking of a digital clock.
     */
    public void stop() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".stop();");
    }

    /**
     * Synchronizes the time with the server in server mode, a digital clock shows the server time afterwards.
     */
    public void sync() {
        PrimeSelenium.executeScript(true, getWidgetByIdScript() + ".sync();");
    }

    private String getSecondHandTransform() {
        List<WebElement> paths = findElements(By.cssSelector("svg > path"));
        // 12 hour marks, hour hand, minute hand, second hand
        return paths.get(paths.size() - 1).getDomAttribute("transform");
    }
}
