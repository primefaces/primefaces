/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under PrimeFaces Commercial License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.primefaces.org/elite/license.xhtml
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.calendar.converter;

public class DateConverter implements PatternConverter {

    public String Convert(String pattern) {
        
        //year
        if(pattern.indexOf("yyyy") != -1)
            pattern = pattern.replaceAll("yyyy*", "yy");
        else
            pattern = pattern.replaceAll("yy", "y");
        
        //month
        if(pattern.indexOf("MMMM") != -1)        // name
            pattern = pattern.replaceAll("MMMM*", "MM");
        else if(pattern.indexOf("MMM") != -1)
            pattern = pattern.replaceAll("MMM", "M");
        else if(pattern.indexOf("MM") != -1)    // number
            pattern = pattern.replaceAll("MM", "mm");
        else
            pattern = pattern.replaceAll("M", "m");
        
        //day of Year
        if(pattern.indexOf("DDD") != -1)
            pattern = pattern.replaceAll("DDD*", "oo");
        else
            pattern = pattern.replaceAll("D", "o");
        
        //day of Week
        if(pattern.indexOf("EEEE") != -1)
            pattern = pattern.replaceAll("EEEE*", "DD");
        else
            pattern = pattern.replaceAll("EEE", "D");
        
        return pattern;
    }
    
}
