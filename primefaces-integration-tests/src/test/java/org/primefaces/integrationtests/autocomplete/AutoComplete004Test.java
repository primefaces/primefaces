/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.integrationtests.autocomplete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.AutoComplete;

class AutoComplete004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: GitHub #6711 AJAX change event in CSP and non CSP mode")
    void ajaxChange(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;
        assertEquals("", autoComplete.getValue());
        assertNotDisplayed(autoComplete.getPanel());

        // Act
        autoComplete.setValue("Nintendo");

        // Assert
        assertEquals("Nintendo", page.output.getText());
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("AutoComplete: AJAX clear event when clearing out the input")
    void ajaxClear(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;
        assertEquals("", autoComplete.getValue());
        assertNotDisplayed(autoComplete.getPanel());

        // Act
        autoComplete.setValue("PlayStation");
        autoComplete.clear();

        // Assert
        assertEquals("", page.output.getText());
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("AutoComplete Config = " + cfg);
        assertTrue(cfg.has("appendTo"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:autocomplete")
        AutoComplete autoComplete;

        @FindBy(id = "form:output")
        WebElement output;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete004.xhtml";
        }
    }
}
