/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.application.resource.csp.scripts;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.primefaces.application.resource.csp.scripts.CspScriptsResponseWriter.DOM_EVENTS;

public class CspScriptsResponseWriterTest {

    private static Base64Matcher base64Matcher = new Base64Matcher();

    private CspScriptsResponseWriter writer;
    private ResponseWriter wrappedWriter;
    private HttpServletRequest servletRequest;

    @Before
    public void setup() {
        wrappedWriter = mock(ResponseWriter.class);
        FacesContext facesContext = mock(FacesContext.class);
        try {
            Method setter = FacesContext.class.getDeclaredMethod("setCurrentInstance", new Class[] { FacesContext.class });
            setter.setAccessible(true);
            setter.invoke(null, new Object[] { facesContext });
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        ExternalContext externalContext = mock(ExternalContext.class);
        when(facesContext.getExternalContext()).thenReturn(externalContext);
        servletRequest = mock(HttpServletRequest.class);
        when(externalContext.getRequest()).thenReturn(servletRequest);
        writer = new CspScriptsResponseWriter(wrappedWriter);
        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
    }

    @Test
    public void testNoScripts() throws IOException {
        Assert.assertTrue(writer.elements.isEmpty());
        Assert.assertTrue(writer.elementsToHandle.isEmpty());
        Assert.assertTrue(writer.nonces.isEmpty());

        writer.startElement("span", null);
        verify(wrappedWriter).startElement("span", null);
        writer.writeAttribute("style", "foo", null);
        verify(wrappedWriter).writeAttribute("style", "foo", null);
        writer.writeText("bar", null);
        verify(wrappedWriter).writeText("bar", null);
        writer.endElement("span");
        verify(wrappedWriter).endElement("span");

        Assert.assertTrue(writer.elements.isEmpty());
        Assert.assertTrue(writer.elementsToHandle.isEmpty());
        Assert.assertTrue(writer.nonces.isEmpty());
    }

    @Test
    public void testSingleEventHandlerWithIdAttribute() throws IOException {
        writer.startElement("body", null);
        verify(wrappedWriter).startElement("body", null);
        Assert.assertEquals(1, writer.elements.size());

        writer.startElement("button", null);
        verify(wrappedWriter).startElement("button", null);
        Assert.assertEquals(2, writer.elements.size());

        writer.writeAttribute("id", "button123", null);
        verify(wrappedWriter).writeAttribute("id", "button123", null);
        writer.writeAttribute("type", "submit", null);
        verify(wrappedWriter).writeAttribute("type", "submit", null);
        writer.writeAttribute("onclick", "alert(1);", null);
        verify(wrappedWriter, never()).writeAttribute("onclick", "alert(1)", null);

        writer.endElement("button");
        verify(wrappedWriter).endElement("button");
        Assert.assertEquals(1, writer.elements.size());
        Assert.assertEquals(1, writer.elementsToHandle.size());

        writer.endElement("body");
        verify(wrappedWriter).endElement("body");
        Assert.assertEquals(1, writer.nonces.size());
        Assert.assertTrue(writer.elements.isEmpty());

        verify(wrappedWriter).startElement("script", null);
        verify(wrappedWriter).writeAttribute(eq("nonce"), argThat(base64Matcher), nullable(String.class));
        verify(wrappedWriter).writeText(contains("pf.csp1(\"button123\",\"click\",function(e){pf.csp0(e);alert(1);});"), nullable(String.class));
        verify(wrappedWriter).endElement("script");
    }

    @Test
    public void testAllEventHandlersWithoutIdAttribute() throws IOException {
        writer.startElement("body", null);

        for (String event : DOM_EVENTS) {
            writer.writeAttribute("on" + event, "alert('" + event + "');", null);
        }
        verify(wrappedWriter, never()).writeAttribute(startsWith("on"), anyString(), nullable(String.class));

        writer.endElement("body");
        Assert.assertEquals(1, writer.elementsToHandle.size());
        Assert.assertEquals(1, writer.nonces.size());
        verify(wrappedWriter).writeAttribute(eq("id"), anyString(), nullable(String.class));

        verify(wrappedWriter).startElement("script", null);
        verify(wrappedWriter).writeAttribute(eq("nonce"), argThat(base64Matcher), nullable(String.class));

        ArgumentCaptor<String> javascript = ArgumentCaptor.forClass(String.class);
        verify(wrappedWriter).writeText(javascript.capture(), nullable(String.class));
        Assert.assertEquals(DOM_EVENTS.size(), countMatches(javascript.getValue(),"pf.csp1("));

        verify(wrappedWriter).endElement("script");
    }

    @Test
    public void testScriptsWithoutNonces() throws IOException {
        writer.startElement("body", null);

        writer.startElement("script", null);
        writer.writeAttribute("src", "about:blank", null);
        verify(wrappedWriter).writeAttribute("src", "about:blank", null);
        writer.endElement("script");

        writer.startElement("script", null);
        writer.writeText("alert(1);", null);
        writer.endElement("script");

        verify(wrappedWriter, times(2)).startElement("script", null);
        verify(wrappedWriter, times(2)).writeAttribute(eq("nonce"), argThat(base64Matcher), nullable(String.class));
        verify(wrappedWriter).writeText("alert(1);", null);
        verify(wrappedWriter, times(2)).endElement("script");

        writer.endElement("body");

        verify(wrappedWriter, never()).writeAttribute(eq("id"), anyString(), nullable(String.class));
    }

    @Test
    public void testScriptWithNonce() throws IOException {
        writer.startElement("body", null);

        writer.startElement("script", null);
        verify(wrappedWriter).startElement("script", null);
        writer.writeAttribute("src", "about:blank", null);
        verify(wrappedWriter).writeAttribute("src", "about:blank", null);
        writer.writeAttribute("nonce", "bla", null);
        writer.endElement("script");
        verify(wrappedWriter).writeAttribute("nonce", "bla", null);
        verify(wrappedWriter).endElement("script");

        writer.endElement("body");

        Assert.assertTrue(writer.nonces.isEmpty());

        verify(wrappedWriter, never()).writeAttribute(eq("id"), anyString(), nullable(String.class));
    }

    private static int countMatches(String string, String search) {
        return string.split(Pattern.quote(search)).length - 1;
    }

    static class Base64Matcher implements ArgumentMatcher<String> {

        @Override
        public boolean matches(String argument) {
            return org.primefaces.util.Base64.decode(argument) != null;
        }

    }

}