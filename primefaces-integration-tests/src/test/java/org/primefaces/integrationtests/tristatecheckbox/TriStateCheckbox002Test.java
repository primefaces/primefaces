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
package org.primefaces.integrationtests.tristatecheckbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.TriStateCheckbox;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriStateCheckbox002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("TriStateCheckbox: using String constants 'true', 'false' and 'null'")
    void clickThrough(Page page) {
        // Arrange
        TriStateCheckbox triStateConstantNull = page.triStateConstantNull;
        assertEquals(null, triStateConstantNull.getValue());

        TriStateCheckbox triStateConstantTrue = page.triStateConstantTrue;
        assertEquals(Boolean.TRUE, triStateConstantTrue.getValue());

        TriStateCheckbox triStateConstantFalse = page.triStateConstantFalse;
        assertEquals(Boolean.FALSE, triStateConstantFalse.getValue());

        // Act
        page.button.click();

        // Assert
        assertEquals(null, triStateConstantNull.getValue());
        assertEquals(Boolean.TRUE, triStateConstantTrue.getValue());
        assertEquals(Boolean.FALSE, triStateConstantFalse.getValue());
        assertConfiguration(triStateConstantNull.getWidgetConfiguration());
        assertConfiguration(triStateConstantTrue.getWidgetConfiguration());
        assertConfiguration(triStateConstantFalse.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TriStateCheckbox Config = " + cfg);
        assertTrue(cfg.has("id"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:triStateConstantNull")
        TriStateCheckbox triStateConstantNull;

        @FindBy(id = "form:triStateConstantTrue")
        TriStateCheckbox triStateConstantTrue;

        @FindBy(id = "form:triStateConstantFalse")
        TriStateCheckbox triStateConstantFalse;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "tristatecheckbox/triStateCheckbox002.xhtml";
        }
    }
}
