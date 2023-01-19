/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

/**
 * Component wrapper for the PrimeFaces {@code p:panel}.
 */
public abstract class Panel extends AbstractComponent {

    @FindByParentPartialId("_toggler")
    private WebElement toggler;

    @FindBy(className = "ui-panel-content")
    private WebElement content;

    public void toggle() {
        if (ComponentUtils.hasAjaxBehavior(getRoot(), "toggle")) {
            PrimeSelenium.guardAjax(toggler).click();
        }
        else {
            toggler.click();
        }

        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(content));
    }

    public WebElement getToggler() {
        return toggler;
    }

    public WebElement getContent() {
        return content;
    }

}
