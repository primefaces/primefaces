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
package org.primefaces.integrationtests.terminal;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Terminal;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Terminal001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Terminal: command is sent via AJAX and the response of the command handler is shown")
    void command(Page page) {
        // Arrange
        Terminal terminal = page.terminal;
        assertEquals("Welcome to the PrimeFaces Terminal", terminal.getWelcomeMessage());
        assertEquals("pf $", terminal.getPrompt().getText());
        assertTrue(terminal.getEntries().isEmpty());

        // Act
        terminal.sendCommand("greet PrimeFaces");

        // Assert
        assertEquals(List.of("greet PrimeFaces"), terminal.getCommands());
        assertEquals("Hello PrimeFaces", terminal.getLastResponse());
        assertEquals("pf $", terminal.getEntries().get(0).findElement(By.tagName("span")).getText());
        assertEquals("", terminal.getValue());
        assertTrue(terminal.getInput().isDisplayed());

        // Act
        terminal.sendCommand("foo bar");

        // Assert
        assertEquals(Arrays.asList("greet PrimeFaces", "foo bar"), terminal.getCommands());
        assertEquals(Arrays.asList("Hello PrimeFaces", "Unknown command: foo"), terminal.getResponses());
        assertConfiguration(terminal.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Terminal: command and response are HTML escaped")
    void escape(Page page) {
        // Arrange
        Terminal terminal = page.terminal;

        // Act
        terminal.sendCommand("greet <b>World</b>");

        // Assert
        assertEquals(List.of("greet <b>World</b>"), terminal.getCommands());
        assertEquals("Hello <b>World</b>", terminal.getLastResponse());
        assertTrue(terminal.getContent().findElements(By.tagName("b")).isEmpty());
        assertConfiguration(terminal.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Terminal: command history via arrow keys and clear")
    void history(Page page) {
        // Arrange
        Terminal terminal = page.terminal;
        terminal.sendCommand("greet A");
        terminal.sendCommand("greet B");

        // Act + Assert
        terminal.previousCommand();
        assertEquals("greet B", terminal.getValue());
        terminal.previousCommand();
        assertEquals("greet A", terminal.getValue());
        terminal.previousCommand();
        assertEquals("greet A", terminal.getValue());
        terminal.nextCommand();
        assertEquals("greet B", terminal.getValue());
        terminal.nextCommand();
        assertEquals("", terminal.getValue());

        // Act
        terminal.clear();

        // Assert
        assertTrue(terminal.getEntries().isEmpty());
        assertEquals("", terminal.getValue());
        assertEquals("Welcome to the PrimeFaces Terminal", terminal.getWelcomeMessage());
        assertConfiguration(terminal.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Terminal: TAB autocompletes commands and arguments from the model")
    void autoComplete(Page page) {
        // Arrange
        Terminal terminal = page.terminal;
        terminal.setValue("gr");

        // Act - single match replaces the input
        terminal.autoComplete();

        // Assert
        assertEquals("greet", terminal.getValue());
        assertTrue(terminal.getAutoCompleteMatches().isEmpty());

        // Act - several matches are listed
        terminal.setValue("g");
        terminal.autoComplete();

        // Assert
        assertEquals(Arrays.asList("greet", "git"), terminal.getAutoCompleteMatches());

        // Act
        terminal.selectAutoCompleteMatch("git");

        // Assert
        assertEquals("git", terminal.getValue());

        // Act - arguments of a command
        terminal.setValue("greet ");
        terminal.autoComplete();
        terminal.selectAutoCompleteMatch("PrimeFaces");
        terminal.execute();

        // Assert
        assertEquals("greet PrimeFaces", terminal.getCommands().get(terminal.getCommands().size() - 1));
        assertEquals("Hello PrimeFaces", terminal.getLastResponse());
        assertConfiguration(terminal.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("Terminal: prompt is HTML escaped in the printed command line")
    void escapePrompt(Page page) {
        // Arrange
        Terminal terminal = page.terminal2;
        assertEquals("<b>pf</b> $", terminal.getPrompt().getText());

        // Act
        terminal.sendCommand("greet World");

        // Assert
        assertEquals("<b>pf</b> $", terminal.getEntries().get(0).findElement(By.tagName("span")).getText());
        assertEquals("Hello World", terminal.getLastResponse());
        assertTrue(terminal.findElements(By.tagName("b")).isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("Terminal: selecting an autocomplete match only updates the input of its own terminal")
    void autoCompleteOtherTerminal(Page page) {
        // Arrange
        Terminal terminal = page.terminal;
        page.terminal2.setValue("foo");
        terminal.setValue("g");
        terminal.autoComplete();

        // Act
        terminal.selectAutoCompleteMatch("git");

        // Assert
        assertEquals("git", terminal.getValue());
        assertEquals("foo", page.terminal2.getValue());
        assertConfiguration(terminal.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("pf $", cfg.getString("prompt"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:terminal")
        Terminal terminal;

        @FindBy(id = "form:terminal2")
        Terminal terminal2;

        @Override
        public String getLocation() {
            return "terminal/terminal001.xhtml";
        }
    }
}
