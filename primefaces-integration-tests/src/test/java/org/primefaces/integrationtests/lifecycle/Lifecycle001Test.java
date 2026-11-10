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
package org.primefaces.integrationtests.lifecycle;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Lifecycle;
import org.primefaces.selenium.component.Messages;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Lifecycle001Test extends AbstractPrimePageTest {

    private static final List<String> PHASES = List.of("RESTORE_VIEW", "APPLY_REQUEST_VALUES", "PROCESS_VALIDATIONS", "UPDATE_MODEL_VALUES",
                "INVOKE_APPLICATION", "RENDER_RESPONSE", Lifecycle.ALL);

    @Test
    @Order(1)
    @DisplayName("Lifecycle: phase table is rendered")
    void render(Page page) {
        // Arrange
        Lifecycle lifecycle = page.lifecycle;

        // Act
        List<String> names = lifecycle.getPhaseNames();

        // Assert
        assertEquals(PHASES.size(), names.size());
        for (int i = 0; i < PHASES.size(); i++) {
            assertTrue(names.get(i).startsWith(PHASES.get(i)), names.get(i));
            assertTrue(lifecycle.getDuration(PHASES.get(i)) >= 0);
            assertTrue(List.of(0, 33, 66, 100).contains(lifecycle.getScore(PHASES.get(i))));
        }
        assertConfiguration(lifecycle.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Lifecycle: shows the phase durations of an AJAX request")
    void invokeApplication(Page page) {
        // Arrange
        Lifecycle lifecycle = page.lifecycle;

        // Act
        lifecycle.waitForUpdate(() -> page.slow.click());

        // Assert
        assertEquals("Slow action", page.messages.getMessage(0).getSummary());
        long invokeApplication = lifecycle.getDuration("INVOKE_APPLICATION");
        assertTrue(invokeApplication >= 300, "INVOKE_APPLICATION: " + invokeApplication);
        assertEquals(0, lifecycle.getScore("INVOKE_APPLICATION"));
        assertTrue(lifecycle.getDuration(Lifecycle.ALL) >= invokeApplication);
        assertEquals(PHASES.size(), lifecycle.getPhaseNames().size());
        assertConfiguration(lifecycle.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Lifecycle: shows the phase durations of an immediate AJAX request")
    void applyRequestValues(Page page) {
        // Arrange
        Lifecycle lifecycle = page.lifecycle;

        // Act
        lifecycle.waitForUpdate(() -> page.slowImmediate.click());

        // Assert
        assertEquals("Slow immediate action", page.messages.getMessage(0).getSummary());
        long applyRequestValues = lifecycle.getDuration("APPLY_REQUEST_VALUES");
        assertTrue(applyRequestValues >= 300, "APPLY_REQUEST_VALUES: " + applyRequestValues);
        assertEquals(0, lifecycle.getScore("APPLY_REQUEST_VALUES"));
        assertTrue(lifecycle.getDuration(Lifecycle.ALL) >= applyRequestValues);
        assertConfiguration(lifecycle.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("form:lifecycle", cfg.getString("id"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:lifecycle")
        Lifecycle lifecycle;

        @FindBy(id = "form:slow")
        CommandButton slow;

        @FindBy(id = "form:slowImmediate")
        CommandButton slowImmediate;

        @Override
        public String getLocation() {
            return "lifecycle/lifecycle001.xhtml";
        }
    }
}
