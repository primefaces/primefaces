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
package org.primefaces.integrationtests.picklist;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.PickList;

import java.util.Collections;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class PickList002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("PickList: GitHub #12059 prevent DOM tampering of form submission to submit a target value not in source list")
    void preventDomTampering(final Page page) {
        // Arrange
        assertEquals(Collections.emptyList(), page.pickListCities.getTargetListLabels());
        assertEquals(0, page.messages.getAllMessages().size());

        // Act
        page.tamperButton.clickUnguarded();
        page.submit.click();

        // Assert
        assertEquals("form:pickListCities: An error occurred when processing your submitted information.", page.messages.getMessage(0).getSummary());
        assertEquals("[]", page.output.getText());
        assertConfiguration(page.pickListCities.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("PickList: GitHub #12059 submit selection twice to ensure its allowing if value is in either source/target list")
    void preventDomTamperingSubmitTwice(final Page page) {
        // Arrange
        assertFalse(page.pickListCities.getSourceListElements().isEmpty());

        // Act
        page.pickListCities.pickAll();
        page.submit.click();
        page.submit.click();

        // Assert
        assertEquals(0, page.messages.getAllMessages().size());
        assertEquals("[San Francisco, London, Istanbul, Berlin, Barcelona, Rome]", page.output.getText());
        assertConfiguration(page.pickListCities.getWidgetConfiguration());
    }


    @Test
    @Order(3)
    @DisplayName("PickList: GitHub #2127 prevent client-side manipulation of disabled entries, when CSS style-class ui-state-disabled is removed.")
    void preventDisabledTargetValue(final Page page) {
        // Arrange
        final String[] valuesToPick = new String[] {"Paris"};
        assertEquals(Collections.emptyList(), page.pickListCities.getTargetListLabels());
        assertEquals(0, page.messages.getAllMessages().size());

        // Act
        page.disableButton.clickUnguarded();
        page.pickListCities.pick(valuesToPick);
        page.submit.click();

        // Assert
        assertEquals("form:pickListCities: An error occurred when processing your submitted information.", page.messages.getMessage(0).getSummary());
        assertEquals("[]", page.output.getText());
        assertConfiguration(page.pickListCities.getWidgetConfiguration());
    }

    private JSONObject assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Picklist Config = " + cfg);
        assertTrue(cfg.has("id"));
        assertTrue(cfg.getBoolean("transferOnDblclick"));
        return cfg;
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:messages")
        Messages messages;

        @FindBy(id = "form:pickListCities")
        PickList pickListCities;

        @FindBy(id = "form:btnTamper")
        CommandButton tamperButton;

        @FindBy(id = "form:btnDisabled")
        CommandButton disableButton;

        @FindBy(id = "form:btnSubmit")
        CommandButton submit;

        @FindBy(id = "form:output")
        WebElement output;

        @Override
        public String getLocation() {
            return "picklist/picklist002.xhtml";
        }
    }

}
