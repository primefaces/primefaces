/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.application.factory;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;
import javax.faces.context.ResponseWriterWrapper;

import org.primefaces.renderkit.RendererUtils;

/**
 * JSF generates all script tags with 'type="text/javascript"' which throws HTML5 validation warnings.
 * NOTE: Not necessary for Faces 4.0+.
 *
 * @since 12.0.0
 */
public class Html5FacesContextResponseWriter extends ResponseWriterWrapper {

    private static final String SCRIPT = "script";
    private boolean inScriptStartTag;

    public Html5FacesContextResponseWriter(ResponseWriter wrapped) {
        super(wrapped);
    }

    @Override
    public void startElement(String name, UIComponent component) throws IOException {
        super.startElement(name, component);
        inScriptStartTag = SCRIPT.equalsIgnoreCase(name);
    }

    @Override
    public void endElement(String name) throws IOException {
        super.endElement(name);
        if (SCRIPT.equalsIgnoreCase(name)) {
            inScriptStartTag = false;
        }
    }

    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {
        if (inScriptStartTag && RendererUtils.SCRIPT_TYPE.equals(value)) {
            return;
        }
        super.writeAttribute(name, value, property);
    }
}
