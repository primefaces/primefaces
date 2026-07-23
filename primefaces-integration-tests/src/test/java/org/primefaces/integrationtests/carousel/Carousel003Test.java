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

class Carousel003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Carousel: circular mode keeps both navigators enabled and wraps around")
    void circular(Page page) {
        // Arrange
        Carousel carousel = page.carousel;
        assertDisplayed(carousel);
        // stop the autoplay timer so navigation is deterministic
        carousel.stopAutoplay();
        // normalize to the first page in case autoplay already advanced
        carousel.select(0);
        assertEquals(0, carousel.getPage());

        // Assert - in circular mode neither navigator is disabled, even on the first page
        assertFalse(carousel.isBackwardDisabled(), "Backward button should be enabled in circular mode");
        assertFalse(carousel.isForwardDisabled(), "Forward button should be enabled in circular mode");

        // circular mode clones items at both ends for the infinite-scroll illusion
        assertFalse(carousel.getClonedItems().isEmpty(), "Circular carousel should render cloned items");

        // Act - going backward from the first page wraps around to the last page
        carousel.previous();

        // Assert
        assertEquals(4, carousel.getPage());
        assertFalse(carousel.isBackwardDisabled(), "Backward button should be enabled in circular mode");
        assertFalse(carousel.isForwardDisabled(), "Forward button should be enabled in circular mode");

        assertConfiguration(carousel.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Carousel Config = " + cfg);
        assertTrue(cfg.has("id"));
        assertTrue(cfg.getBoolean("circular"));
        assertEquals(5000, cfg.getInt("autoplayInterval"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:carousel")
        Carousel carousel;

        @Override
        public String getLocation() {
            return "carousel/carousel003.xhtml";
        }
    }
}
