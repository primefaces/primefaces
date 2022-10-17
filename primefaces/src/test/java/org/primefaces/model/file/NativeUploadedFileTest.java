/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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
package org.primefaces.model.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.faces.FacesException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NativeUploadedFileTest {

    private NativeUploadedFile file = new NativeUploadedFile();

    @Test
    public void testValid1() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"hello.png\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("hello.png", output);
    }

    @Test
    public void testValid2() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"Test;123.txt\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("Test;123.txt", output);
    }

    @Test
    public void testValid3() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"Test; \\\"123.txt\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("Test; \"123.txt", output);
    }

    @Test
    public void testValid4() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"Test;123.txt\"; charset=\"UTF-8\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("Test;123.txt", output);
    }

    @Test
    public void testPercent() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"test%.jpg\"; charset=\"UTF-8\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("test%.jpg", output);
    }

    @Test
    public void testPlus() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"test+.jpg\"; charset=\"UTF-8\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("test+.jpg", output);
    }

    @Test
    public void testSlashes() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"hello\\\\.png\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("hello\\\\.png", output);
    }

    @Test
    public void testOnlySlashes() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"\\\\\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("\\\\", output);
    }

    @Test
    public void testSingleSlash() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"\\\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("\\", output);
    }

    @Test
    public void testSpaces() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"%22%22\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("\"\"", output);
    }

    @Test
    public void testEscapeCharacters() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=\"hello\t\b\t\n.png\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertEquals("hello\t\b\t\n.png", output);
    }

    @Test
    public void testNoFilename() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; file=\"hello.png\"";

        // Act
        String output = file.getContentDispositionFileName(input);

        // Assert
        Assertions.assertNull(output);
    }

    @Test
    public void testNoEquals() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename\"hello.png\"";

        // Act
        FacesException thrown = Assertions.assertThrows(FacesException.class, () -> {
            file.getContentDispositionFileName(input);
        });

        // Assert (expected exception)
        assertEquals("Content-Disposition filename property did not have '='.", thrown.getMessage());
    }

    @Test
    public void testNoStartQuote() {
        // Arrange
        String input = "form-data; name=\"XXX:XXX\"; filename=hello.png\"";

        // Act
        FacesException thrown = Assertions.assertThrows(FacesException.class, () -> {
            file.getContentDispositionFileName(input);
        });

        // Assert (expected exception)
        assertEquals("Content-Disposition filename property was not quoted.", thrown.getMessage());
    }

}
