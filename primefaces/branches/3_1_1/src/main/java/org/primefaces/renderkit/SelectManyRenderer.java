/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.renderkit;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;

public abstract class SelectManyRenderer extends SelectRenderer {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if(!shouldDecode(component)) {
            return;
        }
                
        UISelectMany selectMany = (UISelectMany) component;

        decodeBehaviors(context, selectMany);

        String submitParam = getSubmitParam(context, selectMany);
        Map<String,String[]> params = context.getExternalContext().getRequestParameterValuesMap();
        
        if(params.containsKey(submitParam)) {
            selectMany.setSubmittedValue(params.get(submitParam));
        } else {
            selectMany.setSubmittedValue(new String[0]);
        }
    }
    
    protected Object getValues(UIComponent component) {
        UISelectMany selectMany = (UISelectMany) component;
        Object value = selectMany.getValue();

        if(value == null) {
            return null;
        } else if (value instanceof Collection) {
            return ((Collection) value).toArray();
        } else if(value.getClass().isArray()) {
            if(Array.getLength(value) == 0) {
                return null;
            }
        } else {
            throw new FacesException("Value of '" + component.getClientId() + "'must be an array or a collection");
        }

        return value;
    }
    
    protected Object getSubmittedValues(UIComponent component) {
        UISelectMany select = (UISelectMany) component;
        
        return (Object[]) select.getSubmittedValue();
    }
            
    protected abstract String getSubmitParam(FacesContext context, UISelectMany selectMany);
}
