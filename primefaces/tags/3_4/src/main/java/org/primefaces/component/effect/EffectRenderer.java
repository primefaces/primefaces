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
package org.primefaces.component.effect;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
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
		String target = null;
        String source = component.getParent().getClientId(context);
        String event = effect.getEvent();
        int delay = effect.getDelay();
		
		if(effect.getFor() != null) {
			UIComponent _for = effect.findComponent(effect.getFor());
			if(_for != null)
				target = _for.getClientId(context);
			else
				throw new FacesException("Cannot find component \"" + effect.getFor() + "\" in view.");
		} else {
			target = source;
		}
		
		String animation = getEffectBuilder(effect, target).build();
		
        startScript(writer, clientId);
        
		writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('Effect','" + effect.resolveWidgetVar() + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",source:'" + source + "'");
        writer.write(",event:'" + event + "'");
        writer.write(",fn:function() {" + animation + "}");
        writer.write(",delay:" + delay);

        writer.write("});});");
        
		endScript(writer);
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