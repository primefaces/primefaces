/*
 * Copyright 2013 jagatai.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.convert;

import java.util.HashMap;
import java.util.Map;
import org.primefaces.component.calendar.CalendarUtils;
import org.primefaces.util.HTML;

public class DateTimeConverter extends javax.faces.convert.DateTimeConverter implements ClientConverter {
    
    private Map<String,Object> metadata;
    
    public Map<String, Object> getMetadata() {
        if(metadata == null) {
            String pattern = this.getPattern();
            String type = this.getType();
            
            metadata = new HashMap<String, Object>();
            
            if(pattern != null)
                metadata.put(HTML.VALIDATION_METADATA.PATTERN, CalendarUtils.convertPattern(pattern));
            
            if(type != null)
                metadata.put(HTML.VALIDATION_METADATA.DATETIME_TYPE, type);
        }
        
        return metadata;
    }

    public String getConverterId() {
        return DateTimeConverter.CONVERTER_ID;
    }
}
