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
package org.primefaces.component.galleria;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
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
        String var = galleria.getVar();
        String style = galleria.getStyle();
        String styleClass = galleria.getStyleClass();
        styleClass = (styleClass == null) ? Galleria.CONTAINER_CLASS : Galleria.CONTAINER_CLASS + " " + styleClass;
        UIComponent content = galleria.getFacet("content");
        
        writer.startElement("div", component);
        writer.writeAttribute("id", galleria.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if(style != null) {
            writer.writeAttribute("style", style, "style");
        }
        
        writer.startElement("ul", component);
        writer.writeAttribute("class", Galleria.PANEL_WRAPPER_CLASS, null);
        
        if(var == null) {
            for(UIComponent child : galleria.getChildren()) {
                if(child.isRendered()) {
                    writer.startElement("li", null);
                    writer.writeAttribute("class", Galleria.PANEL_CLASS, null);
                    child.encodeAll(context);
                    
                    if(content != null) {
                        writer.startElement("div", null);
                        writer.writeAttribute("class", Galleria.PANEL_CONTENT_CLASS, null);
                        content.encodeAll(context);
                        writer.endElement("div");
                    }
                    
                    writer.endElement("li");
                }
            }
        }
        else {
            Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
            Collection<?> value = (Collection<?>) galleria.getValue();
            if(value != null) {
                for(Iterator<?> it = value.iterator(); it.hasNext();) {
                    requestMap.put(var, it.next());

                    writer.startElement("li", null);
                    writer.writeAttribute("class", Galleria.PANEL_CLASS, null);
                    renderChildren(context, galleria);
                    
                    if(content != null) {
                        writer.startElement("div", null);
                        writer.writeAttribute("class", Galleria.PANEL_CONTENT_CLASS, null);
                        content.encodeAll(context);
                        writer.endElement("div");
                    }
                    
                    writer.endElement("li");
                }
            }

            requestMap.remove(var);
        }
        
        writer.endElement("ul");
                
        writer.endElement("div");
    }

    public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        Galleria galleria = (Galleria) component;
        String clientId = galleria.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        
        if (context.isPostback()) {
        	wb.initWithDomReady("Galleria", galleria.resolveWidgetVar(), clientId, "galleria");
        } else {
        	wb.initWithWindowLoad("Galleria", galleria.resolveWidgetVar(), clientId, "galleria");
        }

        wb.attr("showFilmstrip", galleria.isShowFilmstrip(), true)
                .attr("frameWidth", galleria.getFrameWidth(), 60)
                .attr("frameHeight", galleria.getFrameHeight(), 40)
                .attr("autoPlay", galleria.isAutoPlay(), true)
                .attr("transitionInterval", galleria.getTransitionInterval(), 4000)
                .attr("effect", galleria.getEffect(), "fade")
                .attr("effectSpeed", galleria.getEffectSpeed(), 500)
                .attr("showCaption", galleria.isShowCaption(), false)
                .attr("panelWidth", galleria.getPanelWidth(), Integer.MIN_VALUE)
                .attr("panelHeight", galleria.getPanelHeight(), Integer.MIN_VALUE)
                .attr("custom", (galleria.getFacet("content") != null));

        wb.finish();
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