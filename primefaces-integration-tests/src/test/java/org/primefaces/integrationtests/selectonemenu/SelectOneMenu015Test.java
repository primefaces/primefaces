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
package org.primefaces.integrationtests.selectonemenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneMenu;
import org.primefaces.selenium.component.base.ComponentUtils;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectOneMenu015Test extends AbstractPrimePageTest {

    SelectOneMenu015Test() {
        super();
    }

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: GitHub #14107 selectOnFocus=false arrow keys only highlight options")
    void arrowKeys(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act - arrow key on the closed menu
        selectOneMenu.getLabel().sendKeys(Keys.ARROW_DOWN);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(selectOneMenu.getPanel()));

        // Assert - the panel opens without changing the value
        assertHighlighted(selectOneMenu, "Lewis");
        assertUnchanged(page);

        // Act - arrow key in the open panel
        selectOneMenu.getFilterInput().sendKeys(Keys.ARROW_DOWN);

        // Assert - the next option is highlighted, the label still shows the selected option
        assertHighlighted(selectOneMenu, "Max");
        assertUnchanged(page);

        // Act
        selectOneMenu.getFilterInput().sendKeys(Keys.ESCAPE);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(selectOneMenu.getPanel()));

        // Assert
        assertUnchanged(page);
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: GitHub #14107 selectOnFocus=false filter only highlights, enter selects")
    void filter(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        selectOneMenu.show();

        // Act
        ComponentUtils.sendKeys(selectOneMenu.getFilterInput(), "Char");

        // Assert
        assertHighlighted(selectOneMenu, "Charles");
        assertUnchanged(page);

        // Act
        PrimeSelenium.guardAjax(selectOneMenu.getFilterInput()).sendKeys(Keys.ENTER);

        // Assert
        assertChanged(page, "Charles", "3");
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: GitHub #14107 selectOnFocus=false typing only highlights, tab selects")
    void typeAhead(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Act - typing on the closed menu
        selectOneMenu.getLabel().sendKeys("m");
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(selectOneMenu.getPanel()));

        // Assert - the panel opens with the match highlighted, without changing the value
        assertHighlighted(selectOneMenu, "Max");
        assertUnchanged(page);

        // Act
        PrimeSelenium.guardAjax(selectOneMenu.getFilterInput()).sendKeys(Keys.TAB);

        // Assert
        assertChanged(page, "Max", "2");
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    private void assertHighlighted(SelectOneMenu selectOneMenu, String label) {
        assertEquals(label, selectOneMenu.getItems().findElement(By.cssSelector("li.ui-state-highlight")).getText());
    }

    private void assertUnchanged(Page page) {
        assertEquals("Lewis", page.selectOneMenu.getSelectedLabel());
        assertEquals("1", page.selectOneMenu.getInput().getDomProperty("value"));
        assertTrue(page.messages.isEmpty());
    }

    private void assertChanged(Page page, String label, String value) {
        assertEquals(label, page.selectOneMenu.getSelectedLabel());
        assertEquals(value, page.selectOneMenu.getInput().getDomProperty("value"));
        assertEquals(1, page.messages.getAllMessages().size());
        assertEquals(value, page.messages.getMessage(0).getDetail());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneMenu Config = " + cfg);
        assertTrue(cfg.getBoolean("filter"));
        assertEquals("contains", cfg.getString("filterMatchMode"));
        assertFalse(cfg.getBoolean("selectOnFocus"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu015.xhtml";
        }
    }
}
