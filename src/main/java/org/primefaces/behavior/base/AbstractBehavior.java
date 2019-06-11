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
package org.primefaces.behavior.base;

import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.context.FacesContext;

public abstract class AbstractBehavior extends ClientBehaviorBase {

    protected Map<String, Object> literals;
    protected Map<String, ValueExpression> bindings;

    public AbstractBehavior() {
        super();

        int attrsCount = getAllAttributes().length;
        literals = new HashMap<>(attrsCount);
        bindings = new HashMap<>(attrsCount);
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
        String attr = property.name();
        setLiteral(attr, val);
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
        String attr = property.name();
        setValueExpression(attr, ve);
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

    protected <T> T eval(Enum<?> property, T unspecifiedValue) {
        return eval(property.name(), unspecifiedValue);
    }

    public void put(String name, Object value) {
        setLiteral(name, value);
    }

    public void put(Enum<?> property, Object value) {
        setLiteral(property.name(), value);
    }

    protected boolean isAttributeSet(String attr) {
        return literals.containsKey(attr) || bindings.containsKey(attr);
    }

    protected boolean isAttributeSet(Enum<?> property) {
        String attr = property.name();
        return isAttributeSet(attr);
    }

    @Override
    public Object saveState(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
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
            values = new Object[3];

            values[0] = superState;
            values[1] = savePropertyMap(context, literals, false);
            values[2] = savePropertyMap(context, bindings, true);
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
            super.restoreState(context, values[0]);

            if (values.length != 1) {
                literals = restorePropertyMap(context, (Object[]) values[1], false);
                bindings = restorePropertyMap(context, (Object[]) values[2], true);

                // If we saved state last time, save state again next time.
                clearInitialState();
            }
        }
    }

    protected Object[] savePropertyMap(FacesContext context, Map map, boolean saveValuesAsAttachedState) {
        if (map == null) {
            return null;
        }

        BehaviorAttribute[] attributes = getAllAttributes();

        Object[] values = new Object[attributes.length];
        for (int i = 0; i < attributes.length; i++) {
            Object val = map.get(attributes[i].getName());

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

        BehaviorAttribute[] attributes = getAllAttributes();

        Map<String, Object> map = new HashMap<>(attributes.length);
        for (int i = 0; i < attributes.length; i++) {
            Object val = values[i];

            if (restoreValuesFromAttachedState) {
                val = UIComponentBase.restoreAttachedState(context, val);
            }

            if (val != null) {
                map.put(attributes[i].getName(), val);
            }
        }

        return map;
    }

    protected abstract BehaviorAttribute[] getAllAttributes();

}
