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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:commandButton}.
 */
public abstract class CommandButton extends AbstractComponent {

    @Override
    public void click() {
        WebElement button = getRoot();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(button));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(button));

        if (button.getDomAttribute("data-pfconfirmcommand") != null) {
            // Confirm Dialog/Popup we don't want to guard for AJAX
        }
        else if (isAjaxified("onclick")) {
            button = PrimeSelenium.guardAjax(button);
        }
        else if ("submit".equals(button.getDomAttribute("type"))) {
            button = PrimeSelenium.guardHttp(button);
        }

        button.click();
    }

    /**
     * #8840 Some scenario's with ajax="false" like in a download you may not want to guard the click.
     */
    public void clickUnguarded() {
        WebElement button = getRoot();
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(button));
        button.click();
    }

    /**
     * Is the inline AJAX loading state shown, i.e. a request of this button is running or the minimum loading animation
     * ({@code PrimeFaces.ajax.minLoadAnimation}) is not over yet.
     *
     * @return true if loading
     */
    public boolean isLoading() {
        return PrimeSelenium.hasCssClass(getRoot(), "p-state-loading");
    }

    /**
     * Gets the icon element of this button.
     *
     * @return the icon {@link WebElement} ({@code .p-button-icon-left} or {@code .p-button-icon-right}), {@code null} if the button has no icon
     */
    public WebElement getIcon() {
        List<WebElement> icons = findElements(By.cssSelector(".p-button-icon-left, .p-button-icon-right"));
        return icons.isEmpty() ? null : icons.get(0);
    }

    /**
     * Gets the label element of this button which holds the text ({@code .p-button-text}).
     *
     * @return the label {@link WebElement}
     */
    public WebElement getLabelElement() {
        return findElement(By.className("p-button-text"));
    }
}
