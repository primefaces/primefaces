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
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:menu}.
 */
public abstract class Menu extends AbstractComponent {

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
     * Is this menu toggleable, so submenus can be expanded and collapsed.
     *
     * @return true if toggleable
     */
    public boolean isToggleable() {
        return getWidgetConfiguration().optBoolean("toggleable", false);
    }

    /**
     * Gets the submenu headers.
     *
     * @return the submenu header {@code li} elements
     */
    public List<WebElement> getSubmenuHeaders() {
        return findElements(By.cssSelector(":scope > ul.p-menu-list > li.p-widget-header"));
    }

    /**
     * Gets the expand / collapse icon of the submenu header at the given index of a toggleable menu.
     *
     * @param index the 0-based submenu index
     * @return the toggle icon
     */
    public WebElement getSubmenuToggleIcon(int index) {
        return getSubmenuHeaders().get(index).findElement(By.cssSelector("h3 > .p-menu-toggle-icon"));
    }

    /**
     * Is the submenu at the given index expanded (see {@code aria-expanded}), always true if the menu is not toggleable.
     *
     * @param index the 0-based submenu index
     * @return true if expanded
     */
    public boolean isSubmenuExpanded(int index) {
        return !"false".equals(getSubmenuHeaders().get(index).getDomAttribute("aria-expanded"));
    }

    /**
     * Expands or collapses the submenu at the given index of a toggleable menu.
     *
     * @param index the 0-based submenu index
     * @throws IllegalStateException if the menu is not toggleable
     */
    public void toggleSubmenu(int index) {
        if (!isToggleable()) {
            throw new IllegalStateException("Menu " + getId() + " is not toggleable");
        }
        WebElement header = getSubmenuHeaders().get(index);
        String expanded = header.getDomAttribute("aria-expanded");
        header.click();
        PrimeSelenium.waitGui().until(ExpectedConditions.not(ExpectedConditions.domAttributeToBe(header, "aria-expanded", expanded)));
    }

    /**
     * Expands the submenu at the given index of a toggleable menu if it is collapsed.
     *
     * @param index the 0-based submenu index
     */
    public void expandSubmenu(int index) {
        if (!isSubmenuExpanded(index)) {
            toggleSubmenu(index);
        }
    }

    /**
     * Collapses the submenu at the given index of a toggleable menu if it is expanded.
     *
     * @param index the 0-based submenu index
     */
    public void collapseSubmenu(int index) {
        if (isSubmenuExpanded(index)) {
            toggleSubmenu(index);
        }
    }

    /**
     * Clears the expanded / collapsed state of a toggleable menu stored in the browser, so the next page load uses the
     * state rendered by the server.
     */
    public void clearState() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".clearState();");
    }

    /**
     * Gets the menu items (submenu headers and separators excluded).
     *
     * @return the {@code li.p-menuitem} elements
     */
    public List<WebElement> getMenuitems() {
        return findElements(By.cssSelector("li.p-menuitem"));
    }

    /**
     * Finds a menu item by its label.
     *
     * @param value the label of the menu item
     * @return the {@code li.p-menuitem} element
     */
    public WebElement findMenuitemByValue(String value) {
        return getMenuitems().stream()
                .filter(e -> e.findElement(By.xpath("./a")).getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no menuitem with value '" + value + "'"));
    }

    /**
     * Clicks a menu item by its label, guarded if it is AJAXified.
     *
     * @param value the label of the menu item
     * @return the {@code li.p-menuitem} element
     */
    public WebElement selectMenuitemByValue(String value) {
        WebElement elt = findMenuitemByValue(value);
        WebElement link = elt.findElement(By.xpath("./a"));

        if (isAjaxified(link, "onclick")) {
            PrimeSelenium.guardAjax(link).click();
        }
        else {
            link.click();
        }

        return elt;
    }
}
