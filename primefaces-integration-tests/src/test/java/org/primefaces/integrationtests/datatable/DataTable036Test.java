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
package org.primefaces.integrationtests.datatable;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;

import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataTable036Test extends AbstractDataTableTest {

    @Test
    @Order(1)
    @DisplayName("DataTable: MultiViewState - selection with non-lazy-mode")
    void multiViewStateSelection(PageNonLazy page, OtherPage otherPage) {
        testImplementationMultiViewStateSelection(page, otherPage, () -> languages);
    }

    @Test
    @Order(2)
    @DisplayName("DataTable: MultiViewState - selection with lazy-mode")
    void multiViewStateSelection(PageLazy page, OtherPage otherPage) {
        testImplementationMultiViewStateSelection(page, otherPage, model::getLangs);
    }

    private void testImplementationMultiViewStateSelection(PageWithMultiViewStateSelection page, OtherPage otherPage,
            Supplier<List<ProgrammingLanguage>> fnLangGetter) {
        // Arrange
        PrimeSelenium.goTo(page);
        PrimeSelenium.guardAjax(page.buttonClearTableState).click();
        DataTable dataTable = page.dataTable;
        assertNotNull(dataTable);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));

        // Act
        PrimeSelenium.guardAjax(dataTable.getCell(2, 0).getWebElement()).click();

        // Assert
        assertMessage(page, "ProgrammingLanguage Selected", fnLangGetter.get().get(2).getName());

        PrimeSelenium.goTo(otherPage);
        PrimeSelenium.goTo(page);

        // Assert - selection must not be lost after navigating to another page and back
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataTable));
        assertTrue(PrimeSelenium.hasCssClass(dataTable.getRow(2).getWebElement(), "ui-state-highlight"));
        assertEquals("true", dataTable.getRow(2).getWebElement().getAttribute("aria-selected"));
    }

    private void assertMessage(PageWithMultiViewStateSelection page, String summary, String detail) {
        System.out.println(page.messages.getMessage(0).getSummary());
        System.out.println("\t" + summary);
        System.out.println(page.messages.getMessage(0).getDetail());
        System.out.println("\t" + detail);
        assertTrue(page.messages.getMessage(0).getSummary().contains(summary));
        assertTrue(page.messages.getMessage(0).getDetail().contains(detail));
    }

    public abstract static class PageWithMultiViewStateSelection extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonClearTableState")
        CommandButton buttonClearTableState;
    }

    public static class PageLazy extends PageWithMultiViewStateSelection {
        @Override
        public String getLocation() {
            return "datatable/dataTable036_lazy.xhtml";
        }
    }

    public static class PageNonLazy extends PageWithMultiViewStateSelection {
        @Override
        public String getLocation() {
            return "datatable/dataTable036_nonLazy.xhtml";
        }
    }

    public static class OtherPage extends AbstractPrimePage {
        @Override
        public String getLocation() {
            return "clientwindow/clientWindow001.xhtml";
        }
    }
}