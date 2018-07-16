package org.primefaces.application.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import javax.faces.context.ResponseWriter;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
        // FIXME tag handling should be case-insensitive
        writer.startElement("html", null);
        verify(wrappedWriter).startElement("html", null);        
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);
        writer.startElement("script", null);
        verify(wrappedWriter, never()).startElement("script", null);
        writer.writeText("inline", null);
        verify(wrappedWriter, never()).writeText(anyString(), anyString());
        writer.endElement("script");
        verify(wrappedWriter, never()).endElement("script");
        Assert.assertEquals(1, state.getInlines().size());
        Assert.assertTrue(state.getIncludes().isEmpty());
        // FIXME normally we would expect state.getSavedInlineTags() to be 0 at this point
        // Assert.assertEquals(0, state.getSavedInlineTags());
        writer.endElement("body");
        verify(wrappedWriter).endElement("body");
        verify(wrappedWriter).startElement("script", null);
        // FIXME normally we would expect the script to be minimized, instead we get: var pf=window.PrimeFaces;inline;
        // verify(wrappedWriter).write("inline");
        // FIXME normally we would expect writeText instead of write to be called
        verify(wrappedWriter).write(contains("inline"));
        // verify(wrappedWriter).writeText("inline", null);
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
        
        Assert.assertEquals(2, state.getInlines().size());
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
        // FIXME we would expect two script tags to be written, one for the javascripts and the other one for the vertex 
        // verify(wrappedWriter, times(2)).startElement("script", null);
        // verify(wrappedWriter).write(matches("(?s).*javascript1.*javascript2(?!.*vertex.*).*"));
        // verify(wrappedWriter).write(matches("(?s)(?!.*javascript.*).*vertex.*"));
        // verify(wrappedWriter, times(2)).endElement("script");        
    }
    
    @Test
    public void testPassthroughAttributes() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.writeAttribute("async", "true", null);
        writer.writeAttribute("defer", "true", null);
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
        // FIXME normally we would expect writeText instead of write to be called
        verify(wrappedWriter).write(contains("pf.settings pf.cw pf.ab pf ;"));
        // verify(wrappedWriter).writeText(contains("pf.settings pf.cw pf.ab pf ;"), null);        
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
        Assert.assertEquals(3, state.getIncludes().size());
        Assert.assertTrue(state.getInlines().isEmpty());
        InOrder inOrder = inOrder(wrappedWriter);
        inOrder.verify(wrappedWriter).startElement("div", null);
        // FIXME wouldn't it be even better to include url2 just once? 
        // FIXME normally we would expect three scripts to be written if we have three includes, however one additional is written for empty inlines
        // inOrder.verify(wrappedWriter, times(3)).startElement("script", null);
        inOrder.verify(wrappedWriter, times(4)).startElement("script", null);
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
        Assert.assertEquals(1, state.getIncludes().size());
        Assert.assertEquals(1, state.getInlines().size());
        verify(wrappedWriter, times(2)).startElement("script", null);
        verify(wrappedWriter).writeAttribute("src", "include", null);
        // FIXME normally we would expect writeText instead of write to be called
        verify(wrappedWriter).write(contains("inline"));
        // verify(wrappedWriter).writeText("inline", null);
    }
    
}