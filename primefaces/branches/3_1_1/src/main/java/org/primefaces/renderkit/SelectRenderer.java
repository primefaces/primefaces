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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class SelectRenderer extends InputRenderer {
    
    protected boolean isSelected(FacesContext context, UIComponent component, Object itemValue, Object valueArray, Converter converter) {
        if(itemValue == null && valueArray == null) {
            return true;
        }
        
        if(valueArray != null) {
            if(!valueArray.getClass().isArray()) {
                return valueArray.equals(itemValue);
            }
            
            int length = Array.getLength(valueArray);
            for(int i = 0; i < length; i++) {
                Object value = Array.get(valueArray, i);
                
                if(value == null && itemValue == null) {
                    return true;
                } 
                else {
                    if((value == null) ^ (itemValue == null)) {
                        continue;
                    }
                    
                    Object compareValue;
                    if (converter == null) {
                        compareValue = coerceToModelType(context, itemValue, value.getClass());
                    } 
                    else {
                        compareValue = itemValue;
                        
                        if (compareValue instanceof String && !(value instanceof String)) {
                            compareValue = converter.getAsObject(context, component, (String) compareValue);
                        }
                    }

                    if (value.equals(compareValue)) {
                        return (true);
                    }
                }
            }
        }
        return false;
    }
}
