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

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Component wrapper for the PrimeFaces {@code p:selectOneMenu}.
 */
public abstract class SelectOneMenu extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindByParentPartialId(value = "_panel", searchFromRoot = true)
    private WebElement panel;

    @FindByParentPartialId(value = "_filter", searchFromRoot = true)
    private WebElement filterInput;

    /**
     * Is the input using AJAX "itemSelect" event?
     *
     * @return true if using AJAX for itemSelect
     */
    public boolean isItemSelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "itemSelect");
    }

    /**
     * Either display the dropdown or hide it if is already displayed.
     */
    public void toggleDropdown() {
        if (getPanel().isDisplayed()) {
            hide();
        }
        else {
            show();
        }
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

    public void deselect(String label) {
        if (!isSelected(label) || !isEnabled()) {
            return;
        }

        if (!getPanel().isDisplayed()) {
            toggleDropdown();
        }

        for (WebElement element : getItems().findElements(By.tagName("li"))) {
            if (element.getText().equalsIgnoreCase(label)) {
                click(element);
                break;
            }
        }

        if (getPanel().isDisplayed()) {
            toggleDropdown();
        }
    }

    public void select(String label) {
        if (isSelected(label) || !isEnabled()) {
            return;
        }

        if (!getPanel().isDisplayed()) {
            toggleDropdown();
        }

        for (WebElement element : getItems().findElements(By.tagName("li"))) {
            if (element.getText().equalsIgnoreCase(label)) {
                click(element);
                break;
            }
        }

        if (getPanel().isDisplayed()) {
            toggleDropdown();
        }
    }

    public String getSelectedLabel() {
        return getLabel().getText();
    }

    public boolean isSelected(String label) {
        boolean result = false;
        try {
            result = getSelectedLabel().equalsIgnoreCase(label);
        }
        catch (Exception e) {
            // do nothing
        }
        return result;
    }

    /**
     * All labels independent of filter.
     * @return
     */
    public List<String> getLabels() {
        JSONObject widgetConfiguration = getWidgetConfiguration();

        if (widgetConfiguration.has("filter") && widgetConfiguration.getBoolean("filter")) {
            return getItems().findElements(By.cssSelector("li.ui-selectonemenu-item")).stream()
                    .filter(listElt -> listElt.isDisplayed())
                    .map(e -> e.getAttribute("innerHTML"))
                    .collect(Collectors.toList());
        }
        else {
            return getInput().findElements(By.tagName("option")).stream()
                    .map(e -> e.getAttribute("innerHTML"))
                    .collect(Collectors.toList());
        }
    }

    public void select(int index) {
        if (isSelected(index)) {
            return;
        }

        select(getLabel(index));
    }

    public void deselect(int index) {
        if (!isSelected(index)) {
            return;
        }

        deselect(getLabel(index));
    }

    public void selectByValue(String value) {
        PrimeSelenium.executeScript(String.format("PrimeFaces.getWidgetById('%s').selectValue('%s');", getId(), value));
    }

    public boolean isSelected(int index) {
        return getLabel(index).equals(getSelectedLabel());
    }

    public String getLabel(int index) {
        return getLabels().get(index);
    }

    @Override
    public WebElement getInput() {
        return input;
    }

    public WebElement getEditableInput() {
        return getRoot().findElement(By.name(getId() + "_editableInput"));
    }

    public WebElement getLabel() {
        return getRoot().findElement(By.id(getId() + "_label"));
    }

    public WebElement getItems() {
        return getWebDriver().findElement(By.id(getId() + "_items"));
    }

    public WebElement getPanel() {
        return panel;
    }

    protected void click(WebElement element) {
        if (isOnchangeAjaxified() || isItemSelectAjaxified()) {
            PrimeSelenium.guardAjax(element).click();
        }
        else {
            element.click();
        }
    }

    public WebElement getFilterInput() {
        return filterInput;
    }
}
