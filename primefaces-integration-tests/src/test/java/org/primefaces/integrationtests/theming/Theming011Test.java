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
import org.primefaces.selenium.component.SelectOneButton;
import org.primefaces.selenium.component.Slider;
import org.primefaces.selenium.component.ToggleSwitch;

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

    @Test
    @Order(1)
    @DisplayName("Theming: Metal preset renders primefaces-metal:theme.css (?theme=metal)")
    void rendered(MetalPage page) {
        // Assert
        List<WebElement> links = PrimeSelenium.getWebDriver().findElements(By.cssSelector("link[href*='ln=primefaces-']"));
        assertTrue(links.stream().anyMatch(l -> l.getDomAttribute("href").contains("ln=primefaces-metal")), "primefaces-metal:theme.css not rendered");
        assertFalse(links.stream().anyMatch(l -> l.getDomAttribute("href").contains("ln=primefaces-aura")), "primefaces-aura:theme.css rendered with metal");
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
        WebElement label = page.primary.getLabelElement();
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
            for (CommandButton button : List.of(page.primary, page.secondary, page.outlined, page.info, page.success, page.warning,
                    page.help, page.danger, page.contrast)) {
                double ratio = minContrast(button.getLabelElement(), button, null);
                assertTrue(ratio >= 4.5, scheme + " " + button.getAttribute("id") + ": " + ratio);
            }
            WebElement checked = page.selectButton.getActiveOption();
            double ratio = minContrast(checked.findElement(By.cssSelector(".p-button-text")), checked, "::before");
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
        WebElement handle = page.slider.getHandle();
        WebElement toggleHandle = page.toggleSwitch.getHandle();

        // Assert: the toggleSwitch is checked, its knob is the aluminium one the slider uses
        String sliderKnob = PrimeSelenium.getComputedStyle(handle, "::before", "background-image");
        assertTrue(sliderKnob.startsWith("conic-gradient"), "slider knob is no turned metal: " + sliderKnob);
        assertEquals(computedStyle(toggleHandle, "background-image"), sliderKnob);
        assertEquals(computedStyle(toggleHandle, "box-shadow"),
                PrimeSelenium.getComputedStyle(handle, "::before", "box-shadow"));
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
        assertEquals("none", computedStyle(page.primary.getLabelElement(), "text-shadow"));
        assertEquals("none", computedStyle(page.toggleSwitch.getHandle(), "box-shadow"));
        assertNoJavascriptErrors();
    }

    public static class MetalPage extends AbstractPrimePage {
        @FindBy(id = "form:primary")
        CommandButton primary;

        @FindBy(id = "form:secondary")
        CommandButton secondary;

        @FindBy(id = "form:outlined")
        CommandButton outlined;

        @FindBy(id = "form:info")
        CommandButton info;

        @FindBy(id = "form:success")
        CommandButton success;

        @FindBy(id = "form:warning")
        CommandButton warning;

        @FindBy(id = "form:help")
        CommandButton help;

        @FindBy(id = "form:danger")
        CommandButton danger;

        @FindBy(id = "form:contrast")
        CommandButton contrast;

        @FindBy(id = "form:selectButton")
        SelectOneButton selectButton;

        @FindBy(id = "form:toggleSwitch")
        ToggleSwitch toggleSwitch;

        @FindBy(id = "form:slider")
        Slider slider;

        @Override
        public String getLocation() {
            return "theming/theming011.xhtml?theme=metal";
        }
    }

    public static class AuraPage extends AbstractPrimePage {
        @FindBy(id = "form:primary")
        CommandButton primary;

        @FindBy(id = "form:toggleSwitch")
        ToggleSwitch toggleSwitch;

        @Override
        public String getLocation() {
            return "theming/theming011.xhtml";
        }
    }
}
