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
package org.primefaces.component.collector;

import java.util.Collection;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

public class Collector implements ActionListener, StateHolder {

    private ValueExpression addTo;
    private ValueExpression removeFrom;
    private ValueExpression value;
    private ValueExpression unique;
    private boolean _transient;

    public Collector() {
    }

    public Collector(ValueExpression addTo, ValueExpression removeFrom, ValueExpression value, ValueExpression unique) {
        this.addTo = addTo;
        this.removeFrom = removeFrom;
        this.value = value;
        this.unique = unique;
    }

    @Override
    public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
        if (value == null) {
            throw new AbortProcessingException("Value has not been set");
        }

        ELContext elContext = FacesContext.getCurrentInstance().getELContext();

        Object val = value.getValue(elContext);

        if (addTo != null) {
            Collection collection = (Collection) addTo.getValue(elContext);
            Object uniqueValue = (unique != null) ? unique.getValue(elContext) : null;
            boolean checkUniqueness = (uniqueValue == null) ? true : Boolean.parseBoolean(uniqueValue.toString());

            if (checkUniqueness) {
                if (!collection.contains(val)) {
                    collection.add(val);
                }
            }
            else {
                collection.add(val);
            }
        }
        else if (removeFrom != null) {
            Collection collection = (Collection) removeFrom.getValue(elContext);
            collection.remove(val);
        }
        else {
            throw new IllegalArgumentException("Specify either addTo or removeFrom as collection reference");
        }
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[4];
        state[0] = addTo;
        state[1] = removeFrom;
        state[2] = value;
        state[3] = unique;

        return state;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object[] values = (Object[]) state;
        addTo = (ValueExpression) values[0];
        removeFrom = (ValueExpression) values[1];
        value = (ValueExpression) values[2];
        unique = (ValueExpression) values[3];
    }

    @Override
    public boolean isTransient() {
        return _transient;
    }

    @Override
    public void setTransient(boolean _transient) {
        this._transient = _transient;
    }

    public ValueExpression getAddTo() {
        return addTo;
    }

    public void setAddTo(ValueExpression addTo) {
        this.addTo = addTo;
    }

    public ValueExpression getRemoveFrom() {
        return removeFrom;
    }

    public void setRemoveFrom(ValueExpression removeFrom) {
        this.removeFrom = removeFrom;
    }

    public ValueExpression getValue() {
        return value;
    }

    public void setValue(ValueExpression value) {
        this.value = value;
    }

    public ValueExpression getUnique() {
        return unique;
    }

    public void setUnique(ValueExpression unique) {
        this.unique = unique;
    }
}
