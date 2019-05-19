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
package org.primefaces.application.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import javax.faces.context.ResponseWriter;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class MoveScriptsToBottomResponseWriterTest {

    private MoveScriptsToBottomResponseWriter writer;
    private MoveScriptsToBottomState state;
    private ResponseWriter wrappedWriter;

    @Before
    public void setup() {
        wrappedWriter = mock(ResponseWriter.class);
        state = new MoveScriptsToBottomState();
        writer = new MoveScriptsToBottomResponseWriter(wrappedWriter, state);
    }

    @Test
    public void testNoScripts() throws IOException {
        Assert.assertTrue(state.getInlines().isEmpty());
        Assert.assertTrue(state.getIncludes().isEmpty());

        writer.startElement("style", null);
        verify(wrappedWriter).startElement("style", null);
        writer.writeAttribute("src", "about:blank", null);
        verify(wrappedWriter).writeAttribute("src", "about:blank", null);
        writer.writeText("css", null);
        verify(wrappedWriter).writeText("css", null);
        writer.endElement("style");
        verify(wrappedWriter).endElement("style");

        Assert.assertTrue(state.getInlines().isEmpty());
        Assert.assertTrue(state.getIncludes().isEmpty());
    }

    @Test
    public void testSingleInlineScript() throws IOException {
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

        Assert.assertEquals(1, state.getInlines().get("text/javascript").size());
        Assert.assertTrue(state.getIncludes().isEmpty());
        Assert.assertEquals(0, state.getSavedInlineTags());

        writer.endElement("body");
        verify(wrappedWriter).endElement("body");

        verify(wrappedWriter).startElement("script", null);
        verify(wrappedWriter).write("inline;");
        verify(wrappedWriter).endElement("script");

        writer.endElement("html");
        verify(wrappedWriter).endElement("html");
    }

    @Test
    public void testMultipleInlineScripts() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeText("script1", null);
        writer.endElement("script");

        writer.startElement("script", null);
        writer.writeText("script2", null);
        writer.endElement("script");

        Assert.assertEquals(2, state.getInlines().get("text/javascript").size());
        Assert.assertTrue(state.getIncludes().isEmpty());
        Assert.assertEquals(1, state.getSavedInlineTags());

        writer.endElement("body");

        verify(wrappedWriter).startElement("script", null);
        verify(wrappedWriter).write(matches("(?s).*script1.*script2.*"));
        verify(wrappedWriter).endElement("body");
    }

    /**
     * https://github.com/primefaces/primefaces/issues/3854
     */
    @Test
    public void testMultipleInlineScriptsDifferentTypes() throws IOException {
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

    @Test
    public void testPassthroughAttributes() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.writeAttribute("async", "true", null);
        writer.writeAttribute("defer", "true", null);
        writer.writeText("someJS", null);
        writer.endElement("script");

        writer.endElement("body");

        verify(wrappedWriter).startElement("script", null);
        verify(wrappedWriter).writeAttribute("type", "text/javascript", null);
        // FIXME we would expect the attributes to be passed through
        // verify(wrappedWriter).writeAttribute("async", "true", null);
        // verify(wrappedWriter).writeAttribute("defer", "true", null);
        verify(wrappedWriter).endElement("script");
    }

    @Test
    public void testInlineScriptMinimization() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeText("PrimeFaces.settings PrimeFaces.cw PrimeFaces.ab window.PrimeFaces ;;", null);
        writer.endElement("script");

        writer.endElement("body");

        verify(wrappedWriter).write(contains("pf.settings pf.cw pf.ab pf ;"));
    }

    @Test
    public void testIncludeScripts() throws IOException {
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
        writer.writeText("text",null);
        writer.endElement("div");

        writer.endElement("body");

        Assert.assertEquals(3, state.getIncludes().get("text/javascript").size());
        Assert.assertTrue(state.getInlines().isEmpty());

        InOrder inOrder = inOrder(wrappedWriter);
        inOrder.verify(wrappedWriter).startElement("div", null);
        inOrder.verify(wrappedWriter, times(3)).startElement("script", null);
    }

    @Test
    public void testInlineAndIncludeScripts() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);

        writer.startElement("script", null);
        writer.writeAttribute("src", "include", null);
        writer.endElement("script");

        writer.startElement("script", null);
        writer.writeText("inline", null);
        writer.endElement("script");

        writer.endElement("body");

        Assert.assertEquals(1, state.getIncludes().get("text/javascript").size());
        Assert.assertEquals(1, state.getInlines().get("text/javascript").size());
        verify(wrappedWriter, times(2)).startElement("script", null);
        verify(wrappedWriter).writeAttribute("src", "include", null);
        verify(wrappedWriter).write(contains("inline"));
    }

}