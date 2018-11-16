package org.primefaces.util;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.component.fileupload.FileUpload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
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
    
    @Test
    public void isValidTypeFilenameCheck() {
        when(fileUpload.getAllowTypes()).thenReturn(null);
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.png", inputStream));

        when(fileUpload.getAllowTypes()).thenReturn("/\\.(gif|png|jpe?g)$/i");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.PNG", inputStream));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.jpeg", inputStream));
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, "test.bmp", inputStream));
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
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.TIF", tif));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.mp4", mp4));
        
        when(fileUpload.isValidateContentType()).thenReturn(true);

        when(fileUpload.getAccept()).thenReturn("");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.png", png));

        when(fileUpload.getAccept()).thenReturn(".png,.bmp");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.png", png));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.bmp", bmp));
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, "test.tif", tif));

        when(fileUpload.getAccept()).thenReturn("video/*");
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, "test.png", png));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.mp4", mp4));

        when(fileUpload.getAccept()).thenReturn("image/png");
        //FIXME PNG not recognized by Apache Tika?
//        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.Png", png));
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, "test.tif", tif));

        //Tampered - Apache Tika must be in the classpath for this to work
        when(fileUpload.getAccept()).thenReturn("image/gif");
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, "test.gif", exe));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.png", gif));
    }
    
}