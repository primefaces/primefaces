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

import java.util.Locale;
import org.primefaces.util.Constants;

public class ExactFilterConstraint implements FilterConstraint {

    public boolean applies(Object value, Object filter, Locale locale) {
        String filterText = (filter == null) ? null : filter.toString().trim().toLowerCase(locale);
        
        if(filterText == null || filterText.equals(Constants.EMPTY_STRING)) {
            return true;
        }
        
        if(value == null) {
            return false;
        }
        
        return value.toString().toLowerCase(locale).equalsIgnoreCase(filterText);
    }
}