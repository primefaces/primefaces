package org.primefaces.model;

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
        Assert.assertEquals("Test; \\\"123.txt", output);
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

}
