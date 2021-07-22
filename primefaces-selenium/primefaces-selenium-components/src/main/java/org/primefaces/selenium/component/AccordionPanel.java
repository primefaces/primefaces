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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.component.model.Tab;

/**
 * Component wrapper for the PrimeFaces {@code p:accordionPanel}.
 */
public abstract class AccordionPanel extends AbstractComponent {

    @FindBy(css = ".ui-accordion-header")
    private List<WebElement> headers;

    @FindBy(css = ".ui-accordion-content")
    private List<WebElement> contents;

    private List<Tab> tabs = null;

    public List<WebElement> getHeaders() {
        return headers;
    }

    public List<WebElement> getContents() {
        return contents;
    }

    /**
     * Gets the accordion tabs.
     *
     * @return the list of tabs
     */
    public List<Tab> getTabs() {
        if (tabs == null) {
            List<Tab> tabs = new ArrayList<>();

            AtomicInteger cnt = new AtomicInteger(0);
            headers.forEach(headerElt -> {
                String title = headerElt.getText();
                int index = cnt.getAndIncrement();
                WebElement content = getContents().get(index);

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
        if (ComponentUtils.hasAjaxBehavior(getRoot(), "tabChange")) {
            PrimeSelenium.guardAjax(getHeaders().get(index)).click();
        }
        else {
            getHeaders().get(index).click();
        }
    }

    /**
     * Provides the selected {@link AccordionPanel} tab(s).
     *
     * @return the selected tab(s)
     */
    public List<Tab> getSelectedTabs() {
        return getTabs().stream()
                    .filter(tab -> tab.getHeader().getAttribute("class").contains("ui-state-active"))
                    .collect(Collectors.toList());
    }
}
