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
package org.primefaces.integrationtests.treetable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.TreeTable;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class TreeTable007Test extends AbstractTreeTableTest {

    @Test
    @Order(1)
    @DisplayName("TreeTable: cellEdit (with filter and sort applied)")
    void cellEdit(Page page) {
        // Arrange
        TreeTable treeTable = page.treeTable;
        assertNotNull(treeTable);

        treeTable.getRow(0).toggle();
        root.getChildren().get(0).setExpanded(true);

        // Act - sort ascending & filter
        treeTable.sort("Name");
        treeTable.filter("Name", "down");

        // Act - toggle nodes
        treeTable.getRow(0).toggle();
        treeTable.getRow(1).toggle();

        // Act - edit cell and accept
        treeTable.getRow(1).getCell(0).getWebElement().click();
        String eltId = treeTable.getRow(1).getCell(0).getWebElement().findElement(By.cssSelector("input")).getAttribute("id");
        InputText input = PrimeSelenium.createFragment(InputText.class, By.id(eltId));
        input.clear();
        input.sendKeys("Spanish - IT");
        PrimeSelenium.guardAjax(input).sendKeys(Keys.ENTER);

        // Assert
        assertMessage(page.messages, 0, "Cell Changed", "Old: Spanish, New:Spanish - IT");
        PrimeSelenium.guardAjax(treeTable.getRow(1).getCell(3).getWebElement().findElement(By.cssSelector("button"))).click();
        assertMessage(page.messages, 0, "selected document", "Spanish - IT;10kb;Folder");

        // Act - edit cell and undo
        treeTable.getRow(1).getCell(2).getWebElement().click();
        eltId = treeTable.getRow(1).getCell(2).getWebElement().findElement(By.cssSelector("input")).getAttribute("id");
        input = PrimeSelenium.createFragment(InputText.class, By.id(eltId));
        input.clear();
        input.sendKeys("Folder - IT");
        PrimeSelenium.guardAjax(input).sendKeys(Keys.ESCAPE);

        // Assert
        PrimeSelenium.waitGui().until(
                PrimeExpectedConditions.elementToBeClickable(treeTable.getRow(1).getCell(3).getWebElement().findElement(By.cssSelector("button"))));
        PrimeSelenium.guardAjax(treeTable.getRow(1).getCell(3).getWebElement().findElement(By.cssSelector("button"))).click();
        assertMessage(page.messages, 0, "selected document", "Spanish - IT;10kb;Folder");

        assertConfiguration(treeTable.getWidgetConfiguration());
    }
    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TreeTable Config = " + cfg);
        assertEquals("treeTable", cfg.getString("widgetVar"));
        assertTrue(cfg.getBoolean("editable"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:treeTable")
        TreeTable treeTable;

        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @FindBy(id = "form:buttonResetTable")
        CommandButton buttonResetTable;

        @Override
        public String getLocation() {
            return "treetable/treeTable007.xhtml";
        }
    }
}
