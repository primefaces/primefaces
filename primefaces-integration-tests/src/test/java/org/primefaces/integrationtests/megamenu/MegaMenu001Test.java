/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.MegaMenu;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MegaMenu001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("MegaMenu: basic (horizontal, click to open)")
    void basic(Page page) {
        // Arrange
        MegaMenu megamenu = page.megamenu;

        // Act - open the "Videos" mega panel and select a leaf item
        WebElement videos = megamenu.openRootSubmenu("Videos");
        megamenu.selectMenuitemByValue(videos, "Video 1.1");

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Video 1.1"));

        // Act - a root menuitem (leaf) directly fires its action
        WebElement contact = megamenu.findElement(By.id("form:contact"));
        PrimeSelenium.guardAjax(contact).click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Contact"));

        assertConfiguration(megamenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("MegaMenu: render and column structure (horizontal)")
    void renderStructure(Page page) {
        // Arrange
        MegaMenu megamenu = page.megamenu;

        // Assert - horizontal layout
        assertTrue(PrimeSelenium.hasCssClass(megamenu.getRoot(), "ui-megamenu"));
        assertFalse(megamenu.isVertical());

        // Assert - mega panels render as a table of columns (one <td> per p:column), even while collapsed
        WebElement videos = megamenu.findElement(By.id("form:videos"));
        assertEquals(2, videos.findElements(By.cssSelector("ul.ui-menu-child td")).size());

        WebElement users = megamenu.findElement(By.id("form:users"));
        assertEquals(3, users.findElements(By.cssSelector("ul.ui-menu-child td")).size());

        // Assert - column group titles render as non-clickable headers
        WebElement title = users.findElement(By.cssSelector("li.ui-widget-header"));
        assertEquals("User 1", title.getAttribute("textContent").trim());

        assertConfiguration(megamenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("MegaMenu Config = " + cfg);
        assertTrue(cfg.has("autoDisplay"));
        assertFalse(cfg.getBoolean("autoDisplay"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:megamenu")
        MegaMenu megamenu;

        @Override
        public String getLocation() {
            return "megamenu/megaMenu001.xhtml";
        }
    }
}
