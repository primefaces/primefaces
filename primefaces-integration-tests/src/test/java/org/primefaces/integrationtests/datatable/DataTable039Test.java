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

import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.SelectCheckboxMenu;
import org.primefaces.selenium.component.model.datatable.Row;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

class DataTable039Test extends AbstractDataTableTest {

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(1)
    @DisplayName("DataTable: Lazy: filter - selectCheckboxMenu - filterBean instead of intended API-usage - " +
            "https://github.com/primefaces/primefaces/issues/9349")
    void lazyFilterSelectCheckboxMenu(String xhtml) {
        // Arrange
        goTo(xhtml);
        DataTable dataTable = getDataTable();
        assertNotNull(dataTable);
        List<ProgrammingLanguage> langsFiltered = model.getLangs().stream()
                .filter(l -> l.getType() == ProgrammingLanguage.ProgrammingLanguageType.COMPILED)
                .sorted(Comparator.comparingInt(ProgrammingLanguage::getFirstAppeared))
                .collect(Collectors.toList());

        // Act
        dataTable.selectPage(1);
        dataTable.sort("First Appeared");
        SelectCheckboxMenu filterType = getFilterType();
        filterType.togglePanel();
        List<WebElement> filterTypeCheckboxes = filterType.getPanel().findElements(By.cssSelector(".ui-chkbox-box"));
        PrimeSelenium.guardAjax(filterTypeCheckboxes.get(1)).click();

        // Assert
        List<Row> rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size()); //one page
        for (int row = 0; row < 10; row++) {
            assertEquals(langsFiltered.get(row).getName(), rows.get(row).getCell(1).getText());
        }

        // Act
        getButtonUpdate().click();

        // Assert - filter must not be lost after update
        rows = dataTable.getRows();
        assertNotNull(rows);
        assertEquals(10, rows.size()); //one page
        for (int row = 0; row < 10; row++) {
            assertEquals(langsFiltered.get(row).getName(), rows.get(row).getCell(1).getText());
        }
        assertConfiguration(dataTable.getWidgetConfiguration());
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("DataTable Config = " + cfg);
        assertTrue(cfg.has("paginator"));
    }

    private static Stream<Arguments> provideXhtmls() {
        return Stream.of(Arguments.of("datatable/dataTable039.xhtml"));
    }

    private DataTable getDataTable() {
        return PrimeSelenium.createFragment(DataTable.class, By.id("form:datatable"));
    }

    private Messages getMessages() {
        return PrimeSelenium.createFragment(Messages.class, By.id("form:msgs"));
    }

    private CommandButton getButtonUpdate() {
        return PrimeSelenium.createFragment(CommandButton.class, By.id("form:buttonUpdate"));
    }

    private SelectCheckboxMenu getFilterType() {
        return PrimeSelenium.createFragment(SelectCheckboxMenu.class, By.id("form:datatable:filterType"));
    }
}
