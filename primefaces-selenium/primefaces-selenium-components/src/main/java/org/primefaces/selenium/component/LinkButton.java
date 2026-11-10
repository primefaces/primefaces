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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:linkButton}.
 */
public abstract class LinkButton extends AbstractComponent {

    @FindBy(tagName = "a")
    private WebElement link;

    public WebElement getLink() {
        return link;
    }

    /**
     * Gets the URL the link button navigates to.
     *
     * @return the href, {@code #} without a target URL
     */
    public String getHref() {
        return link.getDomAttribute("href");
    }

    /**
     * Clicks the link, waits for the navigation unless the link opens a new window or has no target URL. Does nothing
     * if the link button is disabled, the mouse cannot reach it (pointer-events: none).
     */
    @Override
    public void click() {
        if (!isEnabled()) {
            return;
        }
        String href = getHref();
        if ("_blank".equals(link.getDomAttribute("target")) || href == null || href.startsWith("#")) {
            link.click();
        }
        else {
            PrimeSelenium.guardHttp(link).click();
        }
    }

    /**
     * Is the link button enabled?
     *
     * @return false if disabled on the server or via {@link #disable()}
     */
    @Override
    public boolean isEnabled() {
        return !"true".equals(getDomAttribute("aria-disabled")) && !PrimeSelenium.hasCssClass(this, "p-state-disabled");
    }

    /**
     * Enables the link button.
     */
    public void enable() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".enable();");
    }

    /**
     * Disables the link button.
     */
    public void disable() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".disable();");
    }
}
