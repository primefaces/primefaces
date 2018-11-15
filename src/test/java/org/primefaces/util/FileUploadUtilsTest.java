package org.primefaces.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.component.fileupload.FileUpload;

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

    @Test
    public void isValidTypeContentTypeCheck() {
        when(fileUpload.isValidateContentType()).thenReturn(false);

        when(fileUpload.getAccept()).thenReturn("image/png");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.JPEG", inputStream));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.mp4", inputStream));
        
        when(fileUpload.isValidateContentType()).thenReturn(true);

        when(fileUpload.getAccept()).thenReturn("");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.png", inputStream));

        when(fileUpload.getAccept()).thenReturn(".png,.bmp");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.png", inputStream));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.bmp", inputStream));
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, "test.jpg", inputStream));

        when(fileUpload.getAccept()).thenReturn("video/*");
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, "test.png", inputStream));
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.mp4", inputStream));

        when(fileUpload.getAccept()).thenReturn("image/png");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.Png", inputStream));
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, "test.jpg", inputStream));

        //TODO provide some tests making use of apache tika
    }
    
}