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
package org.primefaces.component.api;

import java.io.Serializable;

import javax.faces.component.EditableValueHolder;

/**
 * Keeps state of a component implementing {@link javax.faces.component.EditableValueHolder}.
 */
@SuppressWarnings({"SerializableHasSerializationMethods", "NonSerializableFieldInSerializableClass"})
public class SavedState implements Serializable {

    public static final SavedState NULL_STATE = new SavedState();

    private static final long serialVersionUID = 4325654657465654768L;

    private Object submittedValue;
    private boolean submitted;
    private boolean valid = true;
    private Object value;
    private boolean localValueSet = false;

    public SavedState() {
        super();
    }

    public SavedState(EditableValueHolder evh) {
        populate(evh);
    }

    public void populate(EditableValueHolder evh) {
        value = evh.getLocalValue();
        valid = evh.isValid();
        submittedValue = evh.getSubmittedValue();
        localValueSet = evh.isLocalValueSet();
    }

    public void restoreState(EditableValueHolder evh) {
        evh.setValue(value);
        evh.setValid(valid);
        evh.setSubmittedValue(submittedValue);
        evh.setLocalValueSet(localValueSet);
    }

    public Object getSubmittedValue() {
        return (this.submittedValue);
    }

    public void setSubmittedValue(Object submittedValue) {
        this.submittedValue = submittedValue;
    }

    public boolean isValid() {
        return (this.valid);
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Object getValue() {
        return (this.value);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isLocalValueSet() {
        return (this.localValueSet);
    }

    public void setLocalValueSet(boolean localValueSet) {
        this.localValueSet = localValueSet;
    }

    public boolean getSubmitted() {
        return this.submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    @Override
    public String toString() {
        return ("submittedValue: " + submittedValue + " value: " + value
                + " localValueSet: " + localValueSet);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (localValueSet ? 1231 : 1237);
        result = prime * result + (submitted ? 1231 : 1237);
        result = prime * result + ((submittedValue == null) ? 0 : submittedValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SavedState other = (SavedState) obj;
        if (localValueSet != other.localValueSet) {
            return false;
        }
        if (submitted != other.submitted) {
            return false;
        }
        if (submittedValue == null) {
            if (other.submittedValue != null) {
                return false;
            }
        }
        else if (!submittedValue.equals(other.submittedValue)) {
            return false;
        }
        return true;
    }
}
