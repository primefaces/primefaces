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
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.toolbar.Toolbar;

public class ToolbarRenderer extends org.primefaces.component.toolbar.ToolbarRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Toolbar toolbar = (Toolbar) component;
        ResponseWriter writer = context.getResponseWriter();
        String style = toolbar.getStyle();
        String styleClass = toolbar.getStyleClass();
        styleClass = styleClass == null ? Toolbar.MOBILE_CONTAINER_CLASS : Toolbar.MOBILE_CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", toolbar);
        writer.writeAttribute("id", toolbar.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeGroup(context, toolbar.getFacet("left"), Toolbar.MOBILE_LEFT_GROUP_CLASS);
        
        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-title", null);
        writer.endElement("span");
        
        encodeGroup(context, toolbar.getFacet("right"), Toolbar.MOBILE_RIGHT_GROUP_CLASS);
        
        renderDynamicPassThruAttributes(context, toolbar);
        
        writer.endElement("div");
    }
    
    protected void encodeGroup(FacesContext context, UIComponent group, String styleClass) throws IOException {
        if (group == null) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);
        
        writer.startElement("div", null);
        writer.writeAttribute("class", Toolbar.MOBILE_GROUP_CONTAINER_CLASS, null);
        group.encodeAll(context);
        writer.endElement("div");
        
        writer.endElement("div");
    }
}
