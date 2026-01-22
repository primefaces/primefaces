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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.FileUpload;
import org.primefaces.selenium.component.Messages;

import java.io.File;

import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests basic multiple file upload.
 * p:fileUpload mode=simple auto=false multiple=true (skinSimple=true)
 */
// Selenium SafariDriver does not support file uploads
@Tag("SafariExclude")
class FileUpload002Test extends AbstractFileUploadTest {

    @Test
    @Order(1)
    void basicMultipleUploadSingleFile(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.csv");
        fileUpload.setValue(file);
        assertTrue(fileUpload.getFilename().startsWith(file.getName()), fileUpload.getFilename());
        page.button.click();
        wait4File(page.uploadedFiles, 1, file.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(2)
    void basicMultipleUploadMultipleFiles(Page page) throws Exception {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);

        // the widget shows only the first file name and the number of additional files
        assertTrue(fileUpload.getFilename().startsWith(file1.getName()), fileUpload.getFilename());
        assertTrue(fileUpload.getFilename().matches(".*\\+\\s*1$"));
        page.button.click();
        wait4File(page.uploadedFiles, file1.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(2)
    void basicMultipleUploadMultipleFilesTwice(Page page) throws Exception {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);

        // the widget shows only the first file name and the number of additional files
        assertTrue(fileUpload.getFilename().startsWith(file1.getName()), fileUpload.getFilename());
        assertTrue(fileUpload.getFilename().matches(".*\\+\\s*1$"));
        page.button.click();
        wait4File(page.uploadedFiles, 1, file1.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);

        // Act
        File file3 = locateClientSideFile("file2.csv");
        File file4 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file3, file4);

        // the widget shows only the first file name and the number of additional files
        assertTrue(fileUpload.getFilename().startsWith(file3.getName()), fileUpload.getFilename());
        assertTrue(fileUpload.getFilename().matches(".*\\+\\s*1$"));
        page.button.click();
        wait4File(page.uploadedFiles, 3, file3.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2, file3, file4);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(3)
    void basicMultipleUploadFileLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        File file3 = locateClientSideFile("file3.csv");
        fileUpload.setValue(file1, file2, file3);

        page.button.click();
        wait4EmptyMesssage(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertFalse(page.messages.getAllMessages().isEmpty());
        assertEquals("Maximum number of files exceeded.",
                page.messages.getMessage(0).getSummary());
        // Primefaces sends "empty" request if mode=simple skinSimple=true
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(4)
    void basicMultipleUploadSizeLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file3.csv");
        fileUpload.setValue(file);
        assertTrue(fileUpload.getFilename().contains(file.getName()));
        page.button.click();
        wait4EmptyMesssage(page.uploadedFiles);

        // Assert
        assertFalse(page.messages.getAllMessages().isEmpty());
        assertEquals("Invalid file size.",
                page.messages.getMessage(0).getSummary());
        assertNoJavascriptErrors();
        // Primefaces sends "empty" request if mode=simple skinSimple=true
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(5)
    void basicMultipleUploadAllowTypes(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.png");
        fileUpload.setValue(file);
        assertTrue(fileUpload.getFilename().contains(file.getName()));
        page.button.click();
        wait4EmptyMesssage(page.uploadedFiles);

        // Assert
        assertFalse(page.messages.getAllMessages().isEmpty());
        assertEquals("Invalid file type.",
                page.messages.getMessage(0).getSummary());
        assertNoJavascriptErrors();
        // Primefaces sends "empty" request if mode=simple skinSimple=true
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    private void assertConfiguration(FileUpload fileUpload) {
        JSONObject cfg = fileUpload.getWidgetConfiguration();
        System.out.println("FileInput Config = " + cfg);
        assertEquals("{name} {size}", cfg.getString("messageTemplate"));
        assertTrue(cfg.getBoolean("skinSimple"));
        assertFalse(cfg.has("auto"));
        assertEquals(2, Integer.parseInt(fileUpload.getInput().getAttribute("data-p-filelimit")));
        assertEquals(100, Long.parseLong(fileUpload.getInput().getAttribute("data-p-sizelimit")));
        assertEquals("/(\\.|\\/)(csv)$/", fileUpload.getInput().getAttribute("data-p-allowtypes"));
        assertNotNull(fileUpload.getInput().getAttribute("multiple"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:fileupload")
        FileUpload fileupload;

        @FindBy(id = "form:button")
        CommandButton button;

        @FindBy(id = "form:uploadedfiles")
        DataTable uploadedFiles;

        @Override
        public String getLocation() {
            return "fileupload/fileUpload002.xhtml";
        }
    }
}
