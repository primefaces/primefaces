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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectBooleanButton;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataTable004Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: selection - single; click on row & events")
    void selectionSingle(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act
        PrimeSelenium.guardAjax(dataTable.getCell(2, 0).getWebElement()).click();

        // Assert
        assertMessage(page, "ProgrammingLanguage Selected", languages.get(2).getName());

        // Act - other row
        PrimeSelenium.guardAjax(dataTable.getCell(4, 0).getWebElement()).click();

        // Assert
        assertMessage(page, "ProgrammingLanguage Selected", languages.get(4).getName());

        // Act - submit button
        page.button.click();

        // Assert (select row after update still selected)
        assertMessage(page, "Selected ProgrammingLanguage", languages.get(4).getName());
        PrimeSelenium.hasCssClass(dataTable.getRow(4).getWebElement(), "ui-state-highlight");
        assertEquals("true", dataTable.getRow(4).getWebElement().getAttribute("aria-selected"));

        // Act - unselect row
        Actions actions = new Actions(page.getWebDriver());
        Action actionMetaPlusRowClick = actions.keyDown(Keys.META).click(dataTable.getCell(4, 0).getWebElement()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionMetaPlusRowClick).perform();

        // Assert
        assertMessage(page, "ProgrammingLanguage Unselected", languages.get(4).getName());

        // Act
        page.button.click();

        // Assert (no row selected)
        dataTable.getRows().forEach(r -> assertEquals("false", r.getWebElement().getAttribute("aria-selected"), "Found a selected row!"));
        assertMessage(page, "NO ProgrammingLanguage selected", "");
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: selection - single - unselect; https://github.com/primefaces/primefaces/issues/7128")
    void selectionSingleUnselect(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act
        dataTable.getCell(2, 0).getWebElement().click();
        page.buttonMsgOnly.click();

        // Assert
        WebElement card = page.card;
        assertTrue(card.getText().contains(languages.get(2).getName()));

        // Act - unselect row
        Actions actions = new Actions(page.getWebDriver());
        actions.keyDown(Keys.META).click(dataTable.getCell(2, 0).getWebElement()).keyUp(Keys.META).perform();
        page.buttonMsgOnly.click();

        // Assert (no row selected)
        dataTable.getRows().forEach(r -> assertEquals("false", r.getWebElement().getAttribute("aria-selected"), "Found a selected row!"));
        assertTrue(card.getText().contains("NO ProgrammingLanguage selected"));
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: selection - single - unselect programmatically; https://github.com/primefaces/primefaces/issues/7391")
    void selectionSingleUnselectProgrammatically(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act
        dataTable.getCell(2, 0).getWebElement().click();
        page.buttonMsgOnly.click();

        // Assert
        WebElement card = page.card;
        assertTrue(card.getText().contains(languages.get(2).getName()));

        // Act - unselect row programmatically
        page.buttonUnselect.click();

        // Assert (no row selected)
        assertMessage(page, "ProgrammingLanguage unselected via backing bean", "");
        dataTable.getRows().forEach(r -> assertEquals("false", r.getWebElement().getAttribute("aria-selected"), "Found a selected row!"));
        assertTrue(card.getText().contains("NO ProgrammingLanguage selected"));

        // Act (submit button)
        page.button.click();

        // Assert (still no row selected)
        assertMessage(page, "NO ProgrammingLanguage selected", "");

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(10)
    @DisplayName("DataTable: DisabledSelection - feature")
    void disabledSelection(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        page.selectBooleanButtonDisabledSelection.click();
        assertNotNull(dataTable);

        for (int row = 0; row < 5; row++) {
            // Act & Assert
            if (row % 2 == 0) {
                assertTrue(PrimeSelenium.hasCssClass(dataTable.getRow(row).getWebElement(), "ui-datatable-selectable"));

                PrimeSelenium.guardAjax(dataTable.getCell(row, 0).getWebElement()).click();

                assertTrue(PrimeSelenium.hasCssClass(dataTable.getRow(row).getWebElement(), "ui-state-highlight"));
                assertMessage(page, "ProgrammingLanguage Selected", languages.get(row).getName());
            }
            else {
                assertFalse(PrimeSelenium.hasCssClass(dataTable.getRow(row).getWebElement(), "ui-datatable-selectable"));

                dataTable.getCell(row, 0).getWebElement().click(); // nothing happens

                assertFalse(PrimeSelenium.hasCssClass(dataTable.getRow(row).getWebElement(), "ui-state-highlight"));
                assertMessage(page, "ProgrammingLanguage Selected", languages.get(row - 1).getName()); // still the row we selected before
            }
        }

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertMessage(Page page, String summary, String detail) {
        assertTrue(page.messages.getMessage(0).getSummary().contains(summary));
        assertTrue(page.messages.getMessage(0).getDetail().contains(detail));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertTrue(cfg.has("selectionMode"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:chkDisabledSelection")
        SelectBooleanButton selectBooleanButtonDisabledSelection;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:buttonMsgOnly")
        CommandButton buttonMsgOnly;

        @FindBy(id = "form:buttonUnselect")
        CommandButton buttonUnselect;

        @FindBy(id = "form:card")
        WebElement card;

        @Override
        public String getLocation() {
            return "datatable/dataTable004.xhtml";
        }
    }
}
