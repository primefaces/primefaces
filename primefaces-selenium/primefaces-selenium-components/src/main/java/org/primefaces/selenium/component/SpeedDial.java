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
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:speedDial}.
 */
public abstract class SpeedDial extends AbstractComponent {

    @FindBy(className = "p-speeddial-button")
    private WebElement button;

    /**
     * Gets the floating action button that opens and closes the items.
     *
     * @return the button
     */
    public WebElement getButton() {
        return button;
    }

    /**
     * Gets the links of the items.
     *
     * @return the item links
     */
    public List<WebElement> getItems() {
        return findElements(By.cssSelector(".p-speeddial-item > a"));
    }

    /**
     * Are the items open?
     *
     * @return true if open
     */
    public boolean isOpen() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".visible;");
    }

    /**
     * Opens the items by clicking the button, if closed.
     */
    public void open() {
        if (!isOpen()) {
            button.click();
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.animationNotActive());
        }
    }

    /**
     * Closes the items by clicking the button, if open.
     */
    public void close() {
        if (isOpen()) {
            button.click();
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.animationNotActive());
        }
    }
}
