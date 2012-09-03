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

import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;

public abstract class SelectOneRenderer extends SelectRenderer {
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        if(!shouldDecode(component)) {
            return;
        }
        
        UISelectOne selectOne = (UISelectOne) component;

        decodeBehaviors(context, selectOne);

        String clientId = getSubmitParam(context, selectOne);
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        if(params.containsKey(clientId)) {
            selectOne.setSubmittedValue(params.get(clientId));
        }
        else {
            selectOne.setSubmittedValue("");
        }
    }
    
    protected Object getValues(UISelectOne selectOne) {
        Object value = selectOne.getValue();
        
        if(value != null) {
            return new Object[] {value};
        }
        
        return null;
    }
    
    protected Object getSubmittedValues(UIComponent component) {
        UISelectOne select = (UISelectOne) component;
        
        Object val = select.getSubmittedValue();
        if(val != null) {
            return new Object[] { val };
        }
        
        return null;
    }

    protected abstract String getSubmitParam(FacesContext context, UISelectOne selectOne);
}
