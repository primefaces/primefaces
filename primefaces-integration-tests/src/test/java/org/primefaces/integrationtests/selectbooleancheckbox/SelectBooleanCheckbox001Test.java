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
package org.primefaces.integrationtests.selectbooleancheckbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.SelectBooleanCheckbox;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectBooleanCheckbox001Test extends AbstractPrimePageTest {

    @Test
    void submit(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        assertFalse(checkbox.getValue());

        // Act
        checkbox.click();
        page.button.click();

        // Assert
        assertTrue(checkbox.getValue());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    void toggleTrue(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        assertFalse(checkbox.getValue());

        // Act
        checkbox.toggle();

        // Assert
        assertTrue(checkbox.getValue());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    void toggleFalse(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        checkbox.setValue(true);
        assertTrue(checkbox.getValue());

        // Act
        checkbox.toggle();

        // Assert
        assertFalse(checkbox.getValue());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    void uncheck(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        checkbox.setValue(true);
        assertTrue(checkbox.getValue());

        // Act
        checkbox.uncheck();

        // Assert
        assertFalse(checkbox.getValue());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    void check(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        checkbox.setValue(false);
        assertFalse(checkbox.getValue());

        // Act
        checkbox.check();

        // Assert
        assertTrue(checkbox.getValue());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectBooleanCheckbox Config = " + cfg);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:checkbox")
        SelectBooleanCheckbox checkbox;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectbooleancheckbox/selectBooleanCheckBox001.xhtml";
        }
    }
}
