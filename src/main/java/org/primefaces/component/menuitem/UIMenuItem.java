/*
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.menuitem;

import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;
import java.util.List;
import javax.faces.component.UIComponent;
import java.util.Map;
import java.util.HashMap;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.ActionEvent;
import javax.el.MethodExpression;
import javax.faces.component.UIParameter;
import org.primefaces.util.ComponentUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@ResourceDependencies({

})
public class UIMenuItem extends UIMenuItemBase implements org.primefaces.component.api.AjaxSource,org.primefaces.component.api.UIOutcomeTarget,org.primefaces.model.menu.MenuItem,org.primefaces.component.api.Confirmable,javax.faces.component.behavior.ClientBehaviorHolder,org.primefaces.component.api.PrimeClientBehaviorHolder {



    private final static String DEFAULT_EVENT = "click";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("click", null);
    }});

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
         return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

	public void decode(FacesContext facesContext) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = getClientId(facesContext);
		
		if(params.containsKey(clientId)) {
			this.queueEvent(new ActionEvent(this));
		}
	}
	
	public boolean shouldRenderChildren() {
		if(getChildCount() == 0)
			return false;
		else {
			for(UIComponent child : getChildren()) {
				if(! (child instanceof UIParameter) ) {
					return true;
				}
			}
		}
		
		return false;
	}

    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (this.getValueExpression(PropertyKeys.partialSubmit.toString()) != null); 
    }
    
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (this.getValueExpression(PropertyKeys.resetValues.toString()) != null);
    }

    public String getHref() {
        return this.getUrl();
    }

    public boolean isDynamic() {
        return false;
    }

    public Map<String, List<String>> getParams() {
        return ComponentUtils.getUIParams(this);
    }

    public String getCommand() {
        MethodExpression expr = super.getActionExpression();
        return expr != null ? expr.getExpressionString() : null;
    }
    
    public boolean isAjaxified() {
    	return getUrl() == null && getOutcome() == null && isAjax();
    }

    public void setParam(String key, Object value) {
        throw new UnsupportedOperationException("Use UIParameter component instead to add parameters.");
    }

    private String confirmationScript;
    
    public String getConfirmationScript() {
        return this.confirmationScript;
    }
    public void setConfirmationScript(String confirmationScript) {
        this.confirmationScript = confirmationScript;
    }
    public boolean requiresConfirmation() {
        return this.confirmationScript != null;
    }
}