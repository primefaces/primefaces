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
