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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chromium.HasCdp;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Motion tokens (README section "Motion"): transitions that move something use --p-transition-easing; with
 * prefers-reduced-motion the transitions are reduced to a minimum.
 */
class Theming008Test extends AbstractThemingTest {

    /**
     * Returns [timing function, duration] of the transition of the given property, taken from the comma separated lists
     * of the computed transition (the timing functions contain commas themselves), null if the property has no transition.
     */
    private static final String TRANSITION_SCRIPT = ""
            + "var cs = getComputedStyle(arguments[0]);"
            + "var split = function(v) { return v.split(/,(?![^(]*\\))/).map(function(s) { return s.trim(); }); };"
            + "var props = split(cs.getPropertyValue('transition-property'));"
            + "var fns = split(cs.getPropertyValue('transition-timing-function'));"
            + "var durations = split(cs.getPropertyValue('transition-duration'));"
            + "var i = props.indexOf(arguments[1]);"
            + "if (i < 0) { return null; }"
            + "return [fns[i % fns.length], durations[i % durations.length]];";

    @Test
    @Order(1)
    @DisplayName("Theming: toggleSwitch handle moves with --p-transition-easing and --p-toggleswitch-slide-duration")
    void easing(Page page) {
        // Arrange
        // the theme transitions stay enabled here (setColorScheme would disable them)
        removeApplicationStyle("theming-test-no-transitions");

        // Act
        List<String> transition = PrimeSelenium.executeScript(TRANSITION_SCRIPT, page.handle, "transform");

        // Assert
        assertNotNull(transition, "toggleSwitch handle has no transform transition");
        String easing = resolveToken(page.handle, "transition-timing-function", "--p-transition-easing");
        assertNotNull(easing, "--p-transition-easing is not a valid timing function");
        assertEquals(easing, transition.get(0), "transform transition does not use --p-transition-easing");
        assertEquals(resolveToken(page.handle, "transition-duration", "--p-toggleswitch-slide-duration"), transition.get(1),
                "transform transition does not use --p-toggleswitch-slide-duration");

        // Act: another easing, the transition follows the token
        setApplicationStyle("theming-test-easing", ":root { --p-transition-easing: steps(4, jump-end); }");

        // Assert
        List<String> overridden = PrimeSelenium.executeScript(TRANSITION_SCRIPT, page.handle, "transform");
        String overriddenEasing = resolveToken(page.handle, "transition-timing-function", "--p-transition-easing");
        assertTrue(overriddenEasing.startsWith("steps("), "easing override not applied: " + overriddenEasing);
        assertEquals(overriddenEasing, overridden.get(0), "transform transition does not follow --p-transition-easing");

        // Act
        removeApplicationStyle("theming-test-easing");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: prefers-reduced-motion reduces --p-transition-duration and the toggleSwitch handle transition")
    void reducedMotion(Page page) {
        // Arrange
        // the theme transitions stay enabled here (setColorScheme would disable them)
        removeApplicationStyle("theming-test-no-transitions");
        HasCdp cdp = cdp();
        Assumptions.assumeTrue(cdp != null, "the browser cannot emulate prefers-reduced-motion (no Chrome DevTools Protocol)");

        try {
            // Act
            emulateReducedMotion(cdp, true);
            Assumptions.assumeTrue(PrimeSelenium.<Boolean> executeScript("return matchMedia('(prefers-reduced-motion: reduce)').matches;"),
                    "prefers-reduced-motion emulation is not active");

            // Assert
            assertEquals("0s", tokenValue(page.handle, "--p-transition-duration"));
            List<String> transition = PrimeSelenium.executeScript(TRANSITION_SCRIPT, page.handle, "transform");
            assertNotNull(transition, "toggleSwitch handle has no transform transition");
            double seconds = Double.parseDouble(transition.get(1).replace("s", ""));
            assertTrue(seconds < 0.001, "transform transition is not reduced: " + transition.get(1));
        }
        finally {
            emulateReducedMotion(cdp, false);
        }

        // Assert: back to normal
        List<String> transition = PrimeSelenium.executeScript(TRANSITION_SCRIPT, page.handle, "transform");
        assertEquals(resolveToken(page.handle, "transition-duration", "--p-toggleswitch-slide-duration"), transition.get(1));
        assertNoJavascriptErrors();
    }

    private static HasCdp cdp() {
        WebDriver driver = PrimeSelenium.getWebDriver();
        while (!(driver instanceof HasCdp) && driver instanceof WrapsDriver) {
            driver = ((WrapsDriver) driver).getWrappedDriver();
        }
        return driver instanceof HasCdp ? (HasCdp) driver : null;
    }

    private static void emulateReducedMotion(HasCdp cdp, boolean reduce) {
        List<Map<String, Object>> features = reduce
                ? List.of(Map.of("name", "prefers-reduced-motion", "value", "reduce"))
                : Collections.emptyList();
        cdp.executeCdpCommand("Emulation.setEmulatedMedia", Map.of("features", features));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(css = "[id='form:toggleSwitch'] .ui-toggleswitch-handler")
        WebElement handle;

        @Override
        public String getLocation() {
            return "theming/theming008.xhtml";
        }
    }
}
