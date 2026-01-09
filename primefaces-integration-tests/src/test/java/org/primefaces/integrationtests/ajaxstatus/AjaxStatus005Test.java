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
package org.primefaces.integrationtests.ajaxstatus;

import org.primefaces.integrationtests.general.utilities.TestUtils;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AjaxStatus005Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AjaxStatus: Make sure error facet is displayed")
    void errorFacet(Page page) {
        // Arrange
        WebElement ajaxStatus = page.ajaxStatus;
        assertEquals("Default Displayed!", ajaxStatus.getText());

        // Act
        page.button.clickUnguarded();

        // Assert
        assertEquals("Start Facet!", ajaxStatus.getText());
        TestUtils.wait(3000);
        assertEquals("Error Facet!", ajaxStatus.getText());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "ajaxStatus")
        WebElement ajaxStatus;

        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "ajaxstatus/ajaxStatus005.xhtml";
        }
    }
}
