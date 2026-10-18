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

import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.chromium.HasCdp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Helpers for the token based theming (primefaces-theming) tests: they compare computed styles with the resolved values of
 * the design tokens instead of fixed colors, so they survive token value changes but fail when the wiring breaks.
 */
abstract class AbstractThemingTest extends AbstractPrimePageTest {

    /**
     * Resolves the token on the element (substituted value of the custom property) by applying it to a probe element with the
     * same color-scheme and text color (for light-dark() and currentColor).
     * Returns [token value, expected computed value, actual computed value].
     */
    private static final String RESOLVE_SCRIPT = ""
            + "var el = arguments[0], prop = arguments[1], token = arguments[2];"
            + "var cs = getComputedStyle(el);"
            + "var raw = cs.getPropertyValue(token).trim();"
            + "var probe = document.createElement('div');"
            + "probe.style.setProperty('color-scheme', cs.getPropertyValue('color-scheme'));"
            + "probe.style.setProperty('color', cs.getPropertyValue('color'));"
            // border / outline widths compute to 0 without a style
            + "probe.style.setProperty('border-style', 'solid');"
            + "probe.style.setProperty('outline-style', 'solid');"
            + "document.body.appendChild(probe);"
            + "probe.style.setProperty(prop, raw);"
            + "var applied = raw !== '' && probe.style.getPropertyValue(prop) !== '';"
            + "var expected = getComputedStyle(probe).getPropertyValue(prop);"
            + "probe.remove();"
            + "return [raw, applied ? expected : null, cs.getPropertyValue(prop)];";

    /**
     * Overrides the token on the element with a sentinel value and returns the computed property, then restores the element.
     */
    private static final String SENTINEL_SCRIPT = ""
            + "var el = arguments[0], prop = arguments[1], token = arguments[2], sentinel = arguments[3];"
            + "var old = el.style.getPropertyValue(token);"
            + "el.style.setProperty(token, sentinel);"
            + "var result = getComputedStyle(el).getPropertyValue(prop);"
            + "if (old) { el.style.setProperty(token, old); } else { el.style.removeProperty(token); }"
            + "return result;";

    /** Converts any CSS color (oklch, color(), ...) to sRGB through a canvas and returns its relative luminance (0..1). */
    private static final String LUMINANCE_SCRIPT = ""
            + "var ctx = document.createElement('canvas').getContext('2d');"
            + "ctx.fillStyle = '#000'; ctx.fillStyle = arguments[0]; ctx.fillRect(0, 0, 1, 1);"
            + "var d = ctx.getImageData(0, 0, 1, 1).data;"
            + "var c = [d[0], d[1], d[2]].map(function(v) { v = v / 255; return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4); });"
            + "return 0.2126 * c[0] + 0.7152 * c[1] + 0.0722 * c[2];";

    /**
     * Lowest contrast between the text color of arguments[0] and the color stops of the background of arguments[1] (or
     * its pseudo element arguments[2]) behind the text: gradient stops and the background color, no url() textures; when a
     * layer is clipped to the padding box, the border-box layers are edges (Metal) and skipped. Translucent stops (Glass)
     * are blended over the background color of the element, then over the next opaque ancestor (or the page).
     */
    private static final String MIN_CONTRAST_SCRIPT = ""
            + "var text = arguments[0], el = arguments[1], pseudo = arguments[2];"
            + "var ctx = document.createElement('canvas').getContext('2d');"
            + "var rgba = function(c) { ctx.clearRect(0, 0, 1, 1); ctx.fillStyle = '#000'; ctx.fillStyle = c; ctx.fillRect(0, 0, 1, 1);"
            + "  var d = ctx.getImageData(0, 0, 1, 1).data; return [d[0], d[1], d[2], d[3] / 255]; };"
            + "var over = function(top, bottom) { return [0, 1, 2].map(function(i) { return top[i] * top[3] + bottom[i] * (1 - top[3]); }).concat(1); };"
            + "var lum = function(c) { var f = function(v) { v = v / 255; return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4); };"
            + "  return 0.2126 * f(c[0]) + 0.7152 * f(c[1]) + 0.0722 * f(c[2]); };"
            + "var split = function(v) { var out = [], depth = 0, cur = ''; for (var i = 0; i < v.length; i++) { var ch = v[i];"
            + "  if (ch === '(') { depth++; } if (ch === ')') { depth--; } if (ch === ',' && depth === 0) { out.push(cur); cur = ''; } else { cur += ch; } }"
            + "  out.push(cur); return out; };"
            // the opaque base behind the element: the next ancestor with an opaque background color, else the page
            + "var base = null;"
            + "for (var a = el.parentElement; a && !base; a = a.parentElement) {"
            + "  var c = rgba(getComputedStyle(a).backgroundColor); if (c[3] >= 1) { base = c; } }"
            + "if (!base) { var h = rgba(getComputedStyle(document.documentElement).backgroundColor);"
            + "  base = h[3] > 0 ? over(h, [255, 255, 255, 1]) : [255, 255, 255, 1]; }"
            + "var bcs = getComputedStyle(el, pseudo); var layers = split(bcs.backgroundImage);"
            + "var clips = bcs.backgroundClip.split(',').map(function(x) { return x.trim(); });"
            + "var edges = clips.indexOf('padding-box') >= 0;"
            + "var under = over(rgba(bcs.backgroundColor), base);"
            + "var stops = [];"
            + "layers.forEach(function(l, i) {"
            + "  if (/^\\s*(url\\(|none)/.test(l)) { return; }"
            + "  if (edges && (clips[i % clips.length] || 'border-box') === 'border-box') { return; }"
            + "  (l.match(/(?:rgba?|oklch|oklab|lab|lch|color)\\([^()]*\\)/g) || []).forEach(function(s) { stops.push(over(rgba(s), under)); });"
            + "});"
            + "if (!stops.length) { stops.push(under); }"
            + "var fg = lum(rgba(getComputedStyle(text).color));"
            + "return Math.min.apply(null, stops.map(function(s) { var b = lum(s);"
            + "  return (Math.max(fg, b) + 0.05) / (Math.min(fg, b) + 0.05); }));";

    /**
     * Asserts that the computed property of the element equals the resolved value of the token, and that the element really
     * takes the property from the token (a sentinel value of the token shows up in the property).
     *
     * @param property CSS property name (e.g. "background-color")
     * @param token custom property name (e.g. "--p-button-primary-background")
     */
    protected void assertTokenWired(String description, WebElement element, String property, String token) {
        List<Object> result = PrimeSelenium.executeScript(RESOLVE_SCRIPT, element, property, token);
        String tokenValue = (String) result.get(0);
        String expected = (String) result.get(1);
        String actual = (String) result.get(2);
        String message = description + ": " + property + " / " + token + " (token value '" + tokenValue + "')";

        assertFalse(tokenValue.isEmpty(), message + ": token is not defined");
        assertNotNull(expected, message + ": token value is not a valid " + property);
        assertEquals(expected, actual, message);

        // sentinel: proves the property is driven by the token (and does not only happen to have the same value)
        String sentinel = PrimeSelenium.executeScript(SENTINEL_SCRIPT, element, property, token, "rgb(1, 2, 3)");
        assertEquals("rgb(1, 2, 3)", sentinel, message + ": property does not follow the token");
    }

    protected String computedStyle(WebElement element, String property) {
        return PrimeSelenium.executeScript("return getComputedStyle(arguments[0]).getPropertyValue(arguments[1]);", element, property);
    }

    /**
     * Asserts that the computed property of the element equals the resolved value of the token, for properties where the
     * color sentinel of {@link #assertTokenWired} does not apply (lengths, timing functions, ...).
     */
    protected void assertTokenValue(String description, WebElement element, String property, String token) {
        List<Object> result = PrimeSelenium.executeScript(RESOLVE_SCRIPT, element, property, token);
        String tokenValue = (String) result.get(0);
        String expected = (String) result.get(1);
        String actual = (String) result.get(2);
        String message = description + ": " + property + " / " + token + " (token value '" + tokenValue + "')";

        assertFalse(tokenValue.isEmpty(), message + ": token is not defined");
        assertNotNull(expected, message + ": token value is not a valid " + property);
        assertEquals(expected, actual, message);
    }

    /**
     * @return the value the token resolves to on the element as computed value of the property (e.g. "14px" for a rem
     *         length), null if the token is not defined or not valid for the property
     */
    protected String resolveToken(WebElement element, String property, String token) {
        List<Object> result = PrimeSelenium.executeScript(RESOLVE_SCRIPT, element, property, token);
        return (String) result.get(1);
    }

    protected static double pixels(String value) {
        return Double.parseDouble(value.replace("px", "").trim());
    }

    protected String tokenValue(WebElement element, String token) {
        return PrimeSelenium.executeScript("return getComputedStyle(arguments[0]).getPropertyValue(arguments[1]).trim();", element, token);
    }

    /**
     * @return the computed mask-image of the ::before pseudo element
     */
    protected String pseudoMaskImage(WebElement element, String pseudo) {
        return PrimeSelenium.executeScript("var cs = getComputedStyle(arguments[0], arguments[1]);"
                + "return cs.getPropertyValue('mask-image') || cs.getPropertyValue('-webkit-mask-image');", element, pseudo);
    }

    protected double luminance(String color) {
        return ((Number) PrimeSelenium.executeScript(LUMINANCE_SCRIPT, color)).doubleValue();
    }

    /**
     * Sets the color-scheme of the document (light, dark or "light dark") and disables transitions, so computed styles
     * show the new values immediately (the default is "light dark", which follows the OS / browser preference).
     */
    protected void setColorScheme(String colorScheme) {
        disableTransitions();
        PrimeSelenium.executeScript("document.documentElement.style.colorScheme = arguments[0];", colorScheme);
    }

    /**
     * Theme transitions (e.g. background 0.2s) would make computed styles lag behind token / color-scheme changes.
     * Unlayered !important application CSS wins over the layered theme.
     */
    protected void disableTransitions() {
        setApplicationStyle("theming-test-no-transitions", "*, *::before, *::after { transition: none !important; }");
    }

    /**
     * Adds (or replaces) an unlayered application style sheet, like an application overriding tokens.
     */
    protected void setApplicationStyle(String id, String css) {
        PrimeSelenium.executeScript("var s = document.getElementById(arguments[0]);"
                + "if (!s) { s = document.createElement('style'); s.id = arguments[0]; document.head.appendChild(s); }"
                + "s.textContent = arguments[1];", id, css);
    }

    protected void removeApplicationStyle(String id) {
        PrimeSelenium.executeScript("var s = document.getElementById(arguments[0]); if (s) { s.remove(); }", id);
    }

    /**
     * @return the lowest contrast (WCAG ratio, 2 decimals) between the text color of {@code text} and the color stops of
     *         the background of {@code background} (or its pseudo element) behind the text
     */
    protected static double minContrast(WebElement text, WebElement background, String pseudo) {
        Number ratio = PrimeSelenium.executeScript(MIN_CONTRAST_SCRIPT, text, background, pseudo);
        return Math.round(ratio.doubleValue() * 100) / 100.0;
    }

    /**
     * @return the Chrome DevTools Protocol of the driver (to emulate media features), null if the browser has none
     */
    protected static HasCdp cdp() {
        WebDriver driver = PrimeSelenium.getWebDriver();
        while (!(driver instanceof HasCdp) && driver instanceof WrapsDriver) {
            driver = ((WrapsDriver) driver).getWrappedDriver();
        }
        return driver instanceof HasCdp ? (HasCdp) driver : null;
    }

    /**
     * Emulates a media feature (e.g. prefers-reduced-motion: reduce, forced-colors: active); value null ends the emulation.
     */
    protected static void emulateMediaFeature(HasCdp cdp, String name, String value) {
        List<Map<String, Object>> features = value == null
                ? Collections.emptyList()
                : List.of(Map.of("name", name, "value", value));
        cdp.executeCdpCommand("Emulation.setEmulatedMedia", Map.of("features", features));
    }
}
