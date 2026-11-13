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
package org.primefaces.integrationtests.core.component;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoreComponent003Test extends AbstractComponentLifecycleTest {

    /** The components without own markup, destroyed with the elements they work on. */
    private static final String[] COMPONENTS = {
        "form:drag", "form:drop", "form:resize", "form:keyFilter", "form:effect"
    };

    /** The default command works on the form (its listener is there), it stays while the form stays. */
    private static final String DEFAULT_COMMAND = "form:defaultCommand";

    /** The interactions on the targets. */
    private static final String TARGET_INTERACTIONS = "var i = PrimeFaces.interactions;"
                + "return JSON.stringify({"
                + "  draggable: !!i.instance(document.getElementById('form:panel'), 'draggable'),"
                + "  droppable: !!i.instance(document.getElementById('form:dropArea'), 'droppable'),"
                + "  resizable: !!i.instance(document.getElementById('form:resizeBox'), 'resizable')"
                + "});";

    /** Presses Enter in the input, the default command clicks its target button which counts the clicks. */
    private static final String ENTER = "jQuery(document.getElementById('form:input')).trigger(jQuery.Event('keydown', { key: 'Enter' }));"
                + "return window.defaultCommandClicks || 0;";

    /** The keydown handler of the default command on the form, which stays. */
    private static final String DEFAULT_COMMAND_HANDLER = "keydown.form:defaultCommand";

    @Test
    @Order(1)
    @DisplayName("Component lifecycle: components without own markup are destroyed with the elements they work on")
    void remove(Page page) {
        // Arrange
        assertComponentCount(1, COMPONENTS);
        assertEquals(1, handlers("form").getInt(DEFAULT_COMMAND_HANDLER));

        // Act
        toggleWrapper();

        // Assert: destroyed
        assertComponentCount(0, COMPONENTS);
        assertEquals(0, globalState().getInt("droppables"));

        // Assert: the default command stays with the form, without its target Enter is left to the browser
        assertComponentCount(1, DEFAULT_COMMAND);
        assertEquals(1, handlers("form").getInt(DEFAULT_COMMAND_HANDLER));
        assertEquals(0L, enter());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Component lifecycle: components without own markup rendered again by an update are refreshed, not destroyed")
    void update(Page page) {
        // Act
        for (int i = 0; i < 3; i++) {
            updateWrapper();
        }

        // Assert: still there, once, bound to the new elements
        assertComponentCount(1, COMPONENTS);
        assertComponentCount(1, DEFAULT_COMMAND);
        assertSameState(new JSONObject("{draggable:true,droppable:true,resizable:true}"), targetInteractions());
        assertEquals(1, globalState().getInt("droppables"));
        assertEquals(1, handlers("form").getInt(DEFAULT_COMMAND_HANDLER));
        assertEquals(1, handlers("form:input").getInt("beforeinput.keyFilterform:keyFilter"));
        assertEquals(1, handlers("form:effectSource").getInt("click.effectform:effect"));

        // Assert: they still work
        page.input.sendKeys("a1");
        assertEquals("1", page.input.getAttribute("value"));
        assertEquals(1L, enter());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Component lifecycle: removing and rendering components without own markup again doesn't leak")
    void removeAgain(Page page) {
        // Arrange
        toggleWrapper();
        JSONObject removed = globalState();
        JSONObject removedFormHandlers = handlers("form");

        // Act
        toggleWrapper();
        assertComponentCount(1, COMPONENTS);
        assertEquals(1L, enter());
        toggleWrapper();

        // Assert
        assertComponentCount(0, COMPONENTS);
        assertSameState(removed, globalState());
        assertSameState(removedFormHandlers, handlers("form"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Component lifecycle: a default command stays when only its target is updated")
    void updateTarget(Page page) {
        // Act: e.g. an update that enables the target
        ajaxUpdate("form:btnUpdateTarget", "form:btnDefault");
        ajaxUpdate("form:btnUpdateTarget", "form:btnDefault");

        // Assert: Enter clicks the new target, once
        assertComponentCount(1, DEFAULT_COMMAND);
        assertEquals(1, handlers("form").getInt(DEFAULT_COMMAND_HANDLER));
        assertEquals(1L, enter());
        assertNoJavascriptErrors();
    }

    private JSONObject targetInteractions() {
        return new JSONObject((String) PrimeSelenium.executeScript(TARGET_INTERACTIONS));
    }

    private long enter() {
        return (Long) PrimeSelenium.executeScript(ENTER);
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:input")
        WebElement input;

        @Override
        public String getLocation() {
            return "core/component/coreComponent003.xhtml";
        }
    }
}
