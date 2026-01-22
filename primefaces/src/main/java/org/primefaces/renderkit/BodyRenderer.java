/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.context.PrimeRequestContext;
import org.primefaces.util.HTML;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class BodyRenderer extends CoreRenderer<UIComponent> {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        writer.startElement("body", component);

        if (shouldWriteId(component)) {
            writer.writeAttribute("id", clientId, "id");
        }

        String styleClass = (String) component.getAttributes().get("styleClass");
        if (styleClass != null && !styleClass.isEmpty()) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }

        renderPassThruAttributes(context, component, HTML.BODY_ATTRS);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        encodeResources(context);

        if (!context.getPartialViewContext().isAjaxRequest()) {
            encodeOnloadScripts(context, writer);
        }

        writer.endElement("body");
    }

    protected void encodeOnloadScripts(FacesContext context, ResponseWriter writer) throws IOException {
        List<String> scripts = PrimeRequestContext.getCurrentInstance().getScriptsToExecute();

        if (!scripts.isEmpty()) {
            writer.startElement("script", null);
            RendererUtils.encodeScriptTypeIfNecessary(context);

            writer.write("(function(){const pfLoad=() => {");

            for (int i = 0; i < scripts.size(); i++) {
                writer.write(scripts.get(i));
                writer.write(';');
            }

            writer.write("};if(window.$){$(function(){pfLoad()})}");
            writer.write("else if(document.readyState==='complete'){pfLoad()}");
            writer.write("else{document.addEventListener('DOMContentLoaded', pfLoad)}})();");

            writer.endElement("script");
        }
    }

    protected void encodeResources(FacesContext context) throws IOException {
        UIViewRoot viewRoot = context.getViewRoot();
        ListIterator<UIComponent> iter = (viewRoot.getComponentResources(context, "body")).listIterator();
        while (iter.hasNext()) {
            UIComponent resource = iter.next();
            resource.encodeAll(context);
        }
    }
}
