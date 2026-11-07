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
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.AutoComplete;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoComplete010Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: GitHub #14152 itemSelect + change with update=@form submit the clicked item every time")
    void clickedItemIsSubmittedAfterAjaxUpdate(Page page) {
        // first selection works on the initially rendered page,
        // the following ones run against a widget re-initialized by the AJAX update (with CSP the change handler
        // was registered after the widget init and fired with the typed text instead of the selected item)
        selectItem(page, "1", "123456789");
        selectItem(page, "1", "12345");
        selectItem(page, "1", "19875");
        selectItem(page, "1", "12345");
    }

    private void selectItem(Page page, String query, String itemLabel) {
        // Arrange - replace the current text with the query like a user would
        WebElement input = page.autoComplete.getInput();
        String currentValue = input.getAttribute("value");
        input.sendKeys(Keys.END);
        for (int i = 0; i < currentValue.length(); i++) {
            input.sendKeys(Keys.BACK_SPACE);
        }
        input.sendKeys(query);
        page.autoComplete.wait4Panel();

        // Act - click the item, which blurs the input (native change) before the item click
        WebElement item = page.autoComplete.getPanel().findElement(By.cssSelector("li[data-item-label='" + itemLabel + "']"));
        PrimeSelenium.guardAjax(item).click();
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.ajaxQueueEmpty());

        // Assert - model and input hold the selected item, not the typed query
        assertEquals(itemLabel, page.value.getText());
        assertEquals(itemLabel, page.autoComplete.getInput().getAttribute("value"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:autocomplete")
        AutoComplete autoComplete;

        @FindBy(id = "form:value")
        WebElement value;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete010.xhtml";
        }
    }
}
