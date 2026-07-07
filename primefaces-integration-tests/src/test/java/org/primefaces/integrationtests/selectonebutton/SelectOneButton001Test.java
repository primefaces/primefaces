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
package org.primefaces.integrationtests.selectonebutton;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.OutputLabel;
import org.primefaces.selenium.component.SelectOneButton;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectOneButton001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneButton: basic select via AJAX")
    void select(Page page) {
        // Arrange
        SelectOneButton selectOneButton = page.selectOneButton;

        // Act
        selectOneButton.select("Max");

        // Assert
        assertEquals("Max", selectOneButton.getSelectedLabel());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneButton: outputLabel must point at the first native radio, like selectOneRadio")
    void labelPointsToFirstRadio(Page page) {
        // Arrange
        OutputLabel label = page.outputLabel;

        // Act and Assert
        assertEquals("label", label.getTagName());
        assertEquals("form:selectonebutton:0", label.getDomAttribute("for"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneButton: root element must have role=radiogroup")
    void rootHasRadiogroupRole(Page page) {
        // Arrange
        SelectOneButton selectOneButton = page.selectOneButton;

        // Act and Assert
        assertEquals("radiogroup", selectOneButton.getDomAttribute("role"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneButton: radiogroup must be named by the outputLabel via aria-labelledby")
    void ariaLabelledByPointsToOutputLabel(Page page) {
        // Arrange
        SelectOneButton selectOneButton = page.selectOneButton;

        // Act and Assert
        assertEquals("form:outputlabel", selectOneButton.getDomAttribute("aria-labelledby"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneButton: without outputLabel the 'label' attribute must name the radiogroup via aria-label")
    void ariaLabelFallback(Page page) {
        // Arrange
        SelectOneButton selectOneButton = page.labelled;

        // Act and Assert
        assertEquals("Standalone Label", selectOneButton.getDomAttribute("aria-label"));
        assertNull(selectOneButton.getDomAttribute("aria-labelledby"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("SelectOneButton: native radios carry the accessibility - not aria-hidden, each with its own label element")
    void radiosAreNativeAndLabelled(Page page) {
        // Arrange
        SelectOneButton selectOneButton = page.selectOneButton;

        // Act
        List<WebElement> ariaRadios = selectOneButton.findElements(By.cssSelector("[role=radio]"));
        List<WebElement> inputs = selectOneButton.findElements(By.cssSelector("input[type=radio]"));

        // Assert
        assertTrue(ariaRadios.isEmpty());
        assertFalse(inputs.isEmpty());
        for (WebElement input : inputs) {
            assertNull(input.getDomAttribute("aria-hidden"));
            // group-level aria-labelledby on the input would override its item label element
            assertNull(input.getDomAttribute("aria-labelledby"));
            WebElement label = selectOneButton.findElement(By.cssSelector("label[for='" + input.getDomAttribute("id") + "']"));
            assertNotNull(label);
            assertFalse(label.getText().isEmpty());
        }
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("SelectOneButton: widget must track the native radios of the default layout")
    void widgetTracksHiddenRadios(Page page) {
        // Arrange
        SelectOneButton selectOneButton = page.selectOneButton;

        // Act
        Long inputCount = PrimeSelenium.executeScript("return " + selectOneButton.getWidgetByIdScript() + ".inputs.length;");

        // Assert
        assertEquals(4L, inputCount);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("SelectOneButton: clicking an item label must select the option and sync the button state")
    void selectViaItemLabelClick(Page page) {
        // Arrange
        SelectOneButton selectOneButton = page.selectOneButton;
        WebElement charlesLabel = selectOneButton.findElement(By.cssSelector("label[for='form:selectonebutton:2']"));

        // Act
        PrimeSelenium.guardAjax(charlesLabel).click();

        // Assert
        assertTrue(selectOneButton.isSelected("Charles"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(9)
    @DisplayName("SelectOneButton: arrow keys must move the selection between the native radios")
    void arrowKeySelectsNext(Page page) {
        // Arrange - use the non-AJAX button so no guard is needed
        SelectOneButton selectOneButton = page.labelled;
        assertTrue(selectOneButton.isSelected("Lewis"));
        PrimeSelenium.executeScript("document.getElementById('form:labelled:0').focus();");

        // Act
        new Actions(PrimeSelenium.getWebDriver()).sendKeys(Keys.ARROW_RIGHT).perform();

        // Assert - visual state and the actually checked radio must both move
        assertTrue(selectOneButton.isSelected("Max"));
        assertEquals("form:labelled:1",
                PrimeSelenium.executeScript("return document.querySelector(\"#form\\\\:labelled input:checked\").id;"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:outputlabel")
        OutputLabel outputLabel;

        @FindBy(id = "form:selectonebutton")
        SelectOneButton selectOneButton;

        @FindBy(id = "form:labelled")
        SelectOneButton labelled;

        @Override
        public String getLocation() {
            return "selectonebutton/selectOneButton001.xhtml";
        }
    }
}
