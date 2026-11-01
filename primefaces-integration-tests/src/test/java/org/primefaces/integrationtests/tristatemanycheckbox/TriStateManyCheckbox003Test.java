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
package org.primefaces.integrationtests.tristatemanycheckbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TriStateManyCheckbox;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriStateManyCheckbox003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("TriStateManyCheckbox: the validator gets each state, the submitted states are rendered after a failed validation")
    void validator(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.checkbox;
        assertEquals(List.of(1, 1), checkbox.getStates());

        // Act
        checkbox.setState(1, 0);
        page.button.click();

        // Assert
        assertEquals(1, page.msgs.getAllMessages().size());
        assertEquals("State 0 is not allowed", page.msgs.getMessage(0).getSummary());
        assertTrue(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(1), "p-state-error"));
        assertEquals(List.of(1, 0), checkbox.getStates());
        assertEquals("{A=1, B=1}", page.value.getText());

        // Act
        checkbox.setState(1, 2);
        page.button.click();

        // Assert
        assertTrue(page.msgs.isEmpty());
        assertFalse(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(1), "p-state-error"));
        assertEquals(List.of(1, 2), checkbox.getStates());
        assertEquals("{A=1, B=2}", page.value.getText());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TriStateManyCheckbox Config = " + cfg);
        assertTrue(cfg.has("id"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages msgs;

        @FindBy(id = "form:checkbox")
        TriStateManyCheckbox checkbox;

        @FindBy(id = "form:value")
        WebElement value;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "tristatemanycheckbox/triStateManyCheckbox003.xhtml";
        }
    }
}
