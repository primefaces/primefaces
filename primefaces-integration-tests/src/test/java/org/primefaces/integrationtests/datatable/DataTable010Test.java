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
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;

public class DataTable010Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: selection - multiple with paging")
    public void testSelectionMultipleWithPaging(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);
        Actions actions = new Actions(page.getWebDriver());
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Act
        dataTable.getCell(0, 0).getWebElement().click();
        page.buttonSubmit.click();

        // Assert
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("Selected ProgrammingLanguage(s)"));
        Assertions.assertEquals("1", page.messages.getMessage(0).getDetail());

        // Act
        dataTable.selectPage(2);
        actions.keyDown(Keys.META).click(dataTable.getCell(1, 0).getWebElement()).keyUp(Keys.META).perform();
        page.buttonSubmit.click();

        // Assert
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("Selected ProgrammingLanguage(s)"));
        Assertions.assertEquals("1,5", page.messages.getMessage(0).getDetail());

        // Act
        dataTable.selectPage(1);
        actions.keyDown(Keys.META).click(dataTable.getCell(1, 0).getWebElement()).keyUp(Keys.META).perform();
        page.buttonSubmit.click();

        // Assert
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("Selected ProgrammingLanguage(s)"));
        Assertions.assertEquals("1,2,5", page.messages.getMessage(0).getDetail());
        assertConfiguration(page.dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: selection - multiple with paging and filtering")
    public void testSelectionMultipleWithPagingAndFiltering(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        Assertions.assertNotNull(dataTable);
        Actions actions = new Actions(page.getWebDriver());

        // Act
        dataTable.getCell(0, 0).getWebElement().click();
        page.buttonSubmit.click();

        // Assert
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("Selected ProgrammingLanguage(s)"));
        Assertions.assertEquals("1", page.messages.getMessage(0).getDetail());

        // Act
        dataTable.selectPage(2);
        actions.keyDown(Keys.META).click(dataTable.getCell(1, 0).getWebElement()).keyUp(Keys.META).perform();
        page.buttonSubmit.click();

        // Assert
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("Selected ProgrammingLanguage(s)"));
        Assertions.assertEquals("1,5", page.messages.getMessage(0).getDetail());

        // Act
        dataTable.filter(1, "Java");
        page.buttonSubmit.click();

        // Assert
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("Selected ProgrammingLanguage(s)"));
        Assertions.assertEquals("1,5", page.messages.getMessage(0).getDetail());

        // Act
        actions.keyDown(Keys.META).click(dataTable.getCell(1, 0).getWebElement()).keyUp(Keys.META).perform();
        page.buttonSubmit.click();

        // Assert
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("Selected ProgrammingLanguage(s)"));
        Assertions.assertEquals("1,3,5", page.messages.getMessage(0).getDetail());
        assertConfiguration(page.dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: selection - multiple with paging, filtering and delete row")
    public void testSelectionMultipleWithPagingAndFilteringAndDelete(Page page) {
        testSelectionMultipleWithPagingAndFiltering(page);

        // Act
        page.buttonDelete.click();

        // Assert
        Assertions.assertEquals(1, page.dataTable.getRows().stream().count());
        Assertions.assertEquals("JavaScript", page.dataTable.getCell(0, 1).getText());

        // Act
        page.buttonSubmit.click();

        // Assert
        Assertions.assertTrue(page.messages.getMessage(0).getSummary().contains("Selected ProgrammingLanguage(s)"));
        Assertions.assertEquals("3,5", page.messages.getMessage(0).getDetail()); // TODO: this is still kind of weird because we deleted Java (ID 3) before
        assertConfiguration(page.dataTable.getWidgetConfiguration());
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

        @FindBy(id = "form:buttonSubmit")
        CommandButton buttonSubmit;

        @FindBy(id = "form:buttonDelete")
        CommandButton buttonDelete;

        @Override
        public String getLocation() {
            return "datatable/dataTable010.xhtml";
        }
    }
}
