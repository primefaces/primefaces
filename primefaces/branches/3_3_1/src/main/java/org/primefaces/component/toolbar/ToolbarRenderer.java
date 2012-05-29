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
package org.primefaces.component.toolbar;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.separator.Separator;
import org.primefaces.renderkit.CoreRenderer;

public class ToolbarRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Toolbar toolbar = (Toolbar) component;
        ResponseWriter writer = context.getResponseWriter();
        String style = toolbar.getStyle();
        String styleClass = toolbar.getStyleClass();
        styleClass = styleClass == null ? Toolbar.CONTAINER_CLASS : Toolbar.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", toolbar);
        writer.writeAttribute("id", toolbar.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        for(UIComponent child : toolbar.getChildren()) {
            if(child.isRendered() && child instanceof ToolbarGroup) {
                ToolbarGroup group = (ToolbarGroup) child;

                String defaultGroupClass = "ui-toolbar-group-" + group.getAlign();
                String groupClass = group.getStyleClass();
                String groupStyle = group.getStyle();
                groupClass = groupClass == null ? defaultGroupClass : defaultGroupClass + " " + groupClass;

                writer.startElement("div", null);
                writer.writeAttribute("class", groupClass, style);
                if(groupStyle != null) {
                    writer.writeAttribute("style", groupStyle, null);
                }

                for(UIComponent groupChild : group.getChildren()) {
                    if(groupChild instanceof Separator && groupChild.isRendered())
                        encodeSeparator(context, (Separator) groupChild);
                    else
                        groupChild.encodeAll(context);
                }

                writer.endElement("div");
            }
        }

        writer.endElement("div");
    }
    
    public void encodeSeparator(FacesContext context, Separator separator) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = separator.getStyle();
        String styleClass = separator.getStyleClass();
        styleClass = styleClass == null ? Toolbar.SEPARATOR_CLASS : Toolbar.SEPARATOR_CLASS + " " + styleClass;

        writer.startElement("span", null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        writer.startElement("span", null);
        writer.writeAttribute("class", Toolbar.SEPARATOR_ICON_CLASS, null);
        writer.endElement("span");
        
        writer.endElement("span");
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		//Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}