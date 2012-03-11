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
package org.primefaces.component.tooltip;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class TooltipRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Tooltip tooltip = (Tooltip) component;
		
        encodeMarkup(context, tooltip);
		encodeScript(context, tooltip);
	}
    
    protected void encodeMarkup(FacesContext context, Tooltip tooltip) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = tooltip.getStyleClass();
        styleClass = styleClass == null ? Tooltip.CONTAINER_CLASS : Tooltip.CONTAINER_CLASS + " " + styleClass;
        
        writer.startElement("div", tooltip);
        writer.writeAttribute("id", tooltip.getClientId(context), null);
        writer.writeAttribute("class", styleClass, "styleClass");
        
        if(tooltip.getStyle() != null) 
            writer.writeAttribute("style", tooltip.getStyle(), "style");
        
        if(tooltip.getChildCount() > 0) {
            renderChildren(context, tooltip);
        }
        else {
            String valueToRender = ComponentUtils.getValueToRender(context, tooltip);
            if(valueToRender != null)
                writer.writeText(valueToRender, "value");
        }
        
        
        writer.endElement("div");
    }

	protected void encodeScript(FacesContext context, Tooltip tooltip) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        String clientId = tooltip.getClientId(context);
		String target = getTarget(context, tooltip);
		
        startScript(writer, clientId);
		
        writer.write("$(function() {");
			
        writer.write("PrimeFaces.cw('Tooltip','" + tooltip.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",target:'" + target + "'");
        
        if(tooltip.getShowEvent() != null) writer.write(",showEvent:'" + tooltip.getShowEvent() + "'");
        if(tooltip.getHideEvent() != null) writer.write(",hideEvent:'" + tooltip.getHideEvent() + "'");
        if(tooltip.getShowEffect() != null) writer.write(",showEffect:'" + tooltip.getShowEffect() + "'");
        if(tooltip.getHideEffect() != null) writer.write(",hideEffect:'" + tooltip.getHideEffect() + "'");
        		
		writer.write("});});");
		
		endScript(writer);
	}
	
	protected String getTarget(FacesContext context, Tooltip tooltip) {
        String _for = tooltip.getFor();

        if(_for != null) {
            UIComponent forComponent = tooltip.findComponent(_for);

            if(forComponent == null)
                throw new FacesException("Cannot find component \"" + _for + "\" in view.");
            else
                return forComponent.getClientId(context);

        } else {
            throw new FacesException("No target is defined for tooltip '" + tooltip.getClientId(context) + "'");
        }
	}

    @Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		//Rendering happens on encodeEnd
	}

    @Override
	public boolean getRendersChildren() {
		return true;
	}
}