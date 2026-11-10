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
package org.primefaces.integrationtests.dragdrop;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataGrid;
import org.primefaces.selenium.component.Draggable;
import org.primefaces.selenium.component.Droppable;
import org.primefaces.selenium.component.Messages;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DragDrop002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("DragDrop: drop an item of a data grid, the drop event has the row data of the datasource")
    void dropRowData(Page page) {
        // Arrange
        assertEquals(List.of("Java", "C#", "JavaScript", "TypeScript", "Python"), names(page.languages));
        Draggable draggable = draggable(1);
        assertEquals("form:languages:1:pnl", draggable.getTarget().getDomAttribute("id"));

        // Act
        page.droppable.drop(draggable);

        // Assert
        assertEquals("Dropped", page.messages.getMessage(0).getSummary());
        assertEquals("C#", page.messages.getMessage(0).getDetail());
        assertEquals("C#", page.dropped.getText());
        assertEquals(List.of("Java", "JavaScript", "TypeScript", "Python"), names(page.languages));
        assertConfiguration(page.droppable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DragDrop: drop items of a data grid after it was updated")
    void dropAfterUpdate(Page page) {
        // Arrange
        page.droppable.drop(draggable(1));

        // Act - the last item, its row index changed with the update
        page.droppable.drop(draggable(3));

        // Assert
        assertEquals("Python", page.messages.getMessage(0).getDetail());
        assertEquals("C#, Python", page.dropped.getText());
        assertEquals(List.of("Java", "JavaScript", "TypeScript"), names(page.languages));
        assertConfiguration(page.droppable.getWidgetConfiguration());
    }

    private Draggable draggable(int rowIndex) {
        return PrimeSelenium.createFragment(Draggable.class, By.id("form:languages:" + rowIndex + ":drag"));
    }

    private List<String> names(DataGrid dataGrid) {
        return dataGrid.getRowsWebElement().stream()
                    .map(row -> row.findElement(By.className("p-panel-title")).getText())
                    .collect(Collectors.toList());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("pointer", cfg.getString("tolerance"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:dropped")
        WebElement dropped;

        @FindBy(id = "form:drop")
        Droppable droppable;

        @FindBy(id = "form:languages")
        DataGrid languages;

        @Override
        public String getLocation() {
            return "dragdrop/dragDrop002.xhtml";
        }
    }
}
