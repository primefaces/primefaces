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
package org.primefaces.integrationtests.theming;

import org.primefaces.selenium.AbstractPrimePage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class Theming002Test extends AbstractThemingTest {

    /** description, element (css selector), CSS property, token */
    private static final String[][] WIRING = {
        {"Button primary", "[id='form:buttonPrimary']", "background-color", "--p-button-primary-background"},
        {"Button primary", "[id='form:buttonPrimary']", "color", "--p-button-primary-color"},
        {"Button primary", "[id='form:buttonPrimary']", "border-top-color", "--p-button-primary-border-color"},
        {"Button secondary", "[id='form:buttonSecondary']", "background-color", "--p-button-secondary-background"},
        {"Button secondary", "[id='form:buttonSecondary']", "color", "--p-button-secondary-color"},
        {"InputText", "[id='form:inputText']", "background-color", "--p-inputtext-background"},
        {"InputText", "[id='form:inputText']", "color", "--p-inputtext-color"},
        {"InputText", "[id='form:inputText']", "border-top-color", "--p-inputtext-border-color"},
        {"SelectOneMenu", "[id='form:selectOneMenu']", "background-color", "--p-select-background"},
        {"SelectOneMenu", "[id='form:selectOneMenu']", "border-top-color", "--p-select-border-color"},
        {"SelectOneMenu label", "[id='form:selectOneMenu_label']", "color", "--p-select-color"},
        {"Checkbox checked", "[id='form:checkbox'] .ui-chkbox-box", "background-color", "--p-checkbox-checked-background"},
        {"Checkbox checked", "[id='form:checkbox'] .ui-chkbox-box", "border-top-color", "--p-checkbox-checked-border-color"},
        {"DataTable header cell", "[id='form:dataTable'] thead th", "background-color", "--p-datatable-header-cell-background"},
        {"DataTable header cell", "[id='form:dataTable'] thead th", "color", "--p-datatable-header-cell-color"},
        {"DataTable row", "[id='form:dataTable'] tbody > tr", "background-color", "--p-datatable-row-background"},
        {"DataTable row", "[id='form:dataTable'] tbody > tr", "color", "--p-datatable-row-color"},
        {"DataTable body cell", "[id='form:dataTable'] tbody > tr > td", "border-bottom-color", "--p-datatable-body-cell-border-color"},
        {"Panel header", "[id='form:panel'] .ui-panel-titlebar", "background-color", "--p-panel-header-background"},
        {"Panel header", "[id='form:panel'] .ui-panel-titlebar", "color", "--p-panel-header-color"},
        {"Message error", "[id='form:message'].ui-message-error", "background-color", "--p-message-error-background"},
        {"Message error", "[id='form:message'].ui-message-error", "color", "--p-message-error-color"},
        {"Message error", "[id='form:message'].ui-message-error", "border-top-color", "--p-message-error-border-color"},
        {"TabView active tab", "[id='form:tabView'] .ui-tabs-header.ui-state-active", "background-color", "--p-tabs-tab-active-background"},
        {"TabView active tab", "[id='form:tabView'] .ui-tabs-header.ui-state-active", "color", "--p-tabs-tab-active-color"},
    };

    @Test
    @Order(1)
    @DisplayName("Theming: component styles are wired to their tokens in light mode")
    void wiringLight(Page page) {
        // Arrange
        setColorScheme("light");

        // Act & Assert
        assertWiring();
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("Theming: component styles are wired to their tokens in dark mode")
    void wiringDark(Page page) {
        // Arrange
        setColorScheme("light");
        String inputLight = computedStyle(page.inputText, "background-color");
        String buttonLight = computedStyle(page.buttonSecondary, "background-color");

        // Act
        setColorScheme("dark");

        // Assert: light-dark() tokens switch with the color-scheme ...
        assertNotEquals(inputLight, computedStyle(page.inputText, "background-color"), "InputText background does not switch to dark");
        assertNotEquals(buttonLight, computedStyle(page.buttonSecondary, "background-color"), "Button secondary background does not switch to dark");

        // ... and the components still use the (now dark) tokens
        assertWiring();
        assertNoJavascriptErrors();
    }

    private void assertWiring() {
        for (String[] wiring : WIRING) {
            WebElement element = getWebDriver().findElement(By.cssSelector(wiring[1]));
            assertTokenWired(wiring[0], element, wiring[2], wiring[3]);
        }
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:inputText")
        WebElement inputText;

        @FindBy(id = "form:buttonSecondary")
        WebElement buttonSecondary;

        @Override
        public String getLocation() {
            return "theming/theming002.xhtml";
        }
    }
}
