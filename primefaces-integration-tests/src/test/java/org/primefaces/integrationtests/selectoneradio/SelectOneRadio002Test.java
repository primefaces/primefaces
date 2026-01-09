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
package org.primefaces.integrationtests.selectoneradio;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneRadio;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectOneRadio002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneRadio: Selecting again unselects with unselectable='true'")
    void unselectable(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadio;
        assertEquals(4, selectOneRadio.getItemsSize());
        assertEquals("Charles", selectOneRadio.getSelectedLabel());

        // Act
        selectOneRadio.select("Charles");
        page.submit.click();

        // Assert -- should be empty
        assertEquals("", selectOneRadio.getSelectedLabel());

        // Act
        selectOneRadio.select("Charles");
        page.submit.click();

        // Assert
        assertEquals("Charles", selectOneRadio.getSelectedLabel());
        assertConfiguration(selectOneRadio.getWidgetConfiguration());
    }

    @Test
    @Order(1)
    @DisplayName("SelectOneRadio: AJAX Selecting again unselects with unselectable='true' and fires change")
    void unselectableAjax(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadioAjax;
        assertEquals(4, selectOneRadio.getItemsSize());
        assertEquals("Charles", selectOneRadio.getSelectedLabel());

        // Act
        selectOneRadio.select("Charles");

        // Assert -- should be empty
        assertEquals("", selectOneRadio.getSelectedLabel());
        assertEquals("0", page.messages.getMessage(0).getDetail());
        assertEquals("0", page.messages.getMessage(0).getSummary());

        // Act
        selectOneRadio.select("Charles");

        // Assert
        assertEquals("Charles", selectOneRadio.getSelectedLabel());
        assertEquals("3", page.messages.getMessage(0).getDetail());
        assertEquals("3", page.messages.getMessage(0).getSummary());
        assertConfiguration(selectOneRadio.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneRadio Config = " + cfg);
        assertTrue(cfg.getBoolean("unselectable"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectoneradio")
        SelectOneRadio selectOneRadio;

        @FindBy(id = "formAjax:selectoneradioajax")
        SelectOneRadio selectOneRadioAjax;

        @FindBy(id = "formAjax:msgs")
        Messages messages;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "selectoneradio/selectOneRadio002.xhtml";
        }
    }
}
