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
package org.primefaces.integrationtests.columntoggler;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.ColumnToggler;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Arrays;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class ColumnToggler001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ColumnToggler: show and hide the overlay with the columns, close AJAX event")
    void showHide(Page page) {
        // Arrange
        ColumnToggler toggler = page.columnToggler;
        assertFalse(toggler.isPanelVisible());
        assertEquals("false", toggler.getTrigger().getDomAttribute("aria-expanded"));

        // Act
        toggler.show();

        // Assert
        assertTrue(toggler.isPanelVisible());
        assertEquals("true", toggler.getTrigger().getDomAttribute("aria-expanded"));
        assertEquals("dialog", toggler.getDomAttribute("role"));
        assertEquals(Arrays.asList("ID", "Name", "First appeared", "Type"), toggler.getColumnLabels());
        assertTrue(toggler.isChecked("ID"));
        assertTrue(toggler.isChecked("Name"));
        assertTrue(toggler.isChecked("First appeared"));
        assertFalse(toggler.isChecked("Type"));
        assertFalse(toggler.isSelectAllChecked());

        // Act
        toggler.hide();

        // Assert
        assertFalse(toggler.isPanelVisible());
        assertEquals("false", toggler.getTrigger().getDomAttribute("aria-expanded"));
        assertEquals("Toggler Closed", page.messages.getMessage(0).getSummary());
        assertEquals("langId,name,year", page.messages.getMessage(0).getDetail());
        assertConfiguration(toggler.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("ColumnToggler: uncheck and check a column hides and shows it, toggle AJAX event")
    void toggleColumn(Page page) {
        // Arrange
        ColumnToggler toggler = page.columnToggler;
        DataTable dataTable = page.dataTable;
        assertColumnVisible(dataTable, 1, true);
        assertColumnVisible(dataTable, 3, false);

        // Act
        toggler.uncheck("Name");

        // Assert
        assertFalse(toggler.isChecked("Name"));
        assertColumnVisible(dataTable, 1, false);
        assertEquals("Column Toggled", page.messages.getMessage(0).getSummary());
        assertEquals("Name HIDDEN 1", page.messages.getMessage(0).getDetail());

        // Act
        toggler.check("Name");

        // Assert
        assertTrue(toggler.isChecked("Name"));
        assertColumnVisible(dataTable, 1, true);
        assertEquals("Name VISIBLE 1", page.messages.getMessage(0).getDetail());

        // Act - the initially hidden column
        toggler.check("Type");

        // Assert
        assertTrue(toggler.isChecked("Type"));
        assertColumnVisible(dataTable, 3, true);
        assertEquals("Type VISIBLE 3", page.messages.getMessage(0).getDetail());
        assertEquals("Java", dataTable.getCell(0, 1).getText());
        assertEquals("COMPILED", dataTable.getCell(0, 3).getText());
        // the last unchecked column was checked, so select all is checked too
        assertTrue(toggler.isSelectAllChecked());

        // Act
        toggler.uncheck("ID");

        // Assert
        assertFalse(toggler.isChecked("ID"));
        assertFalse(toggler.isSelectAllChecked());
        assertColumnVisible(dataTable, 0, false);
        assertConfiguration(toggler.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("ColumnToggler: hidden column must not be lost after AJAX update")
    void hiddenAfterUpdate(Page page) {
        // Arrange
        ColumnToggler toggler = page.columnToggler;
        toggler.uncheck("Name");
        toggler.hide();

        // Act
        page.update.click();

        // Assert
        DataTable dataTable = page.dataTable;
        assertColumnVisible(dataTable, 1, false);
        assertColumnVisible(dataTable, 3, false);
        assertEquals(1, getWebDriver().findElements(By.id("form:datatable:columnToggler")).size());

        // Act
        toggler.show();

        // Assert
        assertFalse(toggler.isChecked("Name"));
        assertTrue(toggler.isChecked("ID"));

        // Act - the refreshed widget still toggles
        toggler.check("Name");

        // Assert
        assertColumnVisible(dataTable, 1, true);
        assertEquals("Name VISIBLE 1", page.messages.getMessage(0).getDetail());
        assertConfiguration(toggler.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("ColumnToggler: select all shows all columns, unselect all hides all columns")
    void toggleAll(Page page) {
        // Arrange
        ColumnToggler toggler = page.columnToggler;
        DataTable dataTable = page.dataTable;
        assertFalse(toggler.isPanelVisible());

        // Act
        toggler.toggleAll();

        // Assert
        assertTrue(toggler.isSelectAllChecked());
        for (int i = 0; i < 4; i++) {
            assertColumnVisible(dataTable, i, true);
        }
        assertEquals("Type VISIBLE 3", page.messages.getMessage(0).getDetail());

        // Act
        toggler.toggleAll();

        // Assert
        assertFalse(toggler.isSelectAllChecked());
        for (String label : toggler.getColumnLabels()) {
            assertFalse(toggler.isChecked(label));
        }
        for (int i = 0; i < 4; i++) {
            assertColumnVisible(dataTable, i, false);
        }
        assertEquals("Column Toggled", page.messages.getMessage(0).getSummary());
        assertConfiguration(toggler.getWidgetConfiguration());
    }

    private void assertColumnVisible(DataTable dataTable, int index, boolean visible) {
        assertEquals(visible, dataTable.getHeader().getCell(index).getWebElement().isDisplayed(), "header of column " + index);
        for (Row row : dataTable.getRows()) {
            assertEquals(visible, row.getCell(index).getWebElement().isDisplayed(), "cell of column " + index);
        }
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals("form:datatable:toggler", cfg.getString("trigger"));
        assertEquals("form:datatable", cfg.getString("datasource"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:datatable:columnToggler")
        ColumnToggler columnToggler;

        @FindBy(id = "form:update")
        CommandButton update;

        @Override
        public String getLocation() {
            return "columntoggler/columnToggler001.xhtml";
        }
    }
}
