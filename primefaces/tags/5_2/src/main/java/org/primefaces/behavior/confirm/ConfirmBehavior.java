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
package org.primefaces.behavior.confirm;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.Confirmable;
import org.primefaces.json.JSONObject;

public class ConfirmBehavior extends ClientBehaviorBase {

    private String header;
    private String message;
    private String icon;

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        FacesContext context = behaviorContext.getFacesContext();
        UIComponent component = behaviorContext.getComponent();
        String source = component.getClientId(context);
        String headerText = JSONObject.quote(this.getHeader());
        String messageText = JSONObject.quote(this.getMessage());
        
        if(component instanceof Confirmable) {
            String sourceProperty = (source == null) ? "source:this" : "source:\"" + source + "\"";
            String script = "PrimeFaces.confirm({" + sourceProperty + ",header:" + headerText + ",message:" + messageText + ",icon:\"" + getIcon()  + "\"});return false;";
            ((Confirmable) component).setConfirmationScript(script);

            return null;
        }
        else {
            throw new FacesException("Component " + source + " is not a Confirmable. ConfirmBehavior can only be attached to components that implement org.primefaces.component.api.Confirmable interface");
        }
        
    }
    
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }    
}
