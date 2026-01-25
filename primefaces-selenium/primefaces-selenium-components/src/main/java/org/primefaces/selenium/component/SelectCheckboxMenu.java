/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code selectCheckboxMenu }.
 */
public abstract class SelectCheckboxMenu extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindByParentPartialId(value = "_panel", searchFromRoot = true)
    private WebElement panel;

    @FindByParentPartialId(value = "_filter", searchFromRoot = true)
    private WebElement filterInput;

    @FindBy(css = ".ui-selectcheckboxmenu-label")
    private WebElement label;

    @FindBy(css = "input[type='checkbox']")
    private List<WebElement> checkboxes;

    @FindBy(css = "input[type='checkbox']:checked")
    private List<WebElement> selectedCheckboxes;

    public WebElement getFilterInput() {
        return filterInput;
    }

    public WebElement getPanel() {
        return panel;
    }

    public WebElement getLabel() {
        return label;
    }

    public List<WebElement> getCheckboxes() {
        return checkboxes;
    }

    public List<WebElement> getSelectedCheckboxes() {
        return selectedCheckboxes;
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
     * Bring up the overlay panel if it's not showing or hide it if it is showing.
     */
    public void togglePanel() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".togglePanel();");
    }

    /**
     * Shows the SelectOneMenu panel.
     */
    public void show() {
        WebElement panel = getPanel();
        if (isEnabled() && !panel.isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(panel));
        }
    }

    /**
     * Hides the SelectOneMenu panel.
     */
    public void hide() {
        WebElement panel = getPanel();
        if (isEnabled() && panel.isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(panel));
        }
    }

    /**
     * Selects all available options.
     */
    public void checkAll() {
        if (isEnabled()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".checkAll();");
        }
    }

    /**
     * Unselects all available options.
     */
    public void uncheckAll() {
        if (isEnabled()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".uncheckAll();");
        }
    }

    /**
     * Resets the input.
     */
    public void resetValue() {
        if (isEnabled()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".resetValue();");
        }
    }

    /**
     * Checks the checkbox option with the given value.
     *
     * @param value the value to check
     */
    public void selectValue(String value) {
        if (isEnabled()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".selectValue('" + value + "');");
        }
    }

}
