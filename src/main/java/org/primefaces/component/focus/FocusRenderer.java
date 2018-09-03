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
