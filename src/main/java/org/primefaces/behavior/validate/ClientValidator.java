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
package org.primefaces.behavior.validate;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorContext;
import org.primefaces.component.api.InputHolder;

public class ClientValidator extends ClientBehaviorBase {
    
    private String event;
    private boolean disabled;
    
    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        if(disabled) {
           return null; 
        }
        
        UIComponent component = behaviorContext.getComponent();
        String target = (component instanceof InputHolder) ? "'" + ((InputHolder) component).getValidatableInputClientId() + "'" : "this";
        
        return "return PrimeFaces.vi(" + target + ")";
    }

    public String getEvent() {
        return event;
    }
    public void setEvent(String event) {
        this.event = event;
    }

    public boolean isDisabled() {
        return disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
