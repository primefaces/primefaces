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
package org.primefaces.integrationtests.core.widget;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoreWidget003Test extends AbstractWidgetLifecycleTest {

    /** The widgets without own markup, destroyed with the elements they work on. */
    private static final String[] WIDGETS = {
        "form:drag", "form:drop", "form:resize", "form:keyFilter", "form:effect"
    };

    /** The default command works on the form (its listener is there), it stays while the form stays. */
    private static final String DEFAULT_COMMAND = "form:defaultCommand";

    /** The handlers of an element by event type and namespace. */
    private static final String HANDLERS = "var events = jQuery._data(document.getElementById(arguments[0]), 'events') || {}, result = {};"
                + "Object.keys(events).sort().forEach(function(type) {"
                + "  events[type].forEach(function(h) { var key = type + '.' + h.namespace; result[key] = (result[key] || 0) + 1; });"
                + "});"
                + "return JSON.stringify(result);";

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

    private static final String TOGGLE = "PrimeFaces.ab({s: 'form:btnToggle', p: 'form:btnToggle', u: 'form:wrapper'});";
    private static final String UPDATE = "PrimeFaces.ab({s: 'form:btnUpdate', p: 'form:btnUpdate', u: 'form:wrapper'});";
    private static final String UPDATE_TARGET = "PrimeFaces.ab({s: 'form:btnUpdateTarget', p: 'form:btnUpdateTarget', u: 'form:btnDefault'});";

    /** The keydown handler of the default command on the form, which stays. */
    private static final String DEFAULT_COMMAND_HANDLER = "keydown.form:defaultCommand";

    @Test
    @Order(1)
    @DisplayName("Widget lifecycle: widgets without own markup are destroyed with the elements they work on")
    void remove(Page page) {
        // Arrange
        assertWidgetCount(1, WIDGETS);
        assertEquals(1, handlers("form").getInt(DEFAULT_COMMAND_HANDLER));

        // Act
        PrimeSelenium.executeScript(true, TOGGLE);

        // Assert: destroyed
        assertWidgetCount(0, WIDGETS);
        assertEquals(0, globalState().getInt("droppables"));

        // Assert: the default command stays with the form, without its target Enter is left to the browser
        assertWidgetCount(1, DEFAULT_COMMAND);
        assertEquals(1, handlers("form").getInt(DEFAULT_COMMAND_HANDLER));
        assertEquals(0L, enter());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Widget lifecycle: widgets without own markup rendered again by an update are refreshed, not destroyed")
    void update(Page page) {
        // Act
        for (int i = 0; i < 3; i++) {
            PrimeSelenium.executeScript(true, UPDATE);
        }

        // Assert: still there, once, bound to the new elements
        assertWidgetCount(1, WIDGETS);
        assertWidgetCount(1, DEFAULT_COMMAND);
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
    @DisplayName("Widget lifecycle: removing and rendering widgets without own markup again doesn't leak")
    void removeAgain(Page page) {
        // Arrange
        PrimeSelenium.executeScript(true, TOGGLE);
        JSONObject removed = globalState();
        JSONObject removedFormHandlers = handlers("form");

        // Act
        PrimeSelenium.executeScript(true, TOGGLE);
        assertWidgetCount(1, WIDGETS);
        assertEquals(1L, enter());
        PrimeSelenium.executeScript(true, TOGGLE);

        // Assert
        assertWidgetCount(0, WIDGETS);
        assertSameState(removed, globalState());
        assertSameState(removedFormHandlers, handlers("form"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Widget lifecycle: a default command stays when only its target is updated")
    void updateTarget(Page page) {
        // Act: e.g. an update that enables the target
        PrimeSelenium.executeScript(true, UPDATE_TARGET);
        PrimeSelenium.executeScript(true, UPDATE_TARGET);

        // Assert: Enter clicks the new target, once
        assertWidgetCount(1, DEFAULT_COMMAND);
        assertEquals(1, handlers("form").getInt(DEFAULT_COMMAND_HANDLER));
        assertEquals(1L, enter());
        assertNoJavascriptErrors();
    }

    private JSONObject handlers(String clientId) {
        return new JSONObject((String) PrimeSelenium.executeScript(HANDLERS, clientId));
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
            return "core/widget/coreWidget003.xhtml";
        }
    }
}
