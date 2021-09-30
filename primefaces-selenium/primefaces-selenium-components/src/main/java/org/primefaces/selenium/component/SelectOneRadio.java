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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.model.SelectItem;

/**
 * Component wrapper for the PrimeFaces {@code p:selectOneRadio}.
 */
public abstract class SelectOneRadio extends AbstractComponent {

    @FindBy(css = ".ui-radiobutton")
    private List<WebElement> radioButtons;

    public List<WebElement> getRadioButtons() {
        return radioButtons;
    }

    public WebElement getRadioButton(int index) {
        WebElement radiobutton = getRadioButtons().get(index);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(radiobutton));
        return radiobutton;
    }

    public WebElement getRadioButtonBox(int index) {
        WebElement radiobutton = getRadioButton(index);
        return radiobutton.findElement(By.className("ui-radiobutton-box"));
    }

    public void select(int index) {
        if (!getWidgetConfiguration().getBoolean("unselectable")) {
            if (getSelectedIndex() == index) {
                return;
            }
        }

        WebElement radiobutton = getRadioButton(index);
        WebElement box = radiobutton.findElement(By.className("ui-radiobutton-box"));
        WebElement input = radiobutton.findElement(By.tagName("input"));
        if (isAjaxified(input, "onchange")) {
            PrimeSelenium.guardAjax(box).click();
        }
        else {
            box.click();
        }
    }

    public void select(String text) {
        int indexToSelect = getLabels().indexOf(text);
        if (indexToSelect >= 0) {
            select(indexToSelect);
        }
    }

    public int getSelectedIndex() {
        for (SelectItem item : getItems()) {
            if (item.isSelected()) {
                return item.getIndex();
            }
        }

        return -1;
    }

    public String getSelectedLabel() {
        for (SelectItem item : getItems()) {
            if (item.isSelected()) {
                return item.getLabel();
            }
        }

        return "";
    }

    public List<String> getLabels() {
        return getItems().stream()
                    .map(SelectItem::getLabel)
                    .collect(Collectors.toList());
    }

    public String getLabel(int index) {
        return getItems().get(index).getLabel();
    }

    public int getItemsSize() {
        return getRadioButtons().size();
    }

    public List<SelectItem> getItems() {
        ArrayList<SelectItem> items = new ArrayList<>();

        int idx = 0;
        for (WebElement radiobutton : getRadioButtons()) {
            WebElement input = radiobutton.findElement(By.tagName("input"));
            WebElement label = getRoot().findElement(By.cssSelector("label[for='" + input.getAttribute("id") + "']"));
            WebElement box = radiobutton.findElement(By.className("ui-radiobutton-box"));

            SelectItem item = new SelectItem();
            item.setIndex(idx);
            item.setLabel(label.getText());
            item.setValue(input.getAttribute("value"));
            item.setSelected(PrimeSelenium.hasCssClass(box, "ui-state-active"));
            items.add(item);

            idx++;
        }

        return items;
    }

    /**
     * Disables the entire component.
     */
    public void disable() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".disable();");
    }

    /**
     * Enables the entire component
     */
    public void enable() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".enable();");
    }

    /**
     * Disables a given radio button option of this widget.
     *
     * @param index Index of the radio button option to disable.
     */
    public void disableOption(int index) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".disable(" + index + ");");
    }

    /**
     * Enables a given radio button option of this widget.
     *
     * @param index Index of the radio button option to enable.
     */
    public void enableOption(int index) {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".enable(" + index + ");");
    }
}
