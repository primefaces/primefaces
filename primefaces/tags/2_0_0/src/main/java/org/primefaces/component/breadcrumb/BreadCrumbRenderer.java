/*
 * Copyright 2009 Prime Technology.
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

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.renderkit.CoreRenderer;

public class BreadCrumbRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		BreadCrumb breadCrumb = (BreadCrumb) component;

		encodeScript(facesContext, breadCrumb);
		encodeMarkup(facesContext, breadCrumb);
	}

	private void encodeScript(FacesContext facesContext, BreadCrumb breadCrumb) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = breadCrumb.getClientId(facesContext);

		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write("PrimeFaces.onContentReady('" + clientId + "', function() {");
		writer.write("jQuery(PrimeFaces.escapeClientId('" + clientId + "')).jBreadCrumb({");
		writer.write("overlayClass:'pf-breadCrumb-chevron'");
		
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

		writer.write("});\n");

		writer.endElement("script");
	}

	private void encodeMarkup(FacesContext facesContext, BreadCrumb breadCrumb) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = breadCrumb.getClientId(facesContext);
		String styleClass = breadCrumb.getStyleClass() == null ? "pf-breadCrumb pf-module" : "pf-breadCrumb pf-module " + breadCrumb.getStyleClass();

		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		writer.writeAttribute("class", styleClass, null);
		if(breadCrumb.getStyle() != null) writer.writeAttribute("style", breadCrumb.getStyle(), null);

		writer.startElement("ul", null);

		for (Iterator<UIComponent> iterator = breadCrumb.getChildren().iterator(); iterator.hasNext();) {
			UIComponent child = iterator.next();
			
			if(child.isRendered() && child instanceof MenuItem) {
				MenuItem menuItem = (MenuItem) child;
				
				writer.startElement("li", null);

				writer.startElement("a", null);
				writer.writeAttribute("href", menuItem.getUrl(), null);

				if(menuItem.getTarget() != null) writer.writeAttribute("target", menuItem.getTarget(), null);
				if(menuItem.getOnclick() != null) writer.writeAttribute("onclick", menuItem.getOnclick(), null);
				if(menuItem.getLabel() != null) writer.write(menuItem.getLabel());

				writer.endElement("a");

				writer.endElement("li");
			}
		}

		writer.endElement("ul");
		
		writer.endElement("div");
	}

	public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
		// Do nothing
	}

	public boolean getRendersChildren() {
		return true;
	}
}