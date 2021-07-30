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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Random;
import java.util.UUID;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.AbstractPrimePage;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.DataTable;
import org.primefaces.selenium.component.FileUpload;

/**
 * Tests advanced single file upload.
 * p:fileUpload mode=advanced auto=false multiple=false dragDropSupport=false maxChunkSize="1024k"
 */
@Tag("SafariExclude") // Selenium SafariDriver does not support file uploads
public class FileUpload011Test extends AbstractFileUploadTest {

    // must be equal to p:fileUpload components sizeLimit
    private static final int SIZE_LIMIT = 2097152;
    private static Path bigFilesFolder;
    // holds 3 files to upload (last one exeeds sizeLimit)
    private static final Path[] BIG_FILES = new Path[3];

    @BeforeAll
    public static void setupFiles() {
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
            } catch(IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        }
    }

    @AfterAll
    public static void teardownFiles() {
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
        } catch (IOException ignore) {
        }
    }

    private static File chooseBigFile(int i) {
        return BIG_FILES[i].toFile();
    }

    @Test
    @Order(1)
    public void testAdvancedSingleChunkedUpload(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file = chooseBigFile(0);
        fileUpload.setValue(file);
        Assertions.assertTrue(fileUpload.getWidgetValue().startsWith(file.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(2)
    public void testAdvancedSingleChunkedUploadTwice(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file1 = chooseBigFile(0);
        fileUpload.setValue(file1);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1);

        // Act
        File file2 = chooseBigFile(1);
        fileUpload.setValue(file2);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(3)
    public void testAdvancedSingleChunkedUploadCancel(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());

        // Act
        File file = chooseBigFile(0);
        fileUpload.setValue(file);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedCancelButton().click();
        Assertions.assertTrue(fileUpload.getWidgetValues().isEmpty(), fileUpload.getWidgetValues().toString());

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles);
        assertConfiguration(fileUpload);
    }

    @Test
    @Order(4)
    public void testAdvancedSingleChunkedUploadFileLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());
        String fileLimitMsg = fileUpload.getWidgetConfiguration().getString("fileLimitMessage");
        Assertions.assertNotNull(fileLimitMsg);
        Assertions.assertFalse(fileLimitMsg.isEmpty());

        // Act
        File file1 = chooseBigFile(0);
        fileUpload.setValue(file1);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file1.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1);

        // Act
        File file2 = chooseBigFile(1);
        fileUpload.setValue(file2);
        Assertions.assertTrue(fileUpload.getWidgetValues().contains(file2.getName()), fileUpload.getWidgetValues().toString());
        fileUpload.getAdvancedUploadButton().click();
        fileUpload.waitAdvancedUntilAllFilesAreUploaded(page.uploadedFiles);

        // Assert
        assertNoJavascriptErrors();
        assertUploadedFiles(page.uploadedFiles, file1, file2);

        // Act
        File file3 = chooseBigFile(2);
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
    public void testAdvancedSingleChunkedUploadSizeLimit(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Assertions.assertEquals("", fileUpload.getValue());
        String invalidSizeMsg = fileUpload.getWidgetConfiguration().getString("invalidSizeMessage");
        Assertions.assertNotNull(invalidSizeMsg);
        Assertions.assertFalse(invalidSizeMsg.isEmpty());

        // Act
        File file = chooseBigFile(2);
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
    public void testAdvancedSingleChunkedUploadAllowTypes(Page page) {
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
        Assertions.assertEquals(2097152, cfg.getInt("maxFileSize"));
        Assertions.assertEquals("/(\\.|\\/)(.ar)$/", cfg.getString("allowTypes"));
        Assertions.assertNull(fileUpload.getInput().getAttribute("multiple"));
        Assertions.assertEquals(65536, cfg.getInt("maxChunkSize"));
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
