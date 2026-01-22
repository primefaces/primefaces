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
package org.primefaces.validate.base;

import org.primefaces.el.ValueExpressionStateHelper;

import jakarta.faces.component.PartialStateHolder;
import jakarta.faces.component.StateHelper;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;

public abstract class AbstractPrimeValidator implements Validator, PartialStateHolder {
    private StateHelper stateHelper;

    private boolean transientFlag = false;
    private boolean initialState = false;

    public StateHelper getStateHelper() {
        return getStateHelper(true);
    }

    public StateHelper getStateHelper(boolean create) {
        if (stateHelper == null && create) {
            stateHelper = new ValueExpressionStateHelper();
        }

        return stateHelper;
    }

    @Override
    public boolean isTransient() {
        return transientFlag;
    }

    @Override
    public void setTransient(boolean transientFlag) {
        this.transientFlag = transientFlag;
    }

    @Override
    public void markInitialState() {
        initialState = true;
    }

    @Override
    public boolean initialStateMarked() {
        return initialState;
    }

    @Override
    public void clearInitialState() {
        initialState = false;
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context");
        }

        Object[] values;

        if (initialStateMarked()) {
            values = null;
        }
        else {
            if (stateHelper == null) {
                values = null;
            }
            else {
                values = new Object[1];
                values[0] = stateHelper.saveState(context);
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

            if (values.length == 1) {
                getStateHelper().restoreState(context, values[0]);

                // If we saved state last time, save state again next time.
                clearInitialState();
            }
        }

    }
}
