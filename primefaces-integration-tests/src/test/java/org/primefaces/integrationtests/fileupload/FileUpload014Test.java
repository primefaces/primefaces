/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.FileUpload;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests that scripts added via PrimeFaces.current().executeScript() in the upload listener are executed.
 * p:fileUpload mode=simple auto=true (skinSimple=true) and mode=advanced auto=true
 */
// Selenium SafariDriver does not support file uploads
@Tag("SafariExclude")
class FileUpload014Test extends AbstractFileUploadTest {

    @Test
    @Order(1)
    @DisplayName("FileUpload: GitHub #15364 executeScript() in listener of simple auto upload")
    void simpleAutoExecuteScript(Page page) {
        // Arrange
        assertEquals("", page.scriptResult.getText());

        // Act
        page.simple.setValue(locateClientSideFile("file1.csv"));

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.scriptResult, "simple"));
        assertEquals("simple", page.scriptResult.getText());
        assertNoJavascriptErrors();
    }

    @Test
    @Order(2)
    @DisplayName("FileUpload: GitHub #15364 executeScript() in listener of advanced auto upload")
    void advancedAutoExecuteScript(Page page) {
        // Arrange
        assertEquals("", page.scriptResult.getText());

        // Act
        page.advanced.setValue(locateClientSideFile("file1.csv"));

        // Assert
        PrimeSelenium.waitGui().until(ExpectedConditions.textToBePresentInElement(page.scriptResult, "advanced"));
        assertEquals("advanced", page.scriptResult.getText());
        assertNoJavascriptErrors();
    }

    public static class Page extends AbstractPrimePage {
        @FindBy(id = "form:scriptResult")
        WebElement scriptResult;

        @FindBy(id = "form:simple")
        FileUpload simple;

        @FindBy(id = "form:advanced")
        FileUpload advanced;

        @Override
        public String getLocation() {
            return "fileupload/fileUpload014.xhtml";
        }
    }
}
