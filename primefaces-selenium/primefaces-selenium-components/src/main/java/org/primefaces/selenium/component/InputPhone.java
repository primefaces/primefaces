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
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:inputPhone}.
 */
public abstract class InputPhone extends InputText {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindByParentPartialId("_hidden")
    private WebElement hiddenInput;

    @FindByParentPartialId("_country")
    private WebElement countryButton;

    @FindByParentPartialId(value = "_panel", searchFromRoot = true)
    private WebElement panel;

    /**
     * The input with the formatted number.
     *
     * @return the input
     */
    @Override
    public WebElement getInput() {
        return input;
    }

    /**
     * The hidden input with the submitted number (E.164).
     *
     * @return the hidden input
     */
    public WebElement getHiddenInput() {
        return hiddenInput;
    }

    /**
     * The country selector.
     *
     * @return the country selector
     */
    public WebElement getCountryButton() {
        return countryButton;
    }

    /**
     * The panel with the countries.
     *
     * @return the panel
     */
    public WebElement getPanel() {
        return panel;
    }

    /**
     * Is the component using AJAX "countrySelect" event?
     *
     * @return true if using AJAX for countrySelect
     */
    public boolean isCountrySelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "countrySelect");
    }

    /**
     * Gets the submitted number, in the E.164 format if it is valid.
     *
     * @return the number
     */
    public String getWidgetValue() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getValue();");
    }

    /**
     * Gets the selected country.
     *
     * @return the ISO 3166-1 alpha-2 code
     */
    public String getCountry() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".getCountry();");
    }

    /**
     * Whether the number is valid for its country.
     *
     * @return true if the number is valid
     */
    public boolean isValidNumber() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".isValid();");
    }

    /**
     * Shows the panel with the countries.
     */
    public void show() {
        if (isEnabled() && !panel.isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(panel));
        }
    }

    /**
     * Hides the panel with the countries.
     */
    public void hide() {
        if (isEnabled() && panel.isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(panel));
        }
    }

    /**
     * Selects a country in the panel like the user.
     *
     * @param code the ISO 3166-1 alpha-2 code
     */
    public void selectCountry(String code) {
        show();
        WebElement item = panel.findElement(By.id(getId() + "_option_" + code));
        if (isCountrySelectAjaxified()) {
            PrimeSelenium.guardAjax(item).click();
        }
        else {
            item.click();
        }
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(panel));
    }
}
