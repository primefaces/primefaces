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
package org.primefaces.application.resource;


import java.io.IOException;

import jakarta.faces.FacesException;
import jakarta.faces.context.ResponseWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MoveScriptsToBottomResponseWriterTest {

    private MoveScriptsToBottomResponseWriter writer;
    private MoveScriptsToBottomState state;
    private ResponseWriter wrappedWriter;

    @BeforeEach
    void setup() {
        wrappedWriter = mock(ResponseWriter.class);
        state = new MoveScriptsToBottomState();
        writer = new MoveScriptsToBottomResponseWriter(wrappedWriter, state);
        writer = Mockito.spy(writer);
        Mockito.doReturn(Boolean.FALSE).when(writer).isFirefox();
    }

    @Test
    void noScripts() throws IOException {
        assertTrue(state.getInlines().isEmpty());
        assertTrue(state.getIncludes().isEmpty());

        writer.startElement("style", null);
        verify(wrappedWriter).startElement("style", null);
        writer.writeAttribute("src", "about:blank", null);
        verify(wrappedWriter).writeAttribute("src", "about:blank", null);
        writer.writeText("css", null);
        verify(wrappedWriter).writeText("css", null);
        writer.endElement("style");
        verify(wrappedWriter).endElement("style");

        assertTrue(state.getInlines().isEmpty());
        assertTrue(state.getIncludes().isEmpty());
    }

    @Test
    void singleInlineScript() throws IOException {
        writer.startElement("HTML", null);
        verify(wrappedWriter).startElement("HTML", null);

        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("SCRIPT", null);
        verify(wrappedWriter, never()).startElement("SCRIPT", null);
        writer.writeText("inline", null);
        verify(wrappedWriter, never()).writeText(anyString(), anyString());
        writer.endElement("script");
        verify(wrappedWriter, never()).endElement("script");

        assertEquals(1, state.getInlines().get("text/javascript").size());
        assertTrue(state.getIncludes().isEmpty());
        assertEquals(0, state.getSavedInlineTags());

        writer.endElement("body");
        verify(wrappedWriter).endElement("body");

        verify(wrappedWriter).startElement("script", null);
        verify(wrappedWriter).write(ArgumentMatchers.contains("inline;document.getElementById('"));
        verify(wrappedWriter).endElement("script");

        writer.endElement("html");
        verify(wrappedWriter).endElement("html");
    }

    @Test
    void multipleInlineScripts() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeText("script1", null);
        writer.endElement("script");

        writer.startElement("script", null);
        writer.writeText("script2", null);
        writer.endElement("script");

        assertEquals(2, state.getInlines().get("text/javascript").size());
        assertTrue(state.getIncludes().isEmpty());
        assertEquals(1, state.getSavedInlineTags());

        writer.endElement("body");

        verify(wrappedWriter).startElement("script", null);
        verify(wrappedWriter).write(matches("(?s).*script1.*script2.*"));
        verify(wrappedWriter).endElement("body");
    }

    /**
     * https://github.com/primefaces/primefaces/issues/3854
     */
    @Test
    void multipleInlineScriptsDifferentTypes() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.writeText("javascript1", null);
        writer.endElement("script");

        writer.startElement("script", null);
        // default type is text/javascript
        writer.writeText("javascript2", null);
        writer.endElement("script");

        writer.startElement("script", null);
        writer.writeAttribute("type", "x-shader/x-vertex", null);
        writer.writeText("vertex", null);
        writer.endElement("script");

        writer.endElement("body");

        verify(wrappedWriter, times(2)).startElement("script", null);
        verify(wrappedWriter).write(matches("(?s).*javascript1.*javascript2(?!.*vertex.*).*"));
        verify(wrappedWriter).write(matches("(?s)(?!.*javascript.*).*vertex.*"));
        verify(wrappedWriter, times(2)).endElement("script");
    }

    /**
     * https://github.com/primefaces/primefaces/issues/10845
     */
    @Test
    void multipleInlineScriptsNonJavascript() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeAttribute("type", "application/ld+json", null);
        writer.writeText("JSONLinkingData1", null);
        writer.endElement("script");

        writer.startElement("script", null);
        // default type is text/javascript
        writer.writeText("javascript2", null);
        writer.endElement("script");

        writer.startElement("script", null);
        writer.writeAttribute("type", "application/ld+json", null);
        writer.writeText("JSONLinkingData2", null);
        writer.endElement("script");

        writer.endElement("body");

        // assert both LD files are still in their own inline script
        verify(wrappedWriter, times(3)).startElement("script", null);
        verify(wrappedWriter).write(matches("(?s).*JSONLinkingData1(?!.*javascript2.*).*"));
        verify(wrappedWriter).write(matches("(?s).*javascript2(?!.*JSONLinkingData1.*).*"));
        verify(wrappedWriter).write(matches("(?s).*JSONLinkingData2(?!.*javascript2.*).*"));
        verify(wrappedWriter, times(3)).endElement("script");
    }

    @Test
    void passthroughAttributes() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.writeAttribute("src", "include", null);
        writer.writeAttribute("async", "true", null);
        writer.writeAttribute("defer", "true", null);
        writer.endElement("script");

        writer.endElement("body");

        verify(wrappedWriter).startElement("script", null);
        verify(wrappedWriter).writeAttribute("type", "text/javascript", null);
        verify(wrappedWriter).writeAttribute("src", "include", null);
        verify(wrappedWriter).writeAttribute("async", "true", null);
        verify(wrappedWriter).writeAttribute("defer", "true", null);
        verify(wrappedWriter).endElement("script");
    }

    @Test
    void inlineScriptMinimization() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeText("PrimeFaces.settings PrimeFaces.cw PrimeFaces.ab window.PrimeFaces ;;", null);
        writer.endElement("script");

        writer.endElement("body");

        verify(wrappedWriter).write(contains("pf.settings pf.cw pf.ab pf ;"));
    }

    @Test
    void includeScripts() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeAttribute("src", "url1", null);
        writer.endElement("script");

        writer.startElement("script", null);
        writer.writeAttribute("src", "url2", null);
        writer.endElement("script");

        writer.startElement("script", null);
        writer.writeAttribute("src", "url2", null);
        writer.endElement("script");

        writer.startElement("div", null);
        writer.writeText("text", null);
        writer.endElement("div");

        writer.endElement("body");

        assertEquals(3, state.getIncludes().get("text/javascript").size());
        assertTrue(state.getInlines().isEmpty());

        InOrder inOrder = inOrder(wrappedWriter);
        inOrder.verify(wrappedWriter).startElement("div", null);
        inOrder.verify(wrappedWriter, times(3)).startElement("script", null);
    }

    @Test
    void inlineAndIncludeScripts() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeAttribute("src", "include", null);
        writer.writeAttribute("charset", "UTF-8", null);
        writer.endElement("script");

        writer.startElement("script", null);
        writer.writeText("inline", null);
        writer.endElement("script");

        writer.endElement("body");

        assertEquals(1, state.getIncludes().get("text/javascript").size());
        assertEquals(1, state.getInlines().get("text/javascript").size());
        verify(wrappedWriter, times(2)).startElement("script", null);
        verify(wrappedWriter).writeAttribute("src", "include", null);
        verify(wrappedWriter).writeAttribute("charset", "UTF-8", null);
        verify(wrappedWriter).write(contains("inline"));
    }

    @Test
    void duplicateHtmlElements() throws IOException {
        writer.startElement("HTML", null);
        verify(wrappedWriter).startElement("HTML", null);


        FacesException exception = assertThrows(FacesException.class, () -> {
            writer.startElement("HtmL", null);
        });
        assertEquals("Duplicate <html> elements were found in the response.", exception.getMessage());
    }

    @Test
    void duplicateBodyElements() throws IOException {
        writer.startElement("HTML", null);
        verify(wrappedWriter).startElement("HTML", null);
        writer.startElement("BODY", null);
        verify(wrappedWriter).startElement("BODY", null);


        FacesException exception = assertThrows(FacesException.class, () -> {
            writer.startElement("BoDy", null);
        });
        assertEquals("Duplicate <body> elements were found in the response.", exception.getMessage());
    }

}