/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Rating;

public class Rating001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Rating: set value and cancel value using AJAX")
    public void testAjax(Page page) {
        // Arrange
        Rating rating = page.ratingAjax;
        Messages messages = page.messages;
        Assertions.assertNull(rating.getValue());

        // Act - add value
        PrimeSelenium.guardAjax(rating).setValue(4);

        // Assert - rate-event
        Assertions.assertEquals(4L, rating.getValue());
        Assertions.assertEquals("Rate Event", messages.getMessage(0).getSummary());
        Assertions.assertEquals("You rated:4", messages.getMessage(0).getDetail());

        // Act - cancel value
        rating.cancel();

        // Assert
        Assertions.assertNull(rating.getValue());
        Assertions.assertEquals("Cancel Event", messages.getMessage(0).getSummary());
        Assertions.assertEquals("Rate Reset", messages.getMessage(0).getDetail());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Rating: widget reset() method")
    public void testAjaxReset(Page page) {
        // Arrange
        Rating rating = page.ratingAjax;
        Messages messages = page.messages;
        Assertions.assertNull(rating.getValue());

        // Act - add value
        rating.setValue(4);

        // Assert - rate-event
        Assertions.assertEquals(4L, rating.getValue());
        Assertions.assertEquals("Rate Event", messages.getMessage(0).getSummary());
        Assertions.assertEquals("You rated:4", messages.getMessage(0).getDetail());

        // Act - cancel value
        rating.reset();

        // Assert
        Assertions.assertNull(rating.getValue());
        Assertions.assertEquals("Cancel Event", messages.getMessage(0).getSummary());
        Assertions.assertEquals("Rate Reset", messages.getMessage(0).getDetail());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Rating: read only")
    public void testReadonly(Page page) {
        // Arrange
        Rating rating = page.ratingReadOnly;
        Assertions.assertEquals(3L, rating.getValue());
        Assertions.assertTrue(rating.isReadOnly());

        // Act
        rating.setValue(5);

        // Assert
        Assertions.assertEquals(3L, rating.getValue());
        JSONObject cfg = assertConfiguration(rating.getWidgetConfiguration());
        Assertions.assertTrue(cfg.getBoolean("readonly"));
    }

    @Test
    @Order(4)
    @DisplayName("Rating: disabled")
    public void testDisabled(Page page) {
        // Arrange
        Rating rating = page.ratingDisabled;
        Assertions.assertEquals(3L, rating.getValue());
        Assertions.assertTrue(rating.isDisabled());

        // Act
        rating.setValue(5);

        // Assert
        Assertions.assertEquals(3L, rating.getValue());
        JSONObject cfg = assertConfiguration(rating.getWidgetConfiguration());
        Assertions.assertTrue(cfg.getBoolean("disabled"));
    }

    @Test
    @Order(5)
    @DisplayName("Rating: enable and disable")
    public void testEnableAndDisable(Page page) {
        // Arrange
        Rating rating = page.ratingDisabled;
        Assertions.assertEquals(3L, rating.getValue());
        Assertions.assertTrue(rating.isDisabled());

        // Act - enable
        rating.enable();
        rating.setValue(5);

        // Assert
        Assertions.assertTrue(rating.isEnabled());
        Assertions.assertEquals(5L, rating.getValue());

        // Act - disable
        rating.disable();
        rating.setValue(1);

        // Assert
        Assertions.assertTrue(rating.isDisabled());
        Assertions.assertEquals(5L, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("Rating: set value to a string should default it to no stars")
    public void testInvalidNumberClientSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        Assertions.assertEquals(2L, rating.getValue());

        // Act
        PrimeSelenium.executeScript(rating.getWidgetByIdScript() + ".setValue('abc');");

        // Assert
        Assertions.assertNull(rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(7)
    @DisplayName("Rating: set value below minimum should set to no stars")
    public void testMinimumClientSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        Assertions.assertEquals(2L, rating.getValue());

        // Act
        rating.setValue(-1);

        // Assert
        Assertions.assertNull(rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(8)
    @DisplayName("Rating: set value above maximum should set to max stars")
    public void testMaximumClientSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        Assertions.assertEquals(2L, rating.getValue());

        // Act
        rating.setValue(14);

        // Assert
        Assertions.assertEquals(8L, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(9)
    @DisplayName("Rating: Submit value below minimum should return original value")
    public void testMinimumServerSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        Assertions.assertEquals(2L, rating.getValue());

        // Act
        PrimeSelenium.setHiddenInput(rating.getInput(), "-1");
        Assertions.assertEquals("-1", rating.getInput().getAttribute("value"));
        page.submit.click();

        // Assert
        Assertions.assertEquals(2L, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("Rating: Submit value above maximum should return original value")
    public void testMaximumServerSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        Assertions.assertEquals(2L, rating.getValue());

        // Act
        PrimeSelenium.setHiddenInput(rating.getInput(), "14");
        Assertions.assertEquals("14", rating.getInput().getAttribute("value"));
        page.submit.click();

        // Assert
        Assertions.assertEquals(2L, rating.getValue());
        assertConfiguration(rating.getWidgetConfiguration());
    }

    @Test
    @Order(11)
    @DisplayName("Rating: set value to a string should trigger error.xhtml")
    public void testInvalidNumberServerSide(Page page) {
        // Arrange
        Rating rating = page.ratingMinMax;
        Assertions.assertEquals(2L, rating.getValue());

        // Act
        PrimeSelenium.setHiddenInput(rating.getInput(), "def");
        Assertions.assertEquals("def", rating.getInput().getAttribute("value"));
        page.submit.click();

        // Assert
        Assertions.assertEquals("Error", page.getWebDriver().getTitle());
    }

    private JSONObject assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("Rating Config = " + cfg);
        Assertions.assertTrue(cfg.has("id"));
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

        @FindBy(id = "form:button")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "rating/rating001.xhtml";
        }
    }
}
