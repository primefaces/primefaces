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

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;

/**
 * Component wrapper for the PrimeFaces {@code p:commandButton}.
 */
public abstract class CommandButton extends AbstractComponent {

    @Override
    public void click() {
        WebElement button = getRoot();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(button));
        PrimeSelenium.waitGui().until(ExpectedConditions.elementToBeClickable(button));

        if (button.getAttribute("data-pfconfirmcommand") != null) {
            // Confirm Dialog/Popup we don't want to guard for AJAX
        }
        else if (isAjaxified("onclick")) {
            button = PrimeSelenium.guardAjax(button);
        }
        else if ("submit".equals(button.getAttribute("type"))) {
            button = PrimeSelenium.guardHttp(button);
        }

        button.click();
    }
}
