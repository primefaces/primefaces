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
package org.primefaces.component.contextmenu;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

import org.primefaces.component.tieredmenu.TieredMenuRenderer;

public class ContextMenuRenderer extends TieredMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        ContextMenu menu = (ContextMenu) abstractMenu;
		String widgetVar = menu.resolveWidgetVar();
		String clientId = menu.getClientId(context);
		UIComponent target = findTarget(context, menu);
        String event = menu.getEvent();
		
        startScript(writer, clientId);

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('ContextMenu','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");     
        
        if(target != null) {
            String targetWidgetVar = ((Widget) target).resolveWidgetVar();
            
            writer.write(",target:'" + target.getClientId(context) + "'");
            writer.write(",type:'" + target.getClass().getSimpleName() + "'");
            writer.write(",targetWidgetVar:'" + targetWidgetVar + "'");
        }
        
        if(menu.getNodeType() != null) writer.write(",nodeType:'" + menu.getNodeType() + "'");
        if(event != null) writer.write(",event:'" + event + "'");
        
        writer.write("});});");
		
		endScript(writer);
	}
	
    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException{
        ContextMenu menu = (ContextMenu) abstractMenu;
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? ContextMenu.CONTAINER_CLASS : ContextMenu.CONTAINER_CLASS + " " + styleClass;
        
        encodeMenu(context, menu, style, styleClass, "menu");
	}

    protected UIComponent findTarget(FacesContext context, ContextMenu menu) {
		String _for = menu.getFor();

		if(_for != null) {
			UIComponent forComponent = menu.findComponent(_for);
			if(forComponent == null) {
				throw new FacesException("Cannot find component '" + _for + "' in view.");
            }
			 
            return forComponent;   
		}
        else {
            return null;
        }
	}
}