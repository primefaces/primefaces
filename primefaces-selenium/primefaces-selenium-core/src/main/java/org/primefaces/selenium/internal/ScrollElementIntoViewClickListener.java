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
package org.primefaces.selenium.internal;

import org.primefaces.selenium.PrimeSelenium;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

public class ScrollElementIntoViewClickListener implements WebDriverListener {

    private String option;

    public ScrollElementIntoViewClickListener() {

    }

    public ScrollElementIntoViewClickListener(String option) {
        this.option = option;
    }

    @Override
    public void beforeClick(WebElement element) {
        if (!PrimeSelenium.isElementDisplayed(element) || PrimeSelenium.isVisibleInViewport(element)) {
            return;
        }

        // Rely on scrolling through JS
        // this will try to align the top of the element to the top of the window and
        // completely avoid collisions with the navigationbar
        // See:
        // https://developer.mozilla.org/de/docs/Web/API/Element/scrollIntoView
        // https://www.w3.org/TR/webdriver/#dfn-scrolls-into-view
        PrimeSelenium.executeScript("arguments[0].scrollIntoView(" + option + ")", element);
    }
}
