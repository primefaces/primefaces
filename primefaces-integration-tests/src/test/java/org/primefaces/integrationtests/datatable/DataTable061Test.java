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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.HeaderCell;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("DataTable-filter")
class DataTable061Test extends AbstractPrimePageTest {

    private static final List<String> TEXT = List.of("startsWith", "notStartsWith", "endsWith", "notEndsWith",
            "contains", "notContains", "exact", "notExact");
    private static final List<String> NUMERIC = List.of("equals", "notEquals", "lt", "lte", "gt", "gte");

    @Test
    @Order(1)
    @DisplayName("DataTable: GitHub #7427 no match mode picker without filterMatchModeOptions, with none, filter facet or filterFunction")
    void noPicker(Page page) {
        // Assert
        assertNull(cell(page.datatablePlain, "ID").getFilterMatchModeTrigger());
        assertNull(cell(page.datatablePlain, "First name").getFilterMatchModeTrigger());
        // the table default does not apply to them
        assertNull(cell(page.datatable, "Last login").getFilterMatchModeTrigger());
        assertNull(cell(page.datatable, "Facet").getFilterMatchModeTrigger());
        assertNull(cell(page.datatable, "Function").getFilterMatchModeTrigger());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: GitHub #7427 match mode options of the presets, lists and the table default")
    void options(Page page) {
        // Assert
        DataTable dataTable = page.datatable;
        assertEquals(NUMERIC, cell(dataTable, "ID").getFilterMatchModeOptions());
        assertEquals(TEXT, cell(dataTable, "First name").getFilterMatchModeOptions());
        assertEquals(List.of("contains", "startsWith", "exact"), cell(dataTable, "Last name").getFilterMatchModeOptions());
        assertEquals(NUMERIC, cell(dataTable, "Birth date").getFilterMatchModeOptions());
        assertEquals(List.of("equals", "notEquals"), cell(dataTable, "Role (EL)").getFilterMatchModeOptions());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(3)
    @DisplayName("DataTable: GitHub #7427 initial match mode is filterMatchMode if set, otherwise the first option")
    void initialMatchMode(Page page) {
        // Assert
        DataTable dataTable = page.datatable;
        assertEquals("equals", cell(dataTable, "ID").getFilterMatchMode());
        assertEquals("startsWith", cell(dataTable, "First name").getFilterMatchMode());
        assertEquals("contains", cell(dataTable, "Last name").getFilterMatchMode());
        assertEquals("gte", cell(dataTable, "Salary").getFilterMatchMode());

        WebElement trigger = cell(dataTable, "Salary").getFilterMatchModeTrigger();
        assertTrue(PrimeSelenium.hasCssClass(icon(trigger), "p-icon", "p-filter-matchmode-gte-icon", "p-datatable-filter-matchmode-icon"));
        assertEquals("false", trigger.getDomAttribute("aria-expanded"));
        assertNotNull(trigger.getDomAttribute("id"));
        assertTrue(trigger.getDomAttribute("aria-label").endsWith(": " + trigger.getDomAttribute("title")));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(4)
    @DisplayName("DataTable: GitHub #7427 filter with the picked match mode")
    void filterWithMatchMode(Page page) {
        // Arrange
        DataTable dataTable = page.datatable;

        // Act
        dataTable.filter("Salary", "2600");

        // Assert
        assertEquals(List.of("1", "4", "11", "533"), ids(dataTable));

        // Act
        dataTable.filterMatchMode("Salary", "lt");

        // Assert
        HeaderCell salary = cell(dataTable, "Salary");
        WebElement trigger = salary.getFilterMatchModeTrigger();
        assertEquals(List.of("2", "5", "6"), ids(dataTable));
        assertEquals("lt", salary.getFilterMatchMode());
        assertTrue(PrimeSelenium.hasCssClass(icon(trigger), "p-filter-matchmode-lt-icon"));
        assertFalse(PrimeSelenium.hasCssClass(icon(trigger), "p-filter-matchmode-gte-icon"));
        assertFalse(salary.getFilterMatchModeMenu().isDisplayed());
        assertEquals(trigger, PrimeSelenium.getWebDriver().switchTo().activeElement());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(5)
    @DisplayName("DataTable: GitHub #7427 match mode is kept across an AJAX update of the table")
    void keptAcrossUpdate(Page page) {
        // Arrange
        DataTable dataTable = page.datatable;
        dataTable.filter("Salary", "2600");
        dataTable.filterMatchMode("Salary", "lt");

        // Act
        page.buttonUpdate.click();

        // Assert
        HeaderCell salary = cell(dataTable, "Salary");
        assertEquals("lt", salary.getFilterMatchMode());
        assertTrue(PrimeSelenium.hasCssClass(icon(salary.getFilterMatchModeTrigger()), "p-filter-matchmode-lt-icon"));
        assertEquals("2600", salary.getColumnFilter().getDomProperty("value"));
        assertEquals(List.of("2", "5", "6"), ids(dataTable));
        // the menu of the previous rendering is removed
        String menuId = salary.getFilterMatchModeTrigger().getDomAttribute("aria-controls");
        assertEquals(1, PrimeSelenium.getWebDriver().findElements(By.id(menuId)).size());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(6)
    @DisplayName("DataTable: GitHub #7427 auto derives text/numeric/date from the value type, numeric/date need a converter")
    void auto(Page page) {
        // Assert
        DataTable dataTable = page.datatableAuto;
        assertEquals(NUMERIC, cell(dataTable, "ID").getFilterMatchModeOptions());
        assertEquals(TEXT, cell(dataTable, "First name").getFilterMatchModeOptions());
        assertNull(cell(dataTable, "Salary").getFilterMatchModeTrigger());
        assertNull(cell(dataTable, "Birth date").getFilterMatchModeTrigger());
        // enum: no preset
        assertNull(cell(dataTable, "Role").getFilterMatchModeTrigger());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(7)
    @DisplayName("DataTable: GitHub #7427 keyboard navigation of the match mode menu")
    void keyboard(Page page) {
        // Arrange
        HeaderCell salary = cell(page.datatable, "Salary");
        WebElement trigger = salary.getFilterMatchModeTrigger();
        WebElement menu = salary.getFilterMatchModeMenu();

        // Act - opens the menu on the selected match mode
        trigger.sendKeys(Keys.ARROW_DOWN);

        // Assert
        assertTrue(menu.isDisplayed());
        assertEquals("true", trigger.getDomAttribute("aria-expanded"));
        assertEquals("gte", focusedMatchMode());

        // Act - wraps around to the first one
        activeElement().sendKeys(Keys.ARROW_DOWN);

        // Assert
        assertEquals("equals", focusedMatchMode());

        // Act
        activeElement().sendKeys(Keys.ESCAPE);

        // Assert
        assertFalse(menu.isDisplayed());
        assertEquals("false", trigger.getDomAttribute("aria-expanded"));
        assertEquals(trigger, activeElement());
        assertEquals("gte", salary.getFilterMatchMode());

        // Act
        trigger.sendKeys(Keys.ARROW_DOWN);
        activeElement().sendKeys(Keys.ARROW_UP);
        assertEquals("gt", focusedMatchMode());
        PrimeSelenium.guardAjax(activeElement()).sendKeys(Keys.ENTER);

        // Assert
        assertFalse(menu.isDisplayed());
        assertEquals("gt", salary.getFilterMatchMode());
        assertEquals(trigger, activeElement());

        // Act
        trigger.sendKeys(Keys.ARROW_DOWN);
        activeElement().sendKeys(Keys.TAB);

        // Assert
        assertFalse(menu.isDisplayed());
        assertEquals(trigger, activeElement());
        assertEquals("menu", menu.getDomAttribute("role"));
        assertEquals("menuitemradio", menu.findElement(By.cssSelector("[data-match-mode='gt']")).getDomAttribute("role"));
        assertEquals("true", menu.findElement(By.cssSelector("[data-match-mode='gt']")).getDomAttribute("aria-checked"));
        assertNoJavascriptErrors();
    }

    @Test
    @Order(8)
    @DisplayName("DataTable: GitHub #7427 opening a menu closes the menu of another column")
    void outsideClick(Page page) {
        // Arrange
        HeaderCell salary = cell(page.datatable, "Salary");
        HeaderCell id = cell(page.datatable, "ID");

        // Act
        salary.getFilterMatchModeTrigger().click();
        id.getFilterMatchModeTrigger().click();

        // Assert
        assertFalse(salary.getFilterMatchModeMenu().isDisplayed());
        assertTrue(id.getFilterMatchModeMenu().isDisplayed());

        // Act - the open menu may cover the table, so press the mouse outside of it by script
        PrimeSelenium.executeScript("document.body.dispatchEvent(new MouseEvent('mousedown', {bubbles: true}));");

        // Assert
        assertFalse(id.getFilterMatchModeMenu().isDisplayed());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(9)
    @DisplayName("DataTable: GitHub #7427 clearFilters() resets the match modes")
    void clearFilters(Page page) {
        // Arrange
        DataTable dataTable = page.datatable;
        dataTable.filter("Salary", "2600");
        dataTable.filterMatchMode("Salary", "lt");

        // Act
        PrimeSelenium.executeScript(true, "PF('wgtTable').clearFilters();");

        // Assert
        HeaderCell salary = cell(dataTable, "Salary");
        assertEquals("gte", salary.getFilterMatchMode());
        assertTrue(PrimeSelenium.hasCssClass(icon(salary.getFilterMatchModeTrigger()), "p-filter-matchmode-gte-icon"));
        assertEquals(8, dataTable.getRows().size());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(10)
    @DisplayName("DataTable: GitHub #7427 a submitted match mode which is not an option is ignored")
    void submittedMatchModeIsValidated(Page page) {
        // Arrange
        DataTable dataTable = page.datatable;
        HeaderCell salary = cell(dataTable, "Salary");
        PrimeSelenium.executeScript("arguments[0].value = 'contains';",
                salary.getWebElement().findElement(By.className("p-datatable-filter-matchmode-input")));

        // Act
        dataTable.filter("Salary", "2600");

        // Assert - still filtered with gte
        assertEquals(List.of("1", "4", "11", "533"), ids(dataTable));
        page.buttonUpdate.click();
        assertEquals("gte", cell(dataTable, "Salary").getFilterMatchMode());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(11)
    @Tag("DataTable-mvs")
    @DisplayName("DataTable: GitHub #7427 multiViewState restores the match mode")
    void multiViewState(MultiViewStatePage page, OtherPage otherPage) {
        // Arrange
        PrimeSelenium.goTo(page);
        DataTable dataTable = page.datatable;
        dataTable.filter("Salary", "2600");
        dataTable.filterMatchMode("Salary", "gt");
        assertEquals(List.of("1", "4", "533"), ids(dataTable));

        // Act
        PrimeSelenium.goTo(otherPage);
        PrimeSelenium.goTo(page);

        // Assert
        assertEquals("gt", cell(dataTable, "Salary").getFilterMatchMode());
        assertEquals(List.of("1", "4", "533"), ids(dataTable));
        assertNoJavascriptErrors();
    }

    private static HeaderCell cell(DataTable dataTable, String headerText) {
        return dataTable.getHeader().getCell(headerText).orElseThrow();
    }

    private static WebElement icon(WebElement trigger) {
        return trigger.findElement(By.className("p-icon"));
    }

    private static List<String> ids(DataTable dataTable) {
        return dataTable.getRows().stream().map(row -> row.getCell(0).getText()).collect(Collectors.toList());
    }

    private static WebElement activeElement() {
        return PrimeSelenium.getWebDriver().switchTo().activeElement();
    }

    private static String focusedMatchMode() {
        return activeElement().getDomAttribute("data-match-mode");
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable datatable;

        @FindBy(id = "form:datatableAuto")
        DataTable datatableAuto;

        @FindBy(id = "form:datatablePlain")
        DataTable datatablePlain;

        @FindBy(id = "form:buttonUpdate")
        CommandButton buttonUpdate;

        @Override
        public String getLocation() {
            return "datatable/dataTable061.xhtml";
        }
    }

    public static class MultiViewStatePage extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable datatable;

        @Override
        public String getLocation() {
            return "datatable/dataTable061MultiViewState.xhtml";
        }
    }

    public static class OtherPage extends AbstractPrimePage {
        @Override
        public String getLocation() {
            return "clientwindow/clientWindow001.xhtml";
        }
    }
}
