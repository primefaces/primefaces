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
 * Component wrapper for the PrimeFaces {@code p:selectManyCheckbox}.
 */
public abstract class SelectManyCheckbox extends AbstractComponent {

    @FindBy(css = ".ui-chkbox")
    private List<WebElement> checkboxes;

    public List<WebElement> getCheckboxes() {
        return checkboxes;
    }

    public void toggle(int... indexes) {
        for (int i : indexes) {
            WebElement checkbox = getCheckboxes().get(i);
            PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(checkbox));

            WebElement input = checkbox.findElement(By.tagName("input"));
            if (isAjaxified(input, "onchange")) {
                PrimeSelenium.guardAjax(checkbox).click();
            }
            else {
                checkbox.click();
            }
        }
    }

    public void toggleAll() {
        for (int i = 0; i < getItemsSize(); i++) {
            toggle(i);
        }
    }

    public void select(int... indexes) {
        deselectAll();

        for (int i : indexes) {
            if (!isSelected(i)) {
                toggle(i);
            }
        }
    }

    public void selectAll() {
        for (int i = 0; i < getItemsSize(); i++) {
            if (!isSelected(i)) {
                toggle(i);
            }
        }
    }

    public void deselect(int... indexes) {
        for (int i : indexes) {
            if (isSelected(i)) {
                toggle(i);
            }
        }
    }

    public void deselectAll() {
        for (int i = 0; i < getItemsSize(); i++) {
            if (isSelected(i)) {
                toggle(i);
            }
        }
    }

    public int getItemsSize() {
        return getCheckboxes().size();
    }

    public List<String> getLabels() {
        return getCheckboxes().stream()
                    .map(WebElement::getText)
                    .collect(Collectors.toList());
    }

    public String getLabel(int index) {
        return getItems().get(index).getLabel();
    }

    public List<SelectItem> getItems() {
        ArrayList<SelectItem> items = new ArrayList<>();

        int idx = 0;
        for (WebElement checkbox : getCheckboxes()) {
            WebElement input = checkbox.findElement(By.tagName("input"));
            WebElement label = getRoot().findElement(By.cssSelector("label[for='" + input.getAttribute("id") + "']"));
            WebElement box = checkbox.findElement(By.className("ui-chkbox-box"));

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

    public boolean isSelected(int index) {
        return getItems().get(index).isSelected();
    }
}
