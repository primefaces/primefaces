/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

/**
 * Component wrapper for the PrimeFaces {@code p:autoComplete}.
 */
public abstract class AutoComplete extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindByParentPartialId(value = "_panel", searchFromRoot = true)
    private WebElement panel;

    @Override
    public WebElement getInput() {
        return input;
    }

    public WebElement getItems() {
        return getWebDriver().findElement(By.className("ui-autocomplete-items"));
    }

    public List<String> getItemValues() {
        List<WebElement> itemElements = getItems().findElements(By.className("ui-autocomplete-item"));
        return itemElements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public WebElement getPanel() {
        return panel;
    }

    public String getValue() {
        return getInput().getAttribute("value");
    }

    /**
     * Is the input using AJAX "clear" event?
     *
     * @return true if using AJAX for clear
     */
    public boolean isClearAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "clear");
    }

    /**
     * Is the input using AJAX "itemSelect" event?
     *
     * @return true if using AJAX for itemSelect
     */
    public boolean isItemSelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "itemSelect");
    }

    /**
     * Is the input using AJAX "itemUnselect" event?
     *
     * @return true if using AJAX for itemUnselect
     */
    public boolean isItemUnselectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "itemUnselect");
    }

    /**
     * Is the input using AJAX "query" event?
     *
     * @return true if using AJAX for query
     */
    public boolean isQueryAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "query");
    }

    /**
     * If using multiple mode gets the values of the tokens.
     *
     * @return the values in a list
     */
    public List<String> getValues() {
        List<WebElement> tokens = getTokens();
        return tokens.stream()
                    .map(token -> token.findElement(By.className("ui-autocomplete-token-label")).getText())
                    .collect(Collectors.toList());
    }

    /**
     * Gets the actual token elements in mutliple mode.
     *
     * @return the List of tokens
     */
    public List<WebElement> getTokens() {
        return findElements(By.cssSelector("ul li.ui-autocomplete-token"));
    }

    /**
     * Sets the value and presses tab afterwards. Attention: Pressing tab selects the first suggested value.
     *
     * @param value the value to set
     */
    public void setValue(String value) {
        int delay = setValueWithoutTab(value);
        if (delay > 0) {
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.animationNotActive());
        }
        sendTabKey();
    }

    /**
     * Sets the value without pressing tab afterwards.
     *
     * @param value the value to set
     * @return the delay in milliseconds
     */
    public int setValueWithoutTab(Serializable value) {
        WebElement input = getInput();
        input.clear();
        ComponentUtils.sendKeys(input, value.toString());
        int delay = getDelay();
        PrimeSelenium.wait(delay * 2);
        return delay;
    }

    /**
     * Sends the Tab-Key to jump to the next input. Attention: Pressing tab selects the first suggested value.
     */
    public void sendTabKey() {
        if (isOnchangeAjaxified()) {
            PrimeSelenium.guardAjax(getInput()).sendKeys(Keys.TAB);
        }
        else {
            getInput().sendKeys(Keys.TAB);
        }
    }

    /**
     * Clears the Autocomplete input and guards AJAX for "clear" event.
     */
    @Override
    public void clear() {
        PrimeSelenium.clearInput(getInput(), isClearAjaxified());
    }

    /**
     * Waits until the AutoComplete-Panel containing the suggestions shows up. (eg after typing)
     */
    public void wait4Panel() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(panel));
    }

    /**
     * Shows the AutoComplete-Panel.
     */
    public void show() {
        WebElement panel = getPanel();
        if (isEnabled() && !panel.isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
            wait4Panel();
        }
    }

    /**
     * Hides the AutoComplete-Panel.
     */
    public void hide() {
        WebElement panel = getPanel();
        if (isEnabled() && panel.isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(panel));
        }
    }

    /**
     * Activates search behavior
     */
    public void activate() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".activate();");
    }

    /**
     * Deactivates search behavior
     */
    public void deactivate() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".deactivate();");
    }

    /**
     * Adds an item to the input field. Especially useful in 'multiple' mode.
     *
     * @param item the item to add to the tokens
     */
    public void addItem(String item) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".addItem('" + item + "');");
    }

    /**
     * Removes an item from the input field. Especially useful in 'multiple' mode.
     *
     * @param item the item to remove from the tokens
     */
    public void removeItem(String item) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".removeItem('" + item + "');");
    }

    /**
     * Execute the AutoComplete search.
     *
     * @param value the search to execute
     */
    public void search(String value) {
        // search always uses AJAX no matter what
        PrimeSelenium.executeScript(true, getWidgetByIdScript() + ".search(arguments[0]);", value);
        wait4Panel();
    }

    /**
     * Delay to wait in milliseconds before sending each query to the server.
     *
     * @return Delay in milliseconds.
     */
    public int getDelay() {
        return getWidgetConfiguration().getInt("delay");
    }
}
