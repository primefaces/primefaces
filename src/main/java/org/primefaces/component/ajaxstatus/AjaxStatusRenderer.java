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
package org.primefaces.component.ajaxstatus;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class AjaxStatusRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		AjaxStatus status = (AjaxStatus) component;

		encodeMarkup(context, status);
		encodeScript(context, status);
	}

	protected void encodeScript(FacesContext context, AjaxStatus status) throws IOException {
		String clientId = status.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("AjaxStatus", status.resolveWidgetVar(), clientId);

        wb.callback(AjaxStatus.START, AjaxStatus.CALLBACK_SIGNATURE, status.getOnstart())
            .callback(AjaxStatus.ERROR, AjaxStatus.CALLBACK_SIGNATURE, status.getOnerror())
            .callback(AjaxStatus.SUCCESS, AjaxStatus.CALLBACK_SIGNATURE, status.getOnsuccess())
            .callback(AjaxStatus.COMPLETE, AjaxStatus.CALLBACK_SIGNATURE, status.getOncomplete());
        
        wb.finish();
	}

	protected void encodeMarkup(FacesContext context, AjaxStatus status) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = status.getClientId(context);
		
		writer.startElement("div", null);
		writer.writeAttribute("id", clientId, null);
		
		if(status.getStyle() != null)  writer.writeAttribute("style", status.getStyle(), "style");
		if(status.getStyleClass() != null)  writer.writeAttribute("class", status.getStyleClass(), "styleClass");
		
		for(String event : AjaxStatus.EVENTS) {
			UIComponent facet = status.getFacet(event);

            if(facet != null) {
                encodeFacet(context, clientId, facet, event, true);
            }
		}

        UIComponent defaultFacet = status.getFacet(AjaxStatus.DEFAULT);
        if(defaultFacet != null) {
            encodeFacet(context, clientId, defaultFacet, AjaxStatus.DEFAULT, false);
        }
		
		writer.endElement("div");
	}

    protected void encodeFacet(FacesContext context, String clientId, UIComponent facet, String facetName, boolean hidden) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_" + facetName, null);
        if(hidden) {
            writer.writeAttribute("style", "display:none", null);
        }

        renderChild(context, facet);

        writer.endElement("div");
    }
}