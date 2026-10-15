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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AutoComplete008Test extends AbstractPrimePageTest {

    @Test
    @Order(1)
    @DisplayName("AutoComplete: GitHub #15270 moreText row in table layout is announced during keyboard navigation")
    void moreTextAnnouncedInTableLayout(Page page) {
        // Arrange
        AutoComplete autoComplete = page.autoComplete;
        autoComplete.setValueWithoutTab("1");
        autoComplete.wait4Panel();

        List<WebElement> rows = autoComplete.getPanel().findElements(By.cssSelector("tr.ui-autocomplete-item"));
        assertEquals(4, rows.size());
        WebElement moreTextRow = rows.get(3);

        // Assert - moreText attributes are on the row, not on the cell
        assertEquals("form:autocomplete_moretext", moreTextRow.getAttribute("id"));
        assertEquals("More results available", moreTextRow.getAttribute("data-item-label"));
        assertEquals("More results available", moreTextRow.getAttribute("aria-label"));

        // Act - navigate with the keyboard down to the moreText row
        for (int i = 0; i < rows.size(); i++) {
            autoComplete.getInput().sendKeys(Keys.ARROW_DOWN);
        }

        // Assert - aria-activedescendant and live region point at the moreText row
        assertEquals("form:autocomplete_moretext", autoComplete.getInput().getAttribute("aria-activedescendant"));
        assertEquals("More results available",
                autoComplete.findElement(By.className("ui-autocomplete-status")).getAttribute("textContent"));
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:autocomplete")
        AutoComplete autoComplete;

        @Override
        public String getLocation() {
            return "autocomplete/autoComplete008.xhtml";
        }
    }
}
