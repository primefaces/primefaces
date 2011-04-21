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
package org.primefaces.component.layout;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class LayoutUnitRenderer extends CoreRenderer {

    @Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		LayoutUnit unit = (LayoutUnit) component;
        String selector = "ui-layout-" + unit.getLocation();
		
		writer.startElement("div", component);
		writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", "ui-layout-unit ui-widget ui-widget-content ui-corner-all " + selector, "styleClass");

        if(unit.getHeader() != null) {
            encodeRegion(context, unit.getHeader(), Layout.UNIT_HEADER_CLASS, Layout.UNIT_HEADER_TITLE_CLASS);
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", Layout.UNIT_CONTENT_CLASS, null);
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        LayoutUnit unit = (LayoutUnit) component;

        writer.endElement("div");

        if(unit.getFooter() != null) {
            encodeRegion(context, unit.getHeader(), Layout.UNIT_FOOTER_CLASS, Layout.UNIT_FOOTER_TITLE_CLASS);
        }
		
		writer.endElement("div");
	}

	public void encodeRegion(FacesContext context, String text, String regionClass, String titleClass) throws IOException {
		ResponseWriter writer = context.getResponseWriter();

		writer.startElement("div", null);
        writer.writeAttribute("class", regionClass, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", titleClass, null);
        writer.write(text);
        writer.endElement("div");

        writer.endElement("div");
	}
}