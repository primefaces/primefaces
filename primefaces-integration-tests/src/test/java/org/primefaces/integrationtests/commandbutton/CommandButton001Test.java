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
package org.primefaces.integrationtests.commandbutton;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class CommandButton001Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("CommandButton: loading state")
    void loadingState(Page page) throws Exception {
        // Arrange
        CommandButton button = page.button;
        setAjaxMinLoadAnimation(500);

        // Act
        page.button.click();

        // Assert
        // we use an approach which should be more tolerant when CI-infrastructure is slow
        PrimeSelenium.waitGui().until(driver -> PrimeSelenium.hasCssClass(button, "ui-state-loading"));

        // Wait out min Ajax loading animation
        PrimeSelenium.wait(1000);
        assertFalse(PrimeSelenium.hasCssClass(button, "ui-state-loading"));
    }

    @Test
    @Order(2)
    @DisplayName("CommandButton: loading state 2")
    void loadingState2(Page page) {
        // Arrange
        CommandButton button = page.button;
        noAjaxMinLoadAnimation();

        // Act
        page.button.click();

        // Assert
        assertFalse(PrimeSelenium.hasCssClass(button, "ui-state-loading"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:button")
        CommandButton button;

        @Override
        public String getLocation() {
            return "commandbutton/commandButton001.xhtml";
        }
    }

}
