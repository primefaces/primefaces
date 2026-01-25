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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.model.datatable.Header;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

class DataTable033Test extends AbstractDataTableTest {

    @Test
    @DisplayName("DataTable: test filtering after column order change")
    void columnOrderChangeAndFiltering(Page page) {

        int allRowsCount = page.dataTable.getRows().size();

        // test filter
        page.dataTable.filter("ID", "1002");
        assertNotSame(1, allRowsCount);

        // reset filter
        page.dataTable.filter("ID", "");
        assertSame(allRowsCount, page.dataTable.getRows().size());

        // switch column order now
        page.template.setValue("name country date status activity id");
        page.updateColumns.click();

        // test filter
        page.dataTable.filter("ID", "1002");
        assertNotSame(1, allRowsCount);

        // reset filter
        page.dataTable.filter("ID", "");
        assertSame(allRowsCount, page.dataTable.getRows().size());
    }

    @Test
    @DisplayName("DataTable: multiple p:columns")
    void hybridColumns(Page page) {
        // switch column order now
        page.template.setValue("id");
        page.updateColumns.click();

        Header header = page.dataTable.getHeader();
        assertEquals(4, header.getCells().size());
        assertEquals("ID", header.getCell(0).getColumnTitle().getText());
        assertEquals("NAME", header.getCell(1).getColumnTitle().getText());
        assertEquals("COUNTRY", header.getCell(2).getColumnTitle().getText());
        assertEquals("Activity", header.getCell(3).getColumnTitle().getText());

        // test filter
        long countryCount = page.dataTable.getRows().stream().filter(r -> r.getCell(1).getText().equals("Aruna Figeroa")).count();
        page.dataTable.filter(1, "Aruna Figeroa");
        assertEquals(countryCount, page.dataTable.getRows().size());

        // reset filter
        page.dataTable.filter("NAME", "");

        // test sort
        List<String> sortedList = page.dataTable.getRows().stream()
                .sorted(Comparator.comparing(r -> r.getCell(1).getText()))
                .map(r -> r.getCell(1).getText())
                .collect(Collectors.toList());
        page.dataTable.sort("NAME");

        List<String> sortedListPostFilter = page.dataTable.getRows().stream()
                .map(r -> r.getCell(1).getText())
                .collect(Collectors.toList());
        assertEquals(sortedList.size(), sortedListPostFilter.size());
        assertLinesMatch(sortedList, sortedListPostFilter);
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:template")
        InputText template;

        @FindBy(id = "form:customers")
        DataTable dataTable;

        @FindBy(id = "form:updateColumns")
        CommandButton updateColumns;

        @Override
        public String getLocation() {
            return "datatable/dataTable033.xhtml";
        }
    }
}
