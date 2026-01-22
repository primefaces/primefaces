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
package org.primefaces.component.focus;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.renderkit.RendererUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Focus.DEFAULT_RENDERER, componentFamily = Focus.COMPONENT_FAMILY)
public class FocusRenderer extends CoreRenderer<Focus> {

    private static final Map<String, Integer> SEVERITY_ORDINALS = new HashMap<>();

    static {
        SEVERITY_ORDINALS.put("info", FacesMessage.SEVERITY_INFO.getOrdinal());
        SEVERITY_ORDINALS.put("warn", FacesMessage.SEVERITY_WARN.getOrdinal());
        SEVERITY_ORDINALS.put("error", FacesMessage.SEVERITY_ERROR.getOrdinal());
        SEVERITY_ORDINALS.put("fatal", FacesMessage.SEVERITY_FATAL.getOrdinal());
    }

    @Override
    public void encodeEnd(FacesContext context, Focus component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        //Dummy markup for ajax update
        writer.startElement("span", component);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.endElement("span");

        writer.startElement("script", component);
        RendererUtils.encodeScriptTypeIfNecessary(context);

        if (isValueBlank(component.getFor())) {
            encodeImplicitFocus(context, component);
        }
        else {
            encodeExplicitFocus(context, component);
        }

        writer.endElement("script");
    }

    protected void encodeExplicitFocus(FacesContext context, Focus component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent forComponent = SearchExpressionUtils.contextlessResolveComponent(
                context, component, component.getFor());

        String clientId = forComponent.getClientId(context);

        writer.write("$(function(){");
        writer.write("PrimeFaces.focus('" + clientId + "');");
        writer.write("});");
    }

    protected void encodeImplicitFocus(FacesContext context, Focus component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String invalidClientId = findFirstInvalidComponentClientId(context, component);

        writer.write("$(function(){");

        if (invalidClientId != null) {
            writer.write("PrimeFaces.focus('" + invalidClientId + "');");
        }
        else if (component.getContext() != null) {
            UIComponent focusContext = SearchExpressionUtils.contextlessResolveComponent(
                    context, component, component.getContext());

            writer.write("PrimeFaces.focus(null, '" + focusContext.getClientId(context) + "');");
        }
        else {
            writer.write("PrimeFaces.focus();");
        }

        writer.write("});");
    }

    protected String findFirstInvalidComponentClientId(FacesContext context, Focus component) {
        int minSeverityOrdinal = SEVERITY_ORDINALS.get(component.getMinSeverity());

        for (Iterator<String> iterator = context.getClientIdsWithMessages(); iterator.hasNext(); ) {
            String clientId = iterator.next();

            for (Iterator<FacesMessage> messageIter = context.getMessages(clientId); messageIter.hasNext(); ) {
                FacesMessage message = messageIter.next();

                if (message.getSeverity().getOrdinal() >= minSeverityOrdinal) {
                    return clientId;
                }
            }
        }

        return null;
    }
}
