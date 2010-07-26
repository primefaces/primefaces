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
package org.primefaces.component.effect;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class EffectRenderer extends CoreRenderer {

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Effect effect = (Effect) component;
		String parentClientId = effect.getParent().getClientId(facesContext);
		String effectedComponentClientId = null;
		
		if(effect.getFor() != null) {
			UIComponent target = effect.findComponent(effect.getFor());
			if(target != null)
				effectedComponentClientId = target.getClientId(facesContext);
			else
				throw new FacesException("Cannot find component \"" + effect.getFor() + "\" in view.");
		} else {
			effectedComponentClientId = parentClientId;
		}
		
		String animation = getEffectBuilder(effect, effectedComponentClientId).build();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		
		if(effect.getEvent().equals("load")) {
			writer.write(animation);
		} else {
			writer.write("YAHOO.util.Event.addListener('" + parentClientId + "', '" + effect.getEvent() + "', " +
					"function(e) {" + animation + "});\n");
		}
		
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