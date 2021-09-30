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

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.component.model.Tab;

/**
 * Component wrapper for the PrimeFaces {@code p:tabView}.
 */
public abstract class TabView extends AbstractComponent {

    @FindBy(css = ".ui-tabs-header")
    private List<WebElement> headers;

    @FindBy(css = ".ui-tabs-panel")
    private List<WebElement> contents;

    private List<Tab> tabs = null;

    public List<Tab> getTabs() {
        if (tabs == null) {
            List<Tab> tabs = new ArrayList<>();

            headers.forEach(headerElt -> {
                String title = headerElt.findElement(By.tagName("a")).getText();
                int index = getIndexOfHeader(headerElt);
                WebElement content = contents.get(index);

                tabs.add(new Tab(title, index, headerElt, content));
            });

            this.tabs = tabs;
        }

        return tabs;
    }

    /**
     * Toggle the tab denoted by the specified index.
     *
     * @param index the index of the tab to expand
     */
    public void toggleTab(int index) {
        final JSONObject cfg = getWidgetConfiguration();
        final boolean isDynamic = cfg.has("dynamic") && cfg.getBoolean("dynamic");

        if (isDynamic || ComponentUtils.hasAjaxBehavior(getRoot(), "tabChange")) {
            PrimeSelenium.guardAjax(headers.get(index)).click();
        }
        else {
            headers.get(index).click();
        }
    }

    /**
     * Provides the selected {@link TabView} tab.
     *
     * @return the selected tab
     */
    public Tab getSelectedTab() {
        WebElement selectedTabHeader = findElement(new By.ByClassName("ui-tabs-selected"));
        int index = getIndexOfHeader(selectedTabHeader);

        return getTabs().get(index);
    }

    private Integer getIndexOfHeader(WebElement headerElt) {
        return Integer.parseInt(headerElt.getAttribute("data-index"));
    }
}
