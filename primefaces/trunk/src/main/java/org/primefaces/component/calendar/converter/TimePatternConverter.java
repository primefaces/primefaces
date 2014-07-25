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

public class TimePatternConverter implements PatternConverter {

    public String convert(String pattern) {
        
        // Hour
        if(pattern.contains("hh"))
            pattern = pattern.replaceAll("hh*", "hh");
        else
            pattern = pattern.replaceAll("h", "h");

        if(pattern.contains("HH"))
            pattern = pattern.replaceAll("HH*", "HH");
        else
            pattern = pattern.replaceAll("H", "H");

        // Minute
        if(pattern.contains("mm"))     
            pattern = pattern.replaceAll("mm*", "mm");
        else
            pattern = pattern.replaceAll("m", "m");

        // Second
        if(pattern.contains("ss"))     
            pattern = pattern.replaceAll("ss*", "ss");
        else
            pattern = pattern.replaceAll("s", "s");

        // Millisecond
        if(pattern.contains("SSS"))     
            pattern = pattern.replaceAll("SSS*", "l");

        //  AM/PM
        if(pattern.contains("a"))     
            pattern = pattern.replaceAll("a+", "TT");
        
        //  TimeZone
        if(pattern.contains("Z"))
            pattern = pattern.replaceAll("Z+", "z");
        else if(pattern.contains("XXX"))     
            pattern = pattern.replaceAll("XXX*", "Z");
        else if(pattern.contains("XX"))     
            pattern = pattern.replaceAll("XX", "z");
        
        return pattern;
    }
    
}