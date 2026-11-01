/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.integrationtests.tristatemanycheckbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TriStateManyCheckbox;

import java.util.List;

import org.json.JSONObject;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

class TriStateManyCheckbox001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("TriStateManyCheckbox: a click cycles through the three states, the Map is submitted")
    void clickThrough(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.checkbox;
        assertEquals(List.of("Item A", "Item B", "Item C"), checkbox.getLabels());
        assertEquals(List.of(1, 0, 0), checkbox.getStates());

        // Act
        checkbox.toggle(1);

        // Assert
        assertEquals(List.of(1, 1, 0), checkbox.getStates());

        // Act
        checkbox.toggle(1);
        checkbox.toggle(0);
        checkbox.toggle(2);
        checkbox.toggle(2);
        checkbox.toggle(2);

        // Assert
        assertEquals(List.of(2, 2, 0), checkbox.getStates());
        assertIcons(checkbox, "p-checkbox-crossed-icon", "p-checkbox-crossed-icon", null);

        // Act
        page.button.click();

        // Assert
        assertTrue(page.msgs.isEmpty());
        assertEquals("{A=2, B=2, C=0}", page.value.getText());
        assertEquals(List.of(2, 2, 0), checkbox.getStates());
        assertIcons(checkbox, "p-checkbox-crossed-icon", "p-checkbox-crossed-icon", null);
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("TriStateManyCheckbox: label click and keyboard move forward and back")
    void keyboard(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.checkbox;

        // Act: the label toggles and focuses the input
        checkbox.getLabelElements().get(2).click();

        // Assert
        assertEquals(1, checkbox.getState(2));
        assertEquals(checkbox.getInputs().get(2), getWebDriver().switchTo().activeElement());
        assertTrue(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(2), "p-state-focus"));

        // Act
        new Actions(getWebDriver()).sendKeys(Keys.ARROW_DOWN).perform();

        // Assert
        assertEquals(0, checkbox.getState(2));

        // Act
        new Actions(getWebDriver()).sendKeys(Keys.ARROW_LEFT).perform();

        // Assert
        assertEquals(2, checkbox.getState(2));

        // Act: Enter moves to the next state instead of submitting the form
        new Actions(getWebDriver()).sendKeys(Keys.ENTER).perform();
        new Actions(getWebDriver()).sendKeys(Keys.SPACE).perform();

        // Assert
        assertEquals(1, checkbox.getState(2));
        assertEquals("{A=1}", page.value.getText());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("TriStateManyCheckbox: widget toggle and setState")
    void widget(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.checkbox;

        // Act
        checkbox.toggleWithWidget(0, -1);
        PrimeSelenium.executeScript(checkbox.getWidgetByIdScript() + ".setState(1, 2);");

        // Assert
        assertEquals(List.of(0, 2, 0), checkbox.getStates());
        List<Object> states = PrimeSelenium.executeScript("return " + checkbox.getWidgetByIdScript() + ".getStates();");
        assertEquals(List.of(0L, 2L, 0L), states);
        assertFalse(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(0), "p-state-active"));
        assertTrue(PrimeSelenium.hasCssClass(checkbox.getBoxes().get(1), "p-state-active"));

        // Act
        page.button.click();

        // Assert
        assertEquals("{A=0, B=2, C=0}", page.value.getText());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("TriStateManyCheckbox: AJAX change event submits the Map, the states survive the update")
    void ajax(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.ajax;
        assertEquals(List.of(0, 0), checkbox.getStates());
        assertEquals("none", checkbox.getBoxes().get(0).getDomAttribute("title"));

        // Act
        checkbox.toggle(0);

        // Assert
        assertEquals("{A=1, B=0}", page.msgs.getMessage(0).getDetail());
        assertEquals(List.of(1, 0), checkbox.getStates());
        assertEquals("yes", checkbox.getBoxes().get(0).getDomAttribute("title"));

        // Act
        checkbox.toggle(0);
        checkbox.toggle(1);

        // Assert
        assertEquals("{A=2, B=1}", page.msgs.getMessage(0).getDetail());
        assertEquals(List.of(2, 1), checkbox.getStates());
        assertEquals("no", checkbox.getBoxes().get(0).getDomAttribute("title"));
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("TriStateManyCheckbox: the converter converts the states")
    void converter(Page page) {
        // Arrange
        TriStateManyCheckbox checkbox = page.converted;
        assertEquals(List.of(2, 0), checkbox.getStates());
        assertEquals("2", page.convertedSum.getText());

        // Act
        checkbox.toggle(1);
        page.button.click();

        // Assert
        assertTrue(page.msgs.isEmpty());
        assertEquals(List.of(2, 1), checkbox.getStates());
        assertEquals("3", page.convertedSum.getText());
        assertConfiguration(checkbox.getWidgetConfiguration());
    }

    private void assertIcons(TriStateManyCheckbox checkbox, String... stateClasses) {
        List<WebElement> boxes = checkbox.getBoxes();
        for (int i = 0; i < stateClasses.length; i++) {
            WebElement icon = boxes.get(i).findElement(By.className("p-chkbox-icon"));
            if (stateClasses[i] == null) {
                assertFalse(PrimeSelenium.hasCssClass(icon, "p-checkbox-checked-icon"), "icon " + i);
                assertFalse(PrimeSelenium.hasCssClass(icon, "p-checkbox-crossed-icon"), "icon " + i);
            }
            else {
                assertTrue(PrimeSelenium.hasCssClass(icon, stateClasses[i]), "icon " + i);
            }
        }
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TriStateManyCheckbox Config = " + cfg);
        assertTrue(cfg.has("id"));
        assertTrue(cfg.has("icons"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages msgs;

        @FindBy(id = "form:checkbox")
        TriStateManyCheckbox checkbox;

        @FindBy(id = "form:value")
        WebElement value;

        @FindBy(id = "form:ajax")
        TriStateManyCheckbox ajax;

        @FindBy(id = "form:converted")
        TriStateManyCheckbox converted;

        @FindBy(id = "form:convertedSum")
        WebElement convertedSum;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "tristatemanycheckbox/triStateManyCheckbox001.xhtml";
        }
    }
}
