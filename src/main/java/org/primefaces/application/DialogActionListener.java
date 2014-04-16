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
package org.primefaces.application;

import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import org.primefaces.component.api.Widget;
import org.primefaces.context.RequestContext;
import org.primefaces.util.Constants;

public class DialogActionListener implements ActionListener {

    private ActionListener base;

    public DialogActionListener(ActionListener base) {
        this.base = base;
    }
        
    public void processAction(ActionEvent event) throws AbortProcessingException {
        UIComponent source = event.getComponent();
        RequestContext context = RequestContext.getCurrentInstance();
        Map<Object,Object> attrs = context.getAttributes();
        if(source instanceof Widget) {
            attrs.put(Constants.DIALOG_FRAMEWORK.SOURCE_WIDGET, ((Widget) source).resolveWidgetVar());
        }
        
        attrs.put(Constants.DIALOG_FRAMEWORK.SOURCE_COMPONENT, source.getClientId());
        
        base.processAction(event);
    }
    
}
