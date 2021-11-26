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

import java.util.List;

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

public class AutoComplete005Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: Multiple + forceSelection=true - https://github.com/primefaces/primefaces/issues/7211")
    public void testMultipleForce(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autocompleteMultiple;

        // Act forceSelection=true
        page.buttonForceSelection.click();

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
        page.buttonSubmit.click();

        // Assert - Part 2
        Assertions.assertTrue(page.messages.getMessage(0).getDetail().contains("Max"));
        Assertions.assertTrue(page.messages.getMessage(0).getDetail().contains("Lando"));
        Assertions.assertFalse(page.messages.getMessage(0).getDetail().contains("Chr"));
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("AutoComplete: Multiple + forceSelection=false - https://github.com/primefaces/primefaces/issues/8006")
    public void testMultipleWithoutForce(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autocompleteMultiple;

        // Act forceSelection=false
        page.buttonUnforceSelection.click();

        // Act - Ma(x) - allowed
        autoComplete.setValueWithoutTab("Ma");
        autoComplete.wait4Panel();
        autoComplete.getInput().sendKeys(Keys.ENTER);

        // Act - La(ndo) - allowed
        autoComplete.setValueWithoutTab("La");
        autoComplete.wait4Panel();
        autoComplete.getInput().sendKeys(Keys.ENTER);

        // Act - Chr(istoph) - allowed
        autoComplete.setValueWithoutTab("Chr");
        autoComplete.getInput().sendKeys(Keys.ENTER);

        // Assert
        RealDriverService realDriverService = new RealDriverService();
        realDriverService.init();
        List<Driver> drivers = realDriverService.getDrivers();
        Driver driverMax = drivers.stream().filter(d -> "Max".equals(d.getName())).findFirst().get();
        Driver driverLando = drivers.stream().filter(d -> "Lando".equals(d.getName())).findFirst().get();
        
        WebElement hInputSelect = autoComplete.getWrappedElement().findElement(By.id(autoComplete.getId() + "_hinput"));
        List<WebElement> options = hInputSelect.findElements(By.cssSelector("option"));
        Assertions.assertEquals(3, options.size());
        Assertions.assertEquals(Integer.toString(driverMax.getId()), options.get(0).getAttribute("value"));
        Assertions.assertEquals(Integer.toString(driverLando.getId()), options.get(1).getAttribute("value"));
        Assertions.assertEquals("Chr", options.get(2).getAttribute("value"));
        
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }
    
    @Test
    @Order(3)
    @DisplayName("AutoComplete: invalid behaviour for forceSelection=true and autoSelection=false - https://github.com/primefaces/primefaces/issues/7832")
    public void testGitHub7832(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autocompleteSingle;

        // Act forceSelection=true autoSelection=false
        page.buttonForceSelection.click();
        page.buttonUnAutoSelect.click();

        // Act - Max - allowed
        autoComplete.setValueWithoutTab("Max");
        autoComplete.wait4Panel();
        autoComplete.getInput().sendKeys(Keys.ENTER);

        // Act - La - not allowed
        autoComplete.setValueWithoutTab("La");
        autoComplete.wait4Panel();
        page.buttonSubmit.click();

        // Assert        
        Assertions.assertTrue(page.messages.getAllMessages().isEmpty());
        assertConfiguration(autoComplete.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("AutoComplete Config = " + cfg);
        Assertions.assertTrue(cfg.has("appendTo"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:autocompleteMultiple")
        AutoComplete autocompleteMultiple;
        
        @FindBy(id = "form:autocompleteSingle")
        AutoComplete autocompleteSingle;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:btnForce")
        CommandButton buttonForceSelection;

        @FindBy(id = "form:btnUnforce")
        CommandButton buttonUnforceSelection;
        
        @FindBy(id = "form:btnAutoSelect")
        CommandButton buttonAutoSelect;

        @FindBy(id = "form:btnUnAutoSelect")
        CommandButton buttonUnAutoSelect;
        
        @FindBy(id = "form:buttonSubmit")
        CommandButton buttonSubmit;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete005.xhtml";
        }
    }
}
