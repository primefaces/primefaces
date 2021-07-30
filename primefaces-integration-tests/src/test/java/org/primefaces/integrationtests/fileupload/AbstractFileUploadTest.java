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
package org.primefaces.integrationtests.fileupload;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.model.datatable.Row;

public abstract class AbstractFileUploadTest extends AbstractPrimePageTest {

    protected File locateClientSideFile(String fileName) {
        String folder = AbstractFileUploadTest.class.getPackage().getName().replace(".", "/");
        URL url = AbstractFileUploadTest.class.getResource("/" + folder + "/" + fileName);
        Assertions.assertNotNull(url, "client side file " + fileName + " does not exist in /" + folder);
        return new File(url.getPath());
    }

    /**
     * Uploaded files are displayed in a data table with columns name, size and errorMessage.
     * @param uploadedFiles the data tble to check against
     * @param files the (client side) files uploaded
     */
    protected void assertUploadedFiles(DataTable uploadedFiles, File... files) {
        Assertions.assertNotNull(uploadedFiles);
        Assertions.assertNotNull(uploadedFiles.getRows());
        String expectedFiles = Arrays.stream(files).map(f -> f.getName())
                .collect(Collectors.joining(","));
        String actualFiles = uploadedFiles.getRows().stream().map(r -> r.getCell(0).getText())
                .collect(Collectors.joining(","));
        String diag = expectedFiles + " <> " + actualFiles;
        if (files.length == 0) {
            // emptyMessage
            Assertions.assertEquals(1, uploadedFiles.getRows().size(), diag);
            Assertions.assertEquals(1, uploadedFiles.getRow(0).getCells().size(), diag);
        } else {
            Assertions.assertEquals(files.length, uploadedFiles.getRows().size(), diag);
        }

        // sequence is not guarateed to be the same, so sort by name and size
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
            Assertions.assertEquals(files[f].getName(), row.getCell(0).getText()); // same file name
            Assertions.assertEquals("", row.getCell(2).getText(), row.getCell(2).getText()); // empty error message
            Assertions.assertEquals(files[f].length(), Long.parseLong(row.getCell(1).getText())); // same file size
        }
    }

}
