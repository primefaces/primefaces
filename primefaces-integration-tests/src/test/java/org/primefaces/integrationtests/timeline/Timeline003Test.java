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
package org.primefaces.integrationtests.timeline;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.Timeline;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

public class Timeline003Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Schedule: Locale switching english")
    void localeEnglish(Page page) {
        // Arrange
        Timeline timeline = page.timeline;

        // Act
        page.buttonEnglish.click();

        // Assert
        assertMonth(timeline, "July", "Jul");
        assertMonth(timeline, "October", "Oct");
        assertMonth(timeline, "January", "Jan");
        assertMonth(timeline, "April", "Apr");
        assertConfiguration(timeline.getWidgetConfiguration(), "en");
    }

    @Test
    @Order(2)
    @DisplayName("Schedule: Locale switching french")
    void localeFrench(Page page) {
        // Arrange
        Timeline timeline = page.timeline;

        // Act
        page.buttonFrench.click();

        // Assert
        assertMonth(timeline, "July", "juil.");
        assertMonth(timeline, "October", "oct.");
        assertMonth(timeline, "January", "janv.");
        assertMonth(timeline, "April", "avr.");
        assertConfiguration(timeline.getWidgetConfiguration(), "fr");
    }

    private void assertConfiguration(JSONObject cfg, String locale) {
        assertNoJavascriptErrors();
        System.out.println("Timeline Config = " + cfg);
        assertTrue(cfg.has("data"));

        JSONObject options = cfg.getJSONObject("opts");
        assertNotNull(options);
        assertEquals(locale, options.get("locale"));
    }

    private void assertMonth(Timeline timeline, String month, String i18n) {
        List<WebElement> monthElements = getMonth(timeline, month);
        for (WebElement monthElement : monthElements) {
            assertNotNull(monthElement);
            String monthText = monthElement.getText();

            // sometimes the month CSS contains a year which might be a bug in vis-timeline
            if (NumberUtils.isDigits(monthText)) {
                continue;
            }
            assertEquals(i18n, monthText);
        }
    }

    public List<WebElement> getMonth(Timeline timeline, String month) {
        String cssSelector = "div.vis-panel > div.vis-time-axis > div.vis-text.vis-" + month.toLowerCase(Locale.getDefault());
        return timeline.findElements(By.cssSelector(cssSelector));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:timeline")
        Timeline timeline;

        @FindBy(id = "form:btnEnglish")
        CommandButton buttonEnglish;

        @FindBy(id = "form:btnFrench")
        CommandButton buttonFrench;

        @Override
        public String getLocation() {
            return "timeline/timeline003.xhtml";
        }
    }
}
