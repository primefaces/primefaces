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

class TriStateManyCheckbox002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("TriStateManyCheckbox: disabled can not be changed and keeps its value")
    void disabled(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.disabled;
        assertEquals(List.of(1, 2), checkbox.getStates());

        // Act
        checkbox.toggleWithWidget(0, 1);

        // Assert
        for (int i = 0; i < 2; i++) {
            assertFalse(checkbox.isItemEnabled(i));
            assertNotClickable(checkbox.getBoxes().get(i));
            assertTrue(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(i), "p-state-disabled"));
            assertTrue(PrimeSelenium.hasCssClass(checkbox.getLabelElements().get(i), "p-state-disabled"));
        }
        assertEquals(List.of(1, 2), checkbox.getStates());

        // Act
        page.button.click();

        // Assert
        assertTrue(page.msgs.isEmpty());
        assertEquals("{A=1, B=2}", page.disabledValue.getText());
        assertEquals(List.of(1, 2), checkbox.getStates());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("TriStateManyCheckbox: readonly can not be changed and keeps its value")
    void readonly(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.readonly;
        assertTrue(checkbox.isReadonly());

        // Act
        checkbox.toggle(0);
        checkbox.getLabelElements().get(1).click();
        checkbox.toggleWithWidget(1, -1);

        // Assert
        assertEquals(List.of(1, 2), checkbox.getStates());
        assertTrue(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(0), "p-chkbox-readonly"));

        // Act
        page.button.click();

        // Assert
        assertTrue(page.msgs.isEmpty());
        assertEquals("{A=1, B=2}", page.readonlyValue.getText());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("TriStateManyCheckbox: a disabled item keeps its state, the others are submitted")
    void itemDisabled(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.itemDisabled;
        assertFalse(checkbox.isItemEnabled(0));
        assertTrue(checkbox.isItemEnabled(1));
        assertNotClickable(checkbox.getBoxes().get(0));

        // Act
        checkbox.toggle(1);
        page.button.click();

        // Assert
        assertTrue(page.msgs.isEmpty());
        assertEquals("{A=1, B=0}", page.itemDisabledValue.getText());
        assertEquals(List.of(1, 0), checkbox.getStates());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("TriStateManyCheckbox: required needs an item that is not in the first state")
    void required(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.required;

        // Act
        page.requiredButton.click();

        // Assert
        assertEquals(1, page.requiredMsgs.getAllMessages().size());
        String summary = page.requiredMsgs.getMessage(0).getSummary();
        assertTrue(summary.contains("Required"), summary);
        assertTrue(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(0), "p-state-error"));
        assertEquals("{}", page.requiredValue.getText());

        // Act
        checkbox.setState(1, 2);
        page.requiredButton.click();

        // Assert
        assertTrue(page.requiredMsgs.isEmpty());
        assertFalse(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(0), "p-state-error"));
        assertEquals("{A=0, B=2}", page.requiredValue.getText());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("TriStateManyCheckbox: widget disable and enable")
    void disableEnable(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.required;

        // Act
        checkbox.disable();
        checkbox.toggleWithWidget(0, 1);

        // Assert
        assertFalse(checkbox.isItemEnabled(0));
        assertTrue(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(0), "p-state-disabled"));
        assertEquals(List.of(0, 0), checkbox.getStates());

        // Act
        checkbox.enable();
        checkbox.toggle(0);

        // Assert
        assertTrue(checkbox.isItemEnabled(0));
        assertFalse(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(0), "p-state-disabled"));
        assertEquals(List.of(1, 0), checkbox.getStates());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("TriStateManyCheckbox: a disabled item stays disabled after enable, a state submitted for it is ignored")
    void itemDisabledManipulated(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.itemDisabled;

        // Act
        checkbox.disable();
        checkbox.enable();

        // Assert
        assertFalse(checkbox.isItemEnabled(0));
        assertTrue(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(0), "p-state-disabled"));
        assertTrue(checkbox.isItemEnabled(1));
        assertFalse(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(1), "p-state-disabled"));

        // Act: enable the disabled item and change its state like a manipulated client
        PrimeSelenium.executeScript("var input = document.getElementById('form:itemDisabled:0'); input.disabled = false; input.value = '0';");
        checkbox.toggle(1);
        page.button.click();

        // Assert
        assertTrue(page.msgs.isEmpty());
        assertEquals("{A=1, B=0}", page.itemDisabledValue.getText());
        assertEquals(List.of(1, 0), checkbox.getStates());
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

        @FindBy(id = "form:disabled")
        TriStateManyCheckbox disabled;

        @FindBy(id = "form:disabledValue")
        WebElement disabledValue;

        @FindBy(id = "form:readonly")
        TriStateManyCheckbox readonly;

        @FindBy(id = "form:readonlyValue")
        WebElement readonlyValue;

        @FindBy(id = "form:itemDisabled")
        TriStateManyCheckbox itemDisabled;

        @FindBy(id = "form:itemDisabledValue")
        WebElement itemDisabledValue;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "requiredForm:msgs")
        Messages requiredMsgs;

        @FindBy(id = "requiredForm:required")
        TriStateManyCheckbox required;

        @FindBy(id = "requiredForm:requiredValue")
        WebElement requiredValue;

        @FindBy(id = "requiredForm:button")
        CommandButton requiredButton;

        @Override
        public String getLocation() {
            return "tristatemanycheckbox/triStateManyCheckbox002.xhtml";
        }
    }
}
