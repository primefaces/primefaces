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
package org.primefaces.integrationtests.toggleswitch;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.OutputLabel;
import org.primefaces.selenium.component.ToggleSwitch;
import org.primefaces.selenium.component.model.Msg;

public class ToggleSwitch001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ToggleSwitch: Basic test clicking on switch and using Submit button")
    public void testSubmit(Page page) {
        // Arrange
        ToggleSwitch toggleSwitch = page.toggleSwitch;
        Assertions.assertFalse(toggleSwitch.isSelected());

        // Act
        toggleSwitch.click();
        page.submit.click();

        // Assert
        assertChecked(page, true);
    }

    @Test
    @Order(2)
    @DisplayName("ToggleSwitch: Use toggle() widget API to select switch")
    public void testToggleTrue(Page page) {
        // Arrange
        ToggleSwitch toggleSwitch = page.toggleSwitch;
        Assertions.assertFalse(toggleSwitch.isSelected());

        // Act
        toggleSwitch.toggle();

        // Assert
        assertChecked(page, true);
    }

    @Test
    @Order(3)
    @DisplayName("ToggleSwitch: Use toggle() widget API to de-select switch")
    public void testToggleFalse(Page page) {
        // Arrange
        ToggleSwitch toggleSwitch = page.toggleSwitch;
        toggleSwitch.setValue(true);
        Assertions.assertTrue(toggleSwitch.isSelected());

        // Act
        toggleSwitch.toggle();

        // Assert
        assertChecked(page, false);
    }

    @Test
    @Order(4)
    @DisplayName("ToggleSwitch: Use uncheck() widget API to de-select switch")
    public void testUncheck(Page page) {
        // Arrange
        ToggleSwitch toggleSwitch = page.toggleSwitch;
        toggleSwitch.setValue(true);
        Assertions.assertTrue(toggleSwitch.isSelected());

        // Act
        toggleSwitch.uncheck();

        // Assert
        assertChecked(page, false);
    }

    @Test
    @Order(5)
    @DisplayName("ToggleSwitch: Use check() widget API to select switch")
    public void testCheck(Page page) {
        // Arrange
        ToggleSwitch toggleSwitch = page.toggleSwitch;
        toggleSwitch.setValue(false);
        Assertions.assertFalse(toggleSwitch.isSelected());

        // Act
        toggleSwitch.check();

        // Assert
        assertChecked(page, true);
    }

    @Test
    @Order(5)
    @DisplayName("ToggleSwitch: Click on label should trigger switch")
    public void testLabel(Page page) {
        // Arrange
        ToggleSwitch toggleSwitch = page.toggleSwitch;
        OutputLabel label = page.label;
        toggleSwitch.setValue(false);
        Assertions.assertFalse(toggleSwitch.isSelected());

        // Act
        PrimeSelenium.guardAjax(label).click();

        // Assert
        assertChecked(page, true);

        // Act
        PrimeSelenium.guardAjax(label).click();

        // Assert
        assertChecked(page, false);
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("ToggleSwitch Config = " + cfg);
    }

    private void assertChecked(Page page, boolean checked) {
        Assertions.assertEquals(checked, page.toggleSwitch.isSelected());
        Msg message = page.messages.getMessage(0);
        Assertions.assertEquals(checked ? "Checked" : "Unchecked", message.getDetail());
        assertConfiguration(page.toggleSwitch.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:toggleSwitch")
        ToggleSwitch toggleSwitch;

        @FindBy(id = "form:label")
        OutputLabel label;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "toggleswitch/toggleSwitch001.xhtml";
        }
    }
}
