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
package org.primefaces.integrationtests.dataexporter;

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataExporter003Test extends AbstractDataExporterTest {

    @Test
    @Order(1)
    @DisplayName("Exporter: #8205 XML ExportTag and ExportRowTag attributes ")
    @Tag("SafariExclude")
    @Tag("FirefoxExclude")
    void xml(Page page) throws IOException {
        // Arrange
        final String actualFileName = getActualFile("languages-custom-tags.xml");
        File actual = assertDownloadFileNotExists(actualFileName);
        DataTable dataTable = page.dataTable;
        assertEquals(3, dataTable.getRows().size());

        // Act
        page.buttonXml.click();

        // Assert
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.fileExists(actualFileName));
        File expected = getExpectedFile(actual.getName());
        assertFileEquals(expected, actual, "Expected XML files to match but they did not!");
    }


    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:datatable")
        DataTable dataTable;

        @FindBy(id = "form:buttonXml")
        CommandButton buttonXml;


        @Override
        public String getLocation() {
            return "dataexporter/dataExporter003.xhtml";
        }
    }
}
