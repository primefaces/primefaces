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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("DataTable-selection")
@Tag("DataTable-paginator")
class DataTable005PagingTest extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: selection - multiple on Page1")
    void selectionMultiplePage1(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act
        dataTable.getCell(0, 0).getWebElement().click();
        Actions actions = new Actions(getWebDriver());
        actions.keyDown(Keys.META).click(dataTable.getCell(1, 0).getWebElement()).keyUp(Keys.META).perform();
        actions.keyDown(Keys.SHIFT).click(dataTable.getCell(2, 0).getWebElement()).keyUp(Keys.SHIFT).perform();
        page.button.click();

        // Assert
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertMessage(page, "Selected ProgrammingLanguage(s)", "1,2,3");

        // Act
        page.buttonUpdate.click();
        page.button.click();

        // Assert - selection must not be lost after update
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertMessage(page, "Selected ProgrammingLanguage(s)", "1,2,3");
    }

    @Test
    @Order(1)
    @DisplayName("DataTable: selection - multiple on Page2")
    void selectionMultiplePage2(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);

        // Act
        dataTable.selectPage(2);
        Actions actions = new Actions(getWebDriver());
        actions.keyDown(Keys.META).click(dataTable.getCell(0, 0).getWebElement()).keyUp(Keys.META).perform();
        actions.keyDown(Keys.SHIFT).click(dataTable.getCell(1, 0).getWebElement()).keyUp(Keys.SHIFT).perform();
        page.button.click();

        // Assert
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertMessage(page, "Selected ProgrammingLanguage(s)", "4,5");

        // Act
        page.buttonUpdate.click();
        page.button.click();

        // Assert - selection must not be lost after update
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertMessage(page, "Selected ProgrammingLanguage(s)", "4,5");
    }

    private void assertMessage(Page page, String summary, String detail) {
        assertTrue(page.messages.getMessage(0).getSummary().contains(summary));
        assertTrue(page.messages.getMessage(0).getDetail().contains(detail));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("selectionMode"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @Override
        public String getLocation() {
            return "datatable/dataTable005Paging.xhtml";
        }
    }
}
