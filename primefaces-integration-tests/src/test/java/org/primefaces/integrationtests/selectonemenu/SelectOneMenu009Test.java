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
package org.primefaces.integrationtests.selectonemenu;

import org.primefaces.integrationtests.general.model.Driver;
import org.primefaces.integrationtests.general.service.RealDriverService;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectOneMenu009Test extends AbstractPrimePageTest {

    private RealDriverService driverService;

    @BeforeEach
    void setup() {
        driverService = new RealDriverService();
        driverService.init();
    }

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: filter")
    void filter(Page page) {
        List<Driver> drivers = driverService.getDrivers();

        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertNotNull(selectOneMenu.getLabels()); // getLabels() must also work before calling show() the first time
        assertEquals(5, selectOneMenu.getLabels().size());
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());
        selectOneMenu.show();
        assertEquals("some-filter-placeholder", selectOneMenu.getFilterInput().getAttribute("placeholder"));
        assertEquals(drivers.size(), selectOneMenu.getLabels().size() - 1); // -1 because of noSelectionOption

        // Act + Assert - Loop
        List<String> filter = Arrays.asList("L", "asfsd", "Char");
        filter.forEach(f -> {
            // Act
            selectOneMenu.getFilterInput().clear();
            ComponentUtils.sendKeys(selectOneMenu.getFilterInput(), f);

            // Assert
            assertEquals(
                    drivers.stream().filter(d -> d.getName().startsWith(f)).collect(Collectors.toList()).size(),
                    selectOneMenu.getLabels().size());
        });

        // Act
        selectOneMenu.getFilterInput().sendKeys(Keys.TAB);
        page.button.click();

        // Assert
        assertEquals("Charles", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneMenu Config = " + cfg);
        assertTrue(cfg.has("appendTo"));
        assertEquals("auto", cfg.getString("autoWidth"));
        assertFalse(cfg.getBoolean("dynamic"));
        assertTrue(cfg.getBoolean("filter"));
        assertEquals("fade", cfg.getString("effect"));
        assertEquals("normal", cfg.getString("effectSpeed"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu009.xhtml";
        }
    }
}
