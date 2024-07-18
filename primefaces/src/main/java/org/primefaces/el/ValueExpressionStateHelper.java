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
package org.primefaces.el;

import org.primefaces.util.Lazy;

import javax.el.ValueExpression;
import javax.faces.component.StateHelper;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ValueExpressionStateHelper implements StateHelper {
    protected Lazy<Map<String, Object>> literals = new Lazy<>(HashMap::new);
    protected Lazy<Map<String, ValueExpression>> bindings = new Lazy<>(HashMap::new);

    private boolean transientFlag;

    @Override
    public Object put(Serializable key, Object value) {
        Object previous = eval(key);
        literals.get().put(String.valueOf(key), value);

        return previous;
    }

    @Override
    public Object remove(Serializable key) {
        Object previous = eval(key);

        if (literals.isInitialized()) {
            literals.get().remove(String.valueOf(key));
        }

        if (bindings.isInitialized()) {
            bindings.get().remove(String.valueOf(key));
        }

        return previous;
    }

    @Override
    public Object put(Serializable key, String mapKey, Object value) {
        Map<String, Object> internalMap = (Map<String, Object>) get(key);
        if (internalMap == null) {
            internalMap = new HashMap<>();
            put(key, internalMap);
        }

        return internalMap.put(mapKey, value);
    }

    @Override
    public Object get(Serializable key) {
        if (!literals.isInitialized()) {
            return null;
        }

        return literals.get().get(String.valueOf(key));
    }

    @Override
    public Object eval(Serializable key) {
        return eval(key, null);
    }

    @Override
    public Object eval(Serializable key, Object defaultValue) {
        Object literal = get(String.valueOf(key));
        if (literal != null) {
            return literal;
        }

        if (!bindings.isInitialized()) {
            return defaultValue;
        }

        ValueExpression ve = bindings.get().get(String.valueOf(key));
        if (ve == null) {
            return defaultValue;
        }

        return ve.getValue(FacesContext.getCurrentInstance().getELContext());
    }

    @Override
    public void add(Serializable key, Object value) {
        List<Object> internalList = (List<Object>) get(key);
        if (internalList == null) {
            internalList = new ArrayList<>();
            put(key, internalList);
        }

        internalList.add(value);
    }

    @Override
    public Object remove(Serializable key, Object valueOrKey) {
        Object internalStructure = get(key);
        if (internalStructure instanceof List) {
            ((List<?>) internalStructure).remove(valueOrKey);

            return null;
        }

        if (internalStructure instanceof Map) {
            return ((Map<?, ?>) internalStructure).remove(valueOrKey);
        }

        return null;
    }

    @Override
    public Object saveState(FacesContext context) {
        Object[] state = new Object[2];

        state[0] = literals.isInitialized() ? literals.get() : null;
        state[1] = bindings.isInitialized() ? bindings.get() : null;

        return state;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if (!(state instanceof Object[])) {
            return;
        }

        Object[] stateArray = (Object[]) state;
        if (stateArray.length != 2) {
            return;
        }

        Object literalState = stateArray[0];
        if (literalState instanceof Map) {
            literals.reset((Map<String, Object>) literalState);
        }
        else {
            literals.reset();
        }

        Object bindingState = stateArray[1];
        if (bindingState instanceof Map) {
            bindings.reset((Map<String, ValueExpression>) bindingState);
        }
        else {
            bindings.reset();
        }
    }

    @Override
    public boolean isTransient() {
        return transientFlag;
    }

    @Override
    public void setTransient(boolean newTransientValue) {
        this.transientFlag = newTransientValue;
    }

    public void setBinding(String key, ValueExpression ve) {
        this.bindings.get().put(key, ve);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueExpressionStateHelper that = (ValueExpressionStateHelper) o;
        return transientFlag == that.transientFlag && Objects.equals(literals, that.literals) && Objects.equals(bindings, that.bindings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literals, bindings, transientFlag);
    }
}
