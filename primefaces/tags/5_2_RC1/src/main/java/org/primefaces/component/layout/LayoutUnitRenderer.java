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
package org.primefaces.component.layout;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class LayoutUnitRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		LayoutUnit unit = (LayoutUnit) component;
        boolean nesting = unit.isNesting();
        
        String defaultStyleClass = Layout.UNIT_CLASS + " ui-layout-" + unit.getPosition();
        String styleClass = unit.getStyleClass();
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;

		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", styleClass , "styleClass");
        if(unit.getStyle() != null) writer.writeAttribute("style", unit.getStyle() , "style");
        
        encodeHeader(context, unit);

        if(!nesting) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Layout.UNIT_CONTENT_CLASS, null);
        }

        renderChildren(context, unit);

        if(!nesting) {
            writer.endElement("div");
        }

        encodeFooter(context, unit);
		
		writer.endElement("div");
	}

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    public void encodeHeader(FacesContext context, LayoutUnit unit) throws IOException {
        String headerText = unit.getHeader();
        UIComponent headerFacet = unit.getFacet("header");
        
        if(headerText == null && headerFacet == null) {
            return;
        }
        
        ResponseWriter writer = context.getResponseWriter();
        Layout layout = (Layout) unit.getParent();

		writer.startElement("div", null);
        writer.writeAttribute("class", Layout.UNIT_HEADER_CLASS, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", Layout.UNIT_HEADER_TITLE_CLASS, null);
        
        if(headerFacet != null)
            headerFacet.encodeAll(context);
        else if(headerText != null)
            writer.writeText(headerText, null);
        
        writer.endElement("span");

        if(unit.isClosable()) {
            encodeIcon(context, "ui-icon-close", layout.getCloseTitle());
        }

        if(unit.isCollapsible()) {
            encodeIcon(context, unit.getCollapseIcon(), layout.getCollapseTitle());
        }

        writer.endElement("div");
	}

	public void encodeFooter(FacesContext context, LayoutUnit unit) throws IOException {
        String footerText = unit.getFooter();
        UIComponent footerFacet = unit.getFacet("footer");
        
        if(footerText == null && footerFacet == null) {
            return;
        }
        
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);
        writer.writeAttribute("class", Layout.UNIT_FOOTER_CLASS, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", Layout.UNIT_FOOTER_TITLE_CLASS, null);
        
        if(footerFacet != null)
            footerFacet.encodeAll(context);
        else if(footerText != null)
            writer.writeText(footerText, null);
        
        writer.endElement("div");

        writer.endElement("div");
	}

    protected void encodeIcon(FacesContext context, String iconClass, String title) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("href", "javascript:void(0)", null);
        writer.writeAttribute("class", Layout.UNIT_HEADER_ICON_CLASS, null);
        writer.writeAttribute("title", title, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon " + iconClass, null);
        writer.endElement("span");

        writer.endElement("a");
    }
}