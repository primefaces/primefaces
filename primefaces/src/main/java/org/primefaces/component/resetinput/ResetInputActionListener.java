/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.resetinput;

import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.visit.ResetInputVisitCallback;

import java.io.Serializable;
import java.util.List;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;

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
        FacesContext context = event.getFacesContext();
        ELContext elContext = context.getELContext();
        VisitContext visitContext = VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_UNRENDERED);

        String expressions = target.getValue(elContext);
        boolean resetModel = false;
        if (clearModel != null) {
            resetModel = clearModel.isLiteralText()
                         ? Boolean.parseBoolean(clearModel.getValue(context.getELContext()).toString())
                         : (Boolean) clearModel.getValue(context.getELContext());
        }

        ResetInputVisitCallback visitCallback = resetModel
                                                ? ResetInputVisitCallback.INSTANCE_CLEAR_MODEL
                                                : ResetInputVisitCallback.INSTANCE;

        List<UIComponent> components = SearchExpressionUtils.contextlessResolveComponents(context, event.getComponent(), expressions);
        if (components != null && !components.isEmpty()) {
            for (int i = 0; i < components.size(); i++) {
                UIComponent component = components.get(i);
                component.visitTree(visitContext, visitCallback);
            }
        }
    }
}
