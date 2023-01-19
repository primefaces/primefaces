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

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.AutoComplete;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;

public class AutoComplete006Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: GitHub #8725 dropdown=true, querymode=server dynamic=true")
    public void testSuggestions(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;
        Assertions.assertEquals("Driver No. 4", autoComplete.getValue());

        // Act - Part 1
        autoComplete.setValueWithoutTab("1");
        autoComplete.wait4Panel();

        // Assert - Part 1
        assertDisplayed(autoComplete.getPanel());
        Assertions.assertNotNull(autoComplete.getItems());
        List<String> itemValues = autoComplete.getItemValues();
        Assertions.assertEquals(14, itemValues.size());
        Assertions.assertEquals("Driver No. 1", itemValues.get(0));
        Assertions.assertEquals("Driver No. 10", itemValues.get(1));
        Assertions.assertEquals("Driver No. 19", itemValues.get(10));
        Assertions.assertEquals("Driver No. 41", itemValues.get(13));

        // Act - Part 2
        autoComplete.hide();
        autoComplete.setValueWithoutTab("15");
        autoComplete.wait4Panel();

        // Assert - Part 2
        assertDisplayed(autoComplete.getPanel());
        Assertions.assertNotNull(autoComplete.getItems());
        itemValues = autoComplete.getItemValues();
        Assertions.assertEquals(1, itemValues.size());
        Assertions.assertEquals("Driver No. 15", itemValues.get(0));

        // Act - Part 3
        WebElement dropdownButton = autoComplete.getDropDownButton();
        assertDisplayed(dropdownButton);
        dropdownButton.click();

        // Act - Part 4
        autoComplete.wait4Panel();
        autoComplete.sendTabKey();
        page.button.click();

        // Assert - Part 4
        Assertions.assertEquals("Driver No. 0", autoComplete.getValue());
        assertConfiguration(autoComplete.getWidgetConfiguration());

        Msg message = page.messages.getMessage(0);
        Assertions.assertNotNull(message);
        Assertions.assertEquals("Driver", message.getSummary());
        Assertions.assertEquals("id: 0, name: Driver No. 0", message.getDetail());

        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("AutoComplete Config = " + cfg);
        Assertions.assertTrue(cfg.has("appendTo"));
        Assertions.assertTrue(cfg.getBoolean("dynamic"));
        Assertions.assertFalse(cfg.getBoolean("cache"));
        Assertions.assertEquals("blank", cfg.get("dropdownMode"));
        Assertions.assertEquals("server", cfg.get("queryMode"));
        Assertions.assertTrue(cfg.has("moreText"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:autocomplete")
        AutoComplete autoComplete;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete006.xhtml";
        }
    }
}
