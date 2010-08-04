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
package org.primefaces.component.behavior.ajax;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.facelets.BehaviorConfig;
import javax.faces.view.facelets.BehaviorHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRuleset;
import org.primefaces.component.resource.Resource;
import org.primefaces.facelets.MethodRule;

public class AjaxBehaviorHandler extends BehaviorHandler {

    public AjaxBehaviorHandler(BehaviorConfig config) {
        super(config);
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);

		metaRuleset.addRule(new MethodRule("listener", null, new Class[0]));
        
        //For backward compatibility, deprecated
        metaRuleset.addRule(new MethodRule("action", null, new Class[0]));
        metaRuleset.addRule(new MethodRule("actionListener", null, new Class[]{ActionEvent.class}));

		return metaRuleset;
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        super.apply(ctx, parent);

        FacesContext facesContext = FacesContext.getCurrentInstance();
		UIViewRoot viewroot = facesContext.getViewRoot();

        Resource resource = null;
        if(!resourceExists(facesContext, "/jquery/jquery.js")) {
			resource = (Resource) facesContext.getApplication().createComponent("org.primefaces.component.Resource");
			resource.setName("/jquery/jquery.js");
			viewroot.addComponentResource(facesContext, resource, "head");
		}
        if(!resourceExists(facesContext, "/primefaces/core/core.js")) {
			resource = (Resource) facesContext.getApplication().createComponent("org.primefaces.component.Resource");
			resource.setName("/primefaces/core/core.js");
			viewroot.addComponentResource(facesContext, resource, "head");
        }
    }

    private boolean resourceExists(FacesContext facesContext, String name) {
		java.util.List<UIComponent> resources = facesContext.getViewRoot().getComponentResources(facesContext, "head");
		for(UIComponent component : resources) {
			String value = component.toString();
			if(value != null && value.equals(name))
				return true;
		}
		return false;
	}
}