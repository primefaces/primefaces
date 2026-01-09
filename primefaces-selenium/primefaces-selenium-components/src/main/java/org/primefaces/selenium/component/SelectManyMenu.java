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

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:selectOneMenu}.
 */
public abstract class SelectManyMenu extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @FindBy(css = ".ui-selectlistbox-listcontainer .ui-selectlistbox-list")
    private WebElement selectlistbox;

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
     * Is the input using AJAX "itemUnselect" event?
     *
     * @return true if using AJAX for itemUnselect
     */
    public boolean isItemUnselectAjaxified() {
        return ComponentUtils.hasAjaxBehavior(getRoot(), "itemUnselect");
    }

    public void deselect(String label) {
        deselect(label, isItemUnselectAjaxified());
    }

    public void deselect(String label, boolean withGuardAjax) {
        if (!isSelected(label)) {
            return;
        }

        toggleSelection(label, true, withGuardAjax);
    }

    public void select(String label, boolean withMetaKey) {
        select(label, withMetaKey, isItemSelectAjaxified());
    }

    public void select(String label, boolean withMetaKey, boolean withGuardAjax) {
        if (isSelected(label)) {
            return;
        }

        toggleSelection(label, withMetaKey, withGuardAjax);
    }

    public void toggleSelection(String label, boolean withMetaKey) {
        toggleSelection(label, withMetaKey, false);
    }

    public void toggleSelection(String label, boolean withMetaKey, boolean withGuardAjax) {
        if (!isEnabled()) {
            return;
        }

        clickOnListItemWithMetaKey(label, withMetaKey, withGuardAjax);
    }

    private void clickOnListItemWithMetaKey(String label, boolean withMetaKey, boolean withGuardAjax) {
        for (WebElement element : getSelectlistbox().findElements(By.tagName("li"))) {
            if (element.getText().equalsIgnoreCase(label)) {
                if (withMetaKey) {
                    Actions actions = new Actions(getWebDriver());
                    Action action = actions.keyDown(Keys.META).click(element).keyUp(Keys.META).build();
                    if (withGuardAjax) {
                        action = PrimeSelenium.guardAjax(action);
                    }
                    action.perform();
                }
                else {
                    if (withGuardAjax) {
                        element = PrimeSelenium.guardAjax(element);
                    }
                    element.click();
                }
                break;
            }
        }
    }

    public boolean isSelected(String label) {
        try {
            for (WebElement element : getSelectlistbox().findElements(By.tagName("li"))) {
                if (element.getText().equalsIgnoreCase(label)) {
                    if (PrimeSelenium.hasCssClass(element, "ui-state-highlight")) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            // do nothing
        }
        return false;
    }

    public List<String> getLabels() {
        JSONObject widgetConfiguration = getWidgetConfiguration();

        if (widgetConfiguration.has("filter") && widgetConfiguration.getBoolean("filter")) {
            return getSelectlistbox().findElements(By.cssSelector("li.ui-selectlistbox-item")).stream()
                    .filter(listElt -> listElt.isDisplayed())
                    .map(e -> e.getDomProperty("innerHTML"))
                    .collect(Collectors.toList());
        }
        else {
            return getInput().findElements(By.tagName("option")).stream()
                    .map(e -> e.getDomProperty("innerHTML"))
                    .collect(Collectors.toList());
        }
    }

    public boolean isSelected(int index) {
        return isSelected(getLabel(index));
    }

    public String getLabel(int index) {
        return getLabels().get(index);
    }

    public List<String> getSelectedLabels() {
        List<String> selectedLabels = new ArrayList<>();

        for (WebElement element : getSelectlistbox().findElements(By.tagName("li"))) {
            if (PrimeSelenium.hasCssClass(element, "ui-state-highlight")) {
                selectedLabels.add(element.getText());
            }
        }

        return selectedLabels;
    }

    @Override
    public WebElement getInput() {
        return input;
    }

    public WebElement getSelectlistbox() {
        return selectlistbox;
    }

    public WebElement getFilterInput() {
        return filterInput;
    }
}
