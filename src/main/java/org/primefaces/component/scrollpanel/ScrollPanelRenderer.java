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
package org.primefaces.component.scrollpanel;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class ScrollPanelRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ScrollPanel panel = (ScrollPanel) component;

        encodeMarkup(context, panel);
        encodeScript(context, panel);
    }

    protected void encodeMarkup(FacesContext context, ScrollPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        String style = panel.getStyle();
        String styleClass = panel.getStyleClass();

        writer.startElement("div", panel);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("style", style, "style");
        if(styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }
        
        renderChildren(context, panel);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ScrollPanel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);

        writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

        writer.write("jQuery(function() {");

        writer.write(panel.resolveWidgetVar() + " = new PrimeFaces.widget.ScrollPanel('" + clientId + "',{");

        writer.write("scrollMode:'" + panel.getScrollMode() + "'");
        writer.write(",easing:'" + panel.getEasing() + "'");

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
