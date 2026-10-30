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

import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class SelectOneMenu016Test extends AbstractPrimePageTest {

    SelectOneMenu016Test() {
        super();
    }

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: GitHub #14107 selectOnFocus=false arrow down only focuses options")
    void arrowDown(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        assertEquals("Lewis", selectOneMenu.getSelectedLabel());

        // Act - arrow key on the closed menu
        selectOneMenu.getLabel().sendKeys(Keys.ARROW_DOWN);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(selectOneMenu.getPanel()));

        // Assert - the panel opens without changing the value
        assertNotFocused(selectOneMenu, "Lewis");
        assertSelected(selectOneMenu, "Lewis");
        assertUnchanged(page);

        // Act - arrow key in the open panel
        selectOneMenu.getFilterInput().sendKeys(Keys.ARROW_DOWN);

        // Assert - the next option gets the focus, the selected option keeps its highlight and the label
        assertFocused(selectOneMenu, "Max");
        assertSelected(selectOneMenu, "Lewis");
        assertUnchanged(page);

        // Act
        selectOneMenu.getFilterInput().sendKeys(Keys.ESCAPE);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(selectOneMenu.getPanel()));

        // Assert
        assertNoFocusedItem(selectOneMenu);
        assertSelected(selectOneMenu, "Lewis");
        assertUnchanged(page);
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: GitHub #14107 selectOnFocus=false arrow up only focuses options")
    void arrowUp(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Act - arrow key on the closed menu
        selectOneMenu.getLabel().sendKeys(Keys.ARROW_UP);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(selectOneMenu.getPanel()));

        // Assert - the panel opens without changing the value
        assertNotFocused(selectOneMenu, "Lewis");
        assertSelected(selectOneMenu, "Lewis");
        assertUnchanged(page);

        // Act - arrow key in the open panel
        selectOneMenu.getFilterInput().sendKeys(Keys.ARROW_UP);

        // Assert
        assertFocused(selectOneMenu, "Select a driver");
        assertSelected(selectOneMenu, "Lewis");
        assertUnchanged(page);
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectOneMenu: GitHub #14107 selectOnFocus=false filter only focuses, enter selects")
    void filter(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        selectOneMenu.show();

        // Act
        ComponentUtils.sendKeys(selectOneMenu.getFilterInput(), "Char");

        // Assert
        assertFocused(selectOneMenu, "Charles");
        assertSelected(selectOneMenu, "Lewis");
        assertUnchanged(page);

        // Act
        PrimeSelenium.guardAjax(selectOneMenu.getFilterInput()).sendKeys(Keys.ENTER);

        // Assert
        assertNotFocused(selectOneMenu, "Charles");
        assertSelected(selectOneMenu, "Charles");
        assertChanged(page, "Charles", "3");
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("SelectOneMenu: GitHub #14107 selectOnFocus=false typing only focuses, tab selects")
    void typeAhead(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Act - typing on the closed menu
        selectOneMenu.getLabel().sendKeys("m");
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(selectOneMenu.getPanel()));

        // Assert - the panel opens with the match focused, without changing the value
        assertFocused(selectOneMenu, "Max");
        assertSelected(selectOneMenu, "Lewis");
        assertUnchanged(page);

        // Act
        PrimeSelenium.guardAjax(selectOneMenu.getFilterInput()).sendKeys(Keys.TAB);

        // Assert
        assertNotFocused(selectOneMenu, "Max");
        assertSelected(selectOneMenu, "Max");
        assertChanged(page, "Max", "2");
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("SelectOneMenu: GitHub #14107 selectOnFocus=false click outside keeps the selected option")
    void clickOutside(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        selectOneMenu.getLabel().sendKeys(Keys.ARROW_DOWN);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(selectOneMenu.getPanel()));
        selectOneMenu.getFilterInput().sendKeys(Keys.ARROW_DOWN);
        assertFocused(selectOneMenu, "Max");

        // Act
        page.outside.click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.invisibleAndAnimationComplete(selectOneMenu.getPanel()));

        // Assert
        assertNotFocused(selectOneMenu, "Lewis");
        assertSelected(selectOneMenu, "Lewis");
        assertUnchanged(page);
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    private void assertFocused(SelectOneMenu selectOneMenu, String label) {
        WebElement focused = selectOneMenu.getItems().findElement(By.cssSelector("li.p-state-focus"));
        assertEquals(label, focused.getDomAttribute("data-label"));
        assertEquals(focused.getDomAttribute("id"), selectOneMenu.getLabel().getDomAttribute("aria-activedescendant"));
    }

    private void assertNotFocused(SelectOneMenu selectOneMenu, String activeLabel) {
        assertNoFocusedItem(selectOneMenu);
        String activeId = selectOneMenu.getLabel().getDomAttribute("aria-activedescendant");
        assertEquals(activeLabel, selectOneMenu.getItems().findElement(By.id(activeId)).getDomAttribute("data-label"));
    }

    private void assertNoFocusedItem(SelectOneMenu selectOneMenu) {
        assertTrue(selectOneMenu.getItems().findElements(By.cssSelector("li.p-state-focus")).isEmpty());
    }

    private void assertSelected(SelectOneMenu selectOneMenu, String label) {
        WebElement selected = selectOneMenu.getItems().findElement(By.cssSelector("li.p-state-highlight"));
        assertEquals(label, selected.getDomAttribute("data-label"));
        List<WebElement> ariaSelected = selectOneMenu.getItems().findElements(By.cssSelector("li[aria-selected='true']"));
        assertEquals(1, ariaSelected.size());
        assertEquals(selected.getDomAttribute("id"), ariaSelected.get(0).getDomAttribute("id"));
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

        @FindBy(id = "outside")
        WebElement outside;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu016.xhtml";
        }
    }
}
