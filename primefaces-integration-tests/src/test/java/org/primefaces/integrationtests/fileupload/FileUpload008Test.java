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
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.FileUpload;

/**
 * Tests advanced auto multiple file upload.
 * p:fileUpload mode=advanced auto=true multiple=true dragDropSupport=false (sequential=false)
 */
@Tag("SafariExclude") // Selenium SafariDriver does not support file uploads
public class FileUpload008Test extends AbstractFileUploadTest {

    @Test
    @Order(1)
    public void testAdvancedAutoMultipleUploadSingleFile(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.csv");
        fileUpload.setValue(file);
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(2)
    public void testAdvancedAutoMultipleUploadMultipleFiles(Page page) throws Exception {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(3)
    public void testAdvancedAutoMultipleUploadMultipleFilesTwice(Page page) throws Exception {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);

        // Act
        File file3 = locateClientSideFile("file2.csv");
        File file4 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file3, file4);
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2, file3, file4);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(4)
    public void testAdvancedAutoMultipleUploadFileLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());
        String fileLimitMsg = fileUpload.getWidgetConfiguration().getString("fileLimitMessage");
        Assertions.assertNotNull(fileLimitMsg);
        Assertions.assertFalse(fileLimitMsg.isEmpty());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);

        // Act
        File file3 = locateClientSideFile("file2.csv");
        File file4 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file3, file4);
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2, file3, file4);

        // Act
        File file5 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file5);
        Assertions.assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());
        // upload button is not visible
        Assertions.assertTrue(fileUpload.getWidgetErrorMessages().contains(fileLimitMsg), fileUpload.getWidgetErrorMessages().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2, file3, file4);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(5)
    public void testAdvancedAutoMultipleUploadSizeLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());
        String invalidSizeMsg = fileUpload.getWidgetConfiguration().getString("invalidSizeMessage");
        Assertions.assertNotNull(invalidSizeMsg);
        Assertions.assertFalse(invalidSizeMsg.isEmpty());

        // Act
        File file = locateClientSideFile("file3.csv");
        fileUpload.setValue(file);
        Assertions.assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());
        // upload button is not visible
        Assertions.assertTrue(fileUpload.getWidgetErrorMessages().contains(invalidSizeMsg), fileUpload.getWidgetErrorMessages().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(6)
    public void testAdvancedAutoMultipleUploadAllowTypes(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());
        String invalidTypeMsg = fileUpload.getWidgetConfiguration().getString("invalidFileMessage");
        Assertions.assertNotNull(invalidTypeMsg);
        Assertions.assertFalse(invalidTypeMsg.isEmpty());

        // Act
        File file = locateClientSideFile("file1.png");
        fileUpload.setValue(file);
        Assertions.assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());
        // upload button is not visible
        Assertions.assertTrue(fileUpload.getWidgetErrorMessages().contains(invalidTypeMsg), fileUpload.getWidgetErrorMessages().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    private void assertConfiguration(FileUpload fileUpload) {
        JSONObject cfg = fileUpload.getWidgetConfiguration();
        System.out.println("FileInput Config = " + cfg);
        Assertions.assertFalse(cfg.has("skinSimple"));
        Assertions.assertTrue(cfg.getBoolean("auto"));
        Assertions.assertFalse(cfg.getBoolean("dnd"));
        Assertions.assertEquals(4, cfg.getInt("fileLimit"));
        Assertions.assertEquals(100, cfg.getInt("maxFileSize"));
        Assertions.assertEquals("/(\\.|\\/)(csv)$/", cfg.getString("allowTypes"));
        Assertions.assertNotNull(fileUpload.getInput().getAttribute("multiple"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:fileupload")
        FileUpload fileupload;

        @FindBy(id = "form:uploadedfiles")
        DataTable uploadedFiles;

        @Override
        public String getLocation() {
            return "fileupload/fileUpload008.xhtml";
        }
    }
}
