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
package org.primefaces.integrationtests.selectonemenu;

import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
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

public class SelectOneMenu009Test extends AbstractPrimePageTest {

    private RealDriverService driverService;

    @BeforeEach
    public void setup() {
        driverService = new RealDriverService();
        driverService.init();
    }

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: filter")
    public void testFilter(Page page) {
        List<Driver> drivers = driverService.getDrivers();

        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        Assertions.assertEquals("Lewis", selectOneMenu.getSelectedLabel());
        selectOneMenu.show();
        Assertions.assertEquals("some-filter-placeholder", selectOneMenu.getFilterInput().getAttribute("placeholder"));
        Assertions.assertEquals(drivers.size(), selectOneMenu.getLabels().size() - 1); // -1 because of noSelectionOption

        // Act + Assert - Loop
        List<String> filter = Arrays.asList("L", "asfsd", "Char");
        filter.forEach(f -> {
            // Act
            selectOneMenu.getFilterInput().clear();
            ComponentUtils.sendKeys(selectOneMenu.getFilterInput(),f);

            // Assert
            Assertions.assertEquals(drivers.stream().filter(d -> d.getName().startsWith(f)).collect(Collectors.toList()).size(), selectOneMenu.getLabels().size());
        });

        // Act
        selectOneMenu.getFilterInput().sendKeys(Keys.TAB);
        page.button.click();

        // Assert
        Assertions.assertEquals("Charles", selectOneMenu.getSelectedLabel());
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneMenu Config = " + cfg);
        Assertions.assertTrue(cfg.has("appendTo"));
        Assertions.assertTrue(cfg.getBoolean("autoWidth"));
        Assertions.assertFalse(cfg.getBoolean("dynamic"));
        Assertions.assertTrue(cfg.getBoolean("filter"));
        Assertions.assertEquals("fade", cfg.getString("effect"));
        Assertions.assertEquals("normal", cfg.getString("effectSpeed"));
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
