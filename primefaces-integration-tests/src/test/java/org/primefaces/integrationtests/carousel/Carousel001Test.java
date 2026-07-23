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
package org.primefaces.integrationtests.carousel;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Carousel;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Carousel001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Carousel: basic navigation with next/previous buttons and paginator indicators")
    void basic(Page page) {
        // Arrange
        Carousel carousel = page.carousel;
        assertDisplayed(carousel);

        // Assert initial state - 5 items, 5 indicators, first page active, backward disabled
        assertEquals(5, carousel.getItems().size());
        assertEquals(5, carousel.getIndicators().size());
        assertEquals(0, carousel.getPage());
        assertEquals("Item 1", carousel.getActiveItems().get(0).getText());
        assertTrue(carousel.isBackwardDisabled(), "Backward button should be disabled on first page");
        assertFalse(carousel.isForwardDisabled(), "Forward button should be enabled on first page");

        // Act - go to next page
        carousel.next();

        // Assert
        assertEquals(1, carousel.getPage());
        assertEquals("Item 2", carousel.getActiveItems().get(0).getText());
        assertFalse(carousel.isBackwardDisabled(), "Backward button should be enabled on second page");
        assertFalse(carousel.isForwardDisabled(), "Forward button should be enabled on second page");

        // Act - go to previous page
        carousel.previous();

        // Assert
        assertEquals(0, carousel.getPage());
        assertEquals("Item 1", carousel.getActiveItems().get(0).getText());
        assertTrue(carousel.isBackwardDisabled(), "Backward button should be disabled on first page");
        assertFalse(carousel.isForwardDisabled(), "Forward button should be enabled on first page");

        assertConfiguration(carousel.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Carousel: navigate to last page via indicator and assert forward button is disabled")
    void lastPage(Page page) {
        // Arrange
        Carousel carousel = page.carousel;
        assertDisplayed(carousel);

        // Act - jump to the last page by clicking its indicator
        carousel.select(4);

        // Assert - last page active and forward disabled
        assertEquals(4, carousel.getPage());
        assertEquals("Item 5", carousel.getActiveItems().get(0).getText());
        assertTrue(carousel.isForwardDisabled(), "Forward button should be disabled on last page");
        assertFalse(carousel.isBackwardDisabled(), "Backward button should be enabled on last page");

        assertConfiguration(carousel.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Carousel Config = " + cfg);
        assertTrue(cfg.has("id"));
        assertTrue(cfg.getBoolean("paginator"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:carousel")
        Carousel carousel;

        @Override
        public String getLocation() {
            return "carousel/carousel001.xhtml";
        }
    }
}
