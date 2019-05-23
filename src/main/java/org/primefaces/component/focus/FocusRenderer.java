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
package org.primefaces.component.focus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;

public class FocusRenderer extends CoreRenderer {

    private static final Map<String, Integer> SEVERITY_ORDINALS = new HashMap<>();

    static {
        SEVERITY_ORDINALS.put("info", FacesMessage.SEVERITY_INFO.getOrdinal());
        SEVERITY_ORDINALS.put("warn", FacesMessage.SEVERITY_WARN.getOrdinal());
        SEVERITY_ORDINALS.put("error", FacesMessage.SEVERITY_ERROR.getOrdinal());
        SEVERITY_ORDINALS.put("fatal", FacesMessage.SEVERITY_FATAL.getOrdinal());
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Focus focus = (Focus) component;
        ResponseWriter writer = context.getResponseWriter();

        //Dummy markup for ajax update
        writer.startElement("span", focus);
        writer.writeAttribute("id", focus.getClientId(context), "id");
        writer.endElement("span");

        writer.startElement("script", focus);
        writer.writeAttribute("type", "text/javascript", null);

        if (isValueBlank(focus.getFor())) {
            encodeImplicitFocus(context, focus);
        }
        else {
            encodeExplicitFocus(context, focus);
        }

        writer.endElement("script");
    }

    protected void encodeExplicitFocus(FacesContext context, Focus focus) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent forComponent = SearchExpressionFacade.resolveComponent(
                context, focus, focus.getFor());

        String clientId = forComponent.getClientId(context);

        writer.write("$(function(){");
        writer.write("PrimeFaces.focus('" + clientId + "');");
        writer.write("});");
    }

    protected void encodeImplicitFocus(FacesContext context, Focus focus) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String invalidClientId = findFirstInvalidComponentClientId(context, focus);

        writer.write("$(function(){");

        if (invalidClientId != null) {
            writer.write("PrimeFaces.focus('" + invalidClientId + "');");
        }
        else if (focus.getContext() != null) {
            UIComponent focusContext = SearchExpressionFacade.resolveComponent(
                    context, focus, focus.getContext());

            writer.write("PrimeFaces.focus(null, '" + focusContext.getClientId(context) + "');");
        }
        else {
            writer.write("PrimeFaces.focus();");
        }

        writer.write("});");
    }

    protected String findFirstInvalidComponentClientId(FacesContext context, Focus focus) {
        int minSeverityOrdinal = SEVERITY_ORDINALS.get(focus.getMinSeverity());

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
