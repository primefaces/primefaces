/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.integrationtests.menu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Menu002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Menu: GitHub #15131 toggleable menu keeps the server side expanded state")
    void serverStateIsKept(Page page) {
        // Arrange
        clearMenuState();

        // Act
        page.goTo();

        // Assert
        assertExpanded(page.getHeader(page.menu, 0));
        assertCollapsed(page.getHeader(page.menu, 1));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Menu: GitHub #15131 toggleable menu keeps the expanded state of a programmatic MenuModel")
    void serverStateIsKeptForMenuModel(Page page) {
        // Arrange
        clearMenuState();

        // Act
        page.goTo();

        // Assert
        assertExpanded(page.getHeader(page.model, 0));
        assertCollapsed(page.getHeader(page.model, 1));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("Menu: GitHub #15131 an expand stored on the client wins over the server side state")
    void storedExpandWinsOverServerState(Page page) {
        // Arrange - nothing is collapsed anymore, so the stored state is an empty string
        clearMenuState();
        page.goTo();

        // Act - expand the submenu the server rendered collapsed, then reload
        page.getHeader(page.menu, 1).click();
        page.goTo();

        // Assert
        assertExpanded(page.getHeader(page.menu, 0));
        assertExpanded(page.getHeader(page.menu, 1));
        assertNoJavascriptErrors();

        // Cleanup so the stored state does not leak into other tests
        clearMenuState();
    }

    @Test
    @Order(4)
    @DisplayName("Menu: GitHub #15131 a collapse stored on the client wins over the server side state")
    void storedCollapseWinsOverServerState(Page page) {
        // Arrange
        clearMenuState();
        page.goTo();

        // Act - collapse the submenu the server rendered expanded, then reload
        page.getHeader(page.menu, 0).click();
        page.goTo();

        // Assert
        assertCollapsed(page.getHeader(page.menu, 0));
        assertCollapsed(page.getHeader(page.menu, 1));
        assertNoJavascriptErrors();

        // Cleanup so the stored state does not leak into other tests
        clearMenuState();
    }

    private void assertExpanded(WebElement header) {
        assertEquals("true", header.getAttribute("aria-expanded"));
        assertTrue(header.findElement(By.cssSelector("h3 > .ui-icon")).getAttribute("class").contains("ui-icon-triangle-1-s"),
                "Header should display the expanded icon");
        assertTrue(getFirstChild(header).isDisplayed(), "Children of an expanded submenu should be visible");
    }

    private void assertCollapsed(WebElement header) {
        assertEquals("false", header.getAttribute("aria-expanded"));
        assertTrue(header.findElement(By.cssSelector("h3 > .ui-icon")).getAttribute("class").contains("ui-icon-triangle-1-e"),
                "Header should display the collapsed icon");
        assertFalse(getFirstChild(header).isDisplayed(), "Children of a collapsed submenu should be hidden");
    }

    private WebElement getFirstChild(WebElement header) {
        return header.findElement(By.xpath("following-sibling::li[contains(@class, 'ui-submenu-child')][1]"));
    }

    private void clearMenuState() {
        PrimeSelenium.executeScript("PF('menu').clearState(); PF('model').clearState();");
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:menu")
        WebElement menu;

        @FindBy(id = "form:model")
        WebElement model;

        WebElement getHeader(WebElement menuElement, int index) {
            List<WebElement> headers = menuElement.findElements(By.cssSelector("ul.ui-menu-list > li.ui-widget-header"));
            return headers.get(index);
        }

        @Override
        public String getLocation() {
            return "menu/menu002.xhtml";
        }
    }
}
