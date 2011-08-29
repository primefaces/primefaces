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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class BreadCrumbRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		BreadCrumb breadCrumb = (BreadCrumb) component;
		
		if(breadCrumb.isDynamic()) {
			breadCrumb.buildMenuFromModel();
		}

		encodeMarkup(facesContext, breadCrumb);
		encodeScript(facesContext, breadCrumb);
	}

	protected void encodeScript(FacesContext facesContext, BreadCrumb breadCrumb) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = breadCrumb.getClientId(facesContext);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("jQuery(PrimeFaces.escapeClientId('" + clientId + "')).jBreadCrumb({");
		writer.write("overlayClass:'ui-breadcrumb-chevron-overlay ui-icon ui-icon-triangle-1-e'");
		
		if(!breadCrumb.isPreview()) {
			int childCount = breadCrumb.getChildCount();
			writer.write(",endElementsToLeaveOpen:" + childCount);
			writer.write(",beginingElementsToLeaveOpen:" + childCount);
		} else {
			if(breadCrumb.getExpandedEndItems() != 1) writer.write(",endElementsToLeaveOpen:" + breadCrumb.getExpandedEndItems());
			if(breadCrumb.getExpandedBeginningItems() != 1) writer.write(",beginingElementsToLeaveOpen:" + breadCrumb.getExpandedBeginningItems());
		}
		
		if(breadCrumb.getPreviewWidth() != 5) writer.write(",previewWidth:" + breadCrumb.getPreviewWidth());
		if(breadCrumb.getExpandEffectDuration() != 800) writer.write(",timeExpansionAnimation:" + breadCrumb.getExpandEffectDuration());
		if(breadCrumb.getCollapseEffectDuration() != 500) writer.write(",timeCompressionAnimation:" + breadCrumb.getCollapseEffectDuration());
		if(breadCrumb.getInitialCollapseEffectDuration() != 600) writer.write(",timeInitialCollapse:" + breadCrumb.getInitialCollapseEffectDuration());
		        
		writer.write("});");

		writer.endElement("script");
	}

	protected void encodeMarkup(FacesContext facesContext, BreadCrumb breadCrumb) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = breadCrumb.getClientId(facesContext);
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

				encodeMenuItem(facesContext, (MenuItem) child);

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
	
	protected void encodeMenuItem(FacesContext facesContext, MenuItem menuItem) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		if(menuItem.shouldRenderChildren()) {
			renderChildren(facesContext, menuItem);
		} else {
			String clientId = menuItem.getClientId(facesContext);
			
			writer.startElement("a", null);
			writer.writeAttribute("id", clientId, null);
			
			if(menuItem.getStyle() != null) writer.writeAttribute("style", menuItem.getStyle(), null);
			if(menuItem.getStyleClass() != null) writer.writeAttribute("class", menuItem.getStyleClass(), null);
			
			if(menuItem.getUrl() != null) {
				writer.writeAttribute("href", getResourceURL(facesContext, menuItem.getUrl()), null);
				if(menuItem.getOnclick() != null) writer.writeAttribute("onclick", menuItem.getOnclick(), null);
				if(menuItem.getTarget() != null) writer.writeAttribute("target", menuItem.getTarget(), null);
			} else {
				writer.writeAttribute("href", "javascript:void(0)", null);
				
				UIComponent form = ComponentUtils.findParentForm(facesContext, menuItem);
				if(form == null) {
					throw new FacesException("Breadcrumb must be inside a form element");
				}
				
				String formClientId = form.getClientId(facesContext);
				String command = menuItem.isAjax() ? buildAjaxRequest(facesContext, menuItem) : buildNonAjaxRequest(facesContext, menuItem, formClientId, clientId);
				
				command = menuItem.getOnclick() == null ? command : menuItem.getOnclick() + ";" + command;
				
				writer.writeAttribute("onclick", command, null);
			}
			
			if(menuItem.getValue() != null) writer.write((String) menuItem.getValue());
			
			writer.endElement("a");
		}
	}

    @Override
	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		// Do nothing
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}