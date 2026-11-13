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
package org.primefaces.integrationtests.selectonelistbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneListbox;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectOneListbox001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneListbox: select items with AJAX change event")
    void select(Page page) {
        // Arrange
        SelectOneListbox listbox = page.listbox;
        assertNull(listbox.getSelectedLabel());
        assertTrue(listbox.isSelectAjaxified());

        // Act
        listbox.select("Max");

        // Assert
        assertEquals("Max", listbox.getSelectedLabel());
        assertEquals("Max", page.messages.getMessage(0).getDetail());

        // Act
        listbox.select("Lando");

        // Assert
        assertEquals("Lando", listbox.getSelectedLabel());
        assertEquals("Lando", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneListbox: filter with matchMode contains")
    void filter(Page page) {
        // Arrange
        SelectOneListbox listbox = page.listbox;
        assertEquals(Arrays.asList("Lewis", "Max", "Charles", "Lando"), listbox.getVisibleLabels());

        // Act
        listbox.filter("ar");

        // Assert
        assertEquals(Arrays.asList("Charles"), listbox.getVisibleLabels());

        // Act
        listbox.filter("");

        // Assert
        assertEquals(4, listbox.getVisibleLabels().size());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:listbox")
        SelectOneListbox listbox;

        @Override
        public String getLocation() {
            return "selectonelistbox/selectOneListbox001.xhtml";
        }
    }
}
