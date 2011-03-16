/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.component.galleria;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class GalleriaRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Galleria galleria = (Galleria) component;

        encodeMarkup(context, galleria);
        encodeScript(context, galleria);
    }

    public void encodeMarkup(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Galleria galleria = (Galleria) component;
        int dataCount = galleria.getRowCount();
        
        writer.startElement("ul", null);
        writer.writeAttribute("id", galleria.getClientId(context), null);

        if(galleria.getStyleClass() !=  null) writer.writeAttribute("class", galleria.getStyleClass(), "styleClass");
        if(galleria.getStyle() !=  null) writer.writeAttribute("style", galleria.getStyle(), "style");

        for(int i=0; i < dataCount; i++) {
            galleria.setRowIndex(i);

            writer.startElement("li", null);

            renderChildren(context, galleria);

            writer.endElement("li");
        }

        writer.endElement("ul");

        galleria.setRowIndex(-1);
    }

    public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Galleria galleria = (Galleria) component;
        String clientId = galleria.getClientId(context);

        writer.startElement("script", component);
        writer.writeAttribute("type", "text/javascript", null);

        writer.write("$(function() {");

		writer.write(galleria.resolveWidgetVar() + " = new PrimeFaces.widget.Galleria('" + clientId + "',{");

        writer.write("transition:'" + galleria.getEffect() + "'");
        writer.write(",transition_speed:" + galleria.getEffectSpeed());

        writer.write("});});");

        writer.endElement("script");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}