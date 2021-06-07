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
package org.primefaces.integrationtests.tabview;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.TabView;
import org.primefaces.selenium.component.model.Tab;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TabView001Test extends AbstractPrimePageTest {

    @Test
    public void test(Page page) {
        // Arrange
        TabView tabView = page.tabView;

        // Assert - part 1
        List<Tab> tabs = tabView.getTabs();
        Assertions.assertNotNull(tabs);
        Assertions.assertEquals(3, tabs.size());
        AtomicInteger cnt = new AtomicInteger(0);
        tabs.forEach(tab -> {
                    Assertions.assertNotNull(tab.getHeader());
                    Assertions.assertNotNull(tab.getContent());
                    Assertions.assertEquals(tab.getIndex(), cnt.getAndIncrement());
                });
        Assertions.assertEquals("Tab1", tabs.get(0).getTitle());
        Assertions.assertEquals("Tab2", tabs.get(1).getTitle());

        Assertions.assertEquals(0, tabView.getSelectedTab().getIndex());
        Assertions.assertEquals("Tab1", tabView.getSelectedTab().getTitle());

        // Act
        tabView.toggleTab(2);

        // Assert - part 2
        assertNoJavascriptErrors();
        Assertions.assertEquals(2, tabView.getSelectedTab().getIndex());
        Assertions.assertEquals("Tab3", tabView.getSelectedTab().getTitle());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:tabview")
        TabView tabView;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "tabview/tabView001.xhtml";
        }
    }
}
