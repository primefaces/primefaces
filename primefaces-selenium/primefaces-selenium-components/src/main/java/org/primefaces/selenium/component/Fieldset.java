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
import org.primefaces.selenium.component.base.ComponentUtils;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:fieldset}.
 */
public abstract class Fieldset extends AbstractComponent {

    @FindBy(className = "p-fieldset-legend")
    private WebElement legend;

    @FindBy(className = "p-fieldset-content")
    private WebElement content;

    public WebElement getLegend() {
        return legend;
    }

    public WebElement getContent() {
        return content;
    }

    /**
     * Can the content be expanded and collapsed?
     *
     * @return true if toggleable
     */
    public boolean isToggleable() {
        return getWidgetConfiguration().optBoolean("toggleable", false);
    }

    /**
     * Is the content collapsed?
     *
     * @return true if collapsed, always false if not toggleable
     */
    public boolean isCollapsed() {
        return isToggleable() && !Boolean.parseBoolean(legend.getDomAttribute("aria-expanded"));
    }

    /**
     * Is this component AJAX enabled with "toggle"?
     *
     * @return true if AJAX enabled false if not
     */
    public boolean isToggleAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "toggle");
    }

    /**
     * Expands or collapses the content by clicking the legend. Waits for the dynamic content load and the "toggle" AJAX
     * behavior.
     */
    public void toggle() {
        if (isToggleAjaxified() || isContentLoadPending()) {
            PrimeSelenium.guardAjax(legend).click();
        }
        else {
            legend.click();
        }
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.animationNotActive());
    }

    /**
     * Expands the content, if collapsed.
     */
    public void expand() {
        if (isCollapsed()) {
            toggle();
        }
    }

    /**
     * Collapses the content, if expanded.
     */
    public void collapse() {
        if (isToggleable() && !isCollapsed()) {
            toggle();
        }
    }

    private boolean isContentLoadPending() {
        return isCollapsed() && getWidgetConfiguration().optBoolean("dynamic", false)
                    && !Boolean.TRUE.equals(PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".loaded;"));
    }
}
