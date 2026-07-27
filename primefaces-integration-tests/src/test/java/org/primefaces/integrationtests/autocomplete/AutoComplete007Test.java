/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.AutoComplete;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AutoComplete007Test extends AbstractPrimePageTest {

    private static final By PANEL = By.id("form:autocomplete_panel");

    @Test
    @Order(1)
    @DisplayName("AutoComplete: GitHub #15035 removes a loaded dynamic panel when refreshed")
    void refreshLoadedDynamicPanel(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;
        autoComplete.search("test");
        WebElement oldPanel = autoComplete.getPanel();
        assertDisplayed(oldPanel);
        assertEquals(1, getPanels().size());

        // Act
        PrimeSelenium.executeScript(true, "refreshAutocomplete()");

        // Assert
        assertEquals(0, getPanels().size());
        assertEquals(0L, getWidgetPanelLength());
        assertNoJavascriptErrors();

        // Act - query the refreshed widget
        autoComplete.search("new");

        // Assert - the new panel works and can be hidden
        assertEquals(1, getPanels().size());
        assertDisplayed(autoComplete.getPanel());
        assertEquals(List.of("new0", "new1", "new2", "new3", "new4", "new5", "new6", "new7", "new8", "new9"),
                autoComplete.getItemValues());
        autoComplete.hide();
        assertNotDisplayed(PANEL);
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("AutoComplete: refreshes before the dynamic panel is loaded")
    void refreshBeforeDynamicPanelLoaded(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;
        assertEquals(0, getPanels().size());
        assertEquals(0L, getWidgetPanelLength());

        // Act
        PrimeSelenium.executeScript(true, "refreshAutocomplete()");

        // Assert
        assertEquals(0, getPanels().size());
        assertEquals(0L, getWidgetPanelLength());

        // Act - load the panel after refresh
        autoComplete.search("test");

        // Assert
        assertEquals(1, getPanels().size());
        assertDisplayed(autoComplete.getPanel());
        assertEquals(10, autoComplete.getItemValues().size());
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    private List<WebElement> getPanels() {
        return getWebDriver().findElements(PANEL);
    }

    private Long getWidgetPanelLength() {
        return PrimeSelenium.executeScript("return PF('autocomplete').panel.length");
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("AutoComplete Config = " + cfg);
        assertTrue(cfg.getBoolean("dynamic"));
        assertFalse(cfg.getBoolean("cache"));
        assertEquals("server", cfg.getString("queryMode"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:autocomplete")
        AutoComplete autoComplete;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete007.xhtml";
        }
    }
}
