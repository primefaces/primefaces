/*
 * Copyright 2009-2012 Prime Teknoloji.
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
        String styleClass = galleria.getStyleClass();
        styleClass = styleClass == null ? "ui-galleria" : "ui-galleria " + styleClass;
        
        writer.startElement("ul", null);
        writer.writeAttribute("id", galleria.getClientId(context), null);
        writer.writeAttribute("class", styleClass, "style");
        if(galleria.getStyle() !=  null)
            writer.writeAttribute("style", galleria.getStyle(), "style");

        if(galleria.getVar() == null) {
            for(UIComponent child : galleria.getChildren()) {
                if(child.isRendered()) {
                    writer.startElement("li", null);
                    child.encodeAll(context);
                    writer.endElement("li");
                }
            }
        }
        else {
            for(int i=0; i < galleria.getRowCount(); i++) {
                galleria.setRowIndex(i);

                writer.startElement("li", null);
                renderChildren(context, galleria);
                writer.endElement("li");
            }

            galleria.setRowIndex(-1);
        }

        writer.endElement("ul");
    }

    public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Galleria galleria = (Galleria) component;
        String clientId = galleria.getClientId(context);

        startScript(writer, clientId);

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('Galleria','" + galleria.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",panel_animation:'" + galleria.getEffect() + "'");
        writer.write(",transition_speed:" + galleria.getEffectSpeed());
        writer.write(",transition_interval:" + galleria.getTransitionInterval());

        if(galleria.getPanelWidth() != 600) writer.write(",panel_width:" + galleria.getPanelWidth());
        if(galleria.getPanelHeight() != 400) writer.write(",panel_height:" + galleria.getPanelHeight());
        if(galleria.getFrameWidth() != 60) writer.write(",frame_width:" + galleria.getFrameWidth());
        if(galleria.getFrameHeight() != 40) writer.write(",frame_height:" + galleria.getFrameHeight());
        if(galleria.getFilmstripStyle() != null) writer.write(",filmstrip_style:'" + galleria.getFilmstripStyle() + "'");
        if(galleria.getFilmstripPosition() != null) writer.write(",filmstrip_position:'" + galleria.getFilmstripPosition() + "'");
        if(!galleria.isShowFilmstrip()) writer.write(",show_filmstrip:false");
        if(galleria.isShowCaptions()) writer.write(",show_captions:true");
        if(galleria.isShowOverlays()) writer.write(",show_overlays:true");

        writer.write("},'galleria');});");

        endScript(writer);
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