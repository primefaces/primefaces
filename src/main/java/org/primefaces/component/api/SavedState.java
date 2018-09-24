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
