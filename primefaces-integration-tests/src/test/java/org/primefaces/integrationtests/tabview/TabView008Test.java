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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.TabView;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabView008Test extends AbstractPrimePageTest {

    @Test
    @DisplayName("TabView: the deprecated activeIndex is read from and written back to an int property")
    void activeIndexBinding(Page page) {
        // Assert - initial value of the bean
        assertEquals("Tab 2", page.activeIndexBinding.getSelectedTab().getTitle());

        // Act
        page.activeIndexBindingTab3.click();
        page.submit.click();

        // Assert
        assertEquals("2", page.activeIndexValue.getText());
        assertEquals("Tab 3", page.activeIndexBinding.getSelectedTab().getTitle());
        assertNoJavascriptErrors();
    }

    @Test
    @DisplayName("TabView: active bound to an int property gets the index of the tab, even if the tab defines a key")
    void activeBindingToInt(Page page) {
        // Assert - initial value of the bean
        assertEquals("Tab 2", page.activeBinding.getSelectedTab().getTitle());

        // Act
        page.activeBindingTab3.click();
        page.submit.click();

        // Assert
        assertEquals("2", page.activeValue.getText());
        assertEquals("Tab 3", page.activeBinding.getSelectedTab().getTitle());
        assertNoJavascriptErrors();
    }

    @Test
    @DisplayName("TabView: setActiveIndex from Java takes precedence over the active tab submitted by the client")
    void setActiveIndexAfterPostback(Page page) {
        // Arrange - the selected tab is submitted with the action below
        page.programmaticTab2.click();

        // Act - the listener calls setActiveIndex(2)
        page.selectLast.click();

        // Assert
        assertEquals("Tab 3", page.programmatic.getSelectedTab().getTitle());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:activeIndexBinding")
        TabView activeIndexBinding;

        @FindBy(css = "[id='form:activeIndexBinding'] .p-tabs-header[data-index='2'] > a")
        WebElement activeIndexBindingTab3;

        @FindBy(id = "form:activeIndexValue")
        WebElement activeIndexValue;

        @FindBy(id = "form:activeBinding")
        TabView activeBinding;

        @FindBy(css = "[id='form:activeBinding'] .p-tabs-header[data-key='tab3'] > a")
        WebElement activeBindingTab3;

        @FindBy(id = "form:activeValue")
        WebElement activeValue;

        @FindBy(id = "form:programmatic")
        TabView programmatic;

        @FindBy(css = "[id='form:programmatic'] .p-tabs-header[data-index='1'] > a")
        WebElement programmaticTab2;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @FindBy(id = "form:selectLast")
        CommandButton selectLast;

        @Override
        public String getLocation() {
            return "tabview/tabView008.xhtml";
        }
    }
}
