/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.visit;

import javax.el.ValueExpression;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;

public class ResetInputVisitCallback implements VisitCallback {

    public static final ResetInputVisitCallback INSTANCE = new ResetInputVisitCallback();
    public static final ResetInputVisitCallback INSTANCE_CLEAR_MODEL = new ResetInputVisitCallback(true);

    private final boolean clearModel;

    public ResetInputVisitCallback() {
        this.clearModel = false;
    }

    public ResetInputVisitCallback(boolean clearModel) {
        this.clearModel = clearModel;
    }

    @Override
    public VisitResult visit(VisitContext context, UIComponent target) {
        if (target instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) target;
            input.resetValue();

            if (clearModel) {
                ValueExpression ve = target.getValueExpression("value");
                if (ve != null) {
                    ve.setValue(context.getFacesContext().getELContext(), null);
                }
            }
        }

        return VisitResult.ACCEPT;
    }
}
