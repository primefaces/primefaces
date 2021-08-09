/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.integrationtests.datatable.AbstractDataTableTest;
import org.primefaces.integrationtests.datatable.ProgrammingLanguageLazyDataModel;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataView;
import org.primefaces.selenium.component.model.data.Paginator;

import java.util.List;

public class DataView002Test extends AbstractDataTableTest {
    private final ProgrammingLanguageLazyDataModel lazyDataModel = new ProgrammingLanguageLazyDataModel();

    @Test
    @Order(1)
    @DisplayName("DataView: Lazy: Basic & Paginator")
    public void testBasicAndPaginator(Page page) {
        // Arrange
        DataView dataView = page.dataView;
        Assertions.assertNotNull(dataView);

        // Act
        //page.button.click();

        // Assert
        Assertions.assertNotNull(dataView.getLayoutOptionsWebElement());
        Assertions.assertNotNull(dataView.getPaginatorWebElement());
        Assertions.assertEquals(DataView.Layout.LIST, dataView.getActiveLayout());

        List<WebElement> rowElts = dataView.getRowsWebElement();
        Assertions.assertNotNull(rowElts);
        Assertions.assertEquals(9, rowElts.size());

        WebElement firstRowElt = dataView.getRowWebElement(0);
        Assertions.assertTrue(firstRowElt.getText().contains(lazyDataModel.getLangs().get(0).getId().toString()));
        Assertions.assertTrue(firstRowElt.getText().contains(lazyDataModel.getLangs().get(0).getName()));
        Assertions.assertTrue(PrimeSelenium.hasCssClass(firstRowElt, "ui-dataview-row"));

        Paginator paginator = dataView.getPaginator();
        Assertions.assertNotNull(paginator);
        Assertions.assertNotNull(paginator.getPages());
        Assertions.assertEquals(9, paginator.getPages().size());
        Assertions.assertEquals(1, paginator.getPage(0).getNumber());
        Assertions.assertEquals(2, paginator.getPage(1).getNumber());

        // Act - Part 2
        dataView.selectPage(2);

        // Assert - Part 2
        rowElts = dataView.getRowsWebElement();
        Assertions.assertEquals(9, rowElts.size());

        // Act - Part 3
        dataView.selectPage(9);

        // Assert - Part 3
        rowElts = dataView.getRowsWebElement();
        Assertions.assertNotNull(rowElts);
        Assertions.assertEquals(3, rowElts.size());

        firstRowElt = dataView.getRowWebElement(0);
        Assertions.assertTrue(firstRowElt.getText().contains(lazyDataModel.getLangs().get(72).getId().toString()));
        Assertions.assertTrue(firstRowElt.getText().contains(lazyDataModel.getLangs().get(72).getName()));

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

        @Override
        public String getLocation() {
            return "dataview/dataView002.xhtml";
        }
    }
}
