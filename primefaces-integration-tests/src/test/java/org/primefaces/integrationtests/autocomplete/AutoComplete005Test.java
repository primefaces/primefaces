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
package org.primefaces.integrationtests.autocomplete;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.integrationtests.general.model.Driver;
import org.primefaces.integrationtests.general.service.RealDriverService;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.AutoComplete;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;

import java.util.List;

public class AutoComplete005Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: Multiple + forceSelection - https://github.com/primefaces/primefaces/issues/7211")
    public void testMultipleForce(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;

        // Act - Ma(x) - allowed
        autoComplete.setValueWithoutTab("Ma");
        autoComplete.wait4Panel();
        autoComplete.getInput().sendKeys(Keys.ENTER);

        // Act - La(ndo) - allowed
        autoComplete.setValueWithoutTab("La");
        autoComplete.wait4Panel();
        autoComplete.getInput().sendKeys(Keys.ENTER);

        // Act - Chr(istoph) - not allowed
        autoComplete.setValueWithoutTab("Chr");
        PrimeSelenium.guardAjax(autoComplete.getInput()).sendKeys(Keys.ENTER);

        RealDriverService realDriverService = new RealDriverService();
        realDriverService.init();
        List<Driver> drivers = realDriverService.getDrivers();
        Driver driverMax = drivers.stream().filter(d -> "Max".equals(d.getName())).findFirst().get();
        Driver driverLando = drivers.stream().filter(d -> "Lando".equals(d.getName())).findFirst().get();

        // Assert - Part 1
        WebElement hInputSelect = autoComplete.getWrappedElement().findElement(By.id(autoComplete.getId() + "_hinput"));
        List<WebElement> options = hInputSelect.findElements(By.cssSelector("option"));
        Assertions.assertEquals(2, options.size());
        Assertions.assertEquals(Integer.toString(driverMax.getId()), options.get(0).getAttribute("value"));
        Assertions.assertEquals(Integer.toString(driverLando.getId()), options.get(1).getAttribute("value"));

        // Act
        page.button.click();

        // Assert - Part 2
        Assertions.assertTrue(page.messages.getMessage(0).getDetail().contains("Max"));
        Assertions.assertTrue(page.messages.getMessage(0).getDetail().contains("Lando"));
        Assertions.assertFalse(page.messages.getMessage(0).getDetail().contains("Chr"));
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("AutoComplete Config = " + cfg);
        Assertions.assertTrue(cfg.has("appendTo"));
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
            return "autocomplete/autoComplete005.xhtml";
        }
    }
}
