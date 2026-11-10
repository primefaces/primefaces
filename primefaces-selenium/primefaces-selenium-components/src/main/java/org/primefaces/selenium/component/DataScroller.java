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
import org.primefaces.selenium.component.base.AbstractComponent;

import java.util.List;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Component wrapper for the PrimeFaces {@code p:dataScroller}.
 */
public abstract class DataScroller extends AbstractComponent {

    /**
     * Gets the loaded items.
     *
     * @return the items
     */
    public List<WebElement> getItems() {
        return findElements(By.cssSelector(".p-datascroller-list > .p-datascroller-item"));
    }

    /**
     * Gets the total number of items.
     *
     * @return the total size
     */
    public int getTotalSize() {
        return getWidgetConfiguration().getInt("totalSize");
    }

    /**
     * Loads the next chunk: clicks the loader with {@code loader} facet, otherwise scrolls to the end of the content
     * ({@code mode="inline"}) or of the page. Waits for the request, so call it only while items are left.
     */
    public void loadMore() {
        JSONObject cfg = getWidgetConfiguration();
        List<WebElement> loaders = findElements(By.cssSelector(".p-datascroller-loader > *"));
        if ("manual".equals(cfg.optString("loadEvent")) && !loaders.isEmpty()) {
            PrimeSelenium.guardAjax(loaders.get(0)).click();
        }
        else if ("inline".equals(cfg.optString("mode"))) {
            PrimeSelenium.guardAjax("var c = arguments[0].querySelector('.p-datascroller-content'); c.scrollTop = c.scrollHeight;", getRoot());
        }
        else {
            PrimeSelenium.executeScript(true, "window.scrollTo(0, document.body.scrollHeight);");
        }
    }
}
