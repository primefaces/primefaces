/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.fragment;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class FragmentRenderer extends CoreRenderer<Fragment> {

    @Override
    public void encodeBegin(FacesContext context, Fragment component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        Map<Object, Object> attrs = context.getAttributes();

        attrs.put(Constants.FRAGMENT_PROCESS, component.isProcess() ? clientId : null);
        attrs.put(Constants.FRAGMENT_UPDATE, component.isUpdate() ? clientId : null);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
    }

    @Override
    public void encodeEnd(FacesContext context, Fragment component) throws IOException {
        context.getResponseWriter().endElement("div");

        context.getAttributes().remove(Constants.FRAGMENT_PROCESS);
        context.getAttributes().remove(Constants.FRAGMENT_UPDATE);
    }
}
