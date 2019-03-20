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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.util.HTML;

public class BodyRenderer extends CoreRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        writer.startElement("body", component);

        if (shouldWriteId(component)) {
            writer.writeAttribute("id", clientId, "id");
        }

        String styleClass = (String) component.getAttributes().get("styleClass");
        if (styleClass != null && styleClass.length() != 0) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }

        renderPassThruAttributes(context, component, HTML.BODY_ATTRS);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        encodeResources(context);

        if (!context.getPartialViewContext().isAjaxRequest()) {
            encodeOnloadScripts(writer);
        }

        writer.endElement("body");
    }

    protected void encodeOnloadScripts(ResponseWriter writer) throws IOException {
        List<String> scripts = PrimeRequestContext.getCurrentInstance().getScriptsToExecute();

        if (!scripts.isEmpty()) {
            writer.startElement("script", null);
            writer.writeAttribute("type", "text/javascript", null);

            writer.write("$(function(){");

            for (int i = 0; i < scripts.size(); i++) {
                writer.write(scripts.get(i));
                writer.write(';');
            }

            writer.write("});");
            writer.endElement("script");
        }
    }

    protected void encodeResources(FacesContext context) throws IOException {
        UIViewRoot viewRoot = context.getViewRoot();
        ListIterator iter = (viewRoot.getComponentResources(context, "body")).listIterator();
        while (iter.hasNext()) {
            UIComponent resource = (UIComponent) iter.next();
            resource.encodeAll(context);
        }
    }
}
