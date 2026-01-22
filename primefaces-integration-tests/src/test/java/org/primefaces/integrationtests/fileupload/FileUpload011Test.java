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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests advanced single file upload. p:fileUpload mode=advanced auto=false
 * multiple=false dragDrop=false maxChunkSize="1024k"
 */
// Selenium SafariDriver does not support file uploads
@Tag("SafariExclude")
class FileUpload011Test extends AbstractFileUploadTest {

    // must be equal to p:fileUpload components sizeLimit
    private static final int SIZE_LIMIT = 2097152;
    private static Path bigFilesFolder;
    // holds 3 files to upload (last one exeeds sizeLimit)
    private static final Path[] BIG_FILES = new Path[3];

    @BeforeAll
    static void setupFiles() {
        // resolves to the projects `target´ folder
        Path dir = new File(Thread.currentThread().getContextClassLoader().getResource(".")
                .getPath()).getParentFile().toPath();
        // setup a fresh folder with fresh random files
        bigFilesFolder = dir.resolve("test-fileupload");
        if (bigFilesFolder.toFile().exists()) {
            deleteFolder(bigFilesFolder);
        }
        bigFilesFolder.toFile().mkdirs();

        Random rnd = new Random();
        byte[] buf = new byte[64 * 1024];
        for (int i = 0; i < BIG_FILES.length; ++i) {
            BIG_FILES[i] = bigFilesFolder.resolve(UUID.randomUUID().toString() + ".xar");
            int size = i == BIG_FILES.length - 1 ? SIZE_LIMIT + 1024 : SIZE_LIMIT / 2 + rnd.nextInt(SIZE_LIMIT / 2);
            try {
                for (int l = 0; l < size; l += buf.length) {
                    rnd.nextBytes(buf);
                    Files.write(BIG_FILES[i], buf, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            }
            catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }
    }

    @AfterAll
    static void teardownFiles() {
        if (bigFilesFolder != null) {
            deleteFolder(bigFilesFolder);
        }
    }

    private static void deleteFolder(Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        catch (IOException ignore) {
        }
    }

    private static File chooseBigFile(int i) {
        return BIG_FILES[i].toFile();
    }

    @Test
    @Order(1)
    void advancedSingleChunkedUpload(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = chooseBigFile(0);
        fileUpload.setValue(file);
        assertTrue(fileUpload.getFilename().startsWith(file.getName()), fileUpload.getFilename().toString());
        fileUpload.getAdvancedUploadButton().click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(2)
    void advancedSingleChunkedUploadTwice(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = chooseBigFile(0);
        fileUpload.setValue(file1);
        assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1);

        // Act
        File file2 = chooseBigFile(1);
        fileUpload.setValue(file2);
        assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(3)
    void advancedSingleChunkedUploadCancel(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = chooseBigFile(0);
        fileUpload.setValue(file);
        assertTrue(fileUpload.getWidgetValues().contains(file.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedCancelButton().click();
        assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(4)
    void advancedSingleChunkedUploadFileLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file1 = chooseBigFile(0);
        fileUpload.setValue(file1);
        assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1);

        // Act
        File file2 = chooseBigFile(1);
        fileUpload.setValue(file2);
        assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);

        // Act
        File file3 = chooseBigFile(2);
        fileUpload.setValue(file3);
        assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());
        // upload button is not visible
        assertTrue(fileUpload.getWidgetErrorMessages().toString().contains("Maximum number of files exceeded"), fileUpload.getWidgetErrorMessages().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(5)
    void advancedSingleChunkedUploadSizeLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        assertEquals("", fileUpload.getValue());

        // Act
        File file = chooseBigFile(2);
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
    @Order(6)
    void advancedSingleChunkedUploadAllowTypes(Page page) {
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
        assertEquals(2, Integer.parseInt(fileUpload.getInput().getAttribute("data-p-filelimit")));
        assertEquals(2097152, Long.parseLong(fileUpload.getInput().getAttribute("data-p-sizelimit")));
        assertEquals("/(\\.|\\/)(.ar)$/", fileUpload.getInput().getAttribute("data-p-allowtypes"));
        assertNull(fileUpload.getInput().getAttribute("multiple"));
        assertEquals(65536, cfg.getInt("maxChunkSize"));
    }

    public static class Page extends AbstractPrimePage {

        @FindBy(id = "form:fileupload")
        FileUpload fileupload;

        @FindBy(id = "form:uploadedfiles")
        DataTable uploadedFiles;

        @Override
        public String getLocation() {
            return "fileupload/fileUpload011.xhtml";
        }
    }
}
