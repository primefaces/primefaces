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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.findby.FindByParentPartialId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void deselect(String label) {
        if (!isSelected(label)) {
            return;
        }

        toggleSelection(label, true);
    }

    public void select(String label, boolean withMetaKey) {
        if (isSelected(label)) {
            return;
        }

        toggleSelection(label, withMetaKey);
    }

    public void toggleSelection(String label, boolean withMetaKey) {
        if (!isEnabled()) {
            return;
        }

        clickOnListItemWithMetaKey(label, withMetaKey);
    }

    private void clickOnListItemWithMetaKey(String label, boolean withMetaKey) {
        for (WebElement element : getSelectlistbox().findElements(By.tagName("li"))) {
            if (element.getText().equalsIgnoreCase(label)) {
                if (withMetaKey) {
                    Actions actions = new Actions(getWebDriver());
                    actions.keyDown(Keys.META).click(element).keyUp(Keys.META).perform();
                }
                else {
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
                    .map(e -> e.getAttribute("innerHTML"))
                    .collect(Collectors.toList());
        }
        else {
            return getInput().findElements(By.tagName("option")).stream()
                    .map(e -> e.getAttribute("innerHTML"))
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
