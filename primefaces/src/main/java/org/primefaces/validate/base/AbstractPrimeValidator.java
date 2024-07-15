/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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

import org.primefaces.el.ValueExpressionAwareAttributeHandler;

import javax.faces.component.PartialStateHolder;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import java.util.Arrays;

public abstract class AbstractPrimeValidator implements Validator, PartialStateHolder {
    protected ValueExpressionAwareAttributeHandler attributeHandler;

    private boolean transientFlag = false;
    private boolean initialState = false;

    public AbstractPrimeValidator() {
        String[] attributeNames = Arrays.stream(getAllAttributes())
                .map(Enum::name)
                .toArray(String[]::new);

        attributeHandler = new ValueExpressionAwareAttributeHandler(attributeNames);
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
            throw new NullPointerException();
        }

        Object[] values;

        if (initialStateMarked()) {
            values = null;
        }
        else {
            values = new Object[1];
            values[0] = attributeHandler.saveState(context);
        }

        return values;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if (context == null) {
            throw new NullPointerException();
        }

        if (state != null) {
            Object[] values = (Object[]) state;

            if (values.length == 1) {
                attributeHandler.restoreState(context, values[0]);

                // If we saved state last time, save state again next time.
                clearInitialState();
            }
        }

    }

    public ValueExpressionAwareAttributeHandler getAttributeHandler() {
        return attributeHandler;
    }

    protected abstract Enum<?>[] getAllAttributes();
}
