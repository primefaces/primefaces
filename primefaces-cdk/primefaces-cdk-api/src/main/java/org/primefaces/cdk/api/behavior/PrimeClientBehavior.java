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
package org.primefaces.cdk.api.behavior;

import org.primefaces.cdk.api.PrimePropertyKeys;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.state.ValueExpressionStateHelper;

import jakarta.el.ValueExpression;
import jakarta.faces.component.StateHelper;
import jakarta.faces.component.behavior.ClientBehaviorBase;
import jakarta.faces.context.FacesContext;

public abstract class PrimeClientBehavior extends ClientBehaviorBase {

    private StateHelper stateHelper;

    public PrimeClientBehavior() {
        super();
    }

    @Property(description = "A String identifying the type of event the Ajax action will apply to."
            + " If specified, it must be one of the events supported by the component the Ajax behavior is being applied to."
            + " For HTML components this would be the set of supported DOM events for the component, plus \"action\" for Faces ActionSource"
            + " components and \"valueChange\" for Faces EditableValueHolder components. If not specified, the default event is determined for the component."
            + " The DOM event name is the actual DOM event name (for example: \"click\") as opposed to (for example: \"onclick\").")
    public String getEvent() {
        throw new IllegalArgumentException(); // just for documentation
    }

    public StateHelper getStateHelper() {
        return getStateHelper(true);
    }

    public StateHelper getStateHelper(boolean create) {
        if (stateHelper == null && create) {
            stateHelper = new ValueExpressionStateHelper();
        }

        return stateHelper;
    }

    public ValueExpression getValueExpression(String name) {
        if (name == null) {
            throw new NullPointerException();
        }

        return ((ValueExpressionStateHelper) getStateHelper()).getBinding(name);
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context");
        }

        Object[] values;

        Object superState = super.saveState(context);

        if (initialStateMarked()) {
            if (superState == null) {
                values = null;
            }
            else {
                values = new Object[]{superState};
            }
        }
        else {
            if (stateHelper == null) {
                values = new Object[1];
                values[0] = superState;
            }
            else {
                values = new Object[2];
                values[0] = superState;
                values[1] = stateHelper.saveState(context);
            }
        }

        return values;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if (context == null) {
            throw new IllegalArgumentException("context");
        }

        if (state != null) {
            Object[] values = (Object[]) state;
            super.restoreState(context, values[0]);

            if (values.length != 1) {
                getStateHelper().restoreState(context, values[1]);

                // If we saved state last time, save state again next time.
                clearInitialState();
            }
        }
    }

    public abstract PrimePropertyKeys[] getPropertyKeys();
}
