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
package org.primefaces.integrationtests.selectonemenu;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.SelectOneMenu;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectOneMenu015Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneMenu: GitHub #15435 item clicked during the fade-in of a never opened panel is selected")
    void clickDuringFadeInOfFreshWidget(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;

        // Act
        clickItemDuringFadeIn(selectOneMenu, 1);

        // Assert
        assertSelected(selectOneMenu, "Two", "2");
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneMenu: GitHub #15435 item clicked during the fade-in must not revert to the value of the previous opening")
    void clickDuringFadeInAfterPreviousOpening(Page page) {
        // Arrange
        SelectOneMenu selectOneMenu = page.selectOneMenu;
        selectOneMenu.select("Two");
        assertSelected(selectOneMenu, "Two", "2");

        // Act
        clickItemDuringFadeIn(selectOneMenu, 1);

        // Assert
        assertSelected(selectOneMenu, "Two", "2");
        assertConfiguration(selectOneMenu.getWidgetConfiguration());
    }

    private void clickItemDuringFadeIn(SelectOneMenu selectOneMenu, int index) {
        // the panel only fades in with animations enabled, which the test framework turns off
        try {
            PrimeSelenium.executeScript("PrimeFaces.utils.enableAnimations(); var w = " + selectOneMenu.getWidgetByIdScript()
                        + "; w.show(); w.items.eq(" + index + ").trigger('click');");
        }
        finally {
            PrimeSelenium.executeScript("PrimeFaces.utils.disableAnimations();");
        }
    }

    private void assertSelected(SelectOneMenu selectOneMenu, String label, String value) {
        assertEquals(label, selectOneMenu.getSelectedLabel());
        assertEquals(value, selectOneMenu.getInput().getDomProperty("value"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("SelectOneMenu Config = " + cfg);
        assertTrue(cfg.has("appendTo"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectonemenu")
        SelectOneMenu selectOneMenu;

        @Override
        public String getLocation() {
            return "selectonemenu/selectOneMenu015.xhtml";
        }
    }

}
