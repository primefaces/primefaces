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
package org.primefaces.integrationtests.tabview;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.SelectBooleanCheckbox;
import org.primefaces.selenium.component.TabView;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabView007Test extends AbstractPrimePageTest {

    @Test
    @DisplayName("TabView: the tab referenced by key in active is selected (dynamic tabs)")
    void activeTabByKey(Page page) {
        // Assert
        assertEquals("tab2", page.tabView.getSelectedTab().getTitle());
        assertNoJavascriptErrors();
    }

    @Test
    @DisplayName("TabView: the tab referenced by key stays selected when a preceding tab is added (dynamic tabs)")
    void activeTabByKeyStaysSelectedWhenTabsChange(Page page) {
        // Act - render an additional tab in front of the active one
        page.toggle.click();

        // Assert - the tab identified by the key is still selected, although its index changed
        assertEquals("tab2", page.tabView.getSelectedTab().getTitle());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:toggle")
        SelectBooleanCheckbox toggle;

        @FindBy(id = "form:tabview")
        TabView tabView;

        @Override
        public String getLocation() {
            return "tabview/tabView007.xhtml";
        }
    }
}
