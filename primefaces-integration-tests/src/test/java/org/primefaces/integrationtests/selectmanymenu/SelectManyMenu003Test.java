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
package org.primefaces.integrationtests.selectmanymenu;

import org.primefaces.integrationtests.general.model.Driver;
import org.primefaces.integrationtests.general.service.RealDriverService;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectManyMenu;
import org.primefaces.selenium.component.base.ComponentUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectManyMenu003Test extends AbstractSelectManyMenuTest {

    private RealDriverService driverService;

    @BeforeEach
    void setup() {
        driverService = new RealDriverService();
        driverService.init();
    }

    @Test
    @Order(1)
    @DisplayName("SelectManyMenu: filter")
    void filter(Page page) {
        List<Driver> drivers = driverService.getDrivers();

        // Arrange
        SelectManyMenu selectManyMenu = page.selectManyMenu;
        assertSelected(selectManyMenu, Arrays.asList("Max", "Charles"));
        assertEquals(drivers.size(), selectManyMenu.getLabels().size());
        selectManyMenu.deselect("Charles");

        // Act + Assert - Loop
        List<String> filter = Arrays.asList("Char", "asfsd", "Lan");
        filter.forEach(f -> {
            // Act
            selectManyMenu.getFilterInput().clear();
            ComponentUtils.sendKeys(selectManyMenu.getFilterInput(), f);

            // Assert
            assertEquals(
                    drivers.stream().filter(d -> d.getName().startsWith(f)).collect(Collectors.toList()).size(),
                    selectManyMenu.getLabels().size());
        });

        // Act
        selectManyMenu.select("Lando", true);
        page.button.click();

        // Assert
        assertSelected(selectManyMenu, Arrays.asList("Max", "Lando"));
        assertEquals(1, page.messages.getAllMessages().size());
        assertTrue(page.messages.getMessage(0).getSummary().contains("selected drivers"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("Max,Lando"));
        assertConfiguration(selectManyMenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectManyMenu Config = " + cfg);
        assertTrue(cfg.has("id"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectmanymenu")
        SelectManyMenu selectManyMenu;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectmanymenu/selectManyMenu003.xhtml";
        }
    }
}
