/*
 * Copyright 2009-2014 PrimeTek.
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

        int propertiesCount = getAllProperties().length;
        literals = new HashMap<String, Object>(propertiesCount);
        bindings = new HashMap<String, ValueExpression>(propertiesCount);
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
            if (val == null){
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
                values = new Object[] { superState };
            }
        } else {
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

        Enum<?>[] allProperties = getAllProperties();

        Object[] values = new Object[allProperties.length];
        for (int i = 0; i < allProperties.length; i++) {
            Object val = map.get(allProperties[i].name());

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

        Enum<?>[] allProperties = getAllProperties();

        Map<String, Object> map = new HashMap<String, Object>(allProperties.length);
        for (int i = 0; i < allProperties.length; i++) {
            Object val = values[i];

            if (restoreValuesFromAttachedState) {
                val = UIComponentBase.restoreAttachedState(context, val);
            }

            if (val != null) {
                map.put(allProperties[i].name(), val);
            }
        }

        return map;
    }

    protected abstract Enum<?>[] getAllProperties();
}
