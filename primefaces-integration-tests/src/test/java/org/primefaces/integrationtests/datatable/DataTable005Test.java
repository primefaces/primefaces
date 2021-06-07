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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;

public class DataTable005Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: selection - multiple")
    public void testSelectionMultiple(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        dataTable.getCell(0, 0).getWebElement().click();
        Actions actions = new Actions(page.getWebDriver());
        actions.keyDown(Keys.META).click(dataTable.getCell(2, 0).getWebElement()).keyUp(Keys.META).perform();
        actions.keyDown(Keys.SHIFT).click(dataTable.getCell(4, 0).getWebElement()).keyUp(Keys.SHIFT).perform();
        page.button.click();

        // Assert
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertMessage(page, "Selected ProgrammingLanguage(s)", "1,3,4,5");

        // Act
        page.buttonUpdate.click();
        page.button.click();

        // Assert - selection must not be lost after update
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertMessage(page, "Selected ProgrammingLanguage(s)", "1,3,4,5");
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: GitHub #7368 Selection with filtering")
    public void testSelectionWithFilter(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);

        // Act
        dataTable.getCell(0, 0).getWebElement().click();
        Actions actions = new Actions(page.getWebDriver());
        actions.keyDown(Keys.META).click(dataTable.getCell(2, 0).getWebElement()).keyUp(Keys.META).perform();
        actions.keyDown(Keys.SHIFT).click(dataTable.getCell(4, 0).getWebElement()).keyUp(Keys.SHIFT).perform();
        page.button.click();

        // Assert
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertMessage(page, "Selected ProgrammingLanguage(s)", "1,3,4,5");

        // Act - filter
        dataTable.filter("Name", "Java");
        page.buttonUpdate.click();
        page.button.click();

        // Assert - selection must not be lost after filter and update
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertMessage(page, "Selected ProgrammingLanguage(s)", "1,3,4,5");

        // Act - clear filter
        dataTable.removeFilter("Name");
        page.buttonUpdate.click();
        page.button.click();

        // Assert - selection must not be lost after filter and update
        assertConfiguration(dataTable.getWidgetConfiguration());
        assertMessage(page, "Selected ProgrammingLanguage(s)", "1,3,4,5");
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

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @Override
        public String getLocation() {
            return "datatable/dataTable005.xhtml";
        }
    }
}
