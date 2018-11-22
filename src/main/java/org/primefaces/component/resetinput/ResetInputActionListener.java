/**
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
package org.primefaces.component.resetinput;

import java.io.Serializable;
import java.util.List;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.ComponentUtils;
import org.primefaces.visit.ResetInputVisitCallback;

public class ResetInputActionListener implements ActionListener, Serializable {

    private static final long serialVersionUID = 1L;

    private ValueExpression target;
    private ValueExpression clearModel;

    /**
     * Don't remove - it's for serialization.
     */
    public ResetInputActionListener() {

    }

    public ResetInputActionListener(ValueExpression target, ValueExpression clearModel) {
        this.target = target;
        this.clearModel = clearModel;
    }

    @Override
    public void processAction(ActionEvent event) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        ELContext elContext = context.getELContext();
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        String expressions = (String) target.getValue(elContext);
        boolean resetModel = false;
        if (clearModel != null) {
            resetModel = clearModel.isLiteralText()
                         ? Boolean.parseBoolean(clearModel.getValue(context.getELContext()).toString())
                         : (Boolean) clearModel.getValue(context.getELContext());
        }

        ResetInputVisitCallback visitCallback = resetModel
                                                ? ResetInputVisitCallback.INSTANCE_CLEAR_MODEL
                                                : ResetInputVisitCallback.INSTANCE;

        List<UIComponent> components = SearchExpressionFacade.resolveComponents(context, event.getComponent(), expressions);
        for (int i = 0; i < components.size(); i++) {
            UIComponent component = components.get(i);
            component.visitTree(visitContext, visitCallback);
        }
    }
}
