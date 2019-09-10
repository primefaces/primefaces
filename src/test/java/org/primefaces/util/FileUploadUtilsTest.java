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

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.model.file.SingleUploadedFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileUploadUtilsTest {

    private FileUpload fileUpload;
    private InputStream inputStream;

    @Before
    public void setup() {
        fileUpload = mock(FileUpload.class);
        inputStream = mock(InputStream.class);
    }

    @After
    public void teardown() {
        inputStream = null;
        fileUpload = null;
    }
    
    private SingleUploadedFile createFile(String filename, String contentType, InputStream stream) {
        SingleUploadedFile file = Mockito.mock(SingleUploadedFile.class);
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
    public void isValidTypeFilenameCheck() {
        when(fileUpload.getAllowTypes()).thenReturn(null);
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.png", "image/png", inputStream)));

        when(fileUpload.getAllowTypes()).thenReturn("/\\.(gif|png|jpe?g)$/i");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.PNG", "image/png", inputStream)));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.jpeg", "image/png", inputStream)));
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload,createFile( "test.bmp", "text/plain", inputStream)));
    }

    //TODO Once including Apache Tika as test scope dependency, we never check the default implementation which should work well also for the non-tampered cases
    //TODO Can we somehow run specific tests with AND without Apache Tika in place, e.g. by differently configured executions of maven-surefire-plugin?
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
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.TIF", "image/tif", tif)));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.mp4", "application/music", mp4)));
        
        when(fileUpload.isValidateContentType()).thenReturn(true);

        when(fileUpload.getAccept()).thenReturn("");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.png", "image/png", png)));

        when(fileUpload.getAccept()).thenReturn(".png,.bmp");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.png", "image/png", png)));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.bmp", "image/bmp", bmp)));
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, createFile("test.tif", "image/tif", tif)));

        when(fileUpload.getAccept()).thenReturn("video/*");
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, createFile("test.png", "image/png", png)));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.mp4", "application/music", mp4)));

        when(fileUpload.getAccept()).thenReturn("image/png");
        //FIXME PNG not recognized by Apache Tika?
//        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.Png", png));
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, createFile("test.tif", "image/tiff", tif)));

        //Tampered - Apache Tika must be in the classpath for this to work
        when(fileUpload.getAccept()).thenReturn("image/gif");
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, createFile("test.gif", "image/gif", exe)));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, createFile("test.png", "image/png", gif)));
    }
    
}