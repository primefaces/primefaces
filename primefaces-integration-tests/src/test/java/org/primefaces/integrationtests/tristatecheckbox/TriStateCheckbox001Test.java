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
package org.primefaces.integrationtests.tristatecheckbox;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.TriStateCheckbox;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class TriStateCheckbox001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("TriStateCheckbox: click through all states")
    void clickThrough(Page page) {
        // Arrange / Assert
        TriStateCheckbox triStateCheckbox = page.triStateCheckbox;
        assertEquals(null, triStateCheckbox.getValue());

        // Act
        triStateCheckbox.click();

        // Assert
        assertEquals(Boolean.TRUE, triStateCheckbox.getValue());
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());

        // Act
        triStateCheckbox.click();

        // Assert
        assertEquals(Boolean.FALSE, triStateCheckbox.getValue());
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());

        // Act
        page.button.click();

        // Assert
        assertEquals(Boolean.FALSE, triStateCheckbox.getValue());
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());
    }

    @Test
    @Order(1)
    @DisplayName("TriStateCheckbox: widget toggle through all states")
    void widgetToggle(Page page) {
        // Arrange / Assert
        TriStateCheckbox triStateCheckbox = page.triStateCheckbox;
        assertEquals(null, triStateCheckbox.getValue());

        // Act
        triStateCheckbox.toggle();

        // Assert
        assertEquals(Boolean.TRUE, triStateCheckbox.getValue());
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());

        // Act
        triStateCheckbox.toggle();

        // Assert
        assertEquals(Boolean.FALSE, triStateCheckbox.getValue());
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());

        // Act
        page.button.click();

        // Assert
        assertEquals(Boolean.FALSE, triStateCheckbox.getValue());
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("TriStateCheckbox: setValue")
    void setValue(Page page) {
        // Arrange
        TriStateCheckbox triStateCheckbox = page.triStateCheckbox;
        triStateCheckbox.setValue(Boolean.TRUE);
        assertEquals(Boolean.TRUE, triStateCheckbox.getValue());
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());

        // Act
        page.button.click();

        // Assert
        assertEquals(Boolean.TRUE, triStateCheckbox.getValue());
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("TriStateCheckbox: disabled")
    void disable(Page page) {
        // Arrange
        TriStateCheckbox triStateCheckbox = page.triStateCheckbox;
        triStateCheckbox.setValue(Boolean.TRUE);
        assertEquals(Boolean.TRUE, triStateCheckbox.getValue());

        // Act
        triStateCheckbox.disable();
        triStateCheckbox.toggle();

        // Assert - value should not be accepted
        assertNotClickable(triStateCheckbox);
        assertEquals(Boolean.TRUE, triStateCheckbox.getValue());
        assertFalse(triStateCheckbox.isEnabled());
        assertCss(triStateCheckbox, "ui-state-disabled");
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("TriStateCheckbox: enabled")
    void enabled(Page page) {
        // Arrange
        TriStateCheckbox triStateCheckbox = page.triStateCheckbox;
        triStateCheckbox.setValue(Boolean.TRUE);
        assertEquals(Boolean.TRUE, triStateCheckbox.getValue());

        // Act
        triStateCheckbox.disable();
        triStateCheckbox.enable();
        triStateCheckbox.toggle();

        // Assert - value should not be accepted
        assertClickable(triStateCheckbox);
        assertEquals(Boolean.FALSE, triStateCheckbox.getValue());
        assertTrue(triStateCheckbox.isEnabled());
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());
    }

    @Test
    @Order(6)
    @DisplayName("TriStateCheckbox: readonly")
    void readonly(Page page) {
        // Arrange
        TriStateCheckbox triStateCheckbox = page.readonly;
        assertEquals(null, triStateCheckbox.getValue());

        // Act
        triStateCheckbox.toggle();

        // Assert - value should not be accepted
        assertEquals(null, triStateCheckbox.getValue());
        assertCss(triStateCheckbox.getBox(), "ui-chkbox-readonly");
        assertConfiguration(triStateCheckbox.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("TriStateCheckbox Config = " + cfg);
        assertTrue(cfg.has("id"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:triStateCheckbox")
        TriStateCheckbox triStateCheckbox;

        @FindBy(id = "form:readonly")
        TriStateCheckbox readonly;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "tristatecheckbox/triStateCheckbox001.xhtml";
        }
    }
}
