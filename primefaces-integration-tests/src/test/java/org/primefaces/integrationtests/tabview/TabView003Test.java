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
package org.primefaces.integrationtests.tabview;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.SelectOneButton;
import org.primefaces.selenium.component.TabView;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TabView003Test extends AbstractPrimePageTest {

    @Test
    @DisplayName("TabView: Test dynamic & repeating tabs should only be processed when loaded")
    void dynamic(Page page) {
        assertEquals("0", page.setterCalled.getValue());

        // Only tab 0 loaded, setter should be called once
        page.button.click();
        assertEquals("1", page.setterCalled.getValue());

        // tab 1 will be loaded now, setter should be called for tab 0+1
        page.tabView.toggleTab(1);
        page.button.click();
        assertEquals("3", page.setterCalled.getValue());

        // tab 2 will be loaded now, setter should be called for tab 0+1+2
        page.tabView.toggleTab(2);
        page.button.click();
        assertEquals("6", page.setterCalled.getValue());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:tabview")
        TabView tabView;

        @FindBy(id = "form:tabview:0:input")
        InputText input1;

        @FindBy(id = "form:tabview:1:input")
        InputText input2;

        @FindBy(id = "form:tabview:0:sob")
        SelectOneButton sob1;

        @FindBy(id = "form:tabview:1:sob")
        SelectOneButton sob2;

        @FindBy(id = "form:setterCalled")
        InputText setterCalled;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "tabview/tabView003.xhtml";
        }
    }
}
