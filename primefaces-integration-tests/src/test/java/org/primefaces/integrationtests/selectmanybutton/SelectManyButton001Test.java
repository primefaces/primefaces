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
package org.primefaces.integrationtests.selectmanybutton;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectManyButton;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectManyButton001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectManyButton: select and deselect options with AJAX change event")
    void selectDeselect(Page page) {
        // Arrange
        SelectManyButton button = page.selectManyButton;
        assertEquals(Arrays.asList("Option 1", "Option 2", "Option 3"), button.getOptionLabels());
        assertTrue(button.getSelectedLabels().isEmpty());

        // Act
        button.select("Option 1");
        button.select("Option 3");

        // Assert
        assertEquals(Arrays.asList("Option 1", "Option 3"), button.getSelectedLabels());
        assertEquals("1,3", page.messages.getMessage(0).getDetail());

        // Act
        button.deselect("Option 1");

        // Assert
        assertEquals(Collections.singletonList("Option 3"), button.getSelectedLabels());
        assertEquals("3", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SelectManyButton: disable and enable via widget")
    void disableEnable(Page page) {
        // Arrange
        SelectManyButton button = page.selectManyButton;

        // Act
        button.disable();

        // Assert
        assertFalse(button.isEnabled());

        // Act
        button.enable();
        button.select("Option 2");

        // Assert
        assertTrue(button.isEnabled());
        assertEquals(Collections.singletonList("Option 2"), button.getSelectedLabels());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectManyButton")
        SelectManyButton selectManyButton;

        @Override
        public String getLocation() {
            return "selectmanybutton/selectManyButton001.xhtml";
        }
    }
}
