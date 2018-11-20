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

import org.primefaces.context.PrimePartialResponseWriter;

import javax.faces.component.UIComponent;
import javax.faces.context.PartialResponseWriter;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.Writer;

public class CspScriptsPartialResponseWriter extends PrimePartialResponseWriter {

    CspScriptsResponseWriter cspWriter;

    public CspScriptsPartialResponseWriter(PartialResponseWriter wrapped) {
        super(wrapped);
        cspWriter = new CspScriptsResponseWriter(wrapped);
    }

    @Override
    public void startInsertAfter(String targetId) throws IOException {
        cspWriter.elementsToHandle.clear();
        super.startInsertAfter(targetId);
    }

    @Override
    public void startInsertBefore(String targetId) throws IOException {
        cspWriter.elementsToHandle.clear();
        super.startInsertBefore(targetId);
    }

    @Override
    public void endInsert() throws IOException {
        cspWriter.writeJavascriptHandlers();
        cspWriter.registerNoncesAndHashes();
        cspWriter.elementsToHandle.clear();
        super.endInsert();
    }

    @Override
    public void startUpdate(String targetId) throws IOException {
        cspWriter.elementsToHandle.clear();
        super.startUpdate(targetId);
    }

    @Override
    public void endUpdate() throws IOException {
        cspWriter.writeJavascriptHandlers();
        cspWriter.registerNoncesAndHashes();
        cspWriter.elementsToHandle.clear();
        super.endUpdate();
    }

    @Override
    public void startEval() throws IOException {
        //TODO move scripts written to partial-response <eval> to script tag to avoid CSP violation?
        super.startEval();
    }

    @Override
    public void endEval() throws IOException {
        //TODO move scripts written to partial-response <eval> to script tag to avoid CSP violation?
        super.endEval();
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        cspWriter.startElement(name, component);
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        cspWriter.writeAttribute(name, value, property);
    }

    @Override
    public void writeURIAttribute(String name, Object value, String property) throws IOException {
        cspWriter.writeURIAttribute(name, value, property);
    }

    @Override
    public void endElement(String name) throws IOException {
        cspWriter.endElement(name);
    }

    @Override
    public void flush() throws IOException {
        cspWriter.flush();
    }

    @Override
    public void writeComment(Object comment) throws IOException {
        cspWriter.writeComment(comment);
    }

    @Override
    public void writeText(Object text, String property) throws IOException {
        cspWriter.writeText(text, property);
    }

    @Override
    public void writeText(Object text, UIComponent component, String property) throws IOException {
        cspWriter.writeText(text, component, property);
    }

    @Override
    public void writeText(char[] text, int off, int len) throws IOException {
        cspWriter.writeText(text, off, len);
    }

    @Override
    public void close() throws IOException {
        cspWriter.close();
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        cspWriter.write(cbuf, off, len);
    }

    @Override
    public ResponseWriter cloneWithWriter(Writer writer) {
        return getWrapped().cloneWithWriter(writer);
    }
}
