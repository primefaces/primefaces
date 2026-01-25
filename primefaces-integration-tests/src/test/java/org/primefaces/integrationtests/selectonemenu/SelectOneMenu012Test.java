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
package org.primefaces.integrationtests.selectonemenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.SelectOneMenu;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SelectOneMenu012Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: GitHub #8765 menu is generating duplicate IDs")
    void basic(Page page) {
        assertDoesNotThrow(() -> {
            // Arrange
            SelectOneMenu menu = page.selectOneMenu;

            // Act (do nothing)
            menu.show();

            // Assert (when fixed there should no longer be JS errors about duplicate ids)
            assertNoJavascriptErrors();
        });
    }

   /**
     * Checks the browse console and asserts there are SEVERE level messages.
     */
    protected void assertJavascriptErrors() {
        LogEntries logEntries = getLogsForType(LogType.BROWSER);
        if (logEntries == null) {
            return;
        }
        System.out.println(logEntries.toJson());
        List<LogEntry> severe = logEntries.getAll().stream()
                    .filter(l -> l.getLevel() == Level.SEVERE)
                    .collect(Collectors.toList());
        assertFalse(severe.isEmpty(), "No Javascript errors were found but they were expected. Was bug #8765 fixed???.\r\n" + severe.toString());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu012.xhtml";
        }
    }
}
