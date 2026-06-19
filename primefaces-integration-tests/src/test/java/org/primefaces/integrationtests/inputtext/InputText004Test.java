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
package org.primefaces.integrationtests.inputtext;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputText004Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("InputText: readonly value cannot be changed by the user")
    void readonlyCannotBeChanged(Page page) {
        // Arrange
        InputText readonly = page.readonly;
        assertEquals("cannot change me", readonly.getValue());
        assertEquals("true", readonly.getInput().getDomAttribute("readonly"));

        // Act - typing into a readonly input must not alter its value
        readonly.getInput().sendKeys("hacked");
        page.button.click();

        // Assert
        assertEquals("cannot change me", readonly.getValue());
        assertCss(readonly, "ui-state-readonly");
        assertConfiguration(readonly.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("InputText: placeholder is rendered and field starts empty")
    void placeholderShown(Page page) {
        // Arrange
        InputText placeholder = page.placeholder;

        // Assert
        assertEquals("", placeholder.getValue());
        assertEquals("Type here...", placeholder.getInput().getDomAttribute("placeholder"));
        assertConfiguration(placeholder.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("InputText: passthrough attributes (title, size, autocomplete) are rendered")
    void passthroughAttributesRendered(Page page) {
        // Arrange
        InputText passthrough = page.passthrough;

        // Assert
        assertEquals("My Tooltip", passthrough.getInput().getDomAttribute("title"));
        assertEquals("30", passthrough.getInput().getDomAttribute("size"));
        assertEquals("off", passthrough.getInput().getDomAttribute("autocomplete"));
        assertConfiguration(passthrough.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("InputText Config = " + cfg);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:readonly")
        InputText readonly;

        @FindBy(id = "form:placeholder")
        InputText placeholder;

        @FindBy(id = "form:passthrough")
        InputText passthrough;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "inputtext/inputText004.xhtml";
        }
    }
}
