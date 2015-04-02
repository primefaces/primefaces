/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.renderkit;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.context.RequestContext;
import org.primefaces.util.HTML;

public class BodyRenderer extends CoreRenderer {

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

    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        RequestContext requestContext = RequestContext.getCurrentInstance();
        
        encodeResources(context);

        if (!requestContext.isAjaxRequest()) {
            encodeOnloadScripts(writer, requestContext);
        }

        writer.endElement("body");
    }

    protected void encodeOnloadScripts(ResponseWriter writer, RequestContext context) throws IOException {
        List<String> scripts = context.getScriptsToExecute();

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
