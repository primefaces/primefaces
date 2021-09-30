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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;

/**
 * Component wrapper for the PrimeFaces {@code p:selectOneButton}.
 */
public abstract class SelectOneButton extends AbstractInputComponent {

    @FindBy(css = ".ui-button")
    private List<WebElement> options;

    @FindBy(css = ".ui-button.ui-state-active")
    private WebElement activeOption;

    public List<WebElement> getOptions() {
        return options;
    }

    public WebElement getActiveOption() {
        return activeOption;
    }

    public List<String> getOptionLabels() {
        List<String> result = new ArrayList<>();
        getOptions().forEach((element) -> result.add(element.getText()));

        return result;
    }

    public String getSelectedLabel() {
        return getActiveOption().getText();
    }

    public boolean isSelected(String label) {
        return getSelectedLabel().equalsIgnoreCase(label);
    }

    public boolean isSelected(int index) {
        return index == getOptions().indexOf(getActiveOption());
    }

    public void selectNext() {
        int activeIndex = getOptions().indexOf(getActiveOption());
        int nextIndex = activeIndex + 1;

        if (nextIndex >= getOptions().size()) {
            nextIndex = 0;
        }

        select(nextIndex);
    }

    public void select(String label) {
        if (isSelected(label)) {
            return;
        }

        for (WebElement element : getOptions()) {
            if (element.getText().equalsIgnoreCase(label)) {
                click(element);
                return;
            }
        }
    }

    public void select(int index) {
        if (index > getOptions().size()) {
            throw new IndexOutOfBoundsException("Index " + index + ", Size " + getOptions().size());
        }

        if (isSelected(index)) {
            return;
        }

        click(getOptions().get(index));
    }

    public void selectFirst() {
        select(0);
    }

    public void selectLast() {
        select(getOptions().size() - 1);
    }

    public void deselect(String label) {
        deselect(label, false);
    }

    public void deselect(String label, boolean ignoreDeselectable) {
        if (!ignoreDeselectable && !isUnselectable()) {
            return;
        }

        if (!isSelected(label)) {
            return;
        }

        for (WebElement element : getOptions()) {
            if (element.getText().equalsIgnoreCase(label)) {
                click(element);
                return;
            }
        }
    }

    public void deselect(int index) {
        deselect(index, false);
    }

    public void deselect(int index, boolean ignoreDeselectable) {
        if (index > getOptions().size()) {
            throw new IndexOutOfBoundsException("Index " + index + ", Size " + getOptions().size());
        }

        if (!ignoreDeselectable && !isUnselectable()) {
            return;
        }

        if (!isSelected(index)) {
            return;
        }

        click(getOptions().get(index));
    }

    public boolean isUnselectable() {
        return "true".equals(PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".cfg.unselectable"));
    }

    protected void click(WebElement element) {
        if (isOnchangeAjaxified()) {
            PrimeSelenium.guardAjax(element).click();
        }
        else {
            element.click();
        }
    }
}
