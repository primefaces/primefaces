/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

import org.primefaces.renderkit.RendererUtils;
import org.primefaces.util.AgentUtils;
import org.primefaces.util.LangUtils;

public class MoveScriptsToBottomResponseWriter extends ResponseWriterWrapper {

    private static final String SCRIPT_TAG = "script";
    private static final String BODY_TAG = "body";
    private static final String HTML_TAG = "html";
    private static final String TYPE_ATTRIBUTE = "type";

    private final MoveScriptsToBottomState state;

    private boolean inScript;
    private String scriptType;
    private Map<String, String> includeAttributes;
    private StringBuilder inline;
    private boolean scriptsRendered;

    private boolean writeFouc;

    public MoveScriptsToBottomResponseWriter(ResponseWriter wrapped, MoveScriptsToBottomState state) {
        super(wrapped);
        this.state = state;

        inScript = false;
        scriptsRendered = false;
        writeFouc = false;

        includeAttributes = new LinkedHashMap<>(6);
        inline = new StringBuilder(75);
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
            updateAttributes(name, (String) value);
        }
        else {
            getWrapped().writeAttribute(name, value, property);
        }
    }

    @Override
    public void writeURIAttribute(String name, Object value, String property) throws IOException {
        if (inScript) {
            updateAttributes(name, (String) value);
        }
        else {
            getWrapped().writeURIAttribute(name, value, property);
        }
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        if (SCRIPT_TAG.equalsIgnoreCase(name)) {
            inScript = true;
            scriptType = RendererUtils.SCRIPT_TYPE;
        }
        else {
            writeFouc();

            getWrapped().startElement(name, component);

            if (BODY_TAG.equalsIgnoreCase(name) && isFirefox()) {
                writeFouc = true;
            }
        }
    }

    @Override
    public void endElement(String name) throws IOException {
        if (SCRIPT_TAG.equalsIgnoreCase(name)) {
            inScript = false;

            state.addInline(scriptType, inline);
            if (LangUtils.isNotBlank(includeAttributes.get("src"))) {
                state.addInclude(scriptType, includeAttributes);
            }

            scriptType = null;
            includeAttributes.clear();
            inline.setLength(0);
        }
        else if (BODY_TAG.equalsIgnoreCase(name) || (HTML_TAG.equalsIgnoreCase(name) && !scriptsRendered)) {

            // write script includes
            for (Entry<String, List<Map<String, String>>> entry : state.getIncludes().entrySet()) {

                List<Map<String, String>> includes = entry.getValue();
                for (int i = 0; i < includes.size(); i++) {
                    Map<String, String> attributes = includes.get(i);
                    attributes.put(TYPE_ATTRIBUTE, entry.getKey());
                    getWrapped().startElement(SCRIPT_TAG, null);
                    for (Entry<String, String> attribute : attributes.entrySet()) {
                        String attributeName = attribute.getKey();
                        String attributeValue = attribute.getValue();
                        if (LangUtils.isNotBlank(attributeValue)) {
                            getWrapped().writeAttribute(attributeName, attributeValue, null);
                        }
                    }
                    getWrapped().endElement(SCRIPT_TAG);
                }
            }

            // write inline scripts
            for (Map.Entry<String, List<String>> entry : state.getInlines().entrySet()) {
                String type = entry.getKey();
                List<String> inlines = entry.getValue();

                String id = UUID.randomUUID().toString();
                String merged = mergeAndMinimizeInlineScripts(id, type, inlines);

                if (LangUtils.isNotBlank(merged)) {
                    getWrapped().startElement(SCRIPT_TAG, null);
                    getWrapped().writeAttribute("id", id, null);
                    getWrapped().writeAttribute(TYPE_ATTRIBUTE, type, null);
                    getWrapped().write(merged);
                    getWrapped().endElement(SCRIPT_TAG);
                }
            }

            getWrapped().endElement(name);

            scriptsRendered = true;
        }
        else {
            getWrapped().endElement(name);
        }
    }

    protected void writeFouc() throws IOException {
        if (writeFouc) {
            writeFouc = false;
            getWrapped().startElement(SCRIPT_TAG, null);
            getWrapped().writeText("/*FIREFOX_FOUC_FIX*/", null);
            getWrapped().endElement(SCRIPT_TAG);
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

        if (LangUtils.isNotBlank(minimized)) {
            if (RendererUtils.SCRIPT_TYPE.equalsIgnoreCase(type)) {
                minimized = minimized.replace(";;", ";");

                if (minimized.contains("PrimeFaces")) {
                    minimized = minimized.replace("PrimeFaces.settings", "pf.settings")
                        .replace("PrimeFaces.cw", "pf.cw")
                        .replace("PrimeFaces.ab", "pf.ab")
                        .replace("window.PrimeFaces", "pf");

                    minimized = "var pf=window.PrimeFaces;" + minimized;
                }

                if (!minimized.endsWith(";")) {
                    minimized += ";";
                }
                minimized += "document.getElementById('" + id + "').remove();";
            }
        }

        return minimized;
    }

    @Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        return getWrapped().cloneWithWriter(writer);
    }

    protected void updateAttributes(String name, String value) {
        includeAttributes.put(name, value);

        if (TYPE_ATTRIBUTE.equalsIgnoreCase(name)) {
            if (LangUtils.isNotBlank(value)) {
                scriptType = value;
            }
        }
    }

    protected boolean isFirefox() {
        return AgentUtils.isFirefox(FacesContext.getCurrentInstance());
    }
}
