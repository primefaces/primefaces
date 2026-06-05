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
package org.primefaces.integrationtests.carousel;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Carousel;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Carousel002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Carousel: pageChange AJAX event fires the listener with the new page index")
    void pageChangeEvent(Page page) {
        // Arrange
        Carousel carousel = page.carousel;
        assertDisplayed(carousel);
        assertEquals(0, carousel.getPage());

        // Act - navigate forward (AJAX guarded because of the pageChange behavior)
        carousel.next();

        // Assert - listener fired with the new 0-based page index
        assertEquals("pageChange", page.messages.getMessage(0).getSummary());
        assertEquals("1", page.messages.getMessage(0).getDetail());
        assertEquals(1, carousel.getPage());

        assertConfiguration(carousel.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Carousel: current page must not be lost after a full AJAX update of the form")
    void stateAfterUpdate(Page page) {
        // Arrange
        Carousel carousel = page.carousel;
        assertDisplayed(carousel);

        // Act - move to page 2 then trigger a full @form AJAX update
        carousel.select(2);
        assertEquals(2, carousel.getPage());
        page.button.click();

        // Assert - the carousel is re-rendered but keeps the current page
        assertEquals(2, carousel.getPage());
        assertEquals("Item 3", carousel.getActiveItems().get(0).getText());

        assertConfiguration(carousel.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Carousel Config = " + cfg);
        assertTrue(cfg.has("id"));
        if (cfg.has("behaviors")) {
            JSONObject behaviors = cfg.getJSONObject("behaviors");
            assertTrue(behaviors.has("pageChange"));
        }
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:carousel")
        Carousel carousel;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "carousel/carousel002.xhtml";
        }
    }
}
