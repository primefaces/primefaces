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
 * Component wrapper for the PrimeFaces {@code p:resizable}. As {@code p:resizable} renders no element of its own, this
 * wrapper is bound to the element made resizable (the {@code for} target or the parent of the {@code p:resizable}); the
 * widget is looked up by that target.
 */
public abstract class Resizable extends AbstractComponent {

    /**
     * Distance in pixels the pointer moves first, so the resizing starts before the actual movement.
     */
    private static final int RESIZE_START_DISTANCE = 5;

    /**
     * Gets the {@code p:resizable} widget whose target is the root element.
     *
     * @return the JS script
     */
    @Override
    public String getWidgetByIdScript() {
        return "PrimeFaces.getWidgetsByType(PrimeFaces.widget.Resizable).find(function(w) { return w.cfg.target === '" + getId() + "'; })";
    }

    @Override
    public String getWidgetConfigurationAsString() {
        return PrimeSelenium.executeScript("return JSON.stringify(" + getWidgetByIdScript() + ".cfg, function(key, value) {"
                    + " return typeof value === 'function' ? value.toString() : value; });");
    }

    /**
     * Is this component AJAX enabled with "resize"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isResizeAjaxified() {
        String widget = getWidgetByIdScript();
        Boolean hasBehavior = PrimeSelenium.executeScript("return " + widget + ".hasBehavior('resize');");
        if (!Boolean.TRUE.equals(hasBehavior)) {
            return false;
        }
        return ComponentUtils.isAjaxScript(PrimeSelenium.executeScript("return " + widget + ".getBehavior('resize').toString();"));
    }

    /**
     * Gets the resize handles. Elements that can't contain the handles (e.g. a textarea or an image) are wrapped, then
     * the handles are in the wrapper.
     *
     * @return the handles
     */
    public List<WebElement> getHandles() {
        return getResizeElement().findElements(By.cssSelector(":scope > .p-resizable-handle"));
    }

    /**
     * Gets the directions of the resize handles, e.g. {@code e}, {@code s} and {@code se}.
     *
     * @return the directions
     */
    public List<String> getHandleDirections() {
        return getHandles().stream().map(handle -> handle.getDomAttribute("data-axis")).collect(Collectors.toList());
    }

    /**
     * Gets the resize handle of a direction.
     *
     * @param direction the direction, e.g. {@code se}
     * @return the handle
     */
    public WebElement getHandle(String direction) {
        return getResizeElement().findElement(By.cssSelector(":scope > .p-resizable-handle.p-resizable-" + direction));
    }

    /**
     * Resizes the element with the mouse by dragging the bottom right ({@code se}) handle. Waits for the "resize" AJAX
     * behavior.
     *
     * @param dx width change in pixels
     * @param dy height change in pixels
     */
    public void resizeBy(int dx, int dy) {
        resizeBy("se", dx, dy);
    }

    /**
     * Resizes the element with the mouse by dragging a handle. Waits for the "resize" AJAX behavior.
     *
     * @param direction the direction of the handle, e.g. {@code e}
     * @param dx horizontal movement in pixels
     * @param dy vertical movement in pixels
     */
    public void resizeBy(String direction, int dx, int dy) {
        int startX = Integer.signum(dx) * Math.min(RESIZE_START_DISTANCE, Math.abs(dx));
        int startY = Integer.signum(dy) * Math.min(RESIZE_START_DISTANCE, Math.abs(dy));
        Action action = new Actions(getWebDriver())
                    .clickAndHold(getHandle(direction))
                    .moveByOffset(startX, startY)
                    .moveByOffset(dx - startX, dy - startY)
                    .release()
                    .build();
        if (isResizeAjaxified()) {
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            action.perform();
        }
    }

    private WebElement getResizeElement() {
        return PrimeSelenium.executeScript("var resizable = PrimeFaces.interactions.instance(arguments[0], 'resizable');"
                    + "return resizable ? resizable.resizeElement[0] : arguments[0];", getRoot());
    }
}
