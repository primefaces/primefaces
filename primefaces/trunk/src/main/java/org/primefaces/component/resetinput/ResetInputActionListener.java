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
package org.primefaces.component.resetinput;

import java.util.EnumSet;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import org.primefaces.visit.ResetInputVisitCallback;

public class ResetInputActionListener implements ActionListener {
    
    private ValueExpression target;
    
    public ResetInputActionListener(ValueExpression target) {
		this.target = target;
	}

    public void processAction(ActionEvent event) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
		ELContext elContext = context.getELContext();
        EnumSet<VisitHint> hints = EnumSet.of(VisitHint.SKIP_UNRENDERED);
        VisitContext visitContext = VisitContext.createVisitContext(context, null, hints);
        VisitCallback visitCallback = new ResetInputVisitCallback();
        
        String targetIds = (String) target.getValue(elContext);
        UIComponent source = event.getComponent();
        
        String[] ids = targetIds.split("[,\\s]+");
        for(String id : ids) {
            UIComponent targetComponent = source.findComponent(id);
            if(targetComponent == null) {
                throw new FacesException("Cannot find component with identifier \"" + id + "\" referenced from \"" + source.getClientId(context) + "\".");
            }
            
            targetComponent.visitTree(visitContext, visitCallback);
        }
    }
}
