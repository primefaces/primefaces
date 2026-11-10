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
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

/**
 * Component wrapper for the PrimeFaces {@code p:splitter}.
 */
public abstract class Splitter extends AbstractComponent {

    /**
     * Gets the panels.
     *
     * @return the panels
     */
    public List<WebElement> getPanels() {
        return findElements(By.cssSelector(":scope > .p-splitter-panel"));
    }

    /**
     * Gets the gutters between the panels.
     *
     * @return the gutters
     */
    public List<WebElement> getGutters() {
        return findElements(By.cssSelector(":scope > .p-splitter-gutter"));
    }

    /**
     * Gets the focusable handle of a gutter, it resizes the panels with the arrow keys, Home, End and Enter.
     *
     * @param index index of the gutter
     * @return the handle
     */
    public WebElement getGutterHandle(int index) {
        return getGutters().get(index).findElement(By.className("p-splitter-gutter-handle"));
    }

    /**
     * Gets the size of the panel before a gutter from {@code aria-valuenow} of its handle.
     *
     * @param index index of the gutter
     * @return the size in percent
     */
    public double getPanelSizeBeforeGutter(int index) {
        return Double.parseDouble(getGutterHandle(index).getDomAttribute("aria-valuenow"));
    }

    /**
     * Gets the sizes of all panels.
     *
     * @return the sizes in percent
     */
    public List<Double> getPanelSizes() {
        List<Object> sizes = PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".panelSizes;");
        return sizes.stream().map(size -> ((Number) size).doubleValue()).collect(Collectors.toList());
    }

    /**
     * Is this component AJAX enabled with "resizeEnd"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isResizeEndAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "resizeEnd");
    }

    /**
     * Drags a gutter with the mouse.
     *
     * @param index index of the gutter
     * @param offset offset in pixels, horizontally or vertically depending on the layout
     */
    public void dragGutter(int index, int offset) {
        boolean horizontal = "horizontal".equals(getWidgetConfiguration().optString("layout"));
        WebElement gutter = getGutters().get(index);
        Action action = new Actions(getWebDriver())
                    .clickAndHold(gutter)
                    .moveByOffset(horizontal ? offset : 0, horizontal ? 0 : offset)
                    .release()
                    .build();
        if (isResizeEndAjaxified()) {
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            action.perform();
        }
    }
}
