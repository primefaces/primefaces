/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.integrationtests.dataview;

import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.integrationtests.datatable.AbstractDataTableTest;
import org.primefaces.integrationtests.datatable.ProgrammingLanguage;
import org.primefaces.integrationtests.datatable.ProgrammingLanguageService;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataView;

import java.util.List;

public class DataView003Test extends AbstractDataTableTest {
    private final List<ProgrammingLanguage> langs = new ProgrammingLanguageService().getLangs();

    @Test
    @Order(1)
    @DisplayName("DataView: Multiviewstate")
    public void testMVS(Page page, DataView003Test.OtherPage otherPage) {
        // Arrange
        PrimeSelenium.goTo(page);
        DataView dataView = page.dataView;
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataView));

        // Assert
        Assertions.assertEquals(3, dataView.getPaginator().getPages().size());
        WebElement firstRowElt = dataView.getRowWebElement(0);
        Assertions.assertTrue(firstRowElt.getText().contains(langs.get(0).getId().toString()));
        Assertions.assertTrue(firstRowElt.getText().contains(langs.get(0).getName()));

        // Act
        dataView.selectPage(3);
        dataView.selectRowsPerPage(4); // --> we are now on page 2 (5 rows, 4 on page 1, 1 on page 2)

        // Assert
        Assertions.assertEquals(2, dataView.getPaginator().getPages().size());
        firstRowElt = dataView.getRowWebElement(0);
        Assertions.assertTrue(firstRowElt.getText().contains(langs.get(4).getId().toString()));
        Assertions.assertTrue(firstRowElt.getText().contains(langs.get(4).getName()));

        // Act
        PrimeSelenium.goTo(otherPage);
        PrimeSelenium.goTo(page);
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.visibleAndAnimationComplete(dataView));

        // Assert - Part 2
        Assertions.assertEquals(2, dataView.getPaginator().getPages().size());
        firstRowElt = dataView.getRowWebElement(0);
        Assertions.assertTrue(firstRowElt.getText().contains(langs.get(4).getId().toString()));
        Assertions.assertTrue(firstRowElt.getText().contains(langs.get(4).getName()));

        // Act
        page.buttonClearDataViewState.click();

        // Assert - Part 3
        Assertions.assertEquals(3, dataView.getPaginator().getPages().size()); // two rows per page
        firstRowElt = dataView.getRowWebElement(0); // back on page 1
        Assertions.assertTrue(firstRowElt.getText().contains(langs.get(0).getId().toString()));
        Assertions.assertTrue(firstRowElt.getText().contains(langs.get(0).getName()));

        assertConfiguration(dataView.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataView Config = " + cfg);
        Assertions.assertTrue(cfg.has("paginator"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:dataview")
        DataView dataView;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:buttonClearDataViewState")
        CommandButton buttonClearDataViewState;

        @Override
        public String getLocation() {
            return "dataview/dataView003.xhtml";
        }
    }

    public static class OtherPage extends AbstractPrimePage {
        @Override
        public String getLocation() {
            return "clientwindow/clientWindow001.xhtml";
        }
    }
}
