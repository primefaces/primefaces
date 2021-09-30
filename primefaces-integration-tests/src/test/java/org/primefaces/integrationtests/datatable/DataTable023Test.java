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

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.DataTable;

/**
 * Test for multiple filtered columns added via
 * {@link javax.faces.view.facelets.FaceletContext#includeFacelet}
 * and filtered via method
 */
public class DataTable023Test extends AbstractDataTableTest {

    private static final int ID = 0;
    private static final int NAME = 1;

    @Test
    @Order(1)
    @DisplayName("DataTable: filter by name")
    public void testFilterByName(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter(NAME, "C#");

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterByName("C#");
        assertRows(dataTable, langsFiltered);

        // Act
        dataTable.removeFilter(NAME);

        // Assert
        assertRows(dataTable, languages);

        assertNoJavascriptErrors();
    }

    @Test
    @Order(0)
    @DisplayName("DataTable: filter by id")
    public void testFilterById(Page page) {
        // Arrange
        DataTable dataTable = page.dataTable;

        // Act
        dataTable.filter(ID, "1");

        // Assert
        List<ProgrammingLanguage> langsFiltered = filterById(1);
        assertRows(dataTable, langsFiltered);

        // Act
        dataTable.removeFilter(ID);

        // Assert
        assertRows(dataTable, languages);

        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @Override
        public String getLocation() {
            return "datatable/dataTable023.xhtml";
        }
    }
}
