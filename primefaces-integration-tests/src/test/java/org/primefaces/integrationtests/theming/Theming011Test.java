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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Metal preset (?theme=metal): gradient (metal) backgrounds, brushed texture, machined shadows; the text reaches WCAG AA
 * (4.5:1) on every color stop of the gradient backgrounds in light and dark mode. The neutral defaults of the new tokens
 * leave Aura unchanged.
 */
class Theming011Test extends AbstractThemingTest {

    /**
     * Lowest contrast between the text color of arguments[0] and the color stops of the background layers of arguments[1]
     * (or its pseudo element arguments[2]) behind the text: no url() textures, no border-box edge layers.
     */
    private static final String MIN_CONTRAST_SCRIPT = ""
            + "var text = arguments[0], el = arguments[1], pseudo = arguments[2];"
            + "var ctx = document.createElement('canvas').getContext('2d');"
            + "var rgba = function(c) { ctx.clearRect(0, 0, 1, 1); ctx.fillStyle = '#000'; ctx.fillStyle = c; ctx.fillRect(0, 0, 1, 1);"
            + "  var d = ctx.getImageData(0, 0, 1, 1).data; return [d[0], d[1], d[2]]; };"
            + "var lum = function(c) { var f = function(v) { v = v / 255; return v <= 0.03928 ? v / 12.92 : Math.pow((v + 0.055) / 1.055, 2.4); };"
            + "  return 0.2126 * f(c[0]) + 0.7152 * f(c[1]) + 0.0722 * f(c[2]); };"
            + "var split = function(v) { var out = [], depth = 0, cur = ''; for (var i = 0; i < v.length; i++) { var ch = v[i];"
            + "  if (ch === '(') { depth++; } if (ch === ')') { depth--; } if (ch === ',' && depth === 0) { out.push(cur); cur = ''; } else { cur += ch; } }"
            + "  out.push(cur); return out; };"
            + "var bcs = getComputedStyle(el, pseudo); var layers = split(bcs.backgroundImage);"
            + "var clips = bcs.backgroundClip.split(',').map(function(x) { return x.trim(); });"
            + "var stops = [];"
            + "layers.forEach(function(l, i) {"
            + "  if (/^\\s*(url\\(|none)/.test(l)) { return; }"
            + "  if (layers.length > 1 && (clips[i % clips.length] || 'border-box') === 'border-box') { return; }"
            + "  (l.match(/(?:rgba?|oklch|oklab|lab|lch|color)\\([^()]*\\)/g) || []).forEach(function(s) { stops.push(rgba(s)); });"
            + "});"
            + "if (!stops.length) { stops.push(rgba(bcs.backgroundColor)); }"
            + "var fg = lum(rgba(getComputedStyle(text).color));"
            + "return Math.min.apply(null, stops.map(function(s) { var b = lum(s);"
            + "  return (Math.max(fg, b) + 0.05) / (Math.min(fg, b) + 0.05); }));";

    @Test
    @Order(1)
    @DisplayName("Theming: Metal preset renders metal.css (?theme=metal)")
    void rendered(MetalPage page) {
        // Assert
        List<WebElement> links = PrimeSelenium.getWebDriver().findElements(By.cssSelector("link[href*='primefaces-theming']"));
        assertTrue(links.stream().anyMatch(l -> l.getAttribute("href").contains("metal.css")), "metal.css not rendered");
        assertFalse(links.stream().anyMatch(l -> l.getAttribute("href").contains("aura.css")), "aura.css rendered with metal");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: Metal buttons have a metal face and edge, brushed texture, machined shadow and engraved label")
    void metalButton(MetalPage page) {
        // Arrange
        setColorScheme("light");

        // Assert
        String image = computedStyle(page.primary, "background-image");
        assertTrue(image.contains("linear-gradient"), "no metal gradient: " + image);
        assertTrue(image.startsWith("url(\"data:image/svg+xml"), "no brushed texture layer on top");
        assertTrue(computedStyle(page.primary, "background-clip").contains("border-box"), "no edge layer (border-box)");
        assertEquals("rgba(0, 0, 0, 0)", computedStyle(page.primary, "border-top-color"), "the edge is drawn by the background");
        assertNotEquals("none", computedStyle(page.primary, "box-shadow"), "no machined shadow");
        WebElement label = page.primary.findElement(By.cssSelector(".ui-button-text"));
        assertNotEquals("none", computedStyle(label, "text-shadow"), "no engraved label");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Theming: Metal button and select button texts reach 4.5:1 on every gradient stop (light and dark)")
    void contrast(MetalPage page) {
        for (String scheme : List.of("light", "dark")) {
            // Arrange
            setColorScheme(scheme);

            // Assert
            for (WebElement button : List.of(page.primary, page.secondary, page.outlined, page.info, page.success, page.warning,
                    page.help, page.danger, page.contrast)) {
                double ratio = minContrast(button.findElement(By.cssSelector(".ui-button-text")), button, null);
                assertTrue(ratio >= 4.5, scheme + " " + button.getAttribute("id") + ": " + ratio);
            }
            WebElement checked = page.selectButton.findElement(By.cssSelector(".ui-button.ui-state-active"));
            double ratio = minContrast(checked.findElement(By.cssSelector(".ui-button-text")), checked, "::before");
            assertTrue(ratio >= 4.5, scheme + " checked select button: " + ratio);
        }
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Theming: Metal slider knob is the toggleSwitch knob (same background and shadow)")
    void knobs(MetalPage page) {
        // Arrange
        setColorScheme("light");
        WebElement handle = page.slider.findElement(By.cssSelector(".ui-slider-handle"));
        WebElement toggleHandle = page.toggleSwitch.findElement(By.cssSelector(".ui-toggleswitch-handler"));

        // Assert: the toggleSwitch is checked, its knob is the aluminium one the slider uses
        String sliderKnob = PrimeSelenium.executeScript("return getComputedStyle(arguments[0], '::before').backgroundImage;", handle);
        assertTrue(sliderKnob.startsWith("conic-gradient"), "slider knob is no turned metal: " + sliderKnob);
        assertEquals(computedStyle(toggleHandle, "background-image"), sliderKnob);
        assertEquals(computedStyle(toggleHandle, "box-shadow"),
                PrimeSelenium.executeScript("return getComputedStyle(arguments[0], '::before').boxShadow;", handle));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("Theming: the new button / toggleSwitch tokens are neutral in Aura (no texture, shadow, label shadow)")
    void auraUnchanged(AuraPage page) {
        // Arrange
        setColorScheme("light");

        // Assert: background = texture layer (none) + the color, so the image list only has empty layers
        String image = computedStyle(page.primary, "background-image");
        assertTrue(image.matches("none(, none)*"), "aura button has a background image: " + image);
        assertEquals("none", computedStyle(page.primary, "box-shadow"));
        assertEquals("none", computedStyle(page.primary.findElement(By.cssSelector(".ui-button-text")), "text-shadow"));
        assertEquals("none", computedStyle(page.toggleSwitch.findElement(By.cssSelector(".ui-toggleswitch-handler")), "box-shadow"));
        assertNoJavascriptErrors();
    }

    private static double minContrast(WebElement text, WebElement background, String pseudo) {
        Number ratio = PrimeSelenium.executeScript(MIN_CONTRAST_SCRIPT, text, background, pseudo);
        return Math.round(ratio.doubleValue() * 100) / 100.0;
    }

    public static class MetalPage extends AbstractPrimePage {
        @FindBy(id = "form:primary")
        WebElement primary;

        @FindBy(id = "form:secondary")
        WebElement secondary;

        @FindBy(id = "form:outlined")
        WebElement outlined;

        @FindBy(id = "form:info")
        WebElement info;

        @FindBy(id = "form:success")
        WebElement success;

        @FindBy(id = "form:warning")
        WebElement warning;

        @FindBy(id = "form:help")
        WebElement help;

        @FindBy(id = "form:danger")
        WebElement danger;

        @FindBy(id = "form:contrast")
        WebElement contrast;

        @FindBy(id = "form:selectButton")
        WebElement selectButton;

        @FindBy(id = "form:toggleSwitch")
        WebElement toggleSwitch;

        @FindBy(id = "form:slider")
        WebElement slider;

        @Override
        public String getLocation() {
            return "theming/theming011.xhtml?theme=metal";
        }
    }

    public static class AuraPage extends AbstractPrimePage {
        @FindBy(id = "form:primary")
        WebElement primary;

        @FindBy(id = "form:toggleSwitch")
        WebElement toggleSwitch;

        @Override
        public String getLocation() {
            return "theming/theming011.xhtml";
        }
    }
}
