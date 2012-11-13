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
package org.primefaces.component.lightbox;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class LightBoxRenderer extends CoreRenderer {

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		LightBox lb = (LightBox) component;

        encodeMarkup(context, lb);
        encodeScript(context, lb);
	}

	public void encodeMarkup(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		LightBox lb = (LightBox) component;
		String clientId = lb.getClientId(context);
        UIComponent inline = lb.getFacet("inline");
		
		writer.startElement("div", lb);
        writer.writeAttribute("id", clientId, "id");
        if(lb.getStyle() != null) writer.writeAttribute("style", lb.getStyle(), null);
        if(lb.getStyleClass() != null) writer.writeAttribute("class", lb.getStyleClass(), null);
        
        renderChildren(context, lb);
        
        if(inline != null) {
            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-lightbox-inline ui-helper-hidden", null);
            inline.encodeAll(context);
            writer.endElement("div");
        }
        
        writer.endElement("div");
	}
    
	public void encodeScript(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        LightBox lb = (LightBox) component;
		String clientId = lb.getClientId(context);
        
        String mode = "image";
        if(lb.getFacet("inline") != null)
            mode = "inline";
        else if(lb.isIframe())
            mode = "iframe";
        
        startScript(writer, clientId);
        writer.write("$(function() {");
        writer.write("PrimeFaces.cw('LightBox','" + lb.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",mode:'" + mode + "'");
        if(lb.getWidth() != null) writer.write(",width:'" + lb.getWidth() + "'");
        if(lb.getHeight() != null) writer.write(",height:'" + lb.getHeight() + "'");
        if(lb.isVisible()) writer.write(",visible:true");
        if(lb.getOnShow() != null) writer.write(",onShow:function(){" + lb.getOnShow() + "}");
        if(lb.getOnHide() != null) writer.write(",onHide:function(){" + lb.getOnHide() + "}");
        
		writer.write("});});");

		endScript(writer);
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