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
package org.primefaces.selenium.component.html;

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the Faces {@code h:selectOneMenu}.
 */
public abstract class SelectOneMenu extends AbstractInputComponent {

    public void deselect(String label) {
        if (!isSelected(label) || !isEnabled()) {
            return;
        }

        WebElement option = getOptions().stream()
                .filter(e -> Objects.equals(e.getDomProperty("innerHTML"), label) && e.isSelected())
                .findFirst()
                .orElse(null);
        if (option != null) {
            if (isAjaxified("onchange")) {
                option = PrimeSelenium.guardAjax(option);
            }
            option.click();
        }
    }

    public void select(String label) {
        if (isSelected(label) || !isEnabled()) {
            return;
        }

        WebElement option = getOptions().stream()
                .filter(e -> Objects.equals(e.getDomProperty("innerHTML"), label) && !e.isSelected())
                .findFirst()
                .orElse(null);
        if (option != null) {
            if (isAjaxified("onchange")) {
                option = PrimeSelenium.guardAjax(option);
            }
            option.click();
        }
    }

    public String getSelectedLabel() {
        WebElement option = getOptions().stream()
                .filter(e -> e.isSelected())
                .findFirst()
                .orElse(null);
        return option == null ? null : option.getDomProperty("innerHTML");
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
     * All labels.
     * @return
     */
    public List<String> getLabels() {
        return getOptions().stream()
                .map(e -> e.getDomProperty("innerHTML"))
                .collect(Collectors.toList());
    }

    public List<WebElement> getOptions() {
        return getRoot().findElements(By.tagName("option"));
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

    public boolean isSelected(int index) {
        return getLabel(index).equals(getSelectedLabel());
    }

    public String getLabel(int index) {
        return getLabels().get(index);
    }

    @Override
    public WebElement getInput() {
        return getRoot();
    }
}
