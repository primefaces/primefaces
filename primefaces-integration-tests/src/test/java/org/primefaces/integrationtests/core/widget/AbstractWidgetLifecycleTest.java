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

import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;

import org.json.JSONObject;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Probes for the widget lifecycle: what widgets leave behind outside of their own markup (global event handlers,
 * registered droppables, panels appended to the body) must not grow when they are updated or removed by AJAX.
 */
public abstract class AbstractWidgetLifecycleTest extends AbstractPrimePageTest {

    /**
     * The global state left by widgets: the jQuery handlers of document, window and body by event type, the registered
     * droppables (and whether all of them are still attached) and the number of direct children of the body.
     */
    private static final String GLOBAL_STATE = "var count = function(el) {"
                + "  var events = jQuery._data(el, 'events') || {}, result = {};"
                + "  Object.keys(events).sort().forEach(function(type) { result[type] = events[type].length; });"
                + "  return result;"
                + "};"
                + "var droppables = [];"
                + "var registry = PrimeFaces.interactions.dragDrop.droppables;"
                + "Object.keys(registry).forEach(function(scope) { droppables = droppables.concat(registry[scope]); });"
                + "return JSON.stringify({"
                + "  document: count(document), window: count(window), body: count(document.body),"
                + "  droppables: droppables.length,"
                + "  detachedDroppables: droppables.filter(function(d) { return !d.element[0].isConnected; }).length,"
                + "  bodyChildren: document.body.children.length"
                + "});";

    /** The number of interactions of each type on the elements of the document. */
    private static final String INTERACTIONS = "var types = ['draggable', 'droppable', 'sortable', 'resizable'], result = {};"
                + "types.forEach(function(type) { result[type] = 0; });"
                + "document.querySelectorAll('*').forEach(function(el) {"
                + "  types.forEach(function(type) { if (PrimeFaces.interactions.instance(el, type)) { result[type]++; } });"
                + "});"
                + "return JSON.stringify(result);";

    /** The jQuery handlers of an element by event type and namespace. */
    private static final String HANDLERS = "var events = jQuery._data(document.getElementById(arguments[0]), 'events') || {}, result = {};"
                + "Object.keys(events).sort().forEach(function(type) {"
                + "  events[type].forEach(function(h) { var key = type + '.' + h.namespace; result[key] = (result[key] || 0) + 1; });"
                + "});"
                + "return JSON.stringify(result);";

    /** The number of jQuery handlers of an element for an event type. */
    private static final String HANDLER_COUNT = "var events = jQuery._data(document.getElementById(arguments[0]), 'events') || {};"
                + "return (events[arguments[1]] || []).length;";

    /** An AJAX request like a click on the source button, which updates the given component. */
    private static final String AJAX_UPDATE = "PrimeFaces.ab({s: arguments[0], p: arguments[0], u: arguments[1]});";

    protected JSONObject globalState() {
        return new JSONObject((String) PrimeSelenium.executeScript(GLOBAL_STATE));
    }

    protected JSONObject interactions() {
        return new JSONObject((String) PrimeSelenium.executeScript(INTERACTIONS));
    }

    /**
     * The jQuery handlers of the element with the client id, counted by "type.namespace".
     */
    protected JSONObject handlers(String clientId) {
        return new JSONObject((String) PrimeSelenium.executeScript(HANDLERS, clientId));
    }

    /**
     * The number of jQuery handlers of the element with the client id for the event type.
     */
    protected long handlerCount(String clientId, String type) {
        return (Long) PrimeSelenium.executeScript(HANDLER_COUNT, clientId, type);
    }

    /**
     * Sends the AJAX request of the source button without clicking it (e.g. while the mouse is held for a drag) and waits for it.
     */
    protected void ajaxUpdate(String source, String update) {
        PrimeSelenium.executeScript(true, AJAX_UPDATE, source, update);
    }

    /**
     * Updates {@code form:wrapper} with {@code form:btnUpdate}.
     */
    protected void updateWrapper() {
        ajaxUpdate("form:btnUpdate", "form:wrapper");
    }

    /**
     * Removes or renders {@code form:wrapper} again with {@code form:btnToggle}.
     */
    protected void toggleWrapper() {
        ajaxUpdate("form:btnToggle", "form:wrapper");
    }

    protected void assertSameState(JSONObject expected, JSONObject actual) {
        assertTrue(expected.similar(actual), "expected " + expected + " but was " + actual);
    }

    /**
     * Clicks the button and waits for the AJAX request. The minimal AJAX load animation is turned off first, otherwise the
     * button stays disabled for 500ms after the request started and a second click right after the first would miss it.
     */
    protected void ajaxClick(WebElement button) {
        noAjaxMinLoadAnimation();
        PrimeSelenium.guardAjax(button).click();
    }

    /**
     * Counts the widgets with the given client id, a destroyed widget is removed from {@code PrimeFaces.widgets}.
     */
    protected long widgetCount(String clientId) {
        return (Long) PrimeSelenium.executeScript("var id = arguments[0]; return Object.values(PrimeFaces.widgets)"
                    + ".filter(function(w) { return w && w.id === id; }).length;", clientId);
    }

    protected void assertWidgetCount(long expected, String... clientIds) {
        for (String clientId : clientIds) {
            assertEquals(expected, widgetCount(clientId), "widgets with id " + clientId);
        }
    }
}
