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

import org.primefaces.selenium.component.base.AbstractToggleComponent;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:toggleSwitch}.
 */
public abstract class ToggleSwitch extends AbstractToggleComponent {

    /**
     * Gets the track of the switch. A click on it toggles the switch like a user, without guarding a request.
     *
     * @return the slider element
     */
    public WebElement getSlider() {
        return findElement(By.className("p-toggleswitch-slider"));
    }

    /**
     * Gets the knob of the switch which moves to the end of the track when checked.
     *
     * @return the handle element
     */
    public WebElement getHandle() {
        return findElement(By.className("p-toggleswitch-handler"));
    }
}
