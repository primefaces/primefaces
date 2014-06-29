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

public class TimeConverter implements PatternConverter {

    public String Convert(String pattern) {
        
        // Hour
        if(pattern.indexOf("hh") != -1)
            pattern = pattern.replaceAll("hh*", "hh");
        else
            pattern = pattern.replaceAll("h", "h");

        if(pattern.indexOf("HH") != -1)
            pattern = pattern.replaceAll("HH*", "HH");
        else
            pattern = pattern.replaceAll("H", "H");

        // Minute
        if(pattern.indexOf("mm") != -1)     
            pattern = pattern.replaceAll("mm*", "mm");
        else
            pattern = pattern.replaceAll("m", "m");

        // Second
        if(pattern.indexOf("ss") != -1)     
            pattern = pattern.replaceAll("ss*", "ss");
        else
            pattern = pattern.replaceAll("s", "s");

        // Millisecond
        if(pattern.indexOf("SSS") != -1)     
            pattern = pattern.replaceAll("SSS*", "l");

        //  AM/PM
        if(pattern.indexOf("a") != -1)     
            pattern = pattern.replaceAll("a+", "TT");
        
        return pattern;
    }
    
}
