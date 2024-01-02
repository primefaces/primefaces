/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.FileUpload;
import org.primefaces.selenium.component.model.datatable.Row;

/**
 * Tests basic single file upload.
 * p:fileUpload mode=simple auto=false multiple=false (skinSimple=false)
 */
// Selenium SafariDriver does not support file uploads
@Tag("SafariExclude")
class FileUpload000Test extends AbstractFileUploadTest {

    @Test
    @Order(1)
    void basicSingleUpload(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.csv");
        fileUpload.setValue(file);
        assertTrue(fileUpload.getValue().contains(file.getName()));
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(2)
    void basicSingleUploadTwice(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file1);
        assertTrue(fileUpload.getValue().contains(file1.getName()));
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1);

        // Act
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file2);
        assertTrue(fileUpload.getValue().contains(file2.getName()));
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(3)
    void basicSingleUploadSizeLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file3.csv");
        fileUpload.setValue(file);
        assertTrue(fileUpload.getValue().contains(file.getName()));
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        // this error is raised by backing bean
        // Primefaces only limits upload size to sizeLimit if mode=simple skinSimple=false
        assertUploadErrors(page.uploadedFiles, "unexpected file size");
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(4)
    void basicSingleUploadAllowTypes(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.png");
        fileUpload.setValue(file);
        assertTrue(fileUpload.getValue().contains(file.getName()));
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        // this error is raised by backing bean
        // Primefaces does not check allowTypes if mode=simple skinSimple=false
        assertUploadErrors(page.uploadedFiles, "unexpected file type");
        assertConfiguration(fileUpload);
    }

    private void assertUploadErrors(DataTable uploadedFiles, String... errors) {
        assertNotNull(uploadedFiles);
        assertNotNull(uploadedFiles.getRows());
        assertEquals(errors.length, uploadedFiles.getRows().size());
        for (int f = 0; f < errors.length; ++f) {
            Row row = uploadedFiles.getRows().get(f);
            // no file with UPLOADER=commons
            if (row.getCells().size() != 1) {
                assertTrue(row.getCell(2).getText().contains(errors[f]), row.getCell(2).getText()); // matching error message
            }
        }
    }

    private void assertConfiguration(FileUpload fileUpload) {
        JSONObject cfg = fileUpload.getWidgetConfiguration();
        System.out.println("FileInput Config = " + cfg);
        assertFalse(cfg.has("skinSimple"));
        assertFalse(cfg.has("auto"));
        assertEquals(1, cfg.getInt("fileLimit"));
        assertEquals(100, cfg.getInt("maxFileSize"));
        assertEquals("/(\\.|\\/)(csv)$/", cfg.getString("allowTypes"));
        assertNull(fileUpload.getInput().getAttribute("multiple"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:fileupload")
        FileUpload fileupload;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:uploadedfiles")
        DataTable uploadedFiles;

        @Override
        public String getLocation() {
            return "fileupload/fileUpload000.xhtml";
        }
    }
}
