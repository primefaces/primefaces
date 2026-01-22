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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;


import jakarta.el.ValueExpression;
import jakarta.faces.component.StateHelper;
import jakarta.faces.context.FacesContext;

public class ValueExpressionStateHelper implements StateHelper {
    private static final Object UNKNOWN_VALUE = new Object();

    protected Map<String, Object> literals;
    protected Map<String, ValueExpression> bindings;

    private boolean transientFlag;

    @Override
    public Object put(Serializable key, Object value) {
        if (literals == null) {
            literals = new HashMap<>(3);
        }

        return literals.put(String.valueOf(key), value);
    }

    @Override
    public Object remove(Serializable key) {
        Object previous = eval(key);

        if (literals != null) {
            literals.remove(String.valueOf(key));
        }

        if (bindings != null) {
            bindings.remove(String.valueOf(key));
        }

        return previous;
    }

    @Override
    public Object put(Serializable key, String mapKey, Object value) {
        Map<String, Object> internalMap = (Map<String, Object>) get(key);
        if (internalMap == null) {
            internalMap = new HashMap<>(3);
            put(key, internalMap);
        }

        return internalMap.put(mapKey, value);
    }

    @Override
    public Object get(Serializable key) {
        if (literals == null) {
            return null;
        }

        return literals.get(String.valueOf(key));
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

        if (bindings == null) {
            return defaultValue;
        }

        ValueExpression ve = bindings.get(String.valueOf(key));
        if (ve == null) {
            return defaultValue;
        }

        return ve.getValue(FacesContext.getCurrentInstance().getELContext());
    }

    @Override
    public Object eval(Serializable key, Supplier<Object> defaultValueSupplier) {
        Object result = eval(key, UNKNOWN_VALUE);
        if (result == UNKNOWN_VALUE) {
            result = null;

            if (defaultValueSupplier != null) {
                result = defaultValueSupplier.get();
            }
        }

        return result;
    }

    @Override
    public void add(Serializable key, Object value) {
        List<Object> internalList = (List<Object>) get(key);
        if (internalList == null) {
            internalList = new ArrayList<>(3);
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

        state[0] = literals;
        state[1] = bindings;

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

        literals = (Map<String, Object>) stateArray[0];
        bindings = (Map<String, ValueExpression>) stateArray[1];
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
        if (bindings == null) {
            bindings = new HashMap<>(3);
        }

        bindings.put(key, ve);
    }

    public ValueExpression getBinding(String key) {
        return bindings != null ? bindings.get(key) : null;
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
