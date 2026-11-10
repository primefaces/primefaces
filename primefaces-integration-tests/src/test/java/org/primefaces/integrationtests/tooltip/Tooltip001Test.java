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
package org.primefaces.integrationtests.tooltip;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Tooltip;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class Tooltip001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("Tooltip: shown on mouse over the target, hidden on mouse out")
    void showHide(Page page) {
        // Arrange
        Tooltip tooltip = page.buttonTip;
        assertFalse(tooltip.isVisible());
        assertEquals("tooltip", tooltip.getDomAttribute("role"));
        assertEquals("form:buttonTip", tooltip.getTarget().getDomAttribute("aria-describedby"));

        // Act
        tooltip.show();

        // Assert
        assertTrue(tooltip.isVisible());
        assertEquals("Button tooltip", tooltip.getTooltipText());

        // Act
        tooltip.hide();

        // Assert
        assertFalse(tooltip.isVisible());
        assertConfiguration(tooltip.getWidgetConfiguration(), "form:button", "right");
    }

    @Test
    @Order(2)
    @DisplayName("Tooltip: text from the title of the target, hidden with Escape")
    void titleAndEscape(Page page) {
        // Arrange
        Tooltip tooltip = page.inputTip;
        WebElement target = tooltip.getTarget();
        assertNull(target.getDomAttribute("title"));
        assertEquals("form:inputTip", target.getDomAttribute("aria-describedby"));
        assertEquals("Input title", tooltip.getTooltipText());

        // Act
        tooltip.show();

        // Assert
        assertTrue(tooltip.isVisible());
        assertEquals("Input title", tooltip.getText());

        // Act
        tooltip.hideWithEscape();

        // Assert
        assertFalse(tooltip.isVisible());
        assertConfiguration(tooltip.getWidgetConfiguration(), "form:input", "bottom");
    }

    @Test
    @Order(3)
    @DisplayName("Tooltip: updated text after AJAX update of tooltip and target")
    void ajaxUpdate(Page page) {
        // Arrange
        Tooltip tooltip = page.buttonTip;

        // Act
        page.update.click();

        // Assert - the old tooltip element moved to the body was replaced
        assertEquals(1, getWebDriver().findElements(By.id("form:buttonTip")).size());
        assertFalse(tooltip.isVisible());
        assertEquals("form:buttonTip", tooltip.getTarget().getDomAttribute("aria-describedby"));

        // Act
        tooltip.show();

        // Assert
        assertTrue(tooltip.isVisible());
        assertEquals("Updated tooltip", tooltip.getTooltipText());

        // Act
        tooltip.hide();

        // Assert
        assertFalse(tooltip.isVisible());
        assertConfiguration(tooltip.getWidgetConfiguration(), "form:button", "right");
    }

    @Test
    @Order(4)
    @DisplayName("Tooltip: global tooltip shows the title or data-tooltip of the hovered element")
    void global(Page page) {
        // Arrange
        Tooltip tooltip = page.globalTip;
        assertFalse(tooltip.isVisible());

        // Act
        tooltip.show(page.link);

        // Assert - the title is moved to the tooltip, so the browser does not show it too
        assertTrue(tooltip.isVisible());
        assertEquals("Link title", tooltip.getTooltipText());
        assertNull(page.link.getDomAttribute("title"));

        // Act
        tooltip.hide();

        // Assert
        assertFalse(tooltip.isVisible());

        // Act
        tooltip.show(page.dataInput);

        // Assert
        assertTrue(tooltip.isVisible());
        assertEquals("Data tooltip", tooltip.getTooltipText());

        // Act
        tooltip.hideWithEscape();

        // Assert
        assertFalse(tooltip.isVisible());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("Tooltip: showDelay of 150 is not replaced by the default of 400")
    void showDelay(Page page) {
        // Arrange
        Tooltip tooltip = page.delayTip;

        // Act
        tooltip.show();

        // Assert
        assertTrue(tooltip.isVisible());
        assertEquals("Delay tooltip", tooltip.getTooltipText());
        JSONObject cfg = tooltip.getWidgetConfiguration();
        assertNoJavascriptErrors();
        assertEquals("form:delayButton", cfg.getString("target"));
        assertEquals(150, cfg.getInt("showDelay"));
    }

    private void assertConfiguration(JSONObject cfg, String target, String position) {
        assertNoJavascriptErrors();
        assertTrue(cfg.has("widgetVar"));
        assertEquals(target, cfg.getString("target"));
        assertEquals(position, cfg.getString("position"));
        assertEquals(0, cfg.getInt("showDelay"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:buttonTip")
        Tooltip buttonTip;

        @FindBy(id = "form:inputTip")
        Tooltip inputTip;

        @FindBy(id = "form:delayTip")
        Tooltip delayTip;

        @FindBy(css = ".p-tooltip-global.global-tip")
        Tooltip globalTip;

        @FindBy(id = "form:update")
        CommandButton update;

        @FindBy(id = "link")
        WebElement link;

        @FindBy(id = "dataInput")
        WebElement dataInput;

        @Override
        public String getLocation() {
            return "tooltip/tooltip001.xhtml";
        }
    }
}
