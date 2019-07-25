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
package org.primefaces.model.file;

import javax.faces.FacesException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class NativeUploadedFileTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    NativeUploadedFile file = new NativeUploadedFile();

    @Test
    public void testValid1() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"hello.png\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("hello.png", output);
    }

    @Test
    public void testValid2() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"Test;123.txt\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("Test;123.txt", output);
    }

    @Test
    public void testValid3() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"Test; \\\"123.txt\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("Test; \"123.txt", output);
    }

    @Test
    public void testValid4() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"Test;123.txt\"; charset=\"UTF-8\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("Test;123.txt", output);
    }

    @Test
    public void testPercent() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"test%.jpg\"; charset=\"UTF-8\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("test%.jpg", output);
    }

    @Test
    public void testPlus() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"test+.jpg\"; charset=\"UTF-8\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("test+.jpg", output);
    }

    @Test
    public void testSlashes() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"hello\\\\.png\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("hello\\\\.png", output);
    }

    @Test
    public void testOnlySlashes() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"\\\\\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("\\\\", output);
    }

    @Test
    public void testSingleSlash() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"\\\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("\\", output);
    }

    @Test
    public void testSpaces() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"%22%22\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("\"\"", output);
    }

    @Test
    public void testEscapeCharacters() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=\"hello\t\b\t\n.png\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertEquals("hello\t\b\t\n.png", output);
    }

    @Test
    public void testNoFilename() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; file=\"hello.png\"";

        // Act
        final String output = file.getContentDispositionFileName(input);

        // Assert
        Assert.assertNull(output);
    }

    @Test
    public void testNoEquals() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename\"hello.png\"";

        thrown.expect(FacesException.class);
        thrown.expectMessage("Content-Disposition filename property did not have '='.");

        // Act
        file.getContentDispositionFileName(input);

        // Assert (expected exception)
    }

    @Test
    public void testNoStartQuote() {
        // Arrange
        final String input = "form-data; name=\"XXX:XXX\"; filename=hello.png\"";
        thrown.expect(FacesException.class);
        thrown.expectMessage("Content-Disposition filename property was not quoted.");

        // Act
        file.getContentDispositionFileName(input);

        // Assert (expected exception)
    }

}
