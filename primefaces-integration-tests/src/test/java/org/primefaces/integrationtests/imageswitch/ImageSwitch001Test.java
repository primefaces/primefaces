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
package org.primefaces.integrationtests.imageswitch;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.ImageSwitch;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class ImageSwitch001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ImageSwitch: next and previous switch the image and wrap around")
    void nextPrevious(Page page) {
        // Arrange
        ImageSwitch imageSwitch = page.manual;
        assertEquals(3, imageSwitch.getImages().size());
        assertActive(imageSwitch, 0, "Bamboo Watch");

        // Act
        imageSwitch.next();

        // Assert
        assertActive(imageSwitch, 1, "Black Watch");

        // Act
        imageSwitch.previous();

        // Assert
        assertActive(imageSwitch, 0, "Bamboo Watch");

        // Act
        imageSwitch.previous();

        // Assert
        assertActive(imageSwitch, 2, "Blue T-Shirt");

        // Act
        imageSwitch.next();

        // Assert
        assertActive(imageSwitch, 0, "Bamboo Watch");
        assertConfiguration(imageSwitch.getWidgetConfiguration(), 0);
    }

    @Test
    @Order(2)
    @DisplayName("ImageSwitch: switchTo shows the image with the given index")
    void switchTo(Page page) {
        // Arrange
        ImageSwitch imageSwitch = page.manual;

        // Act
        imageSwitch.switchTo(2);

        // Assert
        assertActive(imageSwitch, 2, "Blue T-Shirt");

        // Act
        imageSwitch.switchTo(1);

        // Assert
        assertActive(imageSwitch, 1, "Black Watch");
        assertFalse(imageSwitch.isSlideshowAuto());
        assertConfiguration(imageSwitch.getWidgetConfiguration(), 0);
    }

    @Test
    @Order(3)
    @DisplayName("ImageSwitch: slideshow switches the images automatically, also after pause and resume")
    void slideshow(Page page) {
        // Arrange
        ImageSwitch imageSwitch = page.slideshow;
        assertTrue(imageSwitch.isSlideshowAuto());
        assertFalse(imageSwitch.isSlideshowPaused());
        int index = imageSwitch.getActiveIndex();

        // Act
        imageSwitch.waitUntilNextSlide();

        // Assert
        assertNotEquals(index, imageSwitch.getActiveIndex());

        // Act
        imageSwitch.pauseSlideshow();

        // Assert
        assertTrue(imageSwitch.isSlideshowPaused());

        // Act
        imageSwitch.resumeSlideshow();
        index = imageSwitch.getActiveIndex();
        imageSwitch.waitUntilNextSlide();

        // Assert
        assertFalse(imageSwitch.isSlideshowPaused());
        assertNotEquals(index, imageSwitch.getActiveIndex());
        assertConfiguration(imageSwitch.getWidgetConfiguration(), 500);
    }

    private void assertActive(ImageSwitch imageSwitch, int index, String alt) {
        assertEquals(index, imageSwitch.getActiveIndex());
        List<WebElement> images = imageSwitch.getImages();
        for (int i = 0; i < images.size(); i++) {
            assertEquals(i == index, images.get(i).isDisplayed(), "image " + i);
        }
        assertEquals(alt, images.get(index).getDomAttribute("alt"));
    }

    private void assertConfiguration(JSONObject cfg, int timeout) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("id"));
        assertEquals("fade", cfg.getString("fx"));
        assertEquals(100, cfg.getInt("speed"));
        assertEquals(timeout, cfg.getInt("timeout"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:manual")
        ImageSwitch manual;

        @FindBy(id = "form:slideshow")
        ImageSwitch slideshow;

        @Override
        public String getLocation() {
            return "imageswitch/imageSwitch001.xhtml";
        }
    }
}
