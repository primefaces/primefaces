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
package org.primefaces.integrationtests.datatable;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;

public class DataTable004Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: selection - single; click on row & events")
    public void testSelectionSingle(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

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
        Assertions.assertEquals("true", dataTable.getRow(4).getWebElement().getAttribute("aria-selected"));

        // Act - unselect row
        Actions actions = new Actions(page.getWebDriver());
        Action actionMetaPlusRowClick = actions.keyDown(Keys.META).click(dataTable.getCell(4, 0).getWebElement()).keyUp(Keys.META).build();
        PrimeSelenium.guardAjax(actionMetaPlusRowClick).perform();

        // Assert
        assertMessage(page, "ProgrammingLanguage Unselected", languages.get(4).getName());

        // Act
        page.button.click();

        // Assert (no row selected)
        dataTable.getRows().forEach(r -> Assertions.assertEquals("false", r.getWebElement().getAttribute("aria-selected"), "Found a selected row!"));
        assertMessage(page, "NO ProgrammingLanguage selected", "");
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: selection - single - unselect; https://github.com/primefaces/primefaces/issues/7128")
    public void testSelectionSingleUnselect(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        dataTable.getCell(2, 0).getWebElement().click();
        page.buttonMsgOnly.click();

        // Assert
        WebElement card = page.card;
        Assertions.assertTrue(card.getText().contains(languages.get(2).getName()));

        // Act - unselect row
        Actions actions = new Actions(page.getWebDriver());
        actions.keyDown(Keys.META).click(dataTable.getCell(2, 0).getWebElement()).keyUp(Keys.META).perform();
        page.buttonMsgOnly.click();

        // Assert (no row selected)
        dataTable.getRows().forEach(r -> Assertions.assertEquals("false", r.getWebElement().getAttribute("aria-selected"), "Found a selected row!"));
        Assertions.assertTrue(card.getText().contains("NO ProgrammingLanguage selected"));
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: selection - single - unselect programmatically; https://github.com/primefaces/primefaces/issues/7391")
    public void testSelectionSingleUnselectProgrammatically(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        dataTable.getCell(2, 0).getWebElement().click();
        page.buttonMsgOnly.click();

        // Assert
        WebElement card = page.card;
        Assertions.assertTrue(card.getText().contains(languages.get(2).getName()));

        // Act - unselect row programmatically
        page.buttonUnselect.click();

        // Assert (no row selected)
        assertMessage(page, "ProgrammingLanguage unselected via backing bean", "");
        dataTable.getRows().forEach(r -> Assertions.assertEquals("false", r.getWebElement().getAttribute("aria-selected"), "Found a selected row!"));
        Assertions.assertTrue(card.getText().contains("NO ProgrammingLanguage selected"));

        // Act (submit button)
        page.button.click();

        // Assert (still no row selected)
        assertMessage(page, "NO ProgrammingLanguage selected", "");

        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertMessage(Page page, String summary, String detail) {
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains(summary));
        Assertions.assertTrue(page.messages.getMessage(0).getDetail().contains(detail));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        Assertions.assertTrue(cfg.has("selectionMode"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

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
