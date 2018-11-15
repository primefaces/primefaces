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
        when(fileUpload.isValidateContentType()).thenReturn(true);
        inputStream = mock(InputStream.class);
    }

    @After
    public void teardown() {
        fileUpload = null;
    }
    
    @Test
    public void isValidTypeFilenameCheck() {
        when(fileUpload.getAllowTypes()).thenReturn("/\\.(gif|png|jpe?g)$/i");
        Assert.assertTrue(FileUploadUtils.isValidType(fileUpload, "test.PNG", inputStream));
        Assert.assertFalse(FileUploadUtils.isValidType(fileUpload, "test.bmp", inputStream));
    }
    
    
}