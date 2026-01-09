/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:dialog}.
 */
public abstract class Dialog extends AbstractComponent {

    @FindBy(className = "ui-dialog-content")
    private WebElement content;

    @FindBy(className = "ui-dialog-title")
    private WebElement title;

    @FindBy(className = "ui-dialog-titlebar-close")
    private WebElement closeButton;

    @FindBy(className = "ui-dialog-titlebar-minimize")
    private WebElement minimizeButton;

    @FindBy(className = "ui-dialog-titlebar-maximize")
    private WebElement maximizeButton;

    public WebElement getContent() {
        return content;
    }

    public String getTitle() {
        return title.getText();
    }

    public WebElement getCloseButton() {
        return closeButton;
    }

    public void setCloseButton(WebElement closeButton) {
        this.closeButton = closeButton;
    }

    public WebElement getMinimizeButton() {
        return minimizeButton;
    }

    public void setMinimizeButton(WebElement minimizeButton) {
        this.minimizeButton = minimizeButton;
    }

    public WebElement getMaximizeButton() {
        return maximizeButton;
    }

    public void setMaximizeButton(WebElement maximizeButton) {
        this.maximizeButton = maximizeButton;
    }

    public void setContent(WebElement content) {
        this.content = content;
    }

    /**
     * Is the dialog currently visible.
     *
     * @return true if visible false if not
     */
    public boolean isVisible() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".isVisible();");
    }

    /**
     * Minimize the dialog.
     */
    public void toggleMinimize() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".toggleMinimize();");
    }

    /**
     * Maximize the dialog.
     */
    public void toggleMaximize() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".toggleMaximize();");
    }

    /**
     * Shows the dialog.
     */
    public void show() {
        if (isEnabled() && !isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(this));
        }
    }

    /**
     * Hides the dialog.
     */
    public void hide() {
        if (isEnabled() && isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(this));
        }
    }

}
