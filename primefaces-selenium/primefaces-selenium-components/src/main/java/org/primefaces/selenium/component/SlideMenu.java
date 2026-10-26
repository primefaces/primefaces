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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:slideMenu}.
 */
public abstract class SlideMenu extends AbstractComponent {

    /**
     * Is this menu displayed as an overlay.
     *
     * @return true if overlay false if inline
     */
    public boolean isOverlay() {
        return getWidgetConfiguration().optBoolean("overlay", false);
    }

    /**
     * Shows the overlay menu.
     */
    public void show() {
        if (isOverlay() && !isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(this));
        }
    }

    /**
     * Hides the overlay menu.
     */
    public void hide() {
        if (isOverlay() && isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(this));
        }
    }

    /**
     * Gets the number of submenus the menu has slid into, 0 when the root list is shown.
     *
     * @return the current depth
     */
    public int getDepth() {
        return ((Number) PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".stack.length;")).intValue();
    }

    /**
     * Gets the back link which is shown while a submenu is displayed.
     *
     * @return the back link element
     */
    public WebElement getBackward() {
        return findElement(By.cssSelector(".ui-slidemenu-backward a.ui-menuitem-link"));
    }

    /**
     * Is the back link currently visible.
     *
     * @return true if visible false if not
     */
    public boolean isBackwardVisible() {
        return getBackward().isDisplayed();
    }

    /**
     * Gets the menu items of the currently displayed list (root list or the submenu slid into).
     *
     * @return the menu items
     */
    public List<WebElement> getMenuitems() {
        WebElement list = PrimeSelenium.executeScript("var w = " + getWidgetByIdScript() + ";"
                + "return (w.stack.length ? w.stack[w.stack.length - 1] : w.rootList)[0];");
        return list.findElements(By.cssSelector(":scope > li.ui-menuitem"));
    }

    /**
     * Finds a menu item of the currently displayed list by its label.
     *
     * @param value the label of the menu item
     * @return the menu item
     */
    public WebElement findMenuitemByValue(String value) {
        return getMenuitems().stream()
                .filter(e -> e.findElement(By.xpath("./a")).getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no menuitem with value '" + value + "'"));
    }

    /**
     * Clicks a menu item of the currently displayed list by its label. A submenu slides in, a regular menu item is
     * clicked (guarded if it is AJAXified).
     *
     * @param value the label of the menu item
     * @return the menu item
     */
    public WebElement selectMenuitemByValue(String value) {
        WebElement elt = findMenuitemByValue(value);
        WebElement link = elt.findElement(By.xpath("./a"));

        if (PrimeSelenium.hasCssClass(elt, "ui-menu-parent")) {
            link.click();
            waitForSlide();
        }
        else if (isAjaxified(link, "onclick")) {
            PrimeSelenium.guardAjax(link).click();
        }
        else {
            link.click();
        }

        return elt;
    }

    /**
     * Slides back to the previous list by clicking the back link.
     */
    public void back() {
        if (getDepth() > 0) {
            getBackward().click();
            waitForSlide();
        }
    }

    /**
     * Wait for the slide (and back link fade) animations to complete.
     */
    public void waitForSlide() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.script(
                "return " + getWidgetByIdScript() + ".jq.find(':animated').length === 0;"));
    }
}
