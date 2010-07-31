/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.fieldset;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class FieldsetRenderer extends CoreRenderer {

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Fieldset fieldset = (Fieldset) component;
        String styleClass = "ui-fieldset ui-widget ui-widget-content ui-corner-all";
        String legendText = fieldset.getLegend();
        UIComponent legend = fieldset.getFacet("legend");

        if(fieldset.getStyleClass() != null) {
            styleClass = styleClass + " " + fieldset.getStyleClass();
        }
       
        writer.startElement("fieldset", component);
        writer.writeAttribute("id", fieldset.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(fieldset.getStyle() != null) {
            writer.writeAttribute("style", fieldset.getStyle(), "style");
        }

        if(legendText != null || legend != null) {
            writer.startElement("legend", component);
            writer.writeAttribute("class", "ui-fieldset-legend ui-widget-header ui-corner-all", null);
            
            if (legend != null)
                legend.encodeAll(context);
            else
                writer.write(fieldset.getLegend());

            writer.endElement("legend");
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.endElement("fieldset");
    }
}