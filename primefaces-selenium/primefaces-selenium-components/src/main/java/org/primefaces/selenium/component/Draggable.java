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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

/**
 * Component wrapper for the PrimeFaces {@code p:draggable}, bound to the (hidden) element of the {@code p:draggable} itself.
 * The element made draggable is {@link #getTarget()}. To drop it on a {@code p:droppable}, use {@link Droppable#drop(Draggable)}.
 */
public abstract class Draggable extends AbstractComponent {

    /**
     * Distance in pixels the pointer moves first, so the drag starts before the actual movement.
     */
    private static final int DRAG_START_DISTANCE = 5;

    /**
     * Gets the element that is made draggable.
     *
     * @return the target element
     */
    public WebElement getTarget() {
        return getWebDriver().findElement(By.id(getWidgetConfiguration().getString("target")));
    }

    /**
     * Gets the element on which the drag starts: the {@code handle} inside the target, or the target itself without a
     * handle.
     *
     * @return the handle
     */
    public WebElement getHandle() {
        String handle = getWidgetConfiguration().optString("handle", null);
        WebElement target = getTarget();
        return handle == null || handle.isEmpty() ? target : target.findElement(By.cssSelector(handle));
    }

    /**
     * Is the dragging disabled?
     *
     * @return true if disabled
     */
    public boolean isDisabled() {
        return getWidgetConfiguration().optBoolean("disabled", false);
    }

    /**
     * Drags the target by the given offset with the mouse and releases it there. Waits until the drag (including a
     * revert animation) has ended. This does not wait for the AJAX "drop" event of a droppable, use
     * {@link Droppable#drop(Draggable)} to drop it on a droppable.
     *
     * @param dx horizontal offset in pixels
     * @param dy vertical offset in pixels
     */
    public void dragBy(int dx, int dy) {
        int startX = Integer.signum(dx) * Math.min(DRAG_START_DISTANCE, Math.abs(dx));
        int startY = Integer.signum(dy) * Math.min(DRAG_START_DISTANCE, Math.abs(dy));
        new Actions(getWebDriver())
                    .clickAndHold(getHandle())
                    .moveByOffset(startX, startY)
                    .moveByOffset(dx - startX, dy - startY)
                    .release()
                    .perform();
        waitForDragEnd();
    }

    /**
     * Builds the mouse action that presses on the center of an element, moves it a few pixels to start the drag, moves
     * the pointer to the center of the target (plus the offset) and releases it there.
     *
     * @param driver the web driver
     * @param grab the element to press on, e.g. a drag handle
     * @param target the element to move the pointer to
     * @param xOffset horizontal offset from the center of the target
     * @param yOffset vertical offset from the center of the target
     * @return the action
     */
    static Action dragAndDropAction(WebDriver driver, WebElement grab, WebElement target, int xOffset, int yOffset) {
        return new Actions(driver)
                    .clickAndHold(grab)
                    .moveByOffset(DRAG_START_DISTANCE, DRAG_START_DISTANCE)
                    .moveToElement(target, xOffset, yOffset)
                    .release()
                    .build();
    }

    /**
     * Waits until no element is dragged anymore, e.g. until the revert animation has ended.
     */
    static void waitForDragEnd() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.script(
                    "return document.querySelector('.p-draggable-dragging, .p-sortable-helper') === null;"));
    }
}
