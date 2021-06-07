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
package org.primefaces.integrationtests.selectbooleanbutton;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectBooleanButton;
import org.primefaces.selenium.component.model.Msg;

public class SelectBooleanButton001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectBooleanButton: Basic test clicking on switch and using Submit button")
    public void testSubmit(Page page) {
        // Arrange
        SelectBooleanButton selectBooleanButton = page.selectBooleanButton;
        Assertions.assertFalse(selectBooleanButton.isSelected());

        // Act
        selectBooleanButton.click();
        page.submit.click();

        // Assert
        assertChecked(page, true);
    }

    @Test
    @Order(2)
    @DisplayName("SelectBooleanButton: Use toggle() widget API to select switch")
    public void testToggleTrue(Page page) {
        // Arrange
        SelectBooleanButton selectBooleanButton = page.selectBooleanButton;
        Assertions.assertFalse(selectBooleanButton.isSelected());

        // Act
        selectBooleanButton.toggle();

        // Assert
        assertChecked(page, true);
    }

    @Test
    @Order(3)
    @DisplayName("SelectBooleanButton: Use toggle() widget API to de-select switch")
    public void testToggleFalse(Page page) {
        // Arrange
        SelectBooleanButton selectBooleanButton = page.selectBooleanButton;
        selectBooleanButton.setValue(true);
        Assertions.assertTrue(selectBooleanButton.isSelected());

        // Act
        selectBooleanButton.toggle();

        // Assert
        assertChecked(page, false);
    }

    @Test
    @Order(4)
    @DisplayName("SelectBooleanButton: Use uncheck() widget API to de-select switch")
    public void testUncheck(Page page) {
        // Arrange
        SelectBooleanButton selectBooleanButton = page.selectBooleanButton;
        selectBooleanButton.setValue(true);
        Assertions.assertTrue(selectBooleanButton.isSelected());

        // Act
        selectBooleanButton.uncheck();

        // Assert
        assertChecked(page, false);
    }

    @Test
    @Order(5)
    @DisplayName("SelectBooleanButton: Use check() widget API to select switch")
    public void testCheck(Page page) {
        // Arrange
        SelectBooleanButton selectBooleanButton = page.selectBooleanButton;
        selectBooleanButton.setValue(false);
        Assertions.assertFalse(selectBooleanButton.isSelected());

        // Act
        selectBooleanButton.check();

        // Assert
        assertChecked(page, true);
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectBooleanButton Config = " + cfg);
        Assertions.assertEquals("Yes", cfg.getString("onLabel"));
        Assertions.assertEquals("No", cfg.getString("offLabel"));
        Assertions.assertEquals("pi pi-check", cfg.getString("onIcon"));
        Assertions.assertEquals("pi pi-times", cfg.getString("offIcon"));
    }

    private void assertChecked(Page page, boolean checked) {
        SelectBooleanButton selectBooleanButton = page.selectBooleanButton;
        Assertions.assertEquals(checked, selectBooleanButton.isSelected());
        Msg message = page.messages.getMessage(0);
        Assertions.assertEquals(checked ? "Checked" : "Unchecked", message.getDetail());
        Assertions.assertEquals(checked ? "Yes" : "No", selectBooleanButton.getLabel());
        assertConfiguration(selectBooleanButton.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectBooleanButton")
        SelectBooleanButton selectBooleanButton;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "selectbooleanbutton/selectBooleanButton001.xhtml";
        }
    }
}
