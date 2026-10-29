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
package org.primefaces.integrationtests.megamenu;

import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.MegaMenu;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

class MegaMenu004Test extends AbstractPrimePageTest {

    @ParameterizedTest
    @CsvSource({"horizontal,true,0", "horizontal,true,1", "horizontal,false,0", "horizontal,false,1",
        "vertical,true,0", "vertical,true,1", "vertical,false,0", "vertical,false,1"})
    @Order(1)
    @DisplayName("MegaMenu: #14408 activeIndex survives hover, dismissal, keyboard navigation and refresh")
    void activeIndex(String orientation, boolean auto, int index) {
        // Arrange
        goTo("megamenu/megaMenu004.xhtml?orientation=" + orientation + "&auto=" + auto + "&index=" + index);
        MegaMenu menu = PrimeSelenium.createFragment(MegaMenu.class, By.id("form:menu"));
        assertEquals(index, menu.getWidgetConfiguration().getInt("activeIndex"));
        WebElement selected = roots().get(index);
        assertTrue(PrimeSelenium.hasCssClass(selected, "p-menuitem-selected"));
        assertEquals("page", selected.findElement(By.tagName("a")).getDomAttribute("aria-current"));
        assertFalse(PrimeSelenium.hasCssClass(selected.findElement(By.tagName("a")), "p-state-hover"));
        assertEquals("0", selected.findElement(By.tagName("a")).getDomAttribute("tabindex"));

        // Act: open another panel, then dismiss it outside the menu.
        WebElement other = openRoot(1 - index, auto);
        WebElement panel = other.findElement(By.cssSelector("ul.p-menu-child"));
        // Assert: opening another panel must not erase the configured current item.
        assertTrue(PrimeSelenium.hasCssClass(selected, "p-menuitem-selected"));
        assertEquals("page", selected.findElement(By.tagName("a")).getDomAttribute("aria-current"));
        PrimeSelenium.getWebDriver().findElement(By.id("form:outside")).click();
        PrimeSelenium.waitGui().until(ExpectedConditions.invisibilityOf(panel));
        assertSelection(index);
        assertEquals("false", other.findElement(By.tagName("a")).getDomAttribute("aria-expanded"));

        // Act: keyboard navigation changes the interaction target, not the current page.
        WebElement first = roots().get(0).findElement(By.tagName("a"));
        first.sendKeys("vertical".equals(orientation) ? Keys.ARROW_DOWN : Keys.ARROW_RIGHT);
        assertEquals(Long.valueOf(1), PrimeSelenium.<Long>executeScript("return PF('menu').activeitem.index();"));
        PrimeSelenium.getWebDriver().findElement(By.id("form:outside")).click();
        // Assert
        assertSelection(index);
        PrimeSelenium.guardAjax(PrimeSelenium.getWebDriver().findElement(By.id("form:refresh"))).click();
        assertSelection(index);
        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 2})
    @Order(2)
    @DisplayName("MegaMenu: out-of-range activeIndex does not leave a persistent highlight")
    void invalidIndex(int index) {
        // Arrange
        goTo("megamenu/megaMenu004.xhtml?index=" + index);
        // Act
        openRoot(0, true);
        PrimeSelenium.getWebDriver().findElement(By.id("form:outside")).click();
        // Assert
        assertSelection(-1);
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("MegaMenu: no persistent selection unless activeIndex is configured")
    void defaultIndex() {
        // Arrange / Act
        goTo("megamenu/megaMenu004.xhtml");
        MegaMenu menu = PrimeSelenium.createFragment(MegaMenu.class, By.id("form:defaultMenu"));
        // Assert
        assertFalse(menu.getWidgetConfiguration().has("activeIndex"));
        assertTrue(menu.findElements(By.cssSelector(".p-menuitem-active, .p-menuitem-selected, [aria-current]")).isEmpty());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("MegaMenu: current root link skips hidden items, disabled links and separators")
    void rootLink() {
        // Arrange / Act
        goTo("megamenu/megaMenu004.xhtml");
        WebElement menu = PrimeSelenium.getWebDriver().findElement(By.id("form:itemsMenu"));
        List<WebElement> selected = menu.findElements(By.cssSelector(".p-menuitem-selected > a"));
        // Assert
        assertEquals(1, selected.size());
        assertEquals("Current link", selected.get(0).getText());
        assertEquals("page", selected.get(0).getDomAttribute("aria-current"));
        assertEquals("0", selected.get(0).getDomAttribute("tabindex"));
        assertTrue(menu.findElements(By.cssSelector(".p-menuitem-active")).isEmpty());
        assertNoJavascriptErrors();
    }

    private WebElement openRoot(int index, boolean auto) {
        // Locate by index: an already open panel changes the root item's full text.
        WebElement item = roots().get(index);
        WebElement link = item.findElement(By.tagName("a"));
        new Actions(PrimeSelenium.getWebDriver()).moveToElement(link).perform();
        if (!auto) {
            link.click();
        }
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(item.findElement(By.cssSelector("ul.p-menu-child"))));
        return item;
    }

    private List<WebElement> roots() {
        return PrimeSelenium.getWebDriver().findElements(By.cssSelector("#form\\:menu > ul > li.p-menuitem"));
    }

    private void assertSelection(int index) {
        List<WebElement> items = roots();
        assertEquals(2, items.size());
        for (int i = 0; i < items.size(); i++) {
            assertEquals(i == index, PrimeSelenium.hasCssClass(items.get(i), "p-menuitem-selected"));
            assertEquals(i == index ? "page" : null, items.get(i).findElement(By.tagName("a")).getDomAttribute("aria-current"));
            assertFalse(PrimeSelenium.hasCssClass(items.get(i), "p-menuitem-active"));
            assertEquals(i == (index < 0 ? 0 : index) ? "0" : "-1",
                    items.get(i).findElement(By.tagName("a")).getDomAttribute("tabindex"));
            assertFalse(PrimeSelenium.hasCssClass(items.get(i).findElement(By.tagName("a")), "p-state-hover"));
        }
    }
}
