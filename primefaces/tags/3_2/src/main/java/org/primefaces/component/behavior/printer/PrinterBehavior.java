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
package org.primefaces.component.behavior.printer;

import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="printer/printer.js"),
    @ResourceDependency(library="primefaces", name="primefaces.js")
})
public class PrinterBehavior extends ClientBehaviorBase {
    
    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        FacesContext facesContext = behaviorContext.getFacesContext();
        UIComponent targetComponent = behaviorContext.getComponent().findComponent(target);
        if(targetComponent == null) {
            throw new FacesException("Cannot find component " + target + " in view.");
        }
        
        return "$(PrimeFaces.escapeClientId('" + targetComponent.getClientId(facesContext) + "')).jqprint();return false;";
    }
}
