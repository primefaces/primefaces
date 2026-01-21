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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectBooleanButton;
import org.primefaces.selenium.component.model.Msg;
import org.primefaces.selenium.component.model.datatable.Row;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataTable006Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: selection - multiple with checkboxes")
    void selectionMultipleCheckboxes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.getCell(0, 0).getWebElement().click();
        dataTable.getCell(2, 0).getWebElement().click();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, false);
        assertSelections(page.messages, "1,3");
        int cnt = 0;
        for (Row row : dataTable.getRows()) {
            WebElement checkboxIcon = row.getCell(0).getWebElement().findElement(By.className("ui-chkbox-icon"));
            if (cnt == 0 || cnt == 2) {
                assertCss(checkboxIcon, "ui-icon-check");
            }
            else {
                assertCss(checkboxIcon, "ui-icon-blank");
            }
            cnt++;
        }

        assertConfiguration(dataTable.getWidgetConfiguration(), true);

        // Act - https://github.com/primefaces/primefaces/issues/7128
        page.buttonUnselect.click();

        // Assert
        assertTrue(page.messages.getMessage(0).getSummary().contains("ProgrammingLanguages unselected via backing bean"));
        for (Row row : dataTable.getRows()) {
            WebElement checkboxIcon = row.getCell(0).getWebElement().findElement(By.className("ui-chkbox-icon"));
            assertCss(checkboxIcon, "ui-icon-blank");
        }

        // Act
        page.submit.click();

        // Assert
        assertSelections(page.messages, "");
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: selection - lazy multiple with checkboxes")
    void lazySelectionMultipleCheckboxes(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        page.toggleLazyMode.click();

        // Act
        dataTable.getCell(0, 0).getWebElement().click();
        dataTable.getCell(2, 0).getWebElement().click();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, false);
        assertSelections(page.messages, "1,3");
        assertConfiguration(dataTable.getWidgetConfiguration(), true);
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: selection - lazy multiple with checkboxes and selection on multiple pages - https://github.com/primefaces/primefaces/issues/8110")
    void lazySelectionMultipleCheckboxesWithPagination(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        page.toggleLazyMode.click();

        // Act
        dataTable.getCell(0, 0).getWebElement().click();
        dataTable.getCell(2, 0).getWebElement().click();
        dataTable.selectPage(2);
        dataTable.getCell(1, 0).getWebElement().click();
        dataTable.selectPage(1);

        // Assert - row 0 and 2 on page 1 still selected
        assertEquals("true", dataTable.getCell(0, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        assertEquals("false", dataTable.getCell(1, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        assertEquals("true", dataTable.getCell(2, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));

        // Act
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, false);
        assertSelections(page.messages, "1,3,5");
        assertConfiguration(dataTable.getWidgetConfiguration(), true);

        // Assert - row 0 and 2 on page 1 and row 1 on page 2 still selected
        assertEquals("true",
                dataTable.getCell(0, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        assertEquals("false",
                dataTable.getCell(1, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        assertEquals("true",
                dataTable.getCell(2, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        dataTable.selectPage(2);

        assertEquals("false",
                dataTable.getCell(0, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        assertEquals("true",
                dataTable.getCell(1, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        assertEquals("false",
                dataTable.getCell(2, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: selection - select all for page with selectionPageOnly='true'")
    void selectAllPageOnly(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - select all
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, true);
        assertSelections(page.messages, "1,2,3");

        // Act - unselect one row
        dataTable.getCell(1, 0).getWebElement().click();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, false);
        assertConfiguration(dataTable.getWidgetConfiguration(), true);
        assertSelections(page.messages, "1,3");
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: selection - Lazy select all for page with selectionPageOnly='true'")
    void lazySelectAllPageOnly(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        page.toggleLazyMode.click();

        // Act - select all
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, true);
        assertSelections(page.messages, "1,2,3");

        // Act - unselect one row
        dataTable.getCell(1, 0).getWebElement().click();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, false);
        assertConfiguration(dataTable.getWidgetConfiguration(), true);
        assertSelections(page.messages, "1,3");
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: selection - unselect all for page with selectionPageOnly='true'")
    void unselectAllPageOnly(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - select all
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, true);
        assertSelections(page.messages, "1,2,3");

        // Act - unselect all
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelections(page.messages, "");
        assertSelectAllCheckbox(dataTable, false);
        assertConfiguration(dataTable.getWidgetConfiguration(), true);
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: GitHub #6730 selection - select all rows with selectionPageOnly='false'")
    void selectAllRows(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - select all
        page.toggleSelectPageOnly.click();
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, true);
        assertSelections(page.messages, "1,2,3,4,5");

        // Act - unselect one row
        dataTable.getCell(1, 0).getWebElement().click();
        page.submit.click();

        // Assert - only 1 record unselected
        assertSelectAllCheckbox(dataTable, false);
        assertSelections(page.messages, "1,3,4,5");

        // Act - reselect all record, unselect one and move to next page
        dataTable.toggleSelectAllCheckBox();
        dataTable.getCell(0, 0).getWebElement().click();
        dataTable.selectPage(2);
        page.submit.click();

        // Assert - with selectionPageOnly=false, all rows except unselected one should remain selected
        assertSelections(page.messages, "2,3,4,5");
        assertConfiguration(dataTable.getWidgetConfiguration(), false);
    }

    @Test
    @Order(8)
    @DisplayName("DataTable: GitHub #6730 selection - Lazy select all rows with selectionPageOnly='false'")
    void lazySelectAllRows(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        page.toggleLazyMode.click();

        // Act - select all
        page.toggleSelectPageOnly.click();
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, true);
        assertSelections(page.messages,
                "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,"
                        + "31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,"
                        + "61,62,63,64,65,66,67,68,69,70,71,72,73,74,75");

        // Act - unselect one row (row 2)
        dataTable.getCell(1, 0).getWebElement().click();
        page.submit.click();

        // Assert - only 1 record unselected (row 2), all others remain selected (74 rows)
        assertSelectAllCheckbox(dataTable, false);
        assertSelections(page.messages,
                "1,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,"
                        + "31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,"
                        + "61,62,63,64,65,66,67,68,69,70,71,72,73,74,75");

        // Act - reselect all, unselect one (row 1) and move to next page
        dataTable.toggleSelectAllCheckBox();
        dataTable.getCell(0, 0).getWebElement().click();
        dataTable.selectPage(2);
        page.submit.click();

        // Assert - with @all and !1 deselection marker, all 74 rows remain selected (all except row 1)
        assertSelections(page.messages,
                "2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,"
                        + "31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,"
                        + "61,62,63,64,65,66,67,68,69,70,71,72,73,74,75");
        assertConfiguration(dataTable.getWidgetConfiguration(), false);
    }

    @Test
    @Order(9)
    @DisplayName("DataTable: GitHub #7368, #7737 selection - with filtering")
    void lazySelectionWithFiltering(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        page.toggleLazyMode.click();

        // Act - selection without filter applied
        dataTable.getCell(0, 0).getWebElement().click();
        dataTable.getCell(2, 0).getWebElement().click();
        page.submit.click();

        // Assert
        assertSelections(page.messages, "1,3");

        // Act - filter
        dataTable.filter("Name", "Java");
        page.submit.click();

        // Assert
        assertSelections(page.messages, "1,3");

        // Act - selection with filter applied
        dataTable.filter("Name", "2");
        dataTable.getCell(1, 0).getWebElement().click();

        // Act - remove filter
        dataTable.removeFilter("Name");
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, false);
        assertSelections(page.messages, "1,3,12");
        assertConfiguration(dataTable.getWidgetConfiguration(), true);
    }

    @Test
    @Order(9)
    @DisplayName("DataTable: selection - unselect all for page with selectionPageOnly='false'")
    void unselectAllRows(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act - select all items on all pages
        page.toggleSelectPageOnly.click();
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelectAllCheckbox(dataTable, true);
        assertSelections(page.messages, "1,2,3,4,5");

        // Act - unselect all
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert
        assertSelections(page.messages, "");
        assertSelectAllCheckbox(dataTable, false);
        assertConfiguration(dataTable.getWidgetConfiguration(), false);
    }

    @Test
    @Order(10)
    @DisplayName("DataTable: selection - reselect deselected row with selectionPageOnly='false'")
    void reselectDeselectedRow(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        page.toggleSelectPageOnly.click();

        // Act - select all
        dataTable.toggleSelectAllCheckBox();
        page.submit.click();

        // Assert - all rows selected
        assertSelectAllCheckbox(dataTable, true);
        assertSelections(page.messages, "1,2,3,4,5");

        // Act - deselect row 2
        dataTable.getCell(1, 0).getWebElement().click();
        page.submit.click();

        // Assert - row 2 deselected
        assertSelectAllCheckbox(dataTable, false);
        assertSelections(page.messages, "1,3,4,5");

        // Act - reselect row 2 (the one we just deselected)
        dataTable.getCell(1, 0).getWebElement().click();
        page.submit.click();

        // Assert - row 2 is reselected, all 5 rows should now be selected again
        // Note: selectAll checkbox remains unchecked because we manually reselected rather than using toggle all
        assertSelectAllCheckbox(dataTable, false);
        assertSelections(page.messages, "1,2,3,4,5");
        assertConfiguration(dataTable.getWidgetConfiguration(), false);
    }

    private void assertConfiguration(JSONObject cfg, boolean selectionPageOnly) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertEquals("checkbox", cfg.get("selectionMode"));
        assertEquals(selectionPageOnly, cfg.getBoolean("selectionPageOnly"));
    }

    private void assertSelectAllCheckbox(DataTable datatable, boolean checked) {
        WebElement selectAllCheckBox = datatable.getSelectAllCheckBox();
        WebElement icon = selectAllCheckBox.findElement(By.className("ui-chkbox-icon"));
        assertCss(icon, checked ? "ui-icon-check" : "ui-icon-blank");
    }

    private void assertSelections(Messages messages, String selections) {
        Msg message = messages.getMessage(0);
        assertTrue(message.getSummary().contains("Selected ProgrammingLanguage(s)"));
        assertEquals(selections, message.getDetail());
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:button")
        CommandButton submit;

        @FindBy(id = "form:buttonUnselect")
        CommandButton buttonUnselect;

        @FindBy(id = "form:toggleSelectPageOnly")
        SelectBooleanButton toggleSelectPageOnly;

        @FindBy(id = "form:toggleLazyMode")
        CommandButton toggleLazyMode;

        @Override
        public String getLocation() {
            return "datatable/dataTable006.xhtml";
        }
    }
}
