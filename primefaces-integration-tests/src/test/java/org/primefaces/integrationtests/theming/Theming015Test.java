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

import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Dialog;
import org.primefaces.selenium.component.SelectOneMenu;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Layering in every preset (Glass puts backdrop filters on the floating layer, which must not create stacking contexts or
 * containing blocks around overlays): a dropdown inside a toolbar lies above the following positioned content and receives
 * the clicks there, a non modal dialog leaves the page outside of its box clickable and unblurred.
 */
class Theming015Test extends AbstractThemingTest {

    /** the element at the center of the given element as the browser hit tests it (what a click there reaches) */
    private static final String HIT_SCRIPT = ""
            + "var r = arguments[0].getBoundingClientRect();"
            + "return document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2);";

    private static final String INTERSECTS_SCRIPT = ""
            + "var a = arguments[0].getBoundingClientRect(), b = arguments[1].getBoundingClientRect();"
            + "return a.left < b.right && b.left < a.right && a.top < b.bottom && b.top < a.bottom;";

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara", "nora", "metal", "glass"})
    @Order(1)
    @DisplayName("Theming: a dropdown inside a toolbar lies above the following content and its options can be clicked")
    void dropdownInToolbar(String preset) {
        // Arrange
        open(preset);
        SelectOneMenu select = PrimeSelenium.createFragment(SelectOneMenu.class, By.id("form:select"));
        WebElement after = PrimeSelenium.getWebDriver().findElement(By.id("after"));
        select.show();
        WebElement panel = select.getPanel();
        List<WebElement> items = panel.findElements(By.cssSelector("li.ui-selectonemenu-item"));
        WebElement last = items.get(items.size() - 1);
        Boolean overlaps = PrimeSelenium.executeScript(INTERSECTS_SCRIPT, last, after);
        assertTrue(overlaps, preset + ": the last option does not lie over the following content, the test proves nothing");

        // Assert: what a click on the last option reaches
        assertTrue(contains(panel, hit(last)), preset + ": the option is covered by " + describe(hit(last)));
        WebElement toolbar = PrimeSelenium.getWebDriver().findElement(By.id("form:toolbar"));
        assertEquals("none", computedStyle(toolbar, "backdrop-filter"), preset + ": the toolbar itself must not get a backdrop filter");

        // Act
        last.click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(panel));

        // Assert
        assertEquals("Vienna", select.getSelectedLabel());
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @ValueSource(strings = {"aura", "lara", "nora", "metal", "glass"})
    @Order(2)
    @DisplayName("Theming: a non modal dialog leaves the page outside of its box clickable and unblurred")
    void nonModalDialog(String preset) {
        // Arrange
        open(preset);
        Dialog dialog = PrimeSelenium.createFragment(Dialog.class, By.id("form:dialog"));
        WebElement pageButton = PrimeSelenium.getWebDriver().findElement(By.id("form:pageButton"));

        // Act
        dialog.show();

        // Assert
        WebElement box = dialog.findElement(By.cssSelector(".ui-dialog-box"));
        Boolean overlaps = PrimeSelenium.executeScript(INTERSECTS_SCRIPT, box, pageButton);
        assertFalse(overlaps, preset + ": the dialog box covers the page button, the test proves nothing");
        assertTrue(contains(pageButton, hit(pageButton)), preset + ": the page button is covered by " + describe(hit(pageButton)));
        assertEquals("none", computedStyle(dialog.getWrappedElement(), "backdrop-filter"),
                preset + ": the full viewport dialog container must not blur the page");
        assertNoJavascriptErrors();
    }

    private void open(String preset) {
        PrimeSelenium.goTo("theming/theming015.xhtml?theme=" + preset);
        setColorScheme("light");
    }

    private static WebElement hit(WebElement element) {
        return PrimeSelenium.executeScript(HIT_SCRIPT, element);
    }

    private static boolean contains(WebElement container, WebElement element) {
        Boolean contains = PrimeSelenium.executeScript("return arguments[0].contains(arguments[1]);", container, element);
        return contains;
    }

    private static String describe(WebElement element) {
        return element == null ? "nothing" : element.getTagName() + " id=" + element.getDomAttribute("id") + " class="
                + element.getDomAttribute("class");
    }
}
