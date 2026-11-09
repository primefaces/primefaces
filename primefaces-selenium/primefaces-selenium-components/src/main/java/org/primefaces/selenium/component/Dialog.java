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
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:dialog}.
 */
public abstract class Dialog extends AbstractComponent {

    @FindBy(className = "p-dialog-box")
    private WebElement box;

    @FindBy(className = "p-dialog-titlebar")
    private WebElement titlebar;

    @FindBy(css = ".p-dialog-box > .p-resizable-se")
    private WebElement resizeHandle;

    @FindBy(className = "p-dialog-content")
    private WebElement content;

    @FindBy(className = "p-dialog-title")
    private WebElement title;

    @FindBy(className = "p-dialog-titlebar-close")
    private WebElement closeButton;

    @FindBy(className = "p-dialog-titlebar-minimize")
    private WebElement minimizeButton;

    @FindBy(className = "p-dialog-titlebar-maximize")
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
     * Gets the dialog box. The root element of a dialog is a container which may cover the whole viewport, so use the box for the
     * visible location and size of the dialog.
     *
     * @return the dialog box element
     */
    public WebElement getBox() {
        return box;
    }

    /**
     * Gets the title bar, which is the drag handle of the dialog.
     *
     * @return the title bar element
     */
    public WebElement getTitlebar() {
        return titlebar;
    }

    /**
     * Gets the south east resize handle of a resizable dialog.
     *
     * @return the resize handle element
     */
    public WebElement getResizeHandle() {
        return resizeHandle;
    }

    /**
     * Is the dialog currently maximized.
     *
     * @return true if maximized
     */
    public boolean isMaximized() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".maximized === true;");
    }

    /**
     * Is the dialog currently minimized (docked).
     *
     * @return true if minimized
     */
    public boolean isMinimized() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".minimized === true;");
    }

    /**
     * Minimizes the dialog by clicking its minimize button, if it is not already minimized.
     */
    public void minimize() {
        if (!isMinimized()) {
            clickTitlebarIcon(getMinimizeButton(), "minimize");
            PrimeSelenium.waitGui().until(d -> isMinimized());
        }
    }

    /**
     * Maximizes the dialog by clicking its maximize button, if it is not already maximized.
     */
    public void maximize() {
        if (!isMaximized()) {
            clickTitlebarIcon(getMaximizeButton(), "maximize");
            PrimeSelenium.waitGui().until(d -> isMaximized());
        }
    }

    /**
     * Drags the dialog by its title bar like a user would do with the mouse.
     *
     * @param dx horizontal offset in pixels
     * @param dy vertical offset in pixels
     */
    public void dragBy(int dx, int dy) {
        moveBy(getTitlebar(), dx, dy, "move");
    }

    /**
     * Resizes the dialog with its south east resize handle like a user would do with the mouse.
     *
     * @param dx width change in pixels
     * @param dy height change in pixels
     */
    public void resizeBy(int dx, int dy) {
        moveBy(getResizeHandle(), dx, dy, "resizeStop");
    }

    private void clickTitlebarIcon(WebElement icon, String behavior) {
        if (ComponentUtils.hasAjaxBehavior(getRoot(), behavior)) {
            PrimeSelenium.guardAjax(icon).click();
        }
        else {
            icon.click();
        }
    }

    private void moveBy(WebElement handle, int dx, int dy, String behavior) {
        // move a few pixels first, so the drag starts before the actual movement
        int startX = Integer.signum(dx) * Math.min(5, Math.abs(dx));
        int startY = Integer.signum(dy) * Math.min(5, Math.abs(dy));
        Action action = new Actions(getWebDriver())
                    .clickAndHold(handle)
                    .moveByOffset(startX, startY)
                    .moveByOffset(dx - startX, dy - startY)
                    .release()
                    .build();
        if (ComponentUtils.hasAjaxBehavior(getRoot(), behavior)) {
            PrimeSelenium.guardAjax(action).perform();
        }
        else {
            action.perform();
        }
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
