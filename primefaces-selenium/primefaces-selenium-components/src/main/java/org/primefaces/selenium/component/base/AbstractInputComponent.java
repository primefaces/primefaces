/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.selenium.component.base;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeSelenium;

public abstract class AbstractInputComponent extends AbstractComponent {

    /**
     * The input element reference.
     *
     * @return the {@link WebElement} representing the input.
     */
    public WebElement getInput() {
        return getRoot();
    }

    /**
     * Is this SelectOneMenu enabled?
     *
     * @return true if enabled, false if not
     */
    @Override
    public boolean isEnabled() {
        return getInput().isEnabled() && !PrimeSelenium.hasCssClass(this, "ui-state-disabled");
    }

    /**
     * Is the input using AJAX "change" or "valueChange" event?
     *
     * @return true if using AJAX for onchange, change or valueChange
     */
    public boolean isOnchangeAjaxified() {
        return isAjaxified(getInput(), "onchange") || ComponentUtils.hasAjaxBehavior(getRoot(), "change");
    }

    /**
     * The HTML label assigned to this input.
     *
     * @return the {@link WebElement} representing the label.
     */
    public WebElement getAssignedLabel() {
        return getWebDriver().findElement(By.cssSelector("label[for='" + getInput().getAttribute("id") + "']"));
    }

    /**
     * The HTML label text assigned to this input.
     *
     * @return the value of the label text
     */
    public String getAssignedLabelText() {
        return getAssignedLabel().getText();
    }

    /**
     * Copy the current value in the Input to the clipboard.
     *
     * @return the value copied to the clipboard
     */
    public String copyToClipboard() {
        WebElement input = getInput();
        Keys command = PrimeSelenium.isMacOs() ? Keys.COMMAND : Keys.CONTROL;
        input.sendKeys(Keys.chord(command, "a")); // select everything
        input.sendKeys(Keys.chord(command, "c")); // copy
        return input.getAttribute("value");
    }

    /**
     * Paste the current value of the clipboard to the Input.
     *
     * @return the value pasted into the input
     */
    public String pasteFromClipboard() {
        WebElement input = getInput();
        Keys command = PrimeSelenium.isMacOs() ? Keys.COMMAND : Keys.CONTROL;
        input.sendKeys(Keys.chord(command, "a")); // select everything
        input.sendKeys(Keys.chord(command, "v")); // paste
        return input.getAttribute("value");
    }

    /**
     * Selects all text in the input component.
     */
    public void selectAllText() {
        Keys command = PrimeSelenium.isMacOs() ? Keys.COMMAND : Keys.CONTROL;
        getInput().sendKeys(Keys.chord(command, "a"));
    }

    /**
     * Clears the input and guards AJAX for "clear" event.
     */
    @Override
    public void clear() {
        PrimeSelenium.clearInput(getInput(), false);
    }

    /**
     * Enables the input/
     */
    public void enable() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".enable();");
    }

    /**
     * Disables the input.
     */
    public void disable() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".disable();");
    }

}
