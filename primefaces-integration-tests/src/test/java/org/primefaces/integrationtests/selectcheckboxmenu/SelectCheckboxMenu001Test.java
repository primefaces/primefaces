package org.primefaces.integrationtests.selectcheckboxmenu;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.SelectCheckboxMenu;

public class SelectCheckboxMenu001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectCheckboxMenu: select value and submit")
    public void testSubmit(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        String value = "London";
        assertSelected(menu, 0);

        // Act
        menu.selectValue(value);
        page.button.click();

        // Assert
        assertSelected(menu, 1);
        WebElement checkbox = menu.getSelectedCheckboxes().get(0);
        assertValue(checkbox, value);
        assertConfiguration(menu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectCheckboxMenu: disable the input")
    public void testDisabled(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        String value = "Rome";
        assertSelected(menu, 0);

        // Act
        menu.disable();
        menu.selectValue(value);
        page.button.click();

        // Assert
        assertSelected(menu, 0);
        assertConfiguration(menu.getWidgetConfiguration());
    }

    @Test
    @Order(3)
    @DisplayName("SelectCheckboxMenu: enable the input")
    public void testEnabled(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        String value = "Paris";
        assertSelected(menu, 0);
        menu.disable();

        // Act
        menu.enable();
        menu.selectValue(value);
        page.button.click();

        // Assert
        assertSelected(menu, 1);
        WebElement checkbox = menu.getSelectedCheckboxes().get(0);
        assertValue(checkbox, value);
        assertConfiguration(menu.getWidgetConfiguration());
    }

    @Test
    @Order(4)
    @DisplayName("SelectCheckboxMenu: check all the values")
    public void testCheckAll(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        assertSelected(menu, 0);

        // Act
        menu.checkAll();
        page.button.click();

        // Assert
        assertSelected(menu, 9);
        assertValue(menu.getSelectedCheckboxes().get(0), "Miami");
        assertValue(menu.getSelectedCheckboxes().get(1), "London");
        assertValue(menu.getSelectedCheckboxes().get(2), "Paris");
        assertValue(menu.getSelectedCheckboxes().get(3), "Istanbul");
        assertValue(menu.getSelectedCheckboxes().get(4), "Berlin");
        assertValue(menu.getSelectedCheckboxes().get(5), "Barcelona");
        assertValue(menu.getSelectedCheckboxes().get(6), "Rome");
        assertValue(menu.getSelectedCheckboxes().get(7), "Brasilia");
        assertValue(menu.getSelectedCheckboxes().get(8), "Amsterdam");
        assertConfiguration(menu.getWidgetConfiguration());
    }

    @Test
    @Order(5)
    @DisplayName("SelectCheckboxMenu: uncheck all the values")
    public void testUncheckAll(Page page) {
        // Arrange
        SelectCheckboxMenu menu = page.selectCheckboxMenu;
        menu.checkAll();
        assertSelected(menu, 9);

        // Act
        menu.uncheckAll();
        page.button.click();

        // Assert
        assertSelected(menu, 0);
        assertConfiguration(menu.getWidgetConfiguration());
    }

    private void assertSelected(SelectCheckboxMenu menu, int count) {
        Assertions.assertEquals(count, menu.getSelectedCheckboxes().size());
    }

    private void assertValue(WebElement checkbox, String value) {
        Assertions.assertEquals(value, checkbox.getAttribute("value"));
        Assertions.assertEquals("aria-checked", checkbox.getDomAttribute("aria-checked"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        Assertions.assertFalse(cfg.getBoolean("dynamic"));
        Assertions.assertTrue(cfg.getBoolean("showHeader"));
        Assertions.assertEquals("@(body)", cfg.get("appendTo"));
        System.out.println("SelectCheckboxMenu Config = " + cfg);
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:checkbox-menu")
        SelectCheckboxMenu selectCheckboxMenu;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "selectcheckboxmenu/selectCheckboxMenu001.xhtml";
        }
    }

}
