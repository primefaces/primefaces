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
package org.primefaces.component.tag;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class TagRendererTest {

    private TagRenderer renderer;
    private FacesContext context;
    private ResponseWriter writer;

    @BeforeEach
    void setup() {
        renderer = new TagRenderer();
        context = mock(FacesContext.class);
        writer = mock(ResponseWriter.class);
        when(context.getResponseWriter()).thenReturn(writer);
    }

    @Test
    void encodeEndDefault() throws IOException {
        Tag tag = mock(Tag.class);
        when(tag.getClientId(context)).thenReturn("tag1");

        renderer.encodeEnd(context, tag);

        verify(writer).writeAttribute("class", "ui-tag ui-widget", "styleClass");
    }

    @Test
    void encodeEndOutlined() throws IOException {
        Tag tag = mock(Tag.class);
        when(tag.getClientId(context)).thenReturn("tag1");
        when(tag.isOutlined()).thenReturn(true);
        when(tag.getSeverity()).thenReturn("success");

        renderer.encodeEnd(context, tag);

        verify(writer).writeAttribute("class", "ui-tag ui-widget ui-tag-success ui-tag-outlined", "styleClass");
    }

    @Test
    void encodeEndRoundedAndOutlined() throws IOException {
        Tag tag = mock(Tag.class);
        when(tag.getClientId(context)).thenReturn("tag1");
        when(tag.isRounded()).thenReturn(true);
        when(tag.isOutlined()).thenReturn(true);
        when(tag.getSeverity()).thenReturn("warning");

        renderer.encodeEnd(context, tag);

        verify(writer).writeAttribute("class", "ui-tag ui-widget ui-tag-warning ui-tag-rounded ui-tag-outlined", "styleClass");
    }
}
