package org.primefaces.integrationtests.colorpicker;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.ColorPicker;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.util.LangUtils;

public class ColorPicker002Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("ColorPicker: Check I8LN for Dutch language that all labels are translated")
    public void testAria(Page page) {
        // Arrange
        ColorPicker colorPicker = page.colorPicker;

        // Act
        WebElement panel = colorPicker.showPanel();

        // Assert
        assertConfiguration(colorPicker.getWidgetConfiguration());
        assertAriaLabel(panel, "clr-color-value", "Kleurwaardeveld");
        assertAriaLabel(panel, "clr-open-label", "Open kleurkiezer");
        assertAriaLabel(panel, "clr-close", "Sluit kleurkiezer");
        assertAriaLabel(panel, "clr-clear", "Wis de geselecteerde kleur");
        assertAriaLabel(panel, "clr-hue-slider", "Tintschuif");
        assertAriaLabel(panel, "clr-alpha-slider", "Ondoorzichtigheidschuif");
        assertAriaLabel(panel, "clr-swatch-label", "Kleurstaal");
        assertAriaLabel(panel, "clr-color-area", "Verzadiging- en helderheidkiezer. Gebruik pijltoetsen om te selecteren.");
        assertAriaLabel(panel, "clr-color-marker", "Verzadiging: 73. Helderheid: 62.");
    }

    private void assertAriaLabel(WebElement panel, String id, String value) {
        WebElement ariaElement = panel.findElement(By.id(id));
        assertPresent(ariaElement);
        String ariaLabel = ariaElement.getDomAttribute("aria-label");
        if (LangUtils.isBlank(ariaLabel)) {
            ariaLabel = ariaElement.getAttribute("textContent");
        }
        Assertions.assertEquals(value, ariaLabel);
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("ColorPicker Config = " + cfg);
        Assertions.assertEquals("popup", cfg.getString("mode"));
        Assertions.assertEquals("auto", cfg.getString("themeMode"));
        Assertions.assertEquals("pill", cfg.getString("theme"));
        Assertions.assertEquals("nl", cfg.getString("locale"));
        Assertions.assertEquals("Wissen", cfg.getString("clearLabel"));
        Assertions.assertEquals("Sluit", cfg.getString("closeLabel"));
        Assertions.assertTrue(cfg.getBoolean("clearButton"));
        Assertions.assertTrue(cfg.getBoolean("closeButton"));
        Assertions.assertTrue(cfg.getBoolean("formatToggle"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:colorpicker")
        ColorPicker colorPicker;

        @FindBy(id = "form:button")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "colorpicker/colorPicker002.xhtml";
        }
    }
}
