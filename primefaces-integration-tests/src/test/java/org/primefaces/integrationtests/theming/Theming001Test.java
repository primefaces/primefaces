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
package org.primefaces.integrationtests.theming;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Theming001Test extends AbstractThemingTest {

    /** "library:name" of every stylesheet link of the document, in document order. */
    private static final String STYLESHEETS_SCRIPT = ""
            + "return Array.from(document.querySelectorAll('link[rel=stylesheet]')).map(function(l) {"
            + "  var u = new URL(l.href, location.href);"
            + "  var name = u.pathname.split(';')[0].split('jakarta.faces.resource/').pop().replace(/[.]xhtml$/, '');"
            + "  return (u.searchParams.get('ln') || '') + ':' + name;"
            + "});";

    private static final String SCRIPTS_SCRIPT = ""
            + "return Array.from(document.querySelectorAll('script[src]')).map(function(s) { return s.src; });";

    private static final List<String> EXPECTED_STYLESHEETS = Arrays.asList(
            "primefaces-aura:theme.css",
            "primefaces:primeicons/primeicons.css",
            "primefaces:components.css");

    @Test
    @Order(1)
    @DisplayName("Theming: the theme (primefaces-aura:theme.css) first, then PrimeIcons and components.css")
    void stylesheets(Page page) {
        // Arrange
        setColorScheme("light");

        // Act
        List<String> stylesheets = PrimeSelenium.executeScript(STYLESHEETS_SCRIPT);

        // Assert
        assertEquals(EXPECTED_STYLESHEETS, stylesheets);
        assertThemeApplied(page);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: components added by an ajax update add their CSS, the theme stays applied")
    void stylesheetsAfterAjaxUpdate(Page page) {
        // Arrange
        setColorScheme("light");
        assertEquals(0, getWebDriver().findElements(By.id("form:colorPicker")).size());
        assertFalse(hasScript("colorpicker/colorpicker.js"));

        // Act
        page.show.click();

        // Assert: the new components are rendered and their scripts were added by the resource update ...
        assertEquals(1, getWebDriver().findElements(By.id("form:colorPicker")).size());
        assertTrue(hasScript("colorpicker/colorpicker.js"), "colorpicker.js not added by the ajax resource update");
        assertTrue(hasScript("keyboard/keyboard.js"), "keyboard.js not added by the ajax resource update");

        // ... and their CSS, after the stylesheets of the page
        List<String> stylesheets = PrimeSelenium.executeScript(STYLESHEETS_SCRIPT);
        assertEquals(EXPECTED_STYLESHEETS, stylesheets.subList(0, EXPECTED_STYLESHEETS.size()));
        assertTrue(stylesheets.contains("primefaces:colorpicker/colorpicker.css"), "colorpicker.css not added: " + stylesheets);
        assertTrue(stylesheets.contains("primefaces:keyboard/keyboard.css"), "keyboard.css not added: " + stylesheets);
        assertEquals(EXPECTED_STYLESHEETS.size() + 2, stylesheets.size(), "unexpected stylesheets: " + stylesheets);
        assertThemeApplied(page);
        assertNoJavascriptErrors();
    }

    private boolean hasScript(String name) {
        List<String> scripts = PrimeSelenium.executeScript(SCRIPTS_SCRIPT);
        return scripts.stream().anyMatch(s -> s.contains(name));
    }

    private void assertThemeApplied(Page page) {
        // tokens of aura.css are defined and used by the components of components.css
        assertFalse(tokenValue(page.html, "--p-primary-color").isEmpty(), "--p-primary-color not defined");
        assertTokenWired("InputText", page.inputText, "border-top-color", "--p-inputtext-border-color");
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(tagName = "html")
        WebElement html;

        @FindBy(id = "form:inputText")
        WebElement inputText;

        @FindBy(id = "form:show")
        CommandButton show;

        @Override
        public String getLocation() {
            return "theming/theming001.xhtml";
        }
    }
}
