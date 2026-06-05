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
package org.primefaces.selenium.component;

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:carousel}.
 */
public abstract class Carousel extends AbstractComponent {

    private static final String PAGE_CHANGE = "pageChange";

    /**
     * Gets the real (non-cloned) carousel items.
     *
     * @return the list of item elements
     */
    public List<WebElement> getItems() {
        return findElements(By.cssSelector(".ui-carousel-item:not(.ui-carousel-item-cloned)"));
    }

    /**
     * Gets the currently visible (active) carousel items, ignoring clones used in circular mode.
     *
     * @return the list of active item elements
     */
    public List<WebElement> getActiveItems() {
        return findElements(By.cssSelector(".ui-carousel-item-active:not(.ui-carousel-item-cloned)"));
    }

    /**
     * Gets the cloned carousel items which only exist in circular mode.
     *
     * @return the list of cloned item elements
     */
    public List<WebElement> getClonedItems() {
        return findElements(By.cssSelector(".ui-carousel-item-cloned"));
    }

    /**
     * Gets the paginator indicators.
     *
     * @return the list of indicator {@code li} elements
     */
    public List<WebElement> getIndicators() {
        return findElements(By.cssSelector("ul.ui-carousel-indicators li.ui-carousel-indicator"));
    }

    /**
     * Gets the 0-based index of the currently displayed page based on the highlighted indicator.
     *
     * @return the current page index, or -1 if no indicator is highlighted
     */
    public int getPage() {
        List<WebElement> indicators = getIndicators();
        for (int i = 0; i < indicators.size(); i++) {
            if (PrimeSelenium.hasCssClass(indicators.get(i), "ui-state-highlight")) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Navigates to the next page by clicking the next button.
     */
    public void next() {
        click(getNextButton());
    }

    /**
     * Navigates to the previous page by clicking the previous button.
     */
    public void previous() {
        click(getPrevButton());
    }

    /**
     * Navigates to the given page by clicking the matching paginator indicator.
     *
     * @param page 0-based index of the page to display
     */
    public void select(int page) {
        click(getIndicators().get(page));
    }

    /**
     * Whether the forward (next) navigation button is disabled.
     *
     * @return true if the next button is disabled
     */
    public boolean isForwardDisabled() {
        return PrimeSelenium.hasCssClass(getNextButton(), "ui-state-disabled");
    }

    /**
     * Whether the backward (previous) navigation button is disabled.
     *
     * @return true if the previous button is disabled
     */
    public boolean isBackwardDisabled() {
        return PrimeSelenium.hasCssClass(getPrevButton(), "ui-state-disabled");
    }

    public WebElement getPrevButton() {
        return findElement(By.cssSelector(".ui-carousel-prev"));
    }

    public WebElement getNextButton() {
        return findElement(By.cssSelector(".ui-carousel-next"));
    }

    /**
     * Stops the autoplay slideshow via the widget API.
     */
    public void stopAutoplay() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".stopAutoplay();");
    }

    private void click(WebElement element) {
        if (ComponentUtils.hasAjaxBehavior(getRoot(), PAGE_CHANGE)) {
            PrimeSelenium.guardAjax(element).click();
        }
        else {
            element.click();
        }
    }
}
