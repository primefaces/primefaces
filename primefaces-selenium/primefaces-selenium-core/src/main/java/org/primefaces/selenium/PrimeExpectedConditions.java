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
package org.primefaces.selenium;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

public final class PrimeExpectedConditions {

    private PrimeExpectedConditions() {
        super();
    }

    public static ExpectedCondition<Boolean> documentLoaded() {
        return driver -> (Boolean) ((JavascriptExecutor) driver).executeScript("return document.readyState === 'complete'");
    }

    public static ExpectedCondition<Boolean> animationNotActive() {
        return driver -> (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return ((!window.jQuery || jQuery.active == 0) && (!window.PrimeFaces || PrimeFaces.animationActive === false));");
    }

    public static ExpectedCondition<Boolean> ajaxQueueEmpty() {
        return driver -> (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return (!window.PrimeFaces || PrimeFaces.ajax.Queue.isEmpty());");
    }

    public static ExpectedCondition<Boolean> elementToBeClickable(WebElement element) {
        return ExpectedConditions.and(ExpectedConditions.elementToBeClickable(element));
    }

    public static ExpectedCondition<Boolean> visibleAndAnimationComplete(WebElement element) {
        return ExpectedConditions.and(
                    documentLoaded(),
                    animationNotActive(),
                    ajaxQueueEmpty(),
                    ExpectedConditions.visibilityOf(element));
    }

    public static ExpectedCondition<Boolean> invisibleAndAnimationComplete(WebElement element) {
        return ExpectedConditions.and(
                    documentLoaded(),
                    animationNotActive(),
                    ajaxQueueEmpty(),
                    ExpectedConditions.invisibilityOf(element));
    }

    public static ExpectedCondition<Boolean> visibleInViewport(WebElement element) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return (Boolean) PrimeSelenium.executeScript(
                            "var elem = arguments[0],"
                                        + "    box = elem.getBoundingClientRect(),"
                                        + "    cx = box.left + box.width / 2,"
                                        + "    cy = box.top + box.height / 2,"
                                        + "    e = document.elementFromPoint(cx, cy);"
                                        + "for (; e; e = e.parentElement) {"
                                        + "    if (e === elem) { return true; }"
                                        + "}"
                                        + "return false;",
                            element);
            }

            @Override
            public String toString() {
                return "is " + element + " visible in viewport";
            }
        };
    }

}
