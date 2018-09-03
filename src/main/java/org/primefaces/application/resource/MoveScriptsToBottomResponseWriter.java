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
package org.primefaces.application.resource;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;

public class MoveScriptsToBottomResponseWriter extends ResponseWriterWrapper {

    private final ResponseWriter wrapped;
    private final MoveScriptsToBottomState state;

    private boolean inScript;
    private String scriptType;
    private StringBuilder include;
    private StringBuilder inline;
    private boolean scriptsRendered;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public MoveScriptsToBottomResponseWriter(ResponseWriter wrapped, MoveScriptsToBottomState state) {
        this.wrapped = wrapped;
        this.state = state;

        inScript = false;
        scriptsRendered = false;

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
            getWrapped().write(cbuf, off, len);
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
    public void writeText(char[] cbuf, int off, int len) throws IOException {
        if (inScript) {
            inline.append(cbuf, off, len);
        }
        else {
            getWrapped().writeText(cbuf, off, len);
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
    public void writeText(Object text, UIComponent component, String property) throws IOException {
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
            if ("src".equalsIgnoreCase(name)) {
                String strValue = (String) value;
                if (strValue != null && !strValue.trim().isEmpty()) {
                    include.append(strValue);
                }
            }
            else if ("type".equalsIgnoreCase(name)) {
                String strValue = (String) value;
                if (strValue != null && !strValue.trim().isEmpty()) {
                    scriptType = strValue;
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
            if ("src".equalsIgnoreCase(name)) {
                String strValue = (String) value;
                if (strValue != null && !strValue.trim().isEmpty()) {
                    include.append(strValue);
                }
            }
            else if ("type".equalsIgnoreCase(name)) {
                String strValue = (String) value;
                if (strValue != null && !strValue.trim().isEmpty()) {
                    scriptType = strValue;
                }
            }
        }
        else {
            getWrapped().writeURIAttribute(name, value, property);
        }
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        if ("script".equalsIgnoreCase(name)) {
            inScript = true;
            scriptType = "text/javascript";
        }
        else {
            getWrapped().startElement(name, component);
        }
    }

    @Override
    public void endElement(String name) throws IOException {
        if ("script".equalsIgnoreCase(name)) {
            inScript = false;

            state.addInline(scriptType, inline);
            state.addInclude(scriptType, include);

            scriptType = null;
            include.setLength(0);
            inline.setLength(0);
        }
        else if ("body".equalsIgnoreCase(name) || ("html".equalsIgnoreCase(name) && !scriptsRendered)) {

            for (Map.Entry<String, ArrayList<String>> entry : state.getIncludes().entrySet()) {
                String type = entry.getKey();
                ArrayList<String> includes = entry.getValue();

                for (int i = 0; i < includes.size(); i++) {
                    String src = includes.get(i);
                    if (src != null && !src.isEmpty()) {
                        getWrapped().startElement("script", null);
                        getWrapped().writeAttribute("type", type, null);
                        getWrapped().writeAttribute("src", src, null);
                        getWrapped().endElement("script");
                    }
                }
            }

            for (Map.Entry<String, ArrayList<String>> entry : state.getInlines().entrySet()) {
                String type = entry.getKey();
                ArrayList<String> inlines = entry.getValue();
                String merged = mergeAndMinimizeInlineScripts(type, inlines);

                if (merged != null && !merged.trim().isEmpty()) {
                    getWrapped().startElement("script", null);
                    getWrapped().writeAttribute("type", type, null);
                    getWrapped().write(merged);
                    getWrapped().endElement("script");
                }
            }

            getWrapped().endElement(name);

            scriptsRendered = true;
        }
        else {
            getWrapped().endElement(name);
        }
    }

    protected String mergeAndMinimizeInlineScripts(String type, ArrayList<String> inlines) {
        StringBuilder script = new StringBuilder(inlines.size() * 100);
        for (int i = 0; i < inlines.size(); i++) {
            if (i > 0) {
                script.append("\n");
            }
            script.append(inlines.get(i));
            script.append(";");
        }

        String minimized = script.toString();

        if ("text/javascript".equalsIgnoreCase(type)) {
            minimized = minimized.replace(";;", ";");

            if (minimized.contains("PrimeFaces")) {
                minimized = minimized.replace("PrimeFaces.settings", "pf.settings")
                    .replace("PrimeFaces.cw", "pf.cw")
                    .replace("PrimeFaces.ab", "pf.ab")
                    .replace("window.PrimeFaces", "pf");

                minimized = "var pf=window.PrimeFaces;" + minimized;
            }
        }

        return minimized;
    }

    @Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        return getWrapped().cloneWithWriter(writer);
    }
}
