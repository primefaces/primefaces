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
import org.primefaces.selenium.component.base.AbstractInputComponent;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:megaMenu}.
 */
public abstract class MegaMenu extends AbstractInputComponent {

    /**
     * Finds a menuitem (or root submenu) by its label/value, searching directly under this MegaMenu.
     * @param value the visible label of the menuitem
     * @return the matching {@code li.ui-menuitem} element
     */
    public WebElement findMenuitemByValue(String value) {
        return findMenuitemByValue(this, value);
    }

    /**
     * Finds a menuitem (or submenu) by its label/value within the given parent element.
     * Column titles ({@code li.ui-widget-header}) are not {@code ui-menuitem}s and are therefore ignored.
     * @param parentElt element to search within
     * @param value the visible label of the menuitem
     * @return the matching {@code li.ui-menuitem} element
     */
    public WebElement findMenuitemByValue(WebElement parentElt, String value) {
        List<WebElement> subElements = parentElt.findElements(By.cssSelector("li.ui-menuitem"));
        return subElements.stream()
                .filter(e -> e.getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no menuitem with value '" + value + "'"));
    }

    /**
     * Opens the mega panel of a root submenu identified by its label. Uses hover when {@code autoDisplay}
     * is enabled, otherwise clicks the submenu link, then waits until the panel becomes visible.
     * @param label the label of the root submenu
     * @return the root submenu {@code li.ui-menu-parent} element so nested lookups can be scoped to it
     */
    public WebElement openRootSubmenu(String label) {
        Actions actions = new Actions(getWebDriver());
        actions.moveToElement(getWrappedElement()).build().perform(); // ensure menu is in viewport / reset hover state

        WebElement rootSubmenu = findMenuitemByValue(label);
        WebElement submenuLink = rootSubmenu.findElement(By.cssSelector("a.ui-submenu-link"));
        WebElement panel = rootSubmenu.findElement(By.cssSelector("ul.ui-menu-child"));

        if (isAutoDisplay()) {
            actions = new Actions(getWebDriver());
            actions.moveToElement(submenuLink).build().perform();
        }
        else {
            submenuLink.click();
        }

        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(panel));

        return rootSubmenu;
    }

    /**
     * Selects a leaf menuitem (by label) within the given parent element, guarding the AJAX request when applicable.
     * @param parentElt element to search within (typically the root submenu returned by {@link #openRootSubmenu(String)})
     * @param value the visible label of the menuitem
     * @return the selected {@code li.ui-menuitem} element
     */
    public WebElement selectMenuitemByValue(WebElement parentElt, String value) {
        WebElement elt = findMenuitemByValue(parentElt, value);
        WebElement eltA = elt.findElement(By.xpath("./a"));

        if (isAjaxified(eltA, "onclick")) {
            PrimeSelenium.guardAjax(eltA).click();
        }
        else {
            eltA.click();
        }

        return elt;
    }

    public boolean isAutoDisplay() {
        return getWidgetConfiguration().getBoolean("autoDisplay");
    }

    public boolean isVertical() {
        return PrimeSelenium.hasCssClass(getRoot(), "ui-megamenu-vertical");
    }
}
