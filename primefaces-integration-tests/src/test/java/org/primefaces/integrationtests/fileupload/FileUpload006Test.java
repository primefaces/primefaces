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

import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.FileUpload;

import java.io.File;

import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests advanced multiple file upload.
 * p:fileUpload mode=advanced auto=false multiple=true dragDrop=false (sequential=false)
 */
// Selenium SafariDriver does not support file uploads
@Tag("SafariExclude")
class FileUpload006Test extends AbstractFileUploadTest {

    @Test
    @Order(1)
    void advancedMultipleUploadSingleFile(Page page) throws Exception {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.csv");
        fileUpload.setValue(file);
        assertTrue(fileUpload.getWidgetValues().contains(file.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        wait4File(page.uploadedFiles, 1, file.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(2)
    void advancedMultipleUploadMultipleFiles(Page page) throws Exception {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);

        assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        wait4File(page.uploadedFiles, file1.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(3)
    void advancedMultipleUploadMultipleFilesTwice(Page page) throws Exception {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);

        assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        wait4File(page.uploadedFiles, file1.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);

        // Act
        File file3 = locateClientSideFile("file2.csv");
        File file4 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file3, file4);

        assertTrue(fileUpload.getWidgetValues().contains(file3.getName()), fileUpload.getWidgetValues().toString());
        assertTrue(fileUpload.getWidgetValues().contains(file3.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        wait4File(page.uploadedFiles, 3, ".csv"); // order is not stable

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2, file3, file4);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(4)
    void advancedMultipleUploadCancel(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);

        assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedCancelButton().click();
        assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(5)
    void advancedMultipleUploadFileLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        File file3 = locateClientSideFile("file2.csv");
        File file4 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file1, file2, file3, file4);

        assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        assertTrue(fileUpload.getWidgetValues().contains(file3.getName()), fileUpload.getWidgetValues().toString());
        assertTrue(fileUpload.getWidgetValues().contains(file4.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        wait4File(page.uploadedFiles, file1.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2, file3, file4);

        // Act
        File file5 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file5);
        assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());
        // upload button is not visible
        assertTrue(fileUpload.getWidgetErrorMessages().toString().contains("Maximum number of files exceeded"), fileUpload.getWidgetErrorMessages().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2, file3, file4);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(6)
    void advancedMultipleUploadSizeLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file3.csv");
        fileUpload.setValue(file);
        assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());
        // upload button is not visible
        assertTrue(fileUpload.getWidgetErrorMessages().toString().contains("Invalid file size"), fileUpload.getWidgetErrorMessages().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(7)
    void advancedMultipleUploadAllowTypes(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.png");
        fileUpload.setValue(file);
        assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());
        // upload button is not visible
        assertTrue(fileUpload.getWidgetErrorMessages().toString().contains("Invalid file type"), fileUpload.getWidgetErrorMessages().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    private void assertConfiguration(FileUpload fileUpload) {
        JSONObject cfg = fileUpload.getWidgetConfiguration();
        System.out.println("FileInput Config = " + cfg);
        assertFalse(cfg.has("skinSimple"));
        assertFalse(cfg.has("auto"));
        assertFalse(cfg.getBoolean("dnd"));
        assertEquals(4, Integer.parseInt(fileUpload.getInput().getAttribute("data-p-filelimit")));
        assertEquals(100, Long.parseLong(fileUpload.getInput().getAttribute("data-p-sizelimit")));
        assertEquals("/(\\.|\\/)(csv)$/", fileUpload.getInput().getAttribute("data-p-allowtypes"));
        assertNotNull(fileUpload.getInput().getAttribute("multiple"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:fileupload")
        FileUpload fileupload;

        @FindBy(id = "form:uploadedfiles")
        DataTable uploadedFiles;

        @Override
        public String getLocation() {
            return "fileupload/fileUpload006.xhtml";
        }
    }
}
