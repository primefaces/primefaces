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

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:wizard}.
 */
public abstract class Wizard extends AbstractComponent {

    /**
     * Gets the button which navigates to the next step.
     *
     * @return the next button
     */
    public WebElement getNextButton() {
        return findElement(By.id(getId() + "_next"));
    }

    /**
     * Gets the button which navigates to the previous step.
     *
     * @return the back button
     */
    public WebElement getBackButton() {
        return findElement(By.id(getId() + "_back"));
    }

    /**
     * Navigates to the next step and waits until the step is loaded and all animations are complete.
     */
    public void next() {
        PrimeSelenium.guardAjax(getNextButton()).click();
        waitForAnimations();
    }

    /**
     * Navigates to the previous step and waits until the step is loaded and all animations are complete.
     */
    public void back() {
        PrimeSelenium.guardAjax(getBackButton()).click();
        waitForAnimations();
    }

    /**
     * Gets the step titles of the step status bar, empty if {@code showStepStatus} is disabled.
     *
     * @return the step title elements
     */
    public List<WebElement> getStepTitles() {
        return findElements(By.cssSelector(".p-wizard-step-titles li.p-wizard-step-title"));
    }

    /**
     * Gets the id of the current step.
     *
     * @return the id of the current step tab
     */
    public String getCurrentStep() {
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript() + ".currentStep;");
    }

    /**
     * Gets the index of the current step.
     *
     * @return the 0-based index of the current step, -1 if the current step is unknown
     */
    public int getCurrentStepIndex() {
        return ((Number) PrimeSelenium.executeScript("var w = " + getWidgetByIdScript() + "; return w.getStepIndex(w.currentStep);")).intValue();
    }

    /**
     * Waits until the step change effect and the navigation button fades are complete.
     */
    public void waitForAnimations() {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.script(
                "return " + getWidgetByIdScript() + ".jq.find(':animated').length === 0;"));
    }
}
