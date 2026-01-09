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
import org.primefaces.selenium.component.FileUpload;
import org.primefaces.selenium.component.InputText;
import org.primefaces.selenium.component.Messages;

import java.io.File;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.FindBy;

import static org.junit.jupiter.api.Assertions.*;

// Selenium SafariDriver does not support file uploads
@Tag("SafariExclude")
class FileUpload013Test extends AbstractFileUploadTest {
    @Test
    void dynamicAllowTypes(Page page) {
        // Arrange
        FileUpload fileUpload = page.fileupload;
        Messages messages = page.messages;
        InputText allowTypes = page.allowTypes;
        assertEquals("", allowTypes.getValue());

        // Act
        File file = locateClientSideFile("file1.csv");
        fileUpload.setValue(file);
        fileUpload.getAdvancedUploadButton().click();

        // Assert
        assertTrue(messages.isEmpty());

        // Act
        allowTypes.setValue("/(\\.|\\/)(gif)$/");
        fileUpload.setValue(file);
        fileUpload.getAdvancedUploadButton().click();

        // Assert
        assertFalse(messages.isEmpty());
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:msgs")
        Messages messages;

        @FindBy(id = "form:allowTypes")
        InputText allowTypes;

        @FindBy(id = "form:fileupload")
        FileUpload fileupload;

        @Override
        public String getLocation() {
            return "fileupload/fileUpload013.xhtml";
        }
    }
}
