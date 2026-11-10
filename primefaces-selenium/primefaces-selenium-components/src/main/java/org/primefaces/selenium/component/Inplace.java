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
import org.primefaces.selenium.findby.FindByParentPartialId;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:inplace}.
 */
public abstract class Inplace extends AbstractComponent {

    @FindByParentPartialId("_display")
    private WebElement display;

    @FindByParentPartialId("_content")
    private WebElement content;

    /**
     * Gets the display element, shown while the inline content is hidden.
     *
     * @return the display element
     */
    public WebElement getDisplay() {
        return display;
    }

    /**
     * Gets the inline content, not rendered if the inplace is disabled.
     *
     * @return the inline content
     */
    public WebElement getContent() {
        return content;
    }

    /**
     * Gets the text of the display element.
     *
     * @return the label or value shown while the inline content is hidden
     */
    public String getDisplayText() {
        return display.getText();
    }

    /**
     * Is the inplace disabled, so the inline content cannot be shown?
     *
     * @return true if disabled
     */
    public boolean isDisabled() {
        return getWidgetConfiguration().optBoolean("disabled", false);
    }

    /**
     * Is the inline content shown?
     *
     * @return true if the inline content is visible
     */
    public boolean isContentVisible() {
        return PrimeSelenium.isElementDisplayed(content);
    }

    /**
     * Shows the inline content, if hidden: clicks the display element if the inplace is toggled by click, otherwise
     * uses the widget API. Does nothing if the inplace is disabled, its inline content is not rendered.
     */
    public void show() {
        if (!isDisabled() && !isContentVisible()) {
            JSONObject cfg = getWidgetConfiguration();
            if (cfg.optBoolean("toggleable", false) && "click".equals(cfg.optString("event"))) {
                display.click();
            }
            else {
                PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
            }
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(content));
        }
    }

    /**
     * Hides the inline content, if shown.
     */
    public void hide() {
        if (isContentVisible()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(content));
        }
    }

    /**
     * Gets the save button, only rendered with {@code editor="true"}.
     *
     * @return the save button
     */
    public WebElement getSaveButton() {
        return content.findElement(By.className("p-inplace-save"));
    }

    /**
     * Gets the cancel button, only rendered with {@code editor="true"}.
     *
     * @return the cancel button
     */
    public WebElement getCancelButton() {
        return content.findElement(By.className("p-inplace-cancel"));
    }

    /**
     * Saves the value of the inplace editor via AJAX.
     */
    public void save() {
        PrimeSelenium.guardAjax(getSaveButton()).click();
    }

    /**
     * Discards the changes of the inplace editor via AJAX.
     */
    public void cancel() {
        PrimeSelenium.guardAjax(getCancelButton()).click();
    }
}
