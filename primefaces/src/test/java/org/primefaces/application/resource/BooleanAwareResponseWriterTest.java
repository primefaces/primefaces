/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.application.resource;

import java.io.IOException;

import jakarta.faces.context.ResponseWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BooleanAwareResponseWriterTest {

    private BooleanAwareResponseWriter writer;
    private ResponseWriter wrappedWriter;

    @BeforeEach
    void setup() {
        wrappedWriter = mock(ResponseWriter.class);
        writer = new BooleanAwareResponseWriter(wrappedWriter);
    }

    @Test
    void booleanAttributeWithBooleanTrue() throws IOException {
        writer.writeAttribute("disabled", Boolean.TRUE, null);

        verify(wrappedWriter).writeAttribute("disabled", "", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeWithBooleanFalse() throws IOException {
        writer.writeAttribute("disabled", Boolean.FALSE, null);

        verify(wrappedWriter, never()).writeAttribute(anyString(), any(), anyString());
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeWithStringTrue() throws IOException {
        writer.writeAttribute("checked", "true", null);

        verify(wrappedWriter).writeAttribute("checked", "", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeWithStringFalse() throws IOException {
        writer.writeAttribute("checked", "false", null);

        verify(wrappedWriter, never()).writeAttribute(anyString(), any(), anyString());
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeWithNull() throws IOException {
        writer.writeAttribute("required", null, null);

        // null is treated as false, so attribute should be omitted
        verify(wrappedWriter, never()).writeAttribute(anyString(), any(), anyString());
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeWithEmptyString() throws IOException {
        writer.writeAttribute("disabled", "", null);

        // empty string is treated as true (not "false"), so attribute should be written
        verify(wrappedWriter).writeAttribute("disabled", "", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeWithWhitespaceString() throws IOException {
        writer.writeAttribute("disabled", "   ", null);

        // whitespace string is treated as true (not "false"), so attribute should be written
        verify(wrappedWriter).writeAttribute("disabled", "", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeCaseInsensitive() throws IOException {
        writer.writeAttribute("DISABLED", Boolean.TRUE, null);
        writer.writeAttribute("Disabled", Boolean.TRUE, null);
        writer.writeAttribute("DiSaBlEd", Boolean.TRUE, null);
        writer.writeAttribute("disabled", "disabled", null);

        verify(wrappedWriter).writeAttribute("DISABLED", "", null);
        verify(wrappedWriter).writeAttribute("Disabled", "", null);
        verify(wrappedWriter).writeAttribute("DiSaBlEd", "", null);
        verify(wrappedWriter).writeAttribute("disabled", "", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void nonBooleanAttributeDelegates() throws IOException {
        writer.writeAttribute("id", "myId", "id");
        writer.writeAttribute("class", "myClass", null);
        writer.writeAttribute("href", "http://example.com", null);

        verify(wrappedWriter).writeAttribute("id", "myId", "id");
        verify(wrappedWriter).writeAttribute("class", "myClass", null);
        verify(wrappedWriter).writeAttribute("href", "http://example.com", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void nullAttributeNameDelegates() throws IOException {
        writer.writeAttribute(null, "value", null);

        verify(wrappedWriter).writeAttribute(null, "value", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void allBooleanAttributes() throws IOException {
        // Test a sample of boolean attributes
        String[] booleanAttrs = {
            "allowfullscreen", "async", "autofocus", "autoplay",
            "checked", "controls", "default", "defer",
            "disabled", "formnovalidate", "hidden", "ismap",
            "itemscope", "loop", "multiple", "muted",
            "nomodule", "novalidate", "open", "playsinline",
            "readonly", "required", "reversed", "selected"
        };

        for (String attr : booleanAttrs) {
            reset(wrappedWriter);
            writer.writeAttribute(attr, Boolean.TRUE, null);
            verify(wrappedWriter).writeAttribute(attr, "", null);
            verify(wrappedWriter, never()).write(anyString());
        }
    }

    @Test
    void booleanAttributeWithStringTrueCaseInsensitive() throws IOException {
        writer.writeAttribute("disabled", "True", null);
        writer.writeAttribute("disabled", "TRUE", null);
        writer.writeAttribute("disabled", "TrUe", null);

        verify(wrappedWriter, times(3)).writeAttribute("disabled", "", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeWithStringFalseCaseInsensitive() throws IOException {
        writer.writeAttribute("disabled", "False", null);
        writer.writeAttribute("disabled", "FALSE", null);
        writer.writeAttribute("disabled", "FaLsE", null);

        // string "false" (case-insensitive) should omit the attribute
        verify(wrappedWriter, never()).writeAttribute(anyString(), any(), anyString());
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeWithNumericZero() throws IOException {
        writer.writeAttribute("disabled", 0, null);

        // numeric 0 is treated as true (not "false"), so attribute should be written
        verify(wrappedWriter).writeAttribute("disabled", "", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void booleanAttributeWithNumericOne() throws IOException {
        writer.writeAttribute("disabled", 1, null);

        // numeric 1 is treated as true (not "false"), so attribute should be written
        verify(wrappedWriter).writeAttribute("disabled", "", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void mixedBooleanAndNonBooleanAttributes() throws IOException {
        writer.writeAttribute("id", "myId", "id");
        writer.writeAttribute("disabled", Boolean.TRUE, null);
        writer.writeAttribute("class", "myClass", null);
        writer.writeAttribute("checked", Boolean.FALSE, null);
        writer.writeAttribute("required", Boolean.TRUE, null);

        verify(wrappedWriter).writeAttribute("id", "myId", "id");
        verify(wrappedWriter).writeAttribute("disabled", "", null);
        verify(wrappedWriter).writeAttribute("class", "myClass", null);
        verify(wrappedWriter).writeAttribute("required", "", null);
        verify(wrappedWriter, never()).writeAttribute(eq("checked"), any(), any());
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void propertyParameterPreservedForNonBoolean() throws IOException {
        writer.writeAttribute("id", "myId", "id");
        writer.writeAttribute("class", "myClass", "styleClass");

        verify(wrappedWriter).writeAttribute("id", "myId", "id");
        verify(wrappedWriter).writeAttribute("class", "myClass", "styleClass");
    }

    @Test
    void videoControlsAttribute() throws IOException {
        // Test the video controls example - controls should be written as controls=""
        writer.writeAttribute("controls", Boolean.TRUE, null);

        verify(wrappedWriter).writeAttribute("controls", "", null);
        verify(wrappedWriter, never()).write(anyString());
    }

    @Test
    void videoElementWithMultipleAttributes() throws IOException {
        // Simulate a video element with id, controls, and other attributes
        writer.writeAttribute("id", "j_idt149_video", "id");
        writer.writeAttribute("controls", Boolean.TRUE, null);
        writer.writeAttribute("autoplay", Boolean.FALSE, null);
        writer.writeAttribute("muted", Boolean.TRUE, null);

        verify(wrappedWriter).writeAttribute("id", "j_idt149_video", "id");
        verify(wrappedWriter).writeAttribute("controls", "", null);
        verify(wrappedWriter).writeAttribute("muted", "", null);
        verify(wrappedWriter, never()).writeAttribute(eq("autoplay"), any(), any());
        verify(wrappedWriter, never()).write(anyString());
    }
}

