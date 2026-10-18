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

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Variants of the README section "Variants (style classes)": sizes, message variants, contrast severity, float label
 * variants and blockUI. Sizes and colors are compared with the resolved tokens, not with fixed values.
 */
class Theming005Test extends AbstractThemingTest {

    private static final String TRANSPARENT = "rgba(0, 0, 0, 0)";

    /** pixel tolerance for layout positions (sub pixel rounding) */
    private static final double TOLERANCE = 1.5;

    @Test
    @Order(1)
    @DisplayName("Theming: ui-selectonemenu-sm / -lg change the label padding and font size")
    void selectOneMenuSizes(Page page) {
        // Arrange
        setColorScheme("light");

        // Act
        double defaultPadding = pixels(computedStyle(page.selectDefaultLabel, "padding-top"));
        double defaultFont = pixels(computedStyle(page.selectDefaultLabel, "font-size"));
        double smPadding = pixels(computedStyle(page.selectSmLabel, "padding-top"));
        double smFont = pixels(computedStyle(page.selectSmLabel, "font-size"));
        double lgPadding = pixels(computedStyle(page.selectLgLabel, "padding-top"));
        double lgFont = pixels(computedStyle(page.selectLgLabel, "font-size"));

        // Assert
        assertTokenValue("SelectOneMenu sm label", page.selectSmLabel, "padding-top", "--p-select-sm-padding-y");
        assertTokenValue("SelectOneMenu sm label", page.selectSmLabel, "padding-left", "--p-select-sm-padding-x");
        assertTokenValue("SelectOneMenu sm label", page.selectSmLabel, "font-size", "--p-select-sm-font-size");
        assertTokenValue("SelectOneMenu lg label", page.selectLgLabel, "padding-top", "--p-select-lg-padding-y");
        assertTokenValue("SelectOneMenu lg label", page.selectLgLabel, "padding-left", "--p-select-lg-padding-x");
        assertTokenValue("SelectOneMenu lg label", page.selectLgLabel, "font-size", "--p-select-lg-font-size");
        assertTrue(smPadding < defaultPadding, "sm padding " + smPadding + " not < default " + defaultPadding);
        assertTrue(lgPadding > defaultPadding, "lg padding " + lgPadding + " not > default " + defaultPadding);
        assertTrue(smFont < defaultFont, "sm font size " + smFont + " not < default " + defaultFont);
        assertTrue(lgFont > defaultFont, "lg font size " + lgFont + " not > default " + defaultFont);
        assertTrue(page.selectSm.getSize().getHeight() < page.selectDefault.getSize().getHeight(), "sm field not lower than default");
        assertTrue(page.selectLg.getSize().getHeight() > page.selectDefault.getSize().getHeight(), "lg field not higher than default");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: ui-chkbox-sm / -lg and ui-radiobutton-sm / -lg change the box size")
    void checkboxRadioSizes(Page page) {
        // Arrange
        setColorScheme("light");

        // Act
        double checkboxDefault = pixels(computedStyle(page.checkboxDefaultBox, "width"));
        double radioDefault = pixels(computedStyle(page.radioDefaultBox, "width"));

        // Assert
        assertTokenValue("Checkbox sm", page.checkboxSmBox, "width", "--p-checkbox-sm-width");
        assertTokenValue("Checkbox sm", page.checkboxSmBox, "height", "--p-checkbox-sm-height");
        assertTokenValue("Checkbox lg", page.checkboxLgBox, "width", "--p-checkbox-lg-width");
        assertTokenValue("Checkbox lg", page.checkboxLgBox, "height", "--p-checkbox-lg-height");
        assertTokenValue("RadioButton sm", page.radioSmBox, "width", "--p-radiobutton-sm-width");
        assertTokenValue("RadioButton sm", page.radioSmBox, "height", "--p-radiobutton-sm-height");
        assertTokenValue("RadioButton lg", page.radioLgBox, "width", "--p-radiobutton-lg-width");
        assertTokenValue("RadioButton lg", page.radioLgBox, "height", "--p-radiobutton-lg-height");
        assertTrue(pixels(computedStyle(page.checkboxSmBox, "width")) < checkboxDefault, "checkbox sm not smaller than default");
        assertTrue(pixels(computedStyle(page.checkboxLgBox, "width")) > checkboxDefault, "checkbox lg not larger than default");
        assertTrue(pixels(computedStyle(page.radioSmBox, "width")) < radioDefault, "radio sm not smaller than default");
        assertTrue(pixels(computedStyle(page.radioLgBox, "width")) > radioDefault, "radio lg not larger than default");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Theming: ui-badge-sm is smaller, ui-badge-contrast / ui-tag-contrast use the contrast tokens")
    void badgeAndContrast(Page page) {
        // Arrange
        setColorScheme("light");

        // Act
        double defaultHeight = pixels(computedStyle(page.badgeDefault, "height"));
        double defaultFont = pixels(computedStyle(page.badgeDefault, "font-size"));

        // Assert: small badge
        assertTokenValue("Badge sm", page.badgeSm, "height", "--p-badge-sm-height");
        assertTokenValue("Badge sm", page.badgeSm, "font-size", "--p-badge-sm-font-size");
        assertTrue(pixels(computedStyle(page.badgeSm, "height")) < defaultHeight, "badge sm not lower than default");
        assertTrue(pixels(computedStyle(page.badgeSm, "font-size")) < defaultFont, "badge sm font not smaller than default");

        // Assert: contrast severity, light and dark
        for (String scheme : new String[] {"light", "dark"}) {
            setColorScheme(scheme);
            assertTokenWired("Badge contrast (" + scheme + ")", page.badgeContrast, "background-color", "--p-badge-contrast-background");
            assertTokenWired("Badge contrast (" + scheme + ")", page.badgeContrast, "color", "--p-badge-contrast-color");
            assertTokenWired("Tag contrast (" + scheme + ")", page.tagContrast, "background-color", "--p-tag-contrast-background");
            assertTokenWired("Tag contrast (" + scheme + ")", page.tagContrast, "color", "--p-tag-contrast-color");
        }
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("Theming: ui-message-outlined (border, no background) and ui-message-simple (no border, no background)")
    void messageVariants(Page page) {
        // Arrange
        setColorScheme("light");
        assertTrue(PrimeSelenium.hasCssClass(page.messageOutlined, "ui-message-warn"), "p:message has no warn message");

        // Assert: outlined
        assertEquals(TRANSPARENT, computedStyle(page.messageOutlined, "background-color"), "outlined message has a background");
        assertEquals("solid", computedStyle(page.messageOutlined, "border-top-style"));
        assertTokenValue("Message outlined", page.messageOutlined, "border-top-width", "--p-message-outlined-border-width");
        assertTokenWired("Message outlined", page.messageOutlined, "border-top-color", "--p-message-warn-outlined-border-color");
        assertTokenWired("Message outlined", page.messageOutlined, "color", "--p-message-warn-outlined-color");

        // Assert: simple
        assertEquals(TRANSPARENT, computedStyle(page.staticSimple, "background-color"), "simple message has a background");
        assertEquals("0px", computedStyle(page.staticSimple, "border-top-width"), "simple message has a border");
        assertEquals("0px", computedStyle(page.staticSimple, "border-left-width"), "simple message has a border");
        assertTokenWired("Message simple", page.staticSimple, "color", "--p-message-error-simple-color");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("Theming: ui-float-label-in puts the active label inside the field at the top")
    void floatLabelIn(Page page) {
        // Arrange
        setColorScheme("light");
        assertTrue(PrimeSelenium.hasCssClass(page.floatIn, "ui-state-filled"), "input is not filled on page load");

        // Act
        Map<String, Object> input = rect(page.floatIn);
        Map<String, Object> label = rect(page.floatInLabel);
        double fontSize = pixels(computedStyle(page.floatInLabel, "font-size"));
        double activeTop = pixels(resolveToken(page.floatInLabel, "top", "--p-floatlabel-in-active-top"));
        double inputContentTop = number(input, "top") + pixels(computedStyle(page.floatIn, "border-top-width"))
                + pixels(computedStyle(page.floatIn, "padding-top"));

        // Assert
        assertTokenValue("Float label in: label", page.floatInLabel, "font-size", "--p-floatlabel-active-font-size");
        assertTokenValue("Float label in: input", page.floatIn, "padding-top", "--p-floatlabel-in-input-padding-top");
        assertEquals(activeTop, number(label, "top") - number(input, "top"), TOLERANCE, "label is not at the active top inside the field");
        assertTrue(number(label, "top") > number(input, "top"), "label is not inside the field");
        assertTrue(number(label, "top") + fontSize <= inputContentTop + TOLERANCE,
                "label text (" + number(label, "top") + " + " + fontSize + ") overlaps the input text at " + inputContentTop);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("Theming: ui-float-label-on puts the active label vertically centered on the top border")
    void floatLabelOn(Page page) {
        // Arrange
        setColorScheme("light");
        assertTrue(PrimeSelenium.hasCssClass(page.floatOn, "ui-state-filled"), "input is not filled on page load");

        // Act
        Map<String, Object> input = rect(page.floatOn);
        Map<String, Object> label = rect(page.floatOnLabel);
        double fontSize = pixels(computedStyle(page.floatOnLabel, "font-size"));
        double verticalPadding = pixels(computedStyle(page.floatOnLabel, "padding-top"))
                + pixels(computedStyle(page.floatOnLabel, "padding-bottom"));
        double borderCenter = number(input, "top") + pixels(computedStyle(page.floatOn, "border-top-width")) / 2;
        double labelCenter = number(label, "top") + number(label, "height") / 2;

        // Assert
        assertTokenValue("Float label on: label", page.floatOnLabel, "font-size", "--p-floatlabel-active-font-size");
        assertTokenWired("Float label on: label", page.floatOnLabel, "background-color", "--p-floatlabel-on-active-background");
        assertEquals(borderCenter, labelCenter, TOLERANCE, "label is not vertically centered on the top border");
        assertTrue(number(label, "height") <= fontSize + verticalPadding + TOLERANCE,
                "label box (" + number(label, "height") + "px) is taller than its font (" + fontSize + "px)");
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("Theming: p:blockUI without content shows a spinner, ui-blockui-light has the light mask background")
    void blockUI(Page page) {
        // Arrange
        setColorScheme("light");
        PrimeSelenium.disableAnimations();

        // Act
        PrimeSelenium.executeScript("PF('blockLight').show(); PF('blockDefault').show();");
        WebElement light = PrimeSelenium.executeScript("return PF('blockLight').blocker.get(0);");
        WebElement mask = PrimeSelenium.executeScript("return PF('blockDefault').blocker.get(0);");

        // Assert
        assertTrue(light.isDisplayed(), "light mask is not shown");
        assertTrue(mask.isDisplayed(), "default mask is not shown");
        assertTokenWired("BlockUI light mask", light, "background-color", "--p-blockui-light-mask-background");
        assertTokenWired("BlockUI default mask", mask, "background-color", "--p-blockui-mask-background");
        assertNotEquals(computedStyle(mask, "background-color"), computedStyle(light, "background-color"),
                "light mask looks like the default mask");
        assertSpinner("BlockUI light", light);
        assertSpinner("BlockUI default", mask);

        // Act
        PrimeSelenium.executeScript("PF('blockLight').hide(); PF('blockDefault').hide();");
        PrimeSelenium.enableAnimations();
        assertNoJavascriptErrors();
    }

    private void assertSpinner(String description, WebElement mask) {
        Map<String, Object> before = PrimeSelenium.executeScript("var cs = getComputedStyle(arguments[0], '::before');"
                + "return {content: cs.getPropertyValue('content'), animationName: cs.getPropertyValue('animation-name'),"
                + " animationDuration: cs.getPropertyValue('animation-duration'),"
                + " iterationCount: cs.getPropertyValue('animation-iteration-count'), width: cs.getPropertyValue('width')};", mask);
        assertEquals("\" \"", before.get("content"), description + ": no ::before spinner");
        String maskImage = pseudoMaskImage(mask, "::before");
        assertTrue(maskImage != null && maskImage.startsWith("url("), description + ": spinner has no mask-image: " + maskImage);
        assertNotEquals("none", before.get("animationName"), description + ": spinner has no animation");
        assertEquals("infinite", before.get("iterationCount"), description + ": spinner animation is not infinite");
        assertEquals(resolveToken(mask, "animation-duration", "--p-blockui-loading-icon-animation-duration"), before.get("animationDuration"),
                description + ": spinner animation duration is not --p-blockui-loading-icon-animation-duration");
        assertTrue(pixels((String) before.get("width")) > 0, description + ": spinner has no size");
    }

    private static Map<String, Object> rect(WebElement element) {
        return PrimeSelenium.executeScript("return arguments[0].getBoundingClientRect().toJSON();", element);
    }

    private static double number(Map<String, Object> map, String key) {
        return ((Number) map.get(key)).doubleValue();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectDefault")
        WebElement selectDefault;

        @FindBy(id = "form:selectSm")
        WebElement selectSm;

        @FindBy(id = "form:selectLg")
        WebElement selectLg;

        @FindBy(css = "[id='form:selectDefault'] .ui-selectonemenu-label")
        WebElement selectDefaultLabel;

        @FindBy(css = "[id='form:selectSm'] .ui-selectonemenu-label")
        WebElement selectSmLabel;

        @FindBy(css = "[id='form:selectLg'] .ui-selectonemenu-label")
        WebElement selectLgLabel;

        @FindBy(css = "[id='form:checkboxDefault'] .ui-chkbox-box")
        WebElement checkboxDefaultBox;

        @FindBy(css = "[id='form:checkboxSm'] .ui-chkbox-box")
        WebElement checkboxSmBox;

        @FindBy(css = "[id='form:checkboxLg'] .ui-chkbox-box")
        WebElement checkboxLgBox;

        @FindBy(css = "[id='form:radioDefault'] .ui-radiobutton-box")
        WebElement radioDefaultBox;

        @FindBy(css = "[id='form:radioSm'] .ui-radiobutton-box")
        WebElement radioSmBox;

        @FindBy(css = "[id='form:radioLg'] .ui-radiobutton-box")
        WebElement radioLgBox;

        @FindBy(id = "form:badgeDefault")
        WebElement badgeDefault;

        @FindBy(id = "form:badgeSm")
        WebElement badgeSm;

        @FindBy(id = "form:badgeContrast")
        WebElement badgeContrast;

        @FindBy(id = "form:tagContrast")
        WebElement tagContrast;

        @FindBy(id = "form:messageOutlined")
        WebElement messageOutlined;

        @FindBy(id = "form:staticSimple")
        WebElement staticSimple;

        @FindBy(id = "form:floatIn")
        WebElement floatIn;

        @FindBy(id = "form:floatInLabel")
        WebElement floatInLabel;

        @FindBy(id = "form:floatOn")
        WebElement floatOn;

        @FindBy(id = "form:floatOnLabel")
        WebElement floatOnLabel;

        @Override
        public String getLocation() {
            return "theming/theming005.xhtml";
        }
    }
}
