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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;

/**
 * Component wrapper for the PrimeFaces {@code p:droppable}, bound to the (hidden) element of the {@code p:droppable} itself.
 * The element made droppable is {@link #getTarget()}.
 */
public abstract class Droppable extends AbstractComponent {

    /**
     * Gets the element that is made droppable.
     *
     * @return the target element
     */
    public WebElement getTarget() {
        return getWebDriver().findElement(By.id(getWidgetConfiguration().getString("target")));
    }

    /**
     * Is the dropping disabled?
     *
     * @return true if disabled
     */
    public boolean isDisabled() {
        return getWidgetConfiguration().optBoolean("disabled", false);
    }

    /**
     * Is this component AJAX enabled with "drop"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isDropAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "drop");
    }

    /**
     * Does this droppable accept the draggable element: same scope and matched by the {@code accept} option?
     *
     * @param draggable the element made draggable (the target of a {@code p:draggable})
     * @return true if it would be dropped
     */
    public boolean accepts(WebElement draggable) {
        Boolean accepts = PrimeSelenium.executeScript("var droppable = PrimeFaces.interactions.instance(arguments[0], 'droppable');"
                    + "var draggable = PrimeFaces.interactions.instance(arguments[1], 'draggable');"
                    + "return !!droppable && !!draggable && !droppable.options.disabled && !draggable.options.disabled"
                    + " && droppable.options.scope === draggable.options.scope && droppable.accepts($(arguments[1]));",
                    getTarget(), draggable);
        return Boolean.TRUE.equals(accepts);
    }

    /**
     * Drags the {@code p:draggable} with the mouse by its handle onto the center of this droppable and drops it there.
     * Waits for the "drop" AJAX behavior if the droppable accepts the draggable, and until the drag (including a revert
     * animation) has ended.
     *
     * @param draggable the draggable
     */
    public void drop(Draggable draggable) {
        drop(draggable.getHandle(), draggable.getTarget());
    }

    /**
     * Drags an element made draggable with the mouse by its center onto the center of this droppable and drops it there.
     * Waits for the "drop" AJAX behavior if the droppable accepts the element, and until the drag (including a revert
     * animation) has ended.
     *
     * @param draggable the element made draggable, e.g. the target of a {@code p:draggable}
     */
    public void drop(WebElement draggable) {
        drop(draggable, draggable);
    }

    private void drop(WebElement handle, WebElement draggable) {
        boolean ajax = isDropAjaxified() && accepts(draggable);
        Action action = Draggable.dragAndDropAction(getWebDriver(), handle, getTarget(), 0, 0);
        if (ajax) {
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            action.perform();
        }
        Draggable.waitForDragEnd();
    }
}
