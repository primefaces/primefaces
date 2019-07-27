/**
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

import org.primefaces.util.LangUtils;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    public void write(char[] cbuf) throws IOException {
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
            updateScriptSrcOrType(name, (String) value);
        }
        else {
            getWrapped().writeAttribute(name, value, property);
        }
    }

    @Override
    public void writeURIAttribute(String name, Object value, String property) throws IOException {
        if (inScript) {
            updateScriptSrcOrType(name, (String) value);
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

            // write script includes
            for (Map.Entry<String, List<String>> entry : state.getIncludes().entrySet()) {
                String type = entry.getKey();
                List<String> includes = entry.getValue();

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

            // write inline scripts
            for (Map.Entry<String, List<String>> entry : state.getInlines().entrySet()) {
                String type = entry.getKey();
                List<String> inlines = entry.getValue();

                String id = UUID.randomUUID().toString();
                String merged = mergeAndMinimizeInlineScripts(id, type, inlines);

                if (!LangUtils.isValueBlank(merged)) {
                    getWrapped().startElement("script", null);
                    getWrapped().writeAttribute("id", id, null);
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

    protected String mergeAndMinimizeInlineScripts(String id, String type, List<String> inlines) {
        StringBuilder script = new StringBuilder(inlines.size() * 100);
        for (int i = 0; i < inlines.size(); i++) {
            if (i > 0) {
                script.append("\n");
            }
            script.append(inlines.get(i));
            script.append(";");
        }

        String minimized = script.toString();

        if (!LangUtils.isValueBlank(minimized)) {
            if ("text/javascript".equalsIgnoreCase(type)) {
                minimized = minimized.replace(";;", ";");

                if (minimized.contains("PrimeFaces")) {
                    minimized = minimized.replace("PrimeFaces.settings", "pf.settings")
                        .replace("PrimeFaces.cw", "pf.cw")
                        .replace("PrimeFaces.ab", "pf.ab")
                        .replace("window.PrimeFaces", "pf");

                    minimized = "var pf=window.PrimeFaces;"
                            + minimized
                            + "$(PrimeFaces.escapeClientId(\"" + id + "\")).remove();";
                }
            }
        }

        return minimized;
    }

    @Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        return getWrapped().cloneWithWriter(writer);
    }

    protected void updateScriptSrcOrType(String name, String value) {
        if ("src".equalsIgnoreCase(name)) {
            if (!LangUtils.isValueBlank(value)) {
                include.append(value);
            }
        }
        else if ("type".equalsIgnoreCase(name)) {
            if (!LangUtils.isValueBlank(value)) {
                scriptType = value;
            }
        }
    }
}
