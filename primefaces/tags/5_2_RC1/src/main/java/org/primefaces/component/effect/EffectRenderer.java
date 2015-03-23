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
package org.primefaces.component.effect;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class EffectRenderer extends CoreRenderer {

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		Effect effect = (Effect) component;
        String clientId = effect.getClientId(context);
        String source = component.getParent().getClientId(context);
        String event = effect.getEvent();
        int delay = effect.getDelay();
		
        UIComponent targetComponent = SearchExpressionFacade.resolveComponent(
        		context, effect, effect.getFor(), SearchExpressionFacade.Options.PARENT_FALLBACK);
        String target = targetComponent.getClientId(context);
		
		String animation = getEffectBuilder(effect, target).build();
		
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("Effect", effect.resolveWidgetVar(), clientId)
            .attr("source", source)
            .attr("event", event)
            .attr("delay", delay)
            .callback("fn", "function()", animation);
        
        wb.finish();
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