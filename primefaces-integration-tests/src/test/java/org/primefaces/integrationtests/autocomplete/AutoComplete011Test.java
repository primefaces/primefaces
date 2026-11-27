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
package org.primefaces.integrationtests.autocomplete;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.AutoComplete;
import org.primefaces.selenium.component.CommandButton;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoComplete011Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: GitHub #15602 forceSelection keeps the selected item when labels are duplicated")
    void duplicateLabelKeepsSelectedItem(Page page) {
        // Arrange - both suggestions are labeled "John Smith"
        page.autoComplete.setValueWithoutTab("john");
        page.autoComplete.wait4Panel();
        List<WebElement> items = page.autoComplete.getItems().findElements(By.cssSelector(".ui-autocomplete-item"));
        assertEquals(2, items.size());

        // Act - select the second "John Smith" and submit, which blurs the input
        items.get(1).click();
        assertEquals("2", page.hiddenInput.getAttribute("value"));
        page.submit.click();

        // Assert - the selected person is submitted, not the first one with the same label
        assertEquals("2", page.hiddenInput.getAttribute("value"));
        assertEquals("222", page.selected.getText());
        assertEquals("John Smith", page.autoComplete.getInput().getAttribute("value"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:person")
        AutoComplete autoComplete;

        @FindBy(id = "form:submit")
        CommandButton submit;

        @FindBy(id = "form:person_hinput")
        WebElement hiddenInput;

        @FindBy(id = "form:selected")
        WebElement selected;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete011.xhtml";
        }
    }
}
