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
import org.primefaces.selenium.component.MegaMenu;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MegaMenu001bTest extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("MegaMenu: basic (horizontal, hover to open)")
    void basic(Page page) {
        // Arrange
        MegaMenu megamenu = page.megamenu;

        // Act - hover opens the "Users" mega panel, then select a leaf item
        WebElement users = megamenu.openRootSubmenu("Users");
        megamenu.selectMenuitemByValue(users, "User 2.1");

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("User 2.1"));

        // Act - open another panel and select a different leaf
        WebElement videos = megamenu.openRootSubmenu("Videos");
        megamenu.selectMenuitemByValue(videos, "Video 3.2");

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("Video 3.2"));

        assertConfiguration(megamenu.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("MegaMenu Config = " + cfg);
        assertTrue(cfg.has("autoDisplay"));
        assertTrue(cfg.getBoolean("autoDisplay"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:megamenu")
        MegaMenu megamenu;

        @Override
        public String getLocation() {
            return "megamenu/megaMenu001b.xhtml";
        }
    }
}
