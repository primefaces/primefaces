/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.integrationtests.feedreader;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedReader001Test extends AbstractPrimePageTest {

    @Disabled("Test relies on a valid RSS feed, which is not always available")
    @Test
    @Order(1)
    @DisplayName("FeedReader: valid RSS just check at least a few articles are returned")
    void feedReaderValid(Page page) {
        // Arrange
        WebElement form = page.form;

        // Act
        List<WebElement> rssArticles = form.findElements(By.className("ui-rss-feed"));

        // Assert
        assertTrue(rssArticles.size() > 3);
        assertNoJavascriptErrors();
    }

    @Disabled("Test relies on a valid RSS feed, which is not always available")
    @Test
    @Order(2)
    @DisplayName("FeedReader: invalid RSS URL so ensure error is displayed")
    void feedReaderInvalid(Page page) {
        // Arrange
        WebElement form = page.form;

        // Act
        page.buttonInvalidate.click();

        // Assert
        WebElement error = form.findElement(By.className("ui-feed-error"));
        assertEquals("Error reading RSS Feed!!!", error.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form")
        WebElement form;

        @FindBy(id = "form:buttonInvalidate")
        CommandButton buttonInvalidate;


        @Override
        public String getLocation() {
            return "feedreader/feedReader001.xhtml";
        }
    }
}
