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

public class DatePatternConverter implements PatternConverter {

    public String convert(String pattern) {
        
        //year
        if(pattern.contains("yyyy"))
            pattern = pattern.replaceAll("yyyy*", "yy");
        else
            pattern = pattern.replaceAll("yy", "y");
        
        //month
        if(pattern.contains("MMMM"))        // name
            pattern = pattern.replaceAll("MMMM*", "MM");
        else if(pattern.contains("MMM"))
            pattern = pattern.replaceAll("MMM", "M");
        else if(pattern.contains("MM"))    // number
            pattern = pattern.replaceAll("MM", "mm");
        else
            pattern = pattern.replaceAll("M", "m");
        
        //day of Year
        if(pattern.contains("DDD"))
            pattern = pattern.replaceAll("DDD*", "oo");
        else
            pattern = pattern.replaceAll("D", "o");
        
        //day of Week
        if(pattern.contains("EEEE"))
            pattern = pattern.replaceAll("EEEE*", "DD");
        else
            pattern = pattern.replaceAll("EEE", "D");
        
        return pattern;
    }
    
}

