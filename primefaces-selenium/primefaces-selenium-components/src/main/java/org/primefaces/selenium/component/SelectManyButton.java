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

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Component wrapper for the PrimeFaces {@code p:selectManyButton} (default layout).
 */
public abstract class SelectManyButton extends AbstractInputComponent {

    @FindBy(css = ".p-button")
    private List<WebElement> options;

    public List<WebElement> getOptions() {
        return options;
    }

    /**
     * Gets the labels of all options.
     *
     * @return the labels
     */
    public List<String> getOptionLabels() {
        return getOptions().stream().map(this::getLabel).collect(Collectors.toList());
    }

    /**
     * Gets the labels of the selected options.
     *
     * @return the labels
     */
    public List<String> getSelectedLabels() {
        return getOptions().stream().filter(this::isChecked).map(this::getLabel).collect(Collectors.toList());
    }

    /**
     * Is the option with the given label selected?
     *
     * @param label the label of the option
     * @return true if selected
     */
    public boolean isSelected(String label) {
        return isChecked(getOption(label));
    }

    /**
     * Selects the option with the given label, if not selected.
     *
     * @param label the label of the option
     */
    public void select(String label) {
        if (!isSelected(label)) {
            click(getOption(label));
        }
    }

    /**
     * Deselects the option with the given label, if selected.
     *
     * @param label the label of the option
     */
    public void deselect(String label) {
        if (isSelected(label)) {
            click(getOption(label));
        }
    }

    /**
     * Gets the option with the given label.
     *
     * @param label the label of the option
     * @return the option
     */
    public WebElement getOption(String label) {
        return getOptions().stream()
                    .filter(option -> label.equalsIgnoreCase(getLabel(option)))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Option not found: " + label));
    }

    private String getLabel(WebElement option) {
        return option.findElement(By.className("p-button-text")).getText();
    }

    private boolean isChecked(WebElement option) {
        return option.findElement(By.cssSelector("input[type=checkbox]")).isSelected();
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
