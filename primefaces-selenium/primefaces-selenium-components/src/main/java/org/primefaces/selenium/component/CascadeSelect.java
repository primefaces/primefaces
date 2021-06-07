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

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

/**
 * Component wrapper for the PrimeFaces {@code p:cascadeSelect}.
 */
public abstract class CascadeSelect extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindByParentPartialId(value = "_panel", searchFromRoot = true)
    private WebElement panel;

    /**
     * Is the input using AJAX "itemSelect" event?
     *
     * @return true if using AJAX for itemSelect
     */
    public boolean isItemSelectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "itemSelect");
    }

    /**
     * Either display the dropdown or select the item it if is already displayed.
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
     * Shows the CascadeSelect panel.
     */
    public void show() {
        WebElement panel = getPanel();
        if (isEnabled() && !panel.isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".show();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(panel));
        }
    }

    /**
     * Hides the CascadeSelect panel.
     */
    public void hide() {
        WebElement panel = getPanel();
        if (isEnabled() && panel.isDisplayed()) {
            PrimeSelenium.executeScript(getWidgetByIdScript() + ".hide();");
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(panel));
        }
    }

    public void select(String label) {
        if (isSelected(label) || !isEnabled()) {
            return;
        }

        if (!getPanel().isDisplayed()) {
            toggleDropdown();
        }

        boolean isLeaf = false;
        for (WebElement element : getItems()) {
            if (element.getAttribute("data-label").equalsIgnoreCase(label)) {
                isLeaf = !PrimeSelenium.hasCssClass(element, "ui-cascadeselect-item-group");
                click(element);
                break;
            }
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

    public List<String> getLabels() {
        return getItems().stream()
                    .map(e -> e.getAttribute("data-label"))
                    .collect(Collectors.toList());
    }

    public List<String> getValues() {
        return getItems().stream()
                    .map(e -> e.getAttribute("data-value"))
                    .collect(Collectors.toList());
    }

    @Override
    public WebElement getInput() {
        return input;
    }

    public WebElement getLabel() {
        return findElement(By.className("ui-cascadeselect-label"));
    }

    public List<WebElement> getItems() {
        return getPanel().findElements(By.className("ui-cascadeselect-item"));
    }

    public List<WebElement> getLeafItems() {
        return getItems().stream().filter(i -> !PrimeSelenium.hasCssClass(i, "ui-cascadeselect-item-group")).collect(Collectors.toList());
    }

    public WebElement getPanel() {
        return panel;
    }

    protected void click(WebElement element) {
        if (!PrimeSelenium.hasCssClass(element, "ui-cascadeselect-item-group") && (isOnchangeAjaxified() || isItemSelectAjaxified())) {
            PrimeSelenium.guardAjax(element).click();
        }
        else {
            element.click();
        }
    }
}
