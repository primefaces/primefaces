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
package org.primefaces.integrationtests.selectmanymenu;

import org.primefaces.integrationtests.general.service.RealDriverService;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectManyMenu;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectManyMenu004Test extends AbstractSelectManyMenuTest {

    @Test
    @Order(1)
    @DisplayName("SelectManyMenu: GitHub #12798 showToggleAll and showSelectionCount")
    void toggleAll(Page page) {
        RealDriverService driverService = new RealDriverService();
        driverService.init();
        List<String> allDrivers = driverService.getDrivers().stream().map(d -> d.getName()).collect(Collectors.toList());

        // Arrange
        assertSelected(page.selectManyMenu, Arrays.asList("Max", "Charles"));
        assertEquals("2 items selected", page.footer.getText());
        assertFalse(page.selectManyMenu.isAllSelected());

        // Act
        page.selectManyMenu.toggleAll();

        // Assert
        assertSelected(page.selectManyMenu, allDrivers);
        assertEquals(allDrivers.size() + " items selected", page.footer.getText());
        assertTrue(page.selectManyMenu.isAllSelected());

        // Act
        page.button.click();

        // Assert - state after update
        assertSelected(page.selectManyMenu, allDrivers);
        assertEquals(String.join(",", allDrivers), page.messages.getMessage(0).getDetail());
        assertEquals(allDrivers.size() + " items selected", page.footer.getText());
        assertTrue(page.selectManyMenu.isAllSelected());

        // Act
        page.selectManyMenu.toggleAll();

        // Assert
        assertSelected(page.selectManyMenu, Arrays.asList());
        assertEquals("No selected item", page.footer.getText());
        assertFalse(page.selectManyMenu.isAllSelected());

        // Act
        page.button.click();

        // Assert
        assertEquals("no driver selected", page.messages.getMessage(0).getSummary());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SelectManyMenu: GitHub #12798 showToggleAll only applies to the filtered items")
    void toggleAllWithFilter(Page page) {
        // Act
        page.selectManyMenu.filter("Lan");

        // Assert
        assertFalse(page.selectManyMenu.isAllSelected());

        // Act
        page.selectManyMenu.toggleAll();

        // Assert
        assertEquals("3 items selected", page.footer.getText());
        assertTrue(page.selectManyMenu.isAllSelected());

        // Act
        page.button.click();

        // Assert
        assertSelected(page.selectManyMenu, Arrays.asList("Max", "Charles", "Lando"));
        assertEquals("Max,Charles,Lando", page.messages.getMessage(0).getDetail());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectmanymenu")
        SelectManyMenu selectManyMenu;

        @FindBy(css = ".p-selectmanymenu-footer")
        WebElement footer;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectmanymenu/selectManyMenu004.xhtml";
        }
    }
}
