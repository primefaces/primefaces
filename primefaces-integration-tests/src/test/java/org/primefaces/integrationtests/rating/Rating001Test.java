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
package org.primefaces.integrationtests.rating;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Rating;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Rating001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Rating: set value and cancel value using AJAX")
    void ajax(Page page) {
        // Arrange
        Rating rating = page.ratingAjax;
        Messages messages = page.messages;
        assertNull(rating.getValue());

        // Act - add value
        rating.setValue(4);

        // Assert - rate-event
        assertEquals(4L, rating.getValue());
        assertEquals("Rate Event", messages.getMessage(0).getSummary());
        assertEquals("You rated:4", messages.getMessage(0).getDetail());

        // Act - cancel value
        rating.cancel();

        // Assert
        assertNull(rating.getValue());
        assertEquals("Cancel Event", messages.getMessage(0).getSummary());
        assertEquals("Rate Reset:null", messages.getMessage(0).getDetail());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Rating: widget reset() method")
    void ajaxReset(Page page) {
        // Arrange
        Rating rating = page.ratingAjax;
        Messages messages = page.messages;
        assertNull(rating.getValue());

        // Act - add value
        rating.setValue(4);

        // Assert - rate-event
        assertEquals(4L, rating.getValue());
        assertEquals("Rate Event", messages.getMessage(0).getSummary());
        assertEquals("You rated:4", messages.getMessage(0).getDetail());

        // Act - cancel value
        rating.reset();

        // Assert
        assertNull(rating.getValue());
        assertEquals("Cancel Event", messages.getMessage(0).getSummary());
        assertEquals("Rate Reset:null", messages.getMessage(0).getDetail());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Rating: read only")
    void readonly(Page page) {
        // Arrange
        Rating rating = page.ratingReadOnly;
        assertEquals(3L, rating.getValue());
        assertTrue(rating.isReadOnly());

        // Act
        rating.setValue(5);

        // Assert
        assertEquals(3L, rating.getValue());
        JSONObject cfg = assertConfiguration(rating.getWidgetConfiguration());
        assertTrue(cfg.getBoolean("readonly"));
    }

    @Test
    @Order(4)
    @DisplayName("Rating: disabled")
    void disabled(Page page) {
        // Arrange
        Rating rating = page.ratingDisabled;
        assertEquals(3L, rating.getValue());
        assertTrue(rating.isDisabled());

        // Act
        rating.setValue(5);

        // Assert
        assertEquals(3L, rating.getValue());
        JSONObject cfg = assertConfiguration(rating.getWidgetConfiguration());
        assertTrue(cfg.getBoolean("disabled"));
    }

    @Test
    @Order(5)
    @DisplayName("Rating: enable and disable")
    void enableAndDisable(Page page) {
        // Arrange
        Rating rating = page.ratingDisabled;
        assertEquals(3L, rating.getValue());
        assertTrue(rating.isDisabled());

        // Act - enable
        rating.enable();
        rating.setValue(5);

        // Assert
        assertTrue(rating.isEnabled());
        assertEquals(5L, rating.getValue());

        // Act - disable
        rating.disable();
        rating.setValue(1);

        // Assert
        assertTrue(rating.isDisabled());
        assertEquals(5L, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("Rating: set value to a string should default it to no stars")
    void invalidNumberClientSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        assertEquals(2L, rating.getValue());

        // Act
        PrimeSelenium.executeScript(rating.getWidgetByIdScript() + ".setValue('abc');");

        // Assert
        assertNull(rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("Rating: set value below minimum should set to no stars")
    void minimumClientSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        assertEquals(2L, rating.getValue());

        // Act
        rating.setValue(-1);

        // Assert
        assertNull(rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("Rating: set value above maximum should set to max stars")
    void maximumClientSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        assertEquals(2L, rating.getValue());

        // Act
        rating.setValue(14);

        // Assert
        assertEquals(8L, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("Rating: Submit value below minimum should return NULL")
    void minimumServerSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        assertEquals(2L, rating.getValue());

        // Act
        PrimeSelenium.setHiddenInput(rating.getInput(), "-1");
        assertEquals("0", rating.getInput().getAttribute("value"));
        page.submit.click();

        // Assert
        assertEquals(null, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("Rating: Submit value above maximum should return max value")
    void maximumServerSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        assertEquals(2L, rating.getValue());

        // Act
        PrimeSelenium.setHiddenInput(rating.getInput(), "14");
        assertEquals("8", rating.getInput().getAttribute("value"));
        page.submit.click();

        // Assert
        assertEquals(8L, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("Rating: set value to a string should default range to exact middle of min and max")
    void invalidNumberServerSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        assertEquals(2L, rating.getValue());

        // Act
        PrimeSelenium.setHiddenInput(rating.getInput(), "def");
        page.submit.click();

        // Assert
        // 4 is the exact middle between min=0 max=8 which is range default
        assertEquals(4L, rating.getValue());
    }

    @Test
    @Order(12)
    @DisplayName("Rating: cancel the value and check it should be NULL server side")
    void cancelSetsNull(Page page) {
        // Arrange
        Rating rating = page.ratingAjax;
        Messages messages = page.messages;
        assertNull(rating.getValue());

        // Act
        rating.setValue(3);
        rating.cancel();
        assertEquals("Cancel Event", messages.getMessage(0).getSummary());
        assertEquals("Rate Reset:null", messages.getMessage(0).getDetail());

        // Act
        page.submit.click();

        // Assert
        assertNull(rating.getValue());
    }

    @Test
    @Order(13)
    @DisplayName("Rating: Submit required value with a value should not return an error")
    void requiredWithValue(Page page) {
        // Arrange
        Rating rating = page.ratingRequired;
        assertEquals(4L, rating.getValue());

        // Act
        PrimeSelenium.setHiddenInput(rating.getInput(), "5");
        assertEquals("5", rating.getInput().getAttribute("value"));
        page.submit.click();

        // Assert
        assertEquals(5L, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(14)
    @DisplayName("Rating: Submit required value without a value should validation error")
    void requiredWithoutValue(Page page) {
        // Arrange
        Rating rating = page.ratingRequired;
        assertEquals(4L, rating.getValue());

        // Act
        rating.reset();
        assertEquals("0", rating.getInput().getAttribute("value"));
        page.submit.click();

        // Assert
        assertNull(rating.getValue());
        assertEquals("Please rate us!", page.messages.getMessage(0).getDetail());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    private JSONObject assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Rating Config = " + cfg);
        assertTrue(cfg.has("id"));
        return cfg;
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:ajax")
        Rating ratingAjax;

        @FindBy(id = "form:readonly")
        Rating ratingReadOnly;

        @FindBy(id = "form:disabled")
        Rating ratingDisabled;

        @FindBy(id = "form:minmax")
        Rating ratingMinMax;

        @FindBy(id = "form:required")
        Rating ratingRequired;

        @FindBy(id = "form:button")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "rating/rating001.xhtml";
        }
    }
}
