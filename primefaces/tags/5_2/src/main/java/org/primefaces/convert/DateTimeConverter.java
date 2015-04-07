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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import javax.faces.convert.ConverterException;
import org.primefaces.component.calendar.CalendarUtils;
import org.primefaces.util.HTML;

public class DateTimeConverter extends javax.faces.convert.DateTimeConverter implements ClientConverter {
    
    private Map<String,Object> metadata;
    
    public Map<String, Object> getMetadata() {
        if(metadata == null) {
            String pattern = this.getPattern();
            String type = this.getType();
            String dateStyle = this.getDateStyle();
            String timeStyle = this.getTimeStyle();
            
            metadata = new HashMap<String, Object>();
            
            if(pattern != null) {
                metadata.put(HTML.VALIDATION_METADATA.PATTERN, CalendarUtils.convertPattern(pattern));
            }
            
            if(type != null) { 
                metadata.put(HTML.VALIDATION_METADATA.DATETIME_TYPE, type);
                if(pattern == null) {
                    DateFormat df = null;                
                    if (type.equals("both")) {
                        df = DateFormat.getDateInstance(getStyle(dateStyle), this.getLocale());
                        metadata.put(HTML.VALIDATION_METADATA.DATE_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                        df = DateFormat.getTimeInstance(getStyle(timeStyle), this.getLocale());
                        metadata.put(HTML.VALIDATION_METADATA.TIME_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                    } else if (type.equals("date")) {
                        df = DateFormat.getDateInstance(getStyle(dateStyle), this.getLocale());
                        metadata.put(HTML.VALIDATION_METADATA.DATE_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                    } else if (type.equals("time")) {
                        df = DateFormat.getTimeInstance(getStyle(timeStyle), this.getLocale());
                        metadata.put(HTML.VALIDATION_METADATA.TIME_STYLE_PATTERN, CalendarUtils.convertPattern(((SimpleDateFormat) df).toPattern()));
                    }
                }
            }     
        }
        
        return metadata;
    }

    public String getConverterId() {
        return DateTimeConverter.CONVERTER_ID;
    }
    
    private int getStyle(String style) {
        if ("default".equals(style)) {
            return (DateFormat.DEFAULT);
        } else if ("short".equals(style)) {
            return (DateFormat.SHORT);
        } else if ("medium".equals(style)) {
            return (DateFormat.MEDIUM);
        } else if ("long".equals(style)) {
            return (DateFormat.LONG);
        } else if ("full".equals(style)) {
            return (DateFormat.FULL);
        } else {
            throw new ConverterException("Invalid style '" + style + '\'');
        }
    }
}
