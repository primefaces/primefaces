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

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;

public class ValueExpressionAwareAttributeHandler {
    protected Map<String, Object> literals;
    protected Map<String, ValueExpression> bindings;

    protected final String[] allAttributeNames;

    public ValueExpressionAwareAttributeHandler(String[] allAttributeNames) {
        super();

        literals = new HashMap<>(allAttributeNames.length);
        bindings = new HashMap<>(allAttributeNames.length);

        this.allAttributeNames = allAttributeNames;
    }

    public void setLiteral(String attr, Object val) {
        if (val == null && literals.containsKey(attr)) {
            literals.remove(attr);
        }
        else {
            literals.put(attr, val);
        }
    }

    public void setLiteral(Enum<?> property, Object val) {
        setLiteral(property.name(), val);
    }

    public void setValueExpression(String attr, ValueExpression ve) {
        if (ve == null && bindings.containsKey(attr)) {
            bindings.remove(attr);
        }
        else {
            bindings.put(attr, ve);
        }
    }

    public void setValueExpression(Enum<?> property, ValueExpression ve) {
        setValueExpression(property.name(), ve);
    }

    public <T> T eval(String attr, T unspecifiedValue) {
        if (literals.containsKey(attr)) {
            Object val = literals.get(attr);
            if (val == null) {
                return unspecifiedValue;
            }
            else {
                return (T) val;
            }
        }

        ValueExpression ve = bindings.get(attr);
        if (ve != null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ELContext elContext = facesContext.getELContext();
            return (T) ve.getValue(elContext);
        }
        return unspecifiedValue;
    }

    public <T> T eval(Enum<?> property, T unspecifiedValue) {
        return eval(property.name(), unspecifiedValue);
    }

    public void put(String name, Object value) {
        setLiteral(name, value);
    }

    public void put(Enum<?> property, Object value) {
        setLiteral(property.name(), value);
    }

    public boolean isAttributeSet(String attr) {
        return literals.containsKey(attr) || bindings.containsKey(attr);
    }

    public boolean isAttributeSet(Enum<?> property) {
        return isAttributeSet(property.name());
    }

    public Object saveState(FacesContext context) {
        Object[] values = new Object[3];
        values[1] = savePropertyMap(context, literals, false);
        values[2] = savePropertyMap(context, bindings, true);

        return values;
    }

    public void restoreState(FacesContext context, Object state) {
        if (state == null) {
            return;
        }

        Object[] values = (Object[]) state;
        if (values.length == 2) {
            literals = restorePropertyMap(context, (Object[]) values[1], false);
            bindings = restorePropertyMap(context, (Object[]) values[2], true);
        }
    }

    protected Object[] savePropertyMap(FacesContext context, Map<String, ?> map, boolean saveValuesAsAttachedState) {
        if (map == null) {
            return null;
        }

        Object[] values = new Object[allAttributeNames.length];
        for (int i = 0; i < allAttributeNames.length; i++) {
            Object val = map.get(allAttributeNames[i]);

            if (saveValuesAsAttachedState) {
                val = UIComponentBase.saveAttachedState(context, val);
            }

            if (val != null) {
                values[i] = val;
            }
        }

        return values;
    }

    protected Map restorePropertyMap(FacesContext context, Object[] values, boolean restoreValuesFromAttachedState) {
        if (values == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>(allAttributeNames.length);
        for (int i = 0; i < allAttributeNames.length; i++) {
            Object val = values[i];

            if (restoreValuesFromAttachedState) {
                val = UIComponentBase.restoreAttachedState(context, val);
            }

            if (val != null) {
                map.put(allAttributeNames[i], val);
            }
        }

        return map;
    }
}
