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
import org.primefaces.util.WidgetBuilder;

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
        String style = galleria.getStyle();
        String styleClass = galleria.getStyleClass();
        styleClass = (styleClass == null) ? Galleria.CONTAINER_CLASS : Galleria.CONTAINER_CLASS + " " + styleClass; 
        
        writer.startElement("div", component);
        writer.writeAttribute("id", galleria.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        writer.startElement("div", component);
        writer.writeAttribute("class", Galleria.PANEL_WRAPPER_CLASS, null);
        
        if(galleria.getVar() == null) {
            for(UIComponent child : galleria.getChildren()) {
                if(child.isRendered()) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", Galleria.PANEL_CLASS, null);
                    child.encodeAll(context);
                    writer.endElement("div");
                }
            }
        }
        else {
            for(int i=0; i < galleria.getRowCount(); i++) {
                galleria.setRowIndex(i);

                writer.startElement("div", null);
                writer.writeAttribute("class", Galleria.PANEL_CLASS, null);
                renderChildren(context, galleria);
                writer.endElement("div");
            }

            galleria.setRowIndex(-1);
        }
        
        writer.endElement("div");
                
        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        Galleria galleria = (Galleria) component;
        String clientId = galleria.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.widget("Galleria", galleria.resolveWidgetVar(), clientId, "galleria", true);
        
        wb.attr("showFilmstrip", galleria.isShowFilmstrip(), true)
                .attr("frameWidth", galleria.getFrameWidth(), 60)
                .attr("frameHeight", galleria.getFrameHeight(), 40)
                .attr("autoPlay", galleria.isAutoPlay(), true)
                .attr("transitionInterval", galleria.getTransitionInterval(), 4000)
                .attr("effect", galleria.getEffect(), "fade")
                .attr("effectSpeed", galleria.getEffectSpeed(), 500);

        startScript(writer, clientId);
        writer.write(wb.build());
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