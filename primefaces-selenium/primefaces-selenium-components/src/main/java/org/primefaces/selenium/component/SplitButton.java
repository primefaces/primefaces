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
 * Component wrapper for the PrimeFaces {@code p:splitButton}.
 */
public abstract class SplitButton extends AbstractComponent {

    /**
     * Gets the default (main) button.
     *
     * @return the default button
     */
    public WebElement getButton() {
        return findElement(By.id(getId() + "_button"));
    }

    /**
     * Gets the button which opens the menu.
     *
     * @return the menu button or {@code null} if there is none (no rendered menu items and no custom overlay)
     */
    public WebElement getMenuButton() {
        List<WebElement> menuButtons = findElements(By.id(getId() + "_menuButton"));
        return menuButtons.isEmpty() ? null : menuButtons.get(0);
    }

    /**
     * Gets the overlay menu, which is not a child of the SplitButton element.
     *
     * @return the menu
     */
    public WebElement getMenu() {
        return getWebDriver().findElement(By.id(getId() + "_menu"));
    }

    /**
     * Is the overlay menu currently displayed.
     *
     * @return true if displayed
     */
    public boolean isMenuVisible() {
        List<WebElement> menus = getWebDriver().findElements(By.id(getId() + "_menu"));
        return !menus.isEmpty() && menus.get(0).isDisplayed();
    }

    /**
     * Shows the overlay menu by clicking the menu button and waits until it is visible, which includes loading a dynamic
     * menu.
     *
     * @throws IllegalStateException if the SplitButton has no menu button
     */
    public void show() {
        if (!isMenuVisible()) {
            WebElement menuButton = getMenuButton();
            if (menuButton == null) {
                throw new IllegalStateException("SplitButton " + getId() + " has no menu button");
            }
            menuButton.click();
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getMenu()));
        }
    }

    /**
     * Hides the overlay menu by clicking the menu button.
     */
    public void hide() {
        if (isMenuVisible()) {
            getMenuButton().click();
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(getMenu()));
        }
    }

    /**
     * Shows the overlay menu and clicks a menu item by its label, guarded if it is AJAXified.
     *
     * @param value the label of the menu item
     * @return the menu item link
     */
    public WebElement selectMenuitemByValue(String value) {
        show();
        WebElement link = getMenu().findElements(By.cssSelector("li.p-menuitem > a.p-menuitem-link")).stream()
                .filter(e -> e.getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("no menuitem with value '" + value + "'"));

        if (isAjaxified(link, "onclick")) {
            PrimeSelenium.guardAjax(link).click();
        }
        else {
            link.click();
        }

        return link;
    }
}
