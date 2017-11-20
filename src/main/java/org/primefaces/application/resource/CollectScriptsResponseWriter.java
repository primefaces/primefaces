/**
 * Copyright 2009-2017 PrimeTek.
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
package org.primefaces.application.resource;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import java.io.IOException;
import java.io.Writer;

public class CollectScriptsResponseWriter extends ResponseWriterWrapper {

    private final ResponseWriter wrapped;
    private final CollectScriptsState state;

    private boolean inScript;
    private StringBuilder include;
    private StringBuilder inline;

    public CollectScriptsResponseWriter(ResponseWriter wrapped, CollectScriptsState state) {
        this.wrapped = wrapped;
        this.state = state;
        
        inScript = false;
        include = new StringBuilder(50);
        inline = new StringBuilder(75);
    }

    @Override
    public ResponseWriter getWrapped() {
        return wrapped;
    }

    @Override
    public void write(int c) throws IOException {
        if (inScript) {
            inline.append((char) c);
        }
        else {
            getWrapped().write(c);
        }
    }

    @Override
    public void write(char cbuf[]) throws IOException {
        if (inScript) {
            inline.append(cbuf);
        }
        else {
            getWrapped().write(cbuf);
        }
    }
    
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        if (inScript) {
            inline.append(cbuf, off, len);
        }
        else {
            getWrapped().write(cbuf);
        }
    }
    
    @Override
    public void write(String str) throws IOException {
        if (inScript) {
            inline.append(str);
        }
        else {
            getWrapped().write(str);
        }
    }

    @Override
    public void writeText(Object text, String property) throws IOException {
        if (inScript) {
            inline.append(text);
        }
        else {
            getWrapped().writeText(text, property);
        }
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        if (inScript) {
            if ("src".equals(name)) {
                String strValue = (String) value;
                if (strValue != null && !strValue.trim().isEmpty()) {
                    include.append(strValue);
                }
            }
        }
        else {
            getWrapped().writeAttribute(name, value, property);
        }
    }

    @Override
    public void writeURIAttribute(String name, Object value, String property) throws IOException {
        if (inScript) {
            if ("src".equals(name)) {
                String strValue = (String) value;
                if (strValue != null && !strValue.trim().isEmpty()) {
                    include.append(strValue);
                }
            }
        }
        else {
            getWrapped().writeURIAttribute(name, value, property);
        }
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        if ("script".equals(name)) {
            inScript = true;
        }
        else {
            getWrapped().startElement(name, component);
        }
    }

    @Override
    public void endElement(String name) throws IOException {
        if ("script".equals(name)) {
            inScript = false;

            state.addInline(inline);
            state.addInclude(include);

            include.setLength(0);
            inline.setLength(0);
        }
        else if ("body".equals(name)) {
            for (int i = 0; i < state.getIncludes().size(); i++) {
                String src = state.getIncludes().get(i);
                if (src != null && !src.isEmpty()) {
                    getWrapped().startElement("script", null);
                    getWrapped().writeAttribute("type", "text/javascript", null);
                    getWrapped().writeAttribute("src", src, null);
                    getWrapped().endElement("script");
                }
            }

            getWrapped().startElement("script", null);
            getWrapped().writeAttribute("type", "text/javascript", null);
            getWrapped().write(minimizeInlineScript(state.getInline()));
            getWrapped().endElement("script");

            getWrapped().endElement(name);
        }
        else {
            getWrapped().endElement(name);
        }
    }

    protected String minimizeInlineScript(StringBuilder script) {
        String minimized = script.toString();

        minimized = minimized.replace("PrimeFaces.settings", "pf.settings")
            .replace("PrimeFaces.cw", "pf.cw")
            .replace("PrimeFaces.ab", "pf.ab")
            .replace("window.PrimeFaces", "pf")
            .replace(";;", ";");

        minimized = "var pf=window.PrimeFaces;" + minimized;

        return minimized;
    }
    
    @Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        return getWrapped().cloneWithWriter(writer);
    }
}
