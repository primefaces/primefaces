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
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.FileUpload;

/**
 * Tests basic auto multiple file upload.
 * p:fileUpload mode=simple auto=true multiple=true (skinSimple=true)
 */
@Tag("SafariExclude") // Selenium SafariDriver does not support file uploads
public class FileUpload004Test extends AbstractFileUploadTest {

    @Test
    @Order(1)
    public void testBasicAutoMultipleUploadSingleFile(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.csv");
        PrimeSelenium.guardAjax(fileUpload).setValue(file);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file);
        assertConfiguration(fileUpload.getWidgetConfiguration());
        Assertions.assertNotNull(fileUpload.getInput().getAttribute("multiple"));
    }

    @Test
    @Order(2)
    public void testBasicAutoMultipleUploadMultipleFiles(Page page) throws Exception {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        PrimeSelenium.guardAjax(fileUpload).setValue(file1, file2);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload.getWidgetConfiguration());
        Assertions.assertNotNull(fileUpload.getInput().getAttribute("multiple"));
    }

    @Test
    @Order(3)
    public void testBasicAutoMultipleUploadMultipleFilesTwice(Page page) throws Exception {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        PrimeSelenium.guardAjax(fileUpload).setValue(file1, file2);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);

        // Act
        File file3 = locateClientSideFile("file2.csv");
        File file4 = locateClientSideFile("file1.csv");
        PrimeSelenium.guardAjax(fileUpload).setValue(file3, file4);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2, file3, file4);
        assertConfiguration(fileUpload.getWidgetConfiguration());
        Assertions.assertNotNull(fileUpload.getInput().getAttribute("multiple"));
    }

    private void assertConfiguration(JSONObject cfg) {
        System.out.println("FileInput Config = " + cfg);
        Assertions.assertTrue(cfg.getBoolean("skinSimple"));
        Assertions.assertTrue(cfg.getBoolean("auto"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:fileupload")
        FileUpload fileupload;

        @FindBy(id = "form:uploadedfiles")
        DataTable uploadedFiles;

        @Override
        public String getLocation() {
            return "fileupload/fileUpload004.xhtml";
        }
    }
}
