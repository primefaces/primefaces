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
package org.primefaces.integrationtests.chart;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Chart;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Chart001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Chart: bar chart from a bean model and line chart from a facet are rendered")
    void render(Page page) {
        // Arrange
        Chart bar = page.bar;
        Chart line = page.line;

        // Assert
        assertTrue(bar.isRendered());
        assertEquals("bar", bar.getType());
        assertEquals(List.of("January", "February", "March"), bar.getLabels());
        assertEquals(1, bar.getDatasetCount());
        assertEquals("img", bar.getCanvas().getDomAttribute("role"));
        assertEquals("Sales per month", bar.getCanvas().getDomAttribute("aria-label"));
        assertEquals("form:bar_canvas", bar.getCanvas().getDomAttribute("id"));
        assertTrue(bar.getCanvas().getSize().getWidth() > 0);
        assertTrue(bar.getCanvas().getSize().getHeight() > 0);

        assertTrue(line.isRendered());
        assertEquals("line", line.getType());
        assertEquals(List.of("Q1", "Q2", "Q3", "Q4"), line.getLabels());
        assertEquals(2, line.getDatasetCount());
        assertFalse(line.isItemSelectAjaxified());
        assertConfiguration(bar.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("Chart: itemSelect AJAX event sends the clicked bar")
    void itemSelect(Page page) {
        // Arrange
        Chart bar = page.bar;
        assertTrue(bar.isItemSelectAjaxified());

        // Act
        bar.selectItem(0, 1);

        // Assert
        assertEquals("Item selected", page.messages.getMessage(0).getSummary());
        assertEquals("dataSetIndex=0, itemIndex=1, data=20", page.messages.getMessage(0).getDetail());

        // Act
        bar.selectItem(0, 2);

        // Assert
        assertEquals("dataSetIndex=0, itemIndex=2, data=30", page.messages.getMessage(0).getDetail());
        assertConfiguration(bar.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("Chart: chart is recreated after AJAX update and still fires itemSelect")
    void update(Page page) {
        // Arrange
        Chart bar = page.bar;

        // Act
        page.update.click();

        // Assert
        assertTrue(bar.isRendered());
        assertEquals("bar", bar.getType());
        assertEquals(List.of("January", "February", "March"), bar.getLabels());

        // Act
        bar.selectItem(0, 0);

        // Assert
        assertEquals("dataSetIndex=0, itemIndex=0, data=10", page.messages.getMessage(0).getDetail());
        assertConfiguration(bar.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("Chart: export as image")
    void exportAsImage(Page page) {
        // Arrange
        Chart line = page.line;

        // Act
        String image = line.exportAsImage();

        // Assert
        assertTrue(image.startsWith("data:image/png;base64,"), image);
        assertTrue(image.length() > "data:image/png;base64,".length() + 100);
        assertNoJavascriptErrors();
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("bar", cfg.getJSONObject("config").getString("type"));
        assertFalse(cfg.getJSONObject("config").getJSONObject("options").getBoolean("animation"));
        assertTrue(cfg.getJSONObject("behaviors").has("itemSelect"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:bar")
        Chart bar;

        @FindBy(id = "form:line")
        Chart line;

        @FindBy(id = "form:update")
        CommandButton update;

        @Override
        public String getLocation() {
            return "chart/chart001.xhtml";
        }
    }
}
