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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectBooleanButton;
import org.primefaces.selenium.component.model.Msg;

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * GitHub #15013: Integration tests for {@link org.primefaces.model.JPALazyDataModel} verifying
 * that count(), load() and getRowData() are called correctly during initial render, pagination and selection.
 */
@Tag("DataTable-lazy")
@Tag("DataTable-selection")
@Tag("DataTable-paginator")
class DataTable050Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: JPA lazy model - initial render records count and load")
    void initialRender(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Assert
        assertRowsMatch(dataTable, List.of(
                new ExpectedLanguage(1, "Java"),
                new ExpectedLanguage(2, "C#"),
                new ExpectedLanguage(3, "JavaScript")));
        assertHistory(page, "count()", "load(first=0, size=3)");
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: JPA lazy model - pagination records another load")
    void pagination(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.selectPage(2);

        // Assert
        assertRowsMatch(dataTable, List.of(
                new ExpectedLanguage(4, "TypeScript"),
                new ExpectedLanguage(5, "Python"),
                new ExpectedLanguage(6, "C++")));
        assertHistory(page,
                "count()",
                "load(first=0, size=3)",
                "count()",
                "load(first=3, size=3)");
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: JPA lazy model - row selection records getRowData")
    void rowSelection(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.getCell(0, 0).getWebElement().click();
        dataTable.getCell(2, 0).getWebElement().click();
        page.submit.click();

        // Assert
        assertSelections(page.messages, "1,3");
        assertHistory(page,
                "count()",
                "load(first=0, size=3)",
                "getRowData(rowKey=1)",
                "getRowData(rowKey=3)",
                "count()",
                "load(first=0, size=3)"); // after submit (full-form re-render)
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: JPA lazy model - selection across pages preserves state")
    void selectionAcrossPages(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;
        dataTable.getCell(0, 0).getWebElement().click();
        dataTable.getCell(2, 0).getWebElement().click();
        page.submit.click();

        // Act
        dataTable.selectPage(2);
        dataTable.selectPage(1);

        // Assert
        assertEquals("true", dataTable.getCell(0, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        assertEquals("false", dataTable.getCell(1, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        assertEquals("true", dataTable.getCell(2, 0).getWebElement().findElement(By.className("ui-chkbox-box")).getAttribute("aria-checked"));
        assertHistory(page,
                "count()",
                "load(first=0, size=3)",
                "getRowData(rowKey=1)",
                "getRowData(rowKey=3)",
                "count()",
                "load(first=0, size=3)", // after submit (full-form re-render)
                "count()",
                "load(first=3, size=3)", // after page 2
                "count()",
                "load(first=0, size=3)"); // after page 1
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertRowsMatch(DataTable dataTable, List<ExpectedLanguage> expected) {
        List<org.primefaces.selenium.component.model.datatable.Row> rows = dataTable.getRows();
        assertEquals(expected.size(), rows.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(String.valueOf(expected.get(i).id), rows.get(i).getCell(1).getText());
            assertEquals(expected.get(i).name, rows.get(i).getCell(2).getText());
        }
    }

    private void assertSelections(Messages messages, String selections) {
        Msg message = messages.getMessage(0);
        assertTrue(message.getSummary().contains("Selected ProgrammingLanguage(s)"));
        assertEquals(selections, message.getDetail());
    }

    private void assertHistory(Page page, String... expected) {
        String expectedHistory = String.join(" | ", expected);
        assertEquals(expectedHistory, page.getFullHistory());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        assertEquals("checkbox", cfg.get("selectionMode"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:button")
        CommandButton submit;

        @FindBy(id = "form:toggleSelectPageOnly")
        SelectBooleanButton toggleSelectPageOnly;

        @FindBy(id = "form:loadCallCount")
        WebElement loadCallCountInput;

        @FindBy(id = "form:fullHistory")
        WebElement fullHistoryInput;

        @Override
        public String getLocation() {
            return "datatable/dataTable050.xhtml";
        }

        public int getLoadCallCount() {
            return Integer.parseInt(loadCallCountInput.getAttribute("value"));
        }

        public String getFullHistory() {
            return fullHistoryInput.getAttribute("value");
        }
    }

    private static class ExpectedLanguage {
        final int id;
        final String name;

        ExpectedLanguage(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
