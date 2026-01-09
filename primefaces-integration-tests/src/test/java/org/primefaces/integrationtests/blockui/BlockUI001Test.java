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
package org.primefaces.integrationtests.blockui;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.BlockUI;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Panel;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class BlockUI001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("BlockUI: Show the blocker")
    void show(Page page) {
        // Arrange
        BlockUI blockUI = page.blockUI;
        assertClickable(page.panel1);
        assertClickable(page.panel2);
        assertFalse(blockUI.isBlocking());

        // Act
        blockUI.show();

        // Assert
        assertTrue(blockUI.isBlocking());
        assertNotClickable(page.panel1);
        assertNotClickable(page.panel2);
        assertConfiguration(blockUI.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("BlockUI: Hide the blocker")
    void hide(Page page) {
        // Arrange
        BlockUI blockUI = page.blockUI;
        blockUI.show();
        assertTrue(blockUI.isBlocking());
        assertNotClickable(page.panel1);
        assertNotClickable(page.panel2);

        // Act
        blockUI.hide();

        // Assert
        assertClickable(page.panel1);
        assertClickable(page.panel2);
        assertFalse(blockUI.isBlocking());
        assertConfiguration(blockUI.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("BlockUI: Destroy the blocker and make sure DOM elements removed")
    void destroy(Page page) {
        // Arrange
        BlockUI blockUI = page.blockUI;
        blockUI.show();
        assertTrue(blockUI.isBlocking());
        assertEquals(2, blockUI.getOverlays().size());
        assertEquals(1, blockUI.getContents().size());
        assertNotClickable(page.panel1);
        assertNotClickable(page.panel2);

        // Act
        blockUI.destroy();

        // Assert
        assertClickable(page.panel1);
        assertClickable(page.panel2);
        assertEquals(0, blockUI.getOverlays().size());
        assertEquals(0, blockUI.getContents().size());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("BlockUI: #8548 Refresh the panel and make sure the blocker is not removed")
    void refresh(Page page) {
        // Arrange
        BlockUI blockUI = page.blockUI;
        assertClickable(page.panel1);
        assertClickable(page.panel2);

        // Act
        page.buttonRefresh.click();
        blockUI.show();

        // Assert
        assertTrue(blockUI.isBlocking());
        assertNoJavascriptErrors();
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("BlockUI Config = " + cfg);
        assertEquals("form:panel1,form:panel2", cfg.get("block"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:panel1")
        Panel panel1;

        @FindBy(id = "form:panel2")
        Panel panel2;

        @FindBy(id = "form:blockui")
        BlockUI blockUI;

        @FindBy(id = "form:btnRefresh")
        CommandButton buttonRefresh;

        @Override
        public String getLocation() {
            return "blockui/blockUI001.xhtml";
        }
    }

}
