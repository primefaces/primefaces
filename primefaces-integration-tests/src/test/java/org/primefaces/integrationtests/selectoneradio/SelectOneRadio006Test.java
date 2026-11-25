/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectOneRadio;

public class SelectOneRadio006Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("SelectOneRadio: GitHub #13954 a tampered disabled option fails validation")
    public void selectDisabled(Page page) {
        // Arrange
        SelectOneRadio selectOneRadio = page.selectOneRadio;
        Assertions.assertEquals("One", selectOneRadio.getSelectedLabel());

        // Act - submit the disabled "three-disabled" via the "Two" radio button
        WebElement optionTwo = getWebDriver().findElement(By.cssSelector("input[value='two']"));
        PrimeSelenium.executeScript("arguments[0].value='three-disabled';", optionTwo);
        selectOneRadio.select("Two");
        page.submit.click();

        // Assert
        Assertions.assertEquals(1, page.messages.getAllMessages().size());
        String summary = page.messages.getMessage(0).getSummary();
        Assertions.assertTrue(summary.contains("Radio") && summary.contains("Value is not valid"), summary);
        Assertions.assertEquals("one", page.value.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("SelectOneRadio: GitHub #13954 an enabled option is accepted")
    public void selectEnabled(Page page) {
        // Act
        page.selectOneRadio.select("Two");
        page.submit.click();

        // Assert
        Assertions.assertTrue(page.messages.isEmpty());
        Assertions.assertEquals("two", page.value.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:selectoneradio")
        SelectOneRadio selectOneRadio;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @FindBy(id = "form:value")
        WebElement value;

        @Override
        public String getLocation() {
            return "selectoneradio/selectOneRadio006.xhtml";
        }
    }
}
