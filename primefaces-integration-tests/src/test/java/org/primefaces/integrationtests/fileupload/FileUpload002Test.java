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
import org.primefaces.selenium.component.CommandButton;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.FileUpload;

/**
 * Tests basic multiple file upload.
 * p:fileUpload mode=simple auto=false multiple=true (skinSimple=true)
 */
public class FileUpload002Test extends AbstractFileUploadTest {

    @Test
    @Order(1)
    public void testBasicMultipleUploadSingleFile(Page page) {
        assumeMojarra();
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file = locateClientSideFile("file1.csv");
        fileUpload.setValue(file);
        Assertions.assertTrue(fileUpload.getWidgetValue().startsWith(file.getName()), fileUpload.getWidgetValue());
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file);
        assertConfiguration(fileUpload.getWidgetConfiguration());
        Assertions.assertNotNull(fileUpload.getInput().getAttribute("multiple"));
    }

    @Test
    @Order(2)
    public void testBasicMultipleUploadMultipleFiles(Page page) throws Exception {
        assumeMojarra();
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);

        // the widget shows only the first file name and the number of additional files
        Assertions.assertTrue(fileUpload.getWidgetValue().startsWith(file1.getName()), fileUpload.getWidgetValue());
        Assertions.assertTrue(fileUpload.getWidgetValue().matches(".*\\+\\s*1$"));
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload.getWidgetConfiguration());
        Assertions.assertNotNull(fileUpload.getInput().getAttribute("multiple"));
    }

    @Test
    @Order(2)
    public void testBasicMultipleUploadMultipleFilesTwice(Page page) throws Exception {
        assumeMojarra();
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file1 = locateClientSideFile("file1.csv");
        File file2 = locateClientSideFile("file2.csv");
        fileUpload.setValue(file1, file2);

        // the widget shows only the first file name and the number of additional files
        Assertions.assertTrue(fileUpload.getWidgetValue().startsWith(file1.getName()), fileUpload.getWidgetValue());
        Assertions.assertTrue(fileUpload.getWidgetValue().matches(".*\\+\\s*1$"));
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);

        // Act
        File file3 = locateClientSideFile("file2.csv");
        File file4 = locateClientSideFile("file1.csv");
        fileUpload.setValue(file3, file4);

        // the widget shows only the first file name and the number of additional files
        Assertions.assertTrue(fileUpload.getWidgetValue().startsWith(file3.getName()), fileUpload.getWidgetValue());
        Assertions.assertTrue(fileUpload.getWidgetValue().matches(".*\\+\\s*1$"));
        page.button.click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2, file3, file4);
        assertConfiguration(fileUpload.getWidgetConfiguration());
        Assertions.assertNotNull(fileUpload.getInput().getAttribute("multiple"));
    }

    private void assertConfiguration(JSONObject cfg) {
        assertNoJavascriptErrors();
        System.out.println("FileInput Config = " + cfg);
        Assertions.assertTrue(cfg.getBoolean("skinSimple"));
        Assertions.assertFalse(cfg.has("auto"));
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
            return "fileupload/fileUpload002.xhtml";
        }
    }
}
