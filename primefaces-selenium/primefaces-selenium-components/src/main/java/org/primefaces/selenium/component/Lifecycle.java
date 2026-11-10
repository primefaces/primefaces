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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Component wrapper for the PrimeFaces {@code p:lifecycle}. It shows the duration of each Faces phase of the last request
 * and refreshes itself with an own AJAX request after each AJAX request. Reading waits until that refresh is done.
 */
public abstract class Lifecycle extends AbstractComponent {

    /**
     * Name of the column with the duration of the whole request.
     */
    public static final String ALL = "ALL";

    /**
     * Gets the names of the phases, the last one is {@link #ALL}.
     *
     * @return the phase names like {@code RESTORE_VIEW 1} (depends on the Faces implementation)
     */
    public List<String> getPhaseNames() {
        waitUntilIdle();
        return findElements(By.className("p-lifecycle-name")).stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /**
     * Gets the duration of a phase.
     *
     * @param phase the start of the phase name, like {@code INVOKE_APPLICATION} or {@link #ALL}
     * @return the duration in milliseconds
     */
    public long getDuration(String phase) {
        String text = getResult(phase).getText();
        return Long.parseLong(text.substring(0, text.length() - "ms".length()));
    }

    /**
     * Gets the score of a phase which rates its duration.
     *
     * @param phase the start of the phase name, like {@code INVOKE_APPLICATION} or {@link #ALL}
     * @return 100 (fast), 66, 33 or 0 (slow)
     */
    public int getScore(String phase) {
        for (String styleClass : getResult(phase).getDomAttribute("class").split(" ")) {
            if (styleClass.startsWith("p-lifecycle-score-")) {
                return Integer.parseInt(styleClass.substring("p-lifecycle-score-".length()));
            }
        }
        throw new NoSuchElementException("No score for phase " + phase);
    }

    /**
     * Runs an action which sends an AJAX request and waits until the lifecycle has refreshed itself with the phase
     * durations of that request.
     *
     * @param action the action, like a guarded click on an AJAX button
     */
    public void waitForUpdate(Runnable action) {
        waitUntilIdle();
        WebElement table = getRoot();

        action.run();

        // the refresh request replaces the table
        PrimeSelenium.waitAjax().until(ExpectedConditions.stalenessOf(table));
        waitUntilIdle();
    }

    /**
     * Waits until no AJAX request is running and the lifecycle is not refreshing itself.
     */
    private void waitUntilIdle() {
        PrimeSelenium.waitAjax().until(ExpectedConditions.and(
                    PrimeExpectedConditions.ajaxQueueEmpty(),
                    PrimeExpectedConditions.script("return " + getWidgetByIdScript() + ".updating === false;")));
    }

    private WebElement getResult(String phase) {
        waitUntilIdle();
        for (WebElement cell : findElements(By.tagName("td"))) {
            if (cell.findElement(By.className("p-lifecycle-name")).getText().startsWith(phase)) {
                return cell.findElement(By.className("p-lifecycle-result"));
            }
        }
        throw new NoSuchElementException("Phase not found: " + phase);
    }
}
