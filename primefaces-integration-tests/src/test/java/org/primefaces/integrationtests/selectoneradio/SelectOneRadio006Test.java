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
package org.primefaces.integrationtests.selectoneradio;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.SelectOneRadio;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectOneRadio006Test extends AbstractPrimePageTest {

    @Test
    @Disabled("GitHub #13954: Disabled options are ignored silently for now")
    @Order(1)
    @DisplayName("SelectOneRadio: Select Disabled Option")
    void selectDisabled(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadio;
        assertEquals("One", selectOneRadio.getSelectedLabel());

        // Act
        WebDriver webDriver = getWebDriver();
        WebElement optionTwo = webDriver.findElement(By.cssSelector("input[value='two']"));
        JavascriptExecutor jse = (JavascriptExecutor) webDriver;
        jse.executeScript("arguments[0].value='three-disabled';", optionTwo);
        selectOneRadio.select("Two");

        page.submit.click();

        // page should be displaying an error message because a disabled option was selected
        assertTrue(getWebDriver().getPageSource().contains("Error"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:selectoneradio")
        SelectOneRadio selectOneRadio;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @Override
        public String getLocation() {
            return "selectoneradio/selectOneRadio006.xhtml";
        }
    }
}
