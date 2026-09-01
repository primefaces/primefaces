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
package org.primefaces.integrationtests.accordionpanel;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.AccordionPanel;
import org.primefaces.selenium.component.model.Tab;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccordionPanel006Test extends AbstractPrimePageTest {

    @Test
    void test(Page page) {
        // Assert all inactive accordions have 0 selected tabs
        assertEquals(0, page.accordionPanel1.getSelectedTabs().size());
        assertEquals(0, page.accordionPanel2.getSelectedTabs().size());
        assertEquals(0, page.accordionPanel3.getSelectedTabs().size());

        // Assert dynamic accordion with active="all"
        List<Tab> selectedTabs = page.accordionPanel4.getSelectedTabs();
        assertEquals(3, selectedTabs.size());

        assertEquals("Dynamic 1", selectedTabs.get(0).getTitle());
        assertEquals("Dummy-Content Dynamic 1", selectedTabs.get(0).getContent().getText().trim());

        assertEquals("Dynamic 2", selectedTabs.get(1).getTitle());
        assertEquals("Dummy-Content Dynamic 2", selectedTabs.get(1).getContent().getText().trim());

        assertEquals("Dynamic 3", selectedTabs.get(2).getTitle());
        assertEquals("Dummy-Content Dynamic 3", selectedTabs.get(2).getContent().getText().trim());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:accordionpanel1")
        AccordionPanel accordionPanel1;

        @FindBy(id = "form:accordionpanel2")
        AccordionPanel accordionPanel2;

        @FindBy(id = "form:accordionpanel3")
        AccordionPanel accordionPanel3;

        @FindBy(id = "form:accordionpanel4")
        AccordionPanel accordionPanel4;

        @Override
        public String getLocation() {
            return "accordionpanel/accordionPanel006.xhtml";
        }
    }

}