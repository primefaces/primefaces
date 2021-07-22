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
package org.primefaces.integrationtests.selectoneradio;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.SelectOneRadio;

public class SelectOneRadio001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneRadio: basic usecase")
    public void testBasic(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadio;
        Assertions.assertEquals(4, selectOneRadio.getItemsSize());
        Assertions.assertEquals("Lewis", selectOneRadio.getSelectedLabel());

        // Act
        selectOneRadio.select("Max");
        page.submit.click();

        // Assert - part 1
        Assertions.assertEquals("Max", selectOneRadio.getSelectedLabel());
        assertConfiguration(selectOneRadio.getWidgetConfiguration());

        // Act
        selectOneRadio.select(3);
        page.submit.click();

        // Assert - part 2
        Assertions.assertEquals("Lando", selectOneRadio.getSelectedLabel());
        assertConfiguration(selectOneRadio.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneRadio: Selecting again remains selected with unselectable='false'")
    public void testNotUnselectable(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadio;
        Assertions.assertEquals(4, selectOneRadio.getItemsSize());
        Assertions.assertEquals("Lewis", selectOneRadio.getSelectedLabel());

        // Act
        selectOneRadio.select("Lewis");
        page.submit.click();

        // Assert
        Assertions.assertEquals("Lewis", selectOneRadio.getSelectedLabel());
        assertConfiguration(selectOneRadio.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneRadio: Disable component using widget API")
    public void testDisable(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadio;
        Assertions.assertEquals("Lewis", selectOneRadio.getSelectedLabel());

        // Act
        selectOneRadio.disable();

        // Assert
        for (WebElement radioButton : selectOneRadio.getRadioButtons()) {
            assertNotClickable(radioButton.findElement(By.className("ui-radiobutton-box")));
        }
        assertConfiguration(selectOneRadio.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneRadio: Enable component using widget API")
    public void testEnable(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadio;
        Assertions.assertEquals("Lewis", selectOneRadio.getSelectedLabel());

        // Act
        selectOneRadio.disable();
        selectOneRadio.enable();

        // Assert
        for (WebElement radioButton : selectOneRadio.getRadioButtons()) {
            assertClickable(radioButton.findElement(By.className("ui-radiobutton-box")));
        }
        assertConfiguration(selectOneRadio.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneRadio: Disable option using widget API")
    public void testDisableOption(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadio;
        Assertions.assertEquals("Lewis", selectOneRadio.getSelectedLabel());
        WebElement radioButton = selectOneRadio.getRadioButtonBox(2);
        assertClickable(radioButton);

        // Act
        selectOneRadio.disableOption(2);

        // Assert
        assertNotClickable(radioButton);
        assertConfiguration(selectOneRadio.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("SelectOneRadio: Enable option using widget API")
    public void testEnableOption(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadio;
        Assertions.assertEquals("Lewis", selectOneRadio.getSelectedLabel());
        WebElement radioButton = selectOneRadio.getRadioButtonBox(2);
        assertClickable(radioButton);

        // Act - disable option
        selectOneRadio.disableOption(2);

        // Assert
        assertNotClickable(radioButton);

        //Act - enable option
        selectOneRadio.enableOption(2);

        // Assert
        assertClickable(radioButton);
        assertConfiguration(selectOneRadio.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneRadio Config = " + cfg);
        Assertions.assertFalse(cfg.getBoolean("unselectable"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectoneradio")
        SelectOneRadio selectOneRadio;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "selectoneradio/selectOneRadio001.xhtml";
        }
    }
}
