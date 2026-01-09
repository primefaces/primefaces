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
package org.primefaces.integrationtests.accordionpanel;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.AccordionPanel;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.model.Tab;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccordionPanel001Test extends AbstractPrimePageTest {

    @Test
    void test(Page page) {
        // Arrange
        AccordionPanel accordionPanel = page.accordionPanel;

        // Assert - part 1
        List<Tab> tabs = accordionPanel.getTabs();
        assertNotNull(tabs);
        assertEquals(3, tabs.size());
        AtomicInteger cnt = new AtomicInteger(0);
        tabs.forEach(tab -> {
            assertNotNull(tab.getHeader());
            assertNotNull(tab.getContent());
            assertEquals(cnt.getAndIncrement(), tab.getIndex());
        });
        assertEquals("Panel1", tabs.get(0).getTitle());
        assertEquals("Panel2", tabs.get(1).getTitle());

        assertNotNull(accordionPanel.getSelectedTabs());
        assertEquals(1, accordionPanel.getSelectedTabs().size());
        assertEquals(0, accordionPanel.getSelectedTabs().get(0).getIndex());
        assertEquals("Panel1", accordionPanel.getSelectedTabs().get(0).getTitle());

        // Act
        accordionPanel.toggleTab(2);

        // Assert - part 2
        assertNoJavascriptErrors();
        assertNotNull(accordionPanel.getSelectedTabs());
        assertEquals(1, accordionPanel.getSelectedTabs().size());
        assertEquals(2, accordionPanel.getSelectedTabs().get(0).getIndex());
        assertEquals("Panel3", accordionPanel.getSelectedTabs().get(0).getTitle());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:accordionpanel")
        AccordionPanel accordionPanel;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "accordionpanel/accordionPanel001.xhtml";
        }
    }
}
