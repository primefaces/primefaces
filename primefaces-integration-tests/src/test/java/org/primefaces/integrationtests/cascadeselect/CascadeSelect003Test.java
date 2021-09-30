/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.integrationtests.cascadeselect;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CascadeSelect;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;

public class CascadeSelect003Test extends BaseCascadeSelectTest {

    @Test
    @Order(1)
    @DisplayName("CascadeSelect: select and submit item (SelectItems with converter)")
    public void testBasic(Page page) {
        // Arrange
        CascadeSelect cascadeSelect = page.cascadeSelect;
        cascadeSelect.toggleDropdown();
        assertItems(cascadeSelect, 4);

        // Act
        cascadeSelect.select("Charles");

        // Assert
        assertMessage(page.messages, 0, "Selected driver", "Charles");
        assertConfiguration(cascadeSelect.getWidgetConfiguration());

        // Act
        page.button.click();

        // Assert
        assertMessage(page.messages, 0, "Selected driver", "Charles");
        Assertions.assertEquals("Charles", cascadeSelect.getSelectedLabel());
        assertConfiguration(cascadeSelect.getWidgetConfiguration());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:cascadeSelect")
        CascadeSelect cascadeSelect;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:msgs")
        Messages messages;

        @Override
        public String getLocation() {
            return "cascadeselect/cascadeSelect003.xhtml";
        }
    }
}
