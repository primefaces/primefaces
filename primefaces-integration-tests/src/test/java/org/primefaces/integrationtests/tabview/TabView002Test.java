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

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.TabView;
import org.primefaces.selenium.component.model.Tab;

public class TabView002Test extends AbstractPrimePageTest {

    @Test
    @DisplayName("TabView: Test dynamic tabview loading AJAX tabs")
    public void testDynamic(Page page) {
        // Arrange
        TabView tabView = page.tabView;

        // Assert - part 1
        List<Tab> tabs = tabView.getTabs();
        Assertions.assertNotNull(tabs);
        Assertions.assertEquals("Lewis", page.inputtext1.getValue());

        // Act
        tabView.toggleTab(1);

        // Assert - part 2
        assertNoJavascriptErrors();
        Assertions.assertEquals(1, tabView.getSelectedTab().getIndex());
        Assertions.assertEquals("Max", page.inputtext2.getValue());

        // Act
        tabView.toggleTab(2);

        // Assert - part 3
        assertNoJavascriptErrors();
        Assertions.assertEquals(2, tabView.getSelectedTab().getIndex());
        Assertions.assertEquals("Charles", page.inputtext3.getValue());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:tabview")
        TabView tabView;

        @FindBy(id = "form:tabview:inputtext1")
        InputText inputtext1;

        @FindBy(id = "form:tabview:inputtext2")
        InputText inputtext2;

        @FindBy(id = "form:tabview:inputtext3")
        InputText inputtext3;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "tabview/tabView002.xhtml";
        }
    }
}
