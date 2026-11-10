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
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:log}. The severities are "info", "warn", "debug" and "error".
 */
public abstract class Log extends AbstractComponent {

    /**
     * Gets the logged items, including the ones hidden by the severity filter.
     *
     * @return the {@code li.p-log-item} elements
     */
    public List<WebElement> getItems() {
        return findElements(By.cssSelector(".p-log-items > li.p-log-item"));
    }

    /**
     * Gets the logged items shown for the current severity filter.
     *
     * @return the visible {@code li.p-log-item} elements
     */
    public List<WebElement> getVisibleItems() {
        return getItems().stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
    }

    /**
     * Gets the messages of the items shown for the current severity filter, without the timestamp prefix.
     *
     * @return the messages in logged order
     */
    public List<String> getVisibleMessages() {
        return getVisibleItems().stream().map(this::getMessage).collect(Collectors.toList());
    }

    /**
     * Gets the message of the item, without the timestamp prefix.
     *
     * @param item the {@code li.p-log-item} element
     * @return the message
     */
    public String getMessage(WebElement item) {
        String text = item.getDomProperty("textContent");
        int separator = text.indexOf(" : ");
        return separator < 0 ? text : text.substring(separator + 3);
    }

    /**
     * Gets the severity of the item.
     *
     * @param item the {@code li.p-log-item} element
     * @return "info", "warn", "debug" or "error"
     */
    public String getSeverity(WebElement item) {
        for (String severity : new String[] {"info", "warn", "debug", "error"}) {
            if (PrimeSelenium.hasCssClass(item, "p-log-item-" + severity)) {
                return severity;
            }
        }
        return null;
    }

    /**
     * Logs a message with the global client side API ({@code PrimeFaces.info(...)} etc.), which writes to this log.
     *
     * @param severity "info", "warn", "debug" or "error"
     * @param message the message
     */
    public void log(String severity, String message) {
        PrimeSelenium.executeScript("PrimeFaces[arguments[0]](arguments[1]);", severity, message);
    }

    /**
     * Shows only the items of the given severity by clicking its header button, "all" shows all items.
     *
     * @param severity "all", "info", "warn", "debug" or "error"
     */
    public void filter(String severity) {
        getFilterButton(severity).click();
    }

    /**
     * Is the header button of the given severity active?
     *
     * @param severity "all", "info", "warn", "debug" or "error"
     * @return true if the items are filtered by this severity
     */
    public boolean isFilterActive(String severity) {
        return PrimeSelenium.hasCssClass(getFilterButton(severity), "p-state-active");
    }

    /**
     * Removes all items by clicking the clear button of the header.
     */
    public void clearItems() {
        findElement(By.cssSelector(".p-log-header > a.p-log-clear")).click();
    }

    /**
     * Shows the log window via the widget API.
     */
    public void show() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(getRoot()));
    }

    /**
     * Hides the log window via the widget API.
     */
    public void hide() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(getRoot()));
    }

    private WebElement getFilterButton(String severity) {
        return findElement(By.cssSelector(".p-log-header > a.p-log-" + severity));
    }
}
