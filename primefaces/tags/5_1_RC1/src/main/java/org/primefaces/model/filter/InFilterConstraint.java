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
package org.primefaces.model.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import javax.faces.FacesException;

public class InFilterConstraint implements FilterConstraint {

    public boolean applies(Object value, Object filter, Locale locale) {
        if(filter == null) {
            return true;
        }
        
        if(value == null) {
            return false;
        }
        
        Collection<?> collection = null;
        if(filter.getClass().isArray()) {
            collection = Arrays.asList((Object[])filter);
        } else if(filter instanceof Collection) {
            collection = (Collection<?>) filter;
        } else {
            throw new FacesException("Filter value must be an array or a collection when using \"in\" filter constraint.");
        }
        
        if(collection != null && !collection.isEmpty()) {
            for (Iterator<? extends Object> it = collection.iterator(); it.hasNext();) {
                Object object = it.next();
                if(object.equals(value)) {
                    return true;
                }
            }
            
            return false;
        }
        else {
            return true;
        }
    }
}
