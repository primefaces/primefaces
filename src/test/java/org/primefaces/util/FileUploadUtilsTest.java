/*
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.model.file.UploadedFile;

public class FileUploadUtilsTest {

    private FileUpload fileUpload;
    private InputStream inputStream;
    private PrimeApplicationContext appContext;

    @BeforeEach
    public void setup() {
        fileUpload = mock(FileUpload.class);
        inputStream = mock(InputStream.class);
        FacesContext context = mock(FacesContext.class);
        Application app = mock(Application.class);
        when(context.getApplication()).thenReturn(app);
        ExternalContext externalContext = mock(ExternalContext.class);
        when(context.getExternalContext()).thenReturn(externalContext);
        appContext = new PrimeApplicationContext(context);
    }

    @AfterEach
    public void teardown() {
        inputStream = null;
        fileUpload = null;
    }

    private UploadedFile createFile(String filename, String contentType, InputStream stream) {
        UploadedFile file = Mockito.mock(UploadedFile.class);
        when(file.getFileName()).thenReturn(filename);
        when(file.getContentType()).thenReturn(contentType);
        try {
            when(file.getInputStream()).thenReturn(stream);
        }
        catch (IOException e) {
        }
        return file;
    }

    @Test
    public void isValidType_NameCheck() {
        when(fileUpload.getAllowTypes()).thenReturn(null);
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.png", "image/png", inputStream)));

        when(fileUpload.getAllowTypes()).thenReturn("/\\.(gif|png|jpe?g)$/i");
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.PNG", "image/png", inputStream)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.jpeg", "image/jpeg", inputStream)));
        Assertions.assertFalse(FileUploadUtils.isValidType(appContext, fileUpload,createFile("test.bmp", "image/bitmap", inputStream)));
    }

    @Test
    public void isValidType_MimeTypeCheck() {
        when(fileUpload.getAllowTypes()).thenReturn("/image/g");
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.PNG", "image/png", inputStream)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.jpeg", "image/png", inputStream)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload,createFile("test.bmp", "image/bitmap", inputStream)));
        Assertions.assertFalse(FileUploadUtils.isValidType(appContext, fileUpload,createFile("adobe.pdf", "application/pdf", inputStream)));
    }

    @Test
    public void isValidType_InvalidRegex() {
        when(fileUpload.getAllowTypes()).thenReturn("x");
        // all of these should be true when the regex is not valid
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.PNG", "image/png", inputStream)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.jpeg", "image/png", inputStream)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload,createFile("test.bmp", "image/bitmap", inputStream)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload,createFile("adobe.pdf", "application/pdf", inputStream)));
    }

    @Test
    public void isValidTypeContentTypeCheck() throws IOException {
        InputStream tif = new ByteArrayInputStream(new byte[] { 0x49, 0x49, 0x2A, 0x00 });
        InputStream mp4 = new ByteArrayInputStream(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x66, 0x74, 0x79, 0x70, 0x69, 0x73, 0x6F, 0x6D });
        InputStream png = new ByteArrayInputStream(IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("test.png")));
        InputStream bmp = new ByteArrayInputStream(new byte[] { 0x42, 0x4D });
        InputStream gif = new ByteArrayInputStream(new byte[] { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61 });
        InputStream exe = new ByteArrayInputStream(new byte[] { 0x4D, 0x5A });

        when(fileUpload.isValidateContentType()).thenReturn(false);

        when(fileUpload.getAccept()).thenReturn("image/png");
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.TIF", "image/tif", tif)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.mp4", "application/music", mp4)));

        when(fileUpload.isValidateContentType()).thenReturn(true);

        when(fileUpload.getAccept()).thenReturn("");
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.png", "image/png", png)));

        when(fileUpload.getAccept()).thenReturn(".png,.bmp");
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.png", "image/png", png)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.bmp", "image/bmp", bmp)));
        Assertions.assertFalse(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.tif", "image/tif", tif)));

        when(fileUpload.getAccept()).thenReturn("video/*");
        Assertions.assertFalse(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.png", "image/png", png)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.mp4", "application/music", mp4)));

        when(fileUpload.getAccept()).thenReturn("image/tiff");
        Assertions.assertFalse(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.png", "image/png", png)));

        //Tampered - Apache Tika or Mime Type must be in the classpath for this to work
        when(fileUpload.getAccept()).thenReturn("image/gif");
        Assertions.assertFalse(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.gif", "image/gif", exe)));
        Assertions.assertTrue(FileUploadUtils.isValidType(appContext, fileUpload, createFile("test.png", "image/png", gif)));
    }

    @Test
    public void checkPathTraversal_AbsoluteFile() {
        // Unix systems can start with / but Windows cannot
        String os = System.getProperty("os.name").toLowerCase();

        // Arrange
        String relativePath = FileUploadUtilsTest.class.getResource("/test.png").getFile();

        // Act
        if (os.contains("win")) {
            try {
                FileUploadUtils.checkPathTraversal(relativePath);
                fail("File was absolute path and should have failed");
            }
            catch (Exception e) {
                // Assert
                assertEquals("Path traversal attempt - absolute path not allowed.", e.getMessage());
            }
        } else {
            String result = FileUploadUtils.checkPathTraversal(relativePath);
            assertTrue(result.endsWith("test.png"));
        }
    }

    @Test
    public void checkPathTraversal_PathTraversal() {
        // Arrange
        String relativePath = "../../test.png";

        // Act
        try {
            FileUploadUtils.checkPathTraversal(relativePath);
            fail("File was absolute path and should have failed");
        }
        catch (Exception e) {
            // Assert
            assertEquals("Path traversal attempt for path ../../test.png", e.getMessage());
        }
    }

    @Test
    public void checkPathTraversal_Valid() {
        // Arrange
        String relativePath = "test.png";

        // Act
        String result = FileUploadUtils.checkPathTraversal(relativePath);

        // Assert
        assertEquals(relativePath, result);
    }

    @Test
    public void convertJavaScriptRegex_Normal() {
        // Arrange
        String jsRegex = "/(\\.|\\/)(gif|jpe?g|png)$/";

        // Act
        String result = FileUploadUtils.convertJavaScriptRegex(jsRegex);

        // Assert
        assertEquals("(\\.|\\/)(gif|jpe?g|png)$", result);
    }

    @Test
    public void convertJavaScriptRegex_CaseInsensitive() {
        // Arrange
        String jsRegex = "/\\.(gif|png|jpe?g)$/i";

        // Act
        String result = FileUploadUtils.convertJavaScriptRegex(jsRegex);

        // Assert
        assertEquals("\\.(gif|png|jpe?g)$", result);
    }

    @Test
    public void convertJavaScriptRegex_Short() {
        // Arrange
        String jsRegex = "/x/";

        // Act
        String result = FileUploadUtils.convertJavaScriptRegex(jsRegex);

        // Assert
        assertEquals("x", result);
    }

    @Test
    public void convertJavaScriptRegex_NotRegex() {
        // Arrange
        String jsRegex = "x";

        // Act
        String result = FileUploadUtils.convertJavaScriptRegex(jsRegex);

        // Assert
        assertEquals("", result);
    }


}