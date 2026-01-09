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
package org.primefaces.integrationtests.dataexporter;

import org.primefaces.selenium.AbstractPrimePageTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import com.google.common.base.Objects;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractDataExporterTest extends AbstractPrimePageTest {

    protected String getActualFile(String fileName) {
        return StringUtils.appendIfMissing(System.getProperty("java.io.tmpdir"), File.separator) + fileName;
    }

    protected File assertDownloadFileNotExists(String downloadFileName) {
        File file = new File(downloadFileName);
        FileUtils.deleteQuietly(file);
        assertFalse(file.exists(), "Download file " + downloadFileName + " already exists!");
        return file;
    }

    protected File getExpectedFile(String fileName) {
        String folder = AbstractDataExporterTest.class.getPackage().getName().replace(".", "/");
        URL url = AbstractDataExporterTest.class.getResource("/" + folder + "/" + fileName);
        assertNotNull(url, "dataexporter test file " + fileName + " does not exist in /" + folder);
        return new File(url.getPath().replace("%20", " "));
    }

    protected void assertFileEquals(File expected, File actual, String message) throws IOException {
        assertNoJavascriptErrors();
        assertNotNull(expected);
        assertNotNull(actual);

        assertTrue(expected.exists(), "Expected file does not exist!");
        assertTrue(actual.exists(), "Actual file does not exist!");

        String extension = FilenameUtils.getExtension(expected.getName());
        switch (extension) {
            case "pdf":
                final PdfReader actualPdf = new PdfReader(actual.getAbsolutePath());
                final String actualPdfContent = new PdfTextExtractor(actualPdf).getTextFromPage(1);

                final PdfReader expectedPdf = new PdfReader(expected.getAbsolutePath());
                final String expectedPdfContent = new PdfTextExtractor(expectedPdf).getTextFromPage(1);
                assertTrue(Objects.equal(actualPdfContent, expectedPdfContent), message);
                break;
            case "xlsx":
                String workbookActual = new XSSFExcelExtractor(loadWorkbook(actual)).getText();
                String workbookExpected = new XSSFExcelExtractor(loadWorkbook(expected)).getText();
                assertTrue(Objects.equal(workbookActual, workbookExpected), message);
                break;

            default:
                Reader reader1 = new BufferedReader(new FileReader(expected));
                Reader reader2 = new BufferedReader(new FileReader(actual));
                assertTrue(IOUtils.contentEqualsIgnoreEOL(reader1, reader2), message);
                break;
        }

        actual.deleteOnExit();
    }

    /**
     * Tries to open a {@link File} as XLSX and XLS.
     *
     * @param file The {@link File} to open.
     * @return The opened workbook.
     */
    public static XSSFWorkbook loadWorkbook(final File file) {
        final String name = file.getName();

        if (name.endsWith(".xlsx")) {
            try {
                return new XSSFWorkbook(file);
            }
            catch (final Exception e) {
                throw new IllegalArgumentException("Could not open file " + file, e);
            }
        }

        throw new IllegalArgumentException("Unknown file type: " + file);
    }

}
