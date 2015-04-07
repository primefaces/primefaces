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
package org.primefaces.component.fieldset;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class FieldsetRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Fieldset fieldset = (Fieldset) component;
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = fieldset.getClientId(context);
        String toggleStateParam = clientId + "_collapsed";
        
        if(params.containsKey(toggleStateParam)) {
            fieldset.setCollapsed(Boolean.valueOf(params.get(toggleStateParam)));
        }
        
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Fieldset fieldset = (Fieldset) component;

        encodeMarkup(context, fieldset);
        encodeScript(context, fieldset);
    }

    protected void encodeMarkup(FacesContext context, Fieldset fieldset) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = fieldset.getClientId(context);
        String widgetVar = fieldset.resolveWidgetVar();
        boolean toggleable = fieldset.isToggleable();
        
        String styleClass = toggleable ? Fieldset.TOGGLEABLE_FIELDSET_CLASS : Fieldset.FIELDSET_CLASS;
        if(fieldset.isCollapsed()) {
            styleClass = styleClass + " ui-hidden-container";
        }
        if(fieldset.getStyleClass() != null) {
            styleClass = styleClass + " " + fieldset.getStyleClass();
        }

        writer.startElement("fieldset", fieldset);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(fieldset.getStyle() != null) {
            writer.writeAttribute("style", fieldset.getStyle(), "style");
        }
        
        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);

        encodeLegend(context, fieldset);

        encodeContent(context, fieldset);

        if(toggleable) {
            encodeStateHolder(context, fieldset);
        }

        writer.endElement("fieldset");
    }

    protected void encodeContent(FacesContext context, Fieldset fieldset) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Fieldset.CONTENT_CLASS, null);
        if(fieldset.isCollapsed()) {
            writer.writeAttribute("style", "display:none", null);
        }

        renderChildren(context, fieldset);
        
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Fieldset fieldset) throws IOException {
        String clientId = fieldset.getClientId(context);
        boolean toggleable = fieldset.isToggleable();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Fieldset", fieldset.resolveWidgetVar(), clientId);
        
        if(toggleable) {
            wb.attr("toggleable", true)
                .attr("collapsed", fieldset.isCollapsed())
                .attr("toggleSpeed", fieldset.getToggleSpeed());
        }
        
        encodeClientBehaviors(context, fieldset);
        
        wb.finish();
    }

    protected void encodeLegend(FacesContext context, Fieldset fieldset) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String legendText = fieldset.getLegend();
        UIComponent legend = fieldset.getFacet("legend");
        
        if(legendText != null || legend != null) {
            writer.startElement("legend", null);
            writer.writeAttribute("class", Fieldset.LEGEND_CLASS, null);

            if(fieldset.isToggleable()) {
                String togglerClass = fieldset.isCollapsed() ? Fieldset.TOGGLER_PLUS_CLASS : Fieldset.TOGGLER_MINUS_CLASS;
                               
                writer.startElement("span", null);
                writer.writeAttribute("class", togglerClass, null);
                writer.endElement("span");
            }

            if(legend != null)
                legend.encodeAll(context);
            else
                writer.write(fieldset.getLegend());
            
            writer.endElement("legend");
        }
    }

    protected void encodeStateHolder(FacesContext context, Fieldset fieldset) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String name = fieldset.getClientId(context) + "_collapsed";

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("value", String.valueOf(fieldset.isCollapsed()), null);
        writer.endElement("input");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}