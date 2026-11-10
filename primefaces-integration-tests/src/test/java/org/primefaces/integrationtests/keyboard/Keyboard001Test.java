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
package org.primefaces.integrationtests.keyboard;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Keyboard;

import java.util.Arrays;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Keyboard001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Keyboard: keypadOnly shows the numeric keypad, typing and backspace via the keys")
    void keypadOnly(Page page) {
        // Arrange
        Keyboard keyboard = page.pin;
        assertTrue(keyboard.isKeypadOnly());
        assertEquals("true", keyboard.getInput().getDomProperty("readOnly"));
        assertFalse(keyboard.isKeypadVisible());

        // Act
        keyboard.showKeypad();

        // Assert
        assertTrue(keyboard.isKeypadVisible());
        assertEquals(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"), keyboard.getKeyLabels());
        assertTrue(keyboard.getSpecialKey("close").isDisplayed());
        assertTrue(keyboard.getSpecialKey("clear").isDisplayed());
        assertTrue(keyboard.getSpecialKey("back").isDisplayed());

        // Act
        keyboard.typeWithKeypad("123");
        keyboard.pressSpecialKey("back");

        // Assert
        assertEquals("12", keyboard.getValue());

        // Act
        keyboard.pressSpecialKey("close");
        page.submit.click();

        // Assert
        assertFalse(keyboard.isKeypadVisible());
        assertEquals("12", page.pinOutput.getText());
        assertEquals("12", keyboard.getValue());
        JSONObject cfg = keyboard.getWidgetConfiguration();
        assertConfiguration(cfg);
        assertFalse(cfg.has("keypadOnly"));
        assertFalse(cfg.has("layoutName"));
    }

    @Test
    @Order(2)
    @DisplayName("Keyboard: qwertyBasic layout with shift, clear and a custom close label")
    void qwertyBasic(Page page) {
        // Arrange
        Keyboard keyboard = page.text;
        assertFalse(keyboard.isKeypadOnly());
        assertNotEquals("true", keyboard.getInput().getDomProperty("readOnly"));

        // Act
        keyboard.showKeypad();
        keyboard.typeWithKeypad("pf");
        keyboard.pressSpecialKey("shift");
        keyboard.pressKey("A");

        // Assert
        assertEquals("pfA", keyboard.getValue());
        assertTrue(keyboard.getKeyLabels().contains("Q"));
        assertFalse(keyboard.getKeyLabels().contains("q"));
        assertEquals("Done", keyboard.getSpecialKey("close").getText());

        // Act
        keyboard.pressSpecialKey("shift");
        keyboard.pressSpecialKey("clear");

        // Assert
        assertEquals("", keyboard.getValue());
        assertEquals("q", keyboard.getKeyLabels().get(0));

        // Act
        keyboard.typeWithKeypad("ok");
        keyboard.pressSpecialKey("close");
        page.submit.click();

        // Assert
        assertFalse(keyboard.isKeypadVisible());
        assertEquals("ok", page.textOutput.getText());
        JSONObject cfg = keyboard.getWidgetConfiguration();
        assertConfiguration(cfg);
        assertFalse(cfg.getBoolean("keypadOnly"));
        assertEquals("qwertyBasic", cfg.getString("layoutName"));
        assertEquals("Done", cfg.getString("closeText"));
    }

    @Test
    @Order(3)
    @DisplayName("Keyboard: custom layout template")
    void customLayout(Page page) {
        // Arrange
        Keyboard keyboard = page.custom;

        // Act
        keyboard.showKeypad();

        // Assert
        assertEquals(Arrays.asList("p", "r", "i", "m", "e", "f", "a", "c", "e", "s", "r", "o", "c", "k", "s"), keyboard.getKeyLabels());
        assertTrue(keyboard.getSpecialKey("back").isDisplayed());
        assertTrue(keyboard.getSpecialKey("clear").isDisplayed());
        assertTrue(keyboard.getSpecialKey("close").isDisplayed());

        // Act
        keyboard.typeWithKeypad("faces");
        keyboard.pressSpecialKey("back");
        keyboard.hideKeypad();

        // Assert
        assertFalse(keyboard.isKeypadVisible());
        assertEquals("face", keyboard.getValue());

        // Act - shown again
        keyboard.showKeypad();

        // Assert
        assertTrue(keyboard.isKeypadVisible());
        assertEquals("p", keyboard.getKeyLabels().get(0));

        // Act
        keyboard.hideKeypad();
        page.submit.click();

        // Assert
        assertEquals("face", page.customOutput.getText());
        JSONObject cfg = keyboard.getWidgetConfiguration();
        assertConfiguration(cfg);
        assertEquals("prime-back,faces-clear,rocks-close", cfg.getString("layoutTemplate"));
        // numeric effectDuration must reach jQuery as number, a string is taken as unknown speed name (400ms)
        assertEquals(200, cfg.get("duration"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("focus", cfg.getString("showOn"));
        assertEquals("fadeIn", cfg.getString("showAnim"));
        assertTrue(cfg.getBoolean("useThemeRoller"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:submit")
        CommandButton submit;

        @FindBy(id = "form:pin")
        Keyboard pin;

        @FindBy(id = "form:pinOutput")
        WebElement pinOutput;

        @FindBy(id = "form:text")
        Keyboard text;

        @FindBy(id = "form:textOutput")
        WebElement textOutput;

        @FindBy(id = "form:custom")
        Keyboard custom;

        @FindBy(id = "form:customOutput")
        WebElement customOutput;

        @Override
        public String getLocation() {
            return "keyboard/keyboard001.xhtml";
        }
    }
}
