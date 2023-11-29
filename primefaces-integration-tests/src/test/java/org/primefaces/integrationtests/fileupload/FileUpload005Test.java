/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
import org.primefaces.selenium.component.Messages;

/**
 * Tests advanced single file upload.
 * p:fileUpload mode=advanced auto=false multiple=false dragDropSupport=false
 */
@Tag("SafariExclude") // Selenium SafariDriver does not support file uploads
public class FileUpload005Test extends AbstractFileUploadTest {

    @Test
    @Order(1)
    public void testAdvancedSingleUpload(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.csv");
        fileUpload.setValue(file);
        Assertions.assertTrue(fileUpload.getFilename().startsWith(file.getName()), fileUpload.getFilename().toString());
        fileUpload.getAdvancedUploadButton().click();
        wait4File(page.uploadedFiles, 1, file.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(2)
    public void testAdvancedSingleUploadTwice(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file1);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        wait4File(page.uploadedFiles, 1, file1.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1);

        // Act
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file2);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        wait4File(page.uploadedFiles, 2, file2.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(3)
    public void testAdvancedSingleUploadCancel(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.csv");
        fileUpload.setValue(file);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedCancelButton().click();
        wait4EmptyMesssage(page.uploadedFiles);
        Assertions.assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(4)
    public void testAdvancedSingleUploadFileLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());
        String fileLimitMsg = fileUpload.getWidgetConfiguration().getString("fileLimitMessage");
        Assertions.assertNotNull(fileLimitMsg);
        Assertions.assertFalse(fileLimitMsg.isEmpty());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file1);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        wait4File(page.uploadedFiles, 1, file1.getName());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1);

        // Act
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file2);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);

        // Act
        File file3 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file3);
        Assertions.assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());
        // upload button is not visible
        Assertions.assertTrue(fileUpload.getWidgetErrorMessages().contains(fileLimitMsg), fileUpload.getWidgetErrorMessages().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(5)
    public void testAdvancedSingleUploadSizeLimit(Page page) {
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
    public void testAdvancedSingleUploadAllowTypes(Page page) {
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
        Assertions.assertFalse(cfg.has("auto"));
        Assertions.assertFalse(cfg.getBoolean("dnd"));
        Assertions.assertEquals(2, cfg.getInt("fileLimit"));
        Assertions.assertEquals(100, cfg.getInt("maxFileSize"));
        Assertions.assertEquals("/(\\.|\\/)(csv)$/", cfg.getString("allowTypes"));
        Assertions.assertNull(fileUpload.getInput().getAttribute("multiple"));
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:fileupload")
        FileUpload fileupload;

        @FindBy(id = "form:uploadedfiles")
        DataTable uploadedFiles;

        @Override
        public String getLocation() {
            return "fileupload/fileUpload005.xhtml";
        }
    }
}
