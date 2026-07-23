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
package org.primefaces.integrationtests.selectmanycheckbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectManyCheckbox;

import java.util.Arrays;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectManyCheckbox002Test extends AbstractSelectManyCheckboxTest {

    @Test
    @Order(1)
    @DisplayName("SelectManyCheckbox: disabled option is not clickable, others are")
    void disabledOption(Page page) {
        // Arrange
        SelectManyCheckbox selectManyCheckbox = page.selectManyCheckbox;
        assertEquals(4, selectManyCheckbox.getItemsSize());
        assertEquals(Arrays.asList("Lewis", "Max", "Charles", "Lando"), selectManyCheckbox.getLabels());

        // Act + Assert - Charles (index 2) is disabled, all others are clickable
        assertClickable(box(selectManyCheckbox, 0));
        assertClickable(box(selectManyCheckbox, 1));
        assertNotClickable(box(selectManyCheckbox, 2));
        assertClickable(box(selectManyCheckbox, 3));
        assertConfiguration(selectManyCheckbox.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectManyCheckbox: enabled options can still be selected and submitted")
    void selectEnabledOptions(Page page) {
        // Arrange
        SelectManyCheckbox selectManyCheckbox = page.selectManyCheckbox;

        // Act - select the enabled options
        selectManyCheckbox.select(0, 1, 3);
        page.button.click();

        // Assert
        assertSelected(selectManyCheckbox, Arrays.asList("Lewis", "Max", "Lando"));
        assertTrue(page.messages.getMessage(0).getDetail().contains("Lewis,Max,Lando"));
        assertConfiguration(selectManyCheckbox.getWidgetConfiguration());
    }

    private WebElement box(SelectManyCheckbox selectManyCheckbox, int index) {
        return selectManyCheckbox.getCheckboxes().get(index).findElement(By.className("ui-chkbox-box"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectManyCheckbox Config = " + cfg);
        assertTrue(cfg.has("id"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectmanycheckbox")
        SelectManyCheckbox selectManyCheckbox;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectmanycheckbox/selectManyCheckbox002.xhtml";
        }
    }
}
