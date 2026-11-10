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
package org.primefaces.integrationtests.multiselectlistbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.MultiSelectListbox;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class MultiSelectListbox001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("MultiSelectListbox: select an item through its groups, change event via AJAX")
    void select(Page page) {
        // Arrange
        MultiSelectListbox listbox = page.listbox;
        assertEquals(1, listbox.getLists().size());
        assertTrue(listbox.isChangeAjaxified());

        // Act
        listbox.select("Europe", "Germany", "Berlin");

        // Assert
        assertEquals(3, listbox.getLists().size());
        assertEquals(Arrays.asList("Europe", "Germany", "Berlin"), listbox.getSelectedPath());
        assertEquals("Berlin", listbox.getValue());
        assertEquals("Berlin", page.messages.getMessage(0).getDetail());

        // Act - another root item closes the group lists
        listbox.select("Other");

        // Assert
        assertEquals(1, listbox.getLists().size());
        assertEquals("Other", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("MultiSelectListbox: the value from the server shows its groups")
    void preselect(Page page) {
        // Assert
        MultiSelectListbox listbox = page.preselected;
        assertEquals(3, listbox.getLists().size());
        assertEquals(Arrays.asList("Europe", "Germany", "Munich"), listbox.getSelectedPath());
        assertEquals("Munich", listbox.getValue());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("MultiSelectListbox: disable removes the group lists, enable binds the items again")
    void disableEnable(Page page) {
        // Arrange
        MultiSelectListbox listbox = page.preselected;

        // Act
        listbox.disable();

        // Assert
        assertEquals(1, listbox.getLists().size());

        // Act
        listbox.enable();

        // Assert - the lists of the selected groups are shown again
        assertEquals(3, listbox.getLists().size());
        assertEquals(Arrays.asList("Europe", "Germany", "Munich"), listbox.getSelectedPath());

        // Act
        listbox.select("Europe", "Austria", "Vienna");

        // Assert
        assertEquals("Vienna", listbox.getValue());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:listbox")
        MultiSelectListbox listbox;

        @FindBy(id = "form:preselected")
        MultiSelectListbox preselected;

        @Override
        public String getLocation() {
            return "multiselectlistbox/multiSelectListbox001.xhtml";
        }
    }
}
