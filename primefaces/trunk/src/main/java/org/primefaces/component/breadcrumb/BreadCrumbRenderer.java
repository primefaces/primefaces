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
package org.primefaces.component.breadcrumb;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.menu.BaseMenuRenderer;

import org.primefaces.component.menuitem.MenuItem;

public class BreadCrumbRenderer extends BaseMenuRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		BreadCrumb breadCrumb = (BreadCrumb) component;
		
		if(breadCrumb.isDynamic()) {
			breadCrumb.buildMenuFromModel();
		}

		encodeMarkup(context, breadCrumb);
		encodeScript(context, breadCrumb);
	}

    @Override
	protected void encodeScript(FacesContext context, AbstractMenu menu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        BreadCrumb breadCrumb = (BreadCrumb) menu;
		String clientId = breadCrumb.getClientId(context);
        boolean preview = breadCrumb.isPreview();
        int childCount = breadCrumb.getChildCount();
        int expandedEndItems = preview ? breadCrumb.getExpandedEndItems() : childCount;
        int expandedBeginningItems = preview ? breadCrumb.getExpandedBeginningItems() : childCount;
        
        startScript(writer, clientId);
        
        writer.write("PrimeFaces.cw('Breadcrumb','" + breadCrumb.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");

        writer.write(",endElementsToLeaveOpen:" + expandedEndItems);
        writer.write(",beginingElementsToLeaveOpen:" + expandedBeginningItems);
      
		if(breadCrumb.getPreviewWidth() != 5) writer.write(",previewWidth:" + breadCrumb.getPreviewWidth());
		if(breadCrumb.getExpandEffectDuration() != 800) writer.write(",timeExpansionAnimation:" + breadCrumb.getExpandEffectDuration());
		if(breadCrumb.getCollapseEffectDuration() != 500) writer.write(",timeCompressionAnimation:" + breadCrumb.getCollapseEffectDuration());
		if(breadCrumb.getInitialCollapseEffectDuration() != 600) writer.write(",timeInitialCollapse:" + breadCrumb.getInitialCollapseEffectDuration());
		        
		writer.write("},'breadcrumb');");

		endScript(writer);
	}

	protected void encodeMarkup(FacesContext context, AbstractMenu menu) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        BreadCrumb breadCrumb = (BreadCrumb) menu;
		String clientId = breadCrumb.getClientId(context);
		String defaultStyleClass = "ui-breadcrumb ui-module ui-widget ui-widget-header ui-corner-all";
		String styleClass = breadCrumb.getStyleClass() == null ? defaultStyleClass : defaultStyleClass + " " + breadCrumb.getStyleClass();

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", styleClass, null);
		if(breadCrumb.getStyle() != null) writer.writeAttribute("style", breadCrumb.getStyle(), null);

		writer.startElement("ul", null);

        for(Iterator<UIComponent> iterator = breadCrumb.getChildren().iterator(); iterator.hasNext();) {
            
            UIComponent child = iterator.next();

			if(child.isRendered() && child instanceof MenuItem) {
				writer.startElement("li", null);

				encodeMenuItem(context, (MenuItem) child);

                if(iterator.hasNext()) {
                    writer.startElement("span", null);
                    writer.writeAttribute("class", "ui-breadcrumb-chevron ui-icon ui-icon-triangle-1-e", null);
                    writer.endElement("span");
                }

				writer.endElement("li");
			}
		}

		writer.endElement("ul");
		
		writer.endElement("div");
	}
	
    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		// Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}