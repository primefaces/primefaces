/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.effect;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class EffectRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Effect effect = (Effect) component;
        String clientId = effect.getClientId(context);
		String parentClientId = effect.getParent().getClientId(context);
		String effectedComponentClientId = null;
        String event = effect.getEvent();
        String timeoutKey = "window.effect_" + clientId.replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
		
		if(effect.getFor() != null) {
			UIComponent target = effect.findComponent(effect.getFor());
			if(target != null)
				effectedComponentClientId = target.getClientId(context);
			else
				throw new FacesException("Cannot find component \"" + effect.getFor() + "\" in view.");
		} else {
			effectedComponentClientId = parentClientId;
		}
		
		String animation = getEffectBuilder(effect, effectedComponentClientId).build();
		
		writer.startElement("script", null);
        writer.writeAttribute("id", clientId + "_script", event);
		writer.writeAttribute("type", "text/javascript", null);
        
		writer.write("$(function() {");

        writer.write("clearTimeout(" + timeoutKey + ");");
        
        writer.write(timeoutKey + " = setTimeout(function() {");

		if(event.equals("load")) {
			writer.write(animation);
		} else {
            writer.write("$(PrimeFaces.escapeClientId('" + parentClientId + "'))");
            writer.write(".bind('" + effect.getEvent() + "', function() {" + animation + "});");
		}

        writer.write("}," + effect.getDelay() + ");");

        writer.write("$(PrimeFaces.escapeClientId('" + clientId + "_script')).remove()");
        
        writer.write("});");
        
		writer.endElement("script");
	}
	
	private EffectBuilder getEffectBuilder(Effect effect, String effectedComponentClientId) {
		EffectBuilder effectBuilder = new EffectBuilder(effect.getType(), effectedComponentClientId);
		
		for(UIComponent child : effect.getChildren()) {
			if(child instanceof UIParameter) {
				UIParameter param = (UIParameter) child;
				
				effectBuilder.withOption(param.getName(), (String) param.getValue());		//TODO: Use converter
			}
		}
		
		effectBuilder.atSpeed(effect.getSpeed());
		
		return effectBuilder;
	}
}