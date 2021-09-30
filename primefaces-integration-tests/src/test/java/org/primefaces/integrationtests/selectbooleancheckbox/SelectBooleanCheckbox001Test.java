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
package org.primefaces.integrationtests.selectbooleancheckbox;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.SelectBooleanCheckbox;

public class SelectBooleanCheckbox001Test extends AbstractPrimePageTest {

    @Test
    public void testSubmit(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        Assertions.assertFalse(checkbox.getValue());

        // Act
        checkbox.click();
        page.button.click();

        // Assert
        Assertions.assertTrue(checkbox.getValue());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    public void testToggleTrue(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        Assertions.assertFalse(checkbox.getValue());

        // Act
        checkbox.toggle();

        // Assert
        Assertions.assertTrue(checkbox.getValue());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    public void testToggleFalse(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        checkbox.setValue(true);
        Assertions.assertTrue(checkbox.getValue());

        // Act
        checkbox.toggle();

        // Assert
        Assertions.assertFalse(checkbox.getValue());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    public void testUncheck(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        checkbox.setValue(true);
        Assertions.assertTrue(checkbox.getValue());

        // Act
        checkbox.uncheck();

        // Assert
        Assertions.assertFalse(checkbox.getValue());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    public void testCheck(Page page) {
        // Arrange
        SelectBooleanCheckbox checkbox = page.checkbox;
        checkbox.setValue(false);
        Assertions.assertFalse(checkbox.getValue());

        // Act
        checkbox.check();

        // Assert
        Assertions.assertTrue(checkbox.getValue());
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
