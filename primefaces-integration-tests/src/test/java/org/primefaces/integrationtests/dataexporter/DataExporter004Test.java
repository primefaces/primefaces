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
package org.primefaces.integrationtests.dataexporter;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.TreeTable;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * TreeTable exporting, see GitHub #13590.
 */
class DataExporter004Test extends AbstractPrimePageTest {

    private static final String HEADER = "\"Name\",\"Type\"";

    @Test
    @Order(1)
    @DisplayName("TreeTable Exporter: GitHub #13590 pageOnly must export every row displayed on the current page")
    void exportPageOnly(Page page) {
        // Arrange - page 1 shows "Applications" and "Cloud" plus their expanded children;
        // "Primefaces" is collapsed, so "primefaces.app" is not displayed
        assertEquals(List.of("Applications", "Primefaces", "editor.app", "Cloud", "backup-1.zip"), displayedNames(page));

        // Act
        page.exportPageOnly.click();

        // Assert
        assertEquals(List.of(HEADER,
                "\"Applications\",\"Folder\"",
                "\"Primefaces\",\"Folder\"",
                "\"editor.app\",\"Application\"",
                "\"Cloud\",\"Folder\"",
                "\"backup-1.zip\",\"Zip\""), csv(page));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("TreeTable Exporter: GitHub #13590 pageOnly must follow the paginator to the second page")
    void exportPageOnlySecondPage(Page page) {
        // Arrange - page 2 shows the collapsed "Desktop" only
        page.treeTable.selectPage(2);
        assertEquals(List.of("Desktop"), displayedNames(page));

        // Act
        page.exportPageOnly.click();

        // Assert
        assertEquals(List.of(HEADER, "\"Desktop\",\"Folder\""), csv(page));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("TreeTable Exporter: GitHub #13590 selectionOnly must export the selected nodes, not the root node")
    void exportSelectionOnly(Page page) {
        // Arrange - checking "Cloud" selects it and its child "backup-1.zip"
        PrimeSelenium.guardAjax(row(page, "1").findElement(By.cssSelector("div.ui-chkbox-box"))).click();

        // Act
        page.exportSelectionOnly.click();

        // Assert
        assertEquals(List.of(HEADER,
                "\"Cloud\",\"Folder\"",
                "\"backup-1.zip\",\"Zip\""), csv(page));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("TreeTable Exporter: exporting everything is not restricted to the current page or to expanded nodes")
    void exportAll(Page page) {
        // Act
        page.exportAll.click();

        // Assert
        assertEquals(List.of(HEADER,
                "\"Applications\",\"Folder\"",
                "\"Primefaces\",\"Folder\"",
                "\"primefaces.app\",\"Application\"",
                "\"editor.app\",\"Application\"",
                "\"Cloud\",\"Folder\"",
                "\"backup-1.zip\",\"Zip\"",
                "\"Desktop\",\"Folder\"",
                "\"note-todo.txt\",\"Text\""), csv(page));
        assertNoJavascriptErrors();
    }

    private List<String> displayedNames(Page page) {
        return page.treeTable.getRows().stream().map(r -> r.getCell(0).getText()).toList();
    }

    private List<String> csv(Page page) {
        return page.csv.getText().lines().map(String::trim).filter(l -> !l.isEmpty()).toList();
    }

    private WebElement row(Page page, String rowKey) {
        return page.getWebDriver().findElement(By.id("form:treeTable_node_" + rowKey));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:treeTable")
        TreeTable treeTable;

        @FindBy(id = "form:exportPageOnly")
        CommandButton exportPageOnly;

        @FindBy(id = "form:exportSelectionOnly")
        CommandButton exportSelectionOnly;

        @FindBy(id = "form:exportAll")
        CommandButton exportAll;

        @FindBy(id = "form:csv")
        WebElement csv;

        @Override
        public String getLocation() {
            return "dataexporter/dataExporter004.xhtml";
        }
    }
}
