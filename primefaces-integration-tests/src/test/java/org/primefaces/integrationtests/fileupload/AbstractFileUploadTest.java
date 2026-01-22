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
package org.primefaces.integrationtests.fileupload;

import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.PrimeExpectedConditions;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class AbstractFileUploadTest extends AbstractPrimePageTest {

    protected File locateClientSideFile(String fileName) {
        String folder = AbstractFileUploadTest.class.getPackage().getName().replace(".", "/");
        URL url = AbstractFileUploadTest.class.getResource("/" + folder + "/" + fileName);
        assertNotNull(url, "client side file " + fileName + " does not exist in /" + folder);
        return new File(url.getPath().replace("%20", " "));
    }

    /**
     * Uploaded files are displayed in a data table with columns name and size.
     * @param uploadedFiles the data tble to check against
     * @param files the (client side) files uploaded
     */
    protected void assertUploadedFiles(DataTable uploadedFiles, File... files) {
        assertNotNull(uploadedFiles);
        assertNotNull(uploadedFiles.getRows());
        String expectedFiles = Arrays.stream(files).map(f -> f.getName())
                .collect(Collectors.joining(","));
        String actualFiles = uploadedFiles.getRows().stream().map(r -> r.getCell(0).getText())
                .collect(Collectors.joining(","));
        String diag = expectedFiles + " <> " + actualFiles;
        assertEquals(files.length, uploadedFiles.getRows().size(), diag);

        // sequence is not guaranteed to be the same, so sort by name and size
        Arrays.sort(files, (f1, f2) -> {
            int res = f1.getName().compareTo(f2.getName());
            res = res == 0 ? (int) (f1.length() - f2.length()) : res;
            return res;
        });
        List<Row> rows = new ArrayList<>(uploadedFiles.getRows());
        rows.sort((f1, f2) -> {
            int res = f1.getCell(0).getText().compareTo(f2.getCell(0).getText());
            res = res == 0 ? (int) (Long.parseLong(f1.getCell(1).getText()) - Long.parseLong(f1.getCell(1).getText())) : res;
            return res;
        });
        for (int f = 0; f < files.length; ++f) {
            Row row = rows.get(f);
            assertEquals(files[f].getName(), row.getCell(0).getText()); // same file name
            assertEquals(files[f].length(), Long.parseLong(row.getCell(1).getText())); // same file size
        }
    }

    protected void wait4EmptyMesssage(DataTable uploadedFiles) {
        PrimeSelenium.waitGui().until(ExpectedConditions.visibilityOf(
                uploadedFiles.findElement(By.tagName("tbody")).findElement(By.cssSelector("tr.ui-datatable-empty-message"))));
    }

    protected void wait4File(DataTable uploadedFiles, String filename) {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.ajaxQueueEmpty());
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(
                uploadedFiles.findElement(By.tagName("tbody")), filename));
    }

    protected void wait4File(DataTable uploadedFiles, int row, String filename) {
        PrimeSelenium.waitGui().until(PrimeExpectedConditions.ajaxQueueEmpty());
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(
                uploadedFiles.findElement(By.tagName("tbody")).findElements(By.tagName("tr")).get(row - 1), filename));
    }

}
