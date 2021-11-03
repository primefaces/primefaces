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
package org.primefaces.integrationtests.datatable;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;

import java.util.stream.Stream;

public class DataTable028Test extends AbstractDataTableTest {

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(1)
    @DisplayName("DataTable: filter + sort + edit with own inputs - wrong manipulation of list elements - https://github.com/primefaces/primefaces/issues/7999")
    public void testFilterSortEdit(String xhtml) {
        // Arrange
        getWebDriver().get(PrimeSelenium.getUrl(xhtml));
        getButtonSave().click();

        // Assert
        assertInitalState();

        // Act 1 - filter on name with value BB2
        getDataTable().filter("Name", "bb2");

        // Act 2 - change all BB2 row values to BB3, press Save
        DataTable dataTable = getDataTable();
        for (int row=0; row<=2; row++) {
            WebElement eltName = dataTable.getRow(row).getCell(3).getWebElement().findElement(By.tagName("input"));
            eltName.clear();
            eltName.sendKeys("BB3");
        }
        getButtonSave().click();

        // Assert
        assertAfterBb3Update();

        // Act 3 - remove filter BB2, press Save
        getDataTable().filter("Name", "");
        getButtonSave().click();

        // Assert
        assertAfterBb3Update();

        // Act 4 - sort on code, press Save
        getDataTable().sort("Code");
        getButtonSave().click();

        // Assert
        if (xhtml.contains("Without")) {
            assertAfterBb3UpdateSorted();
        }
        else {
            assertAfterBb3Update();
        }

        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(2)
    @DisplayName("DataTable: filter + sort + edit with own inputs - V2")
    public void testFilterSortEditV2(String xhtml) {
        // Arrange
        getWebDriver().get(PrimeSelenium.getUrl(xhtml));
        getButtonSave().click();

        // Assert
        assertInitalState();

        // Act 1 - sort on code, press Save
        getDataTable().sort("Code");
        getButtonSave().click();

        // Act 2 - filter on name with value BB2
        getDataTable().filter("Name", "bb2");

        // Act 3 - change all BB2 row values to BB3, press Save
        DataTable dataTable = getDataTable();
        for (int row=0; row<=2; row++) {
            WebElement eltName = dataTable.getRow(row).getCell(3).getWebElement().findElement(By.tagName("input"));
            eltName.clear();
            eltName.sendKeys("BB3");
        }
        getButtonSave().click();

        // Assert
        assertAfterBb3UpdateSorted();

        // Act 4 - remove filter BB2, press Save
        getDataTable().filter("Name", "");
        getButtonSave().click();

        // Assert
        assertAfterBb3UpdateSorted();

        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(10)
    @DisplayName("DataTable: sort + edit with own inputs")
    public void testSortEdit(String xhtml) {
        // Arrange
        getWebDriver().get(PrimeSelenium.getUrl(xhtml));
        getButtonSave().click();

        // Assert
        assertInitalState();

        // Act 1 - change all BB2 row values to BB3, press Save
        DataTable dataTable = getDataTable();
        for (int row=0; row<=2; row++) {
            WebElement eltName = dataTable.getRow(row).getCell(3).getWebElement().findElement(By.tagName("input"));
            eltName.clear();
            eltName.sendKeys("BB3");
        }
        getButtonSave().click();

        // Assert
        assertAfterBb3Update();

        // Act 2 - sort on code, press Save
        getDataTable().sort("Code");
        getButtonSave().click();

        // Assert
        assertAfterBb3UpdateSorted();

        assertNoJavascriptErrors();
    }

    @ParameterizedTest
    @MethodSource("provideXhtmls")
    @Order(11)
    @DisplayName("DataTable: sort + edit with own inputs - V2")
    public void testSortEditV2(String xhtml) {
        // Arrange
        getWebDriver().get(PrimeSelenium.getUrl(xhtml));
        getButtonSave().click();

        // Assert
        assertInitalState();

        // Act 1 - sort on code, press Save
        getDataTable().sort("Code");
        getButtonSave().click();

        // Act 2 - change all BB2 row values to BB3, press Save
        DataTable dataTable = getDataTable();
        for (int row=2; row<=4; row++) {
            WebElement eltName = dataTable.getRow(row).getCell(3).getWebElement().findElement(By.tagName("input"));
            eltName.clear();
            eltName.sendKeys("BB3");
        }
        getButtonSave().click();

        // Assert
        assertAfterBb3UpdateSorted();

        assertNoJavascriptErrors();
    }

    private void assertInitalState() {
        String expected = StringUtils.deleteWhitespace("Result:\n" +
                "509, EUR, BB, BB2, A\n" +
                "512, EUR, BB, BB2, B\n" +
                "515, EUR, BB, BB2, C\n" +
                "516, USA, AA, AA, D\n" +
                "517, USA, AA, AA, E");
        Assertions.assertEquals(expected, StringUtils.deleteWhitespace(getEltDebugActual().getText()));
    }

    private void assertAfterBb3Update() {
        String expected = StringUtils.deleteWhitespace("Result:\n" +
                    "509, EUR, BB, BB3, A\n" +
                    "512, EUR, BB, BB3, B\n" +
                    "515, EUR, BB, BB3, C\n" +
                    "516, USA, AA, AA, D\n" +
                    "517, USA, AA, AA, E");
        Assertions.assertEquals(expected, StringUtils.deleteWhitespace(getEltDebugActual().getText()));
    }

    private void assertAfterBb3UpdateSorted() {
        String expected = StringUtils.deleteWhitespace("Result:\n" +
                    "516, USA, AA, AA, D\n" +
                    "517, USA, AA, AA, E\n" +
                    "509, EUR, BB, BB3, A\n" +
                    "512, EUR, BB, BB3, B\n" +
                    "515, EUR, BB, BB3, C");
        Assertions.assertEquals(expected, StringUtils.deleteWhitespace(getEltDebugActual().getText()));
    }

    private static Stream<Arguments> provideXhtmls() {
        return Stream.of(
                Arguments.of("datatable/dataTable028.xhtml"),
                Arguments.of("datatable/dataTable028WithoutFilteredValue.xhtml"));
    }

    private DataTable getDataTable() {
        return PrimeSelenium.createFragment(DataTable.class, By.id("form:referenceTable"));
    }

    private CommandButton getButtonSave() {
        return PrimeSelenium.createFragment(CommandButton.class, By.id("form:cmdSave"));
    }

    private WebElement getEltDebugInital() {
        return PrimeSelenium.createFragment(WebElement.class, By.id("debugInitial"));
    }

    private WebElement getEltDebugActual() {
        return PrimeSelenium.createFragment(WebElement.class, By.id("debugActual"));
    }

}
