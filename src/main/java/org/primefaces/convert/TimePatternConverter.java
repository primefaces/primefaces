/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.convert;

public class TimePatternConverter implements PatternConverter {

    @Override
    public String convert(String pattern) {

        // Hour
        if (pattern.contains("hh")) {
            pattern = pattern.replaceAll("hh*", "hh");
        }
        else {
            pattern = pattern.replaceAll("h", "h");
        }

        if (pattern.contains("HH")) {
            pattern = pattern.replaceAll("HH*", "HH");
        }
        else {
            pattern = pattern.replaceAll("H", "H");
        }

        // Minute
        if (pattern.contains("mm")) {
            pattern = pattern.replaceAll("mm*", "mm");
        }
        else {
            pattern = pattern.replaceAll("m", "m");
        }

        // Second
        if (pattern.contains("ss")) {
            pattern = pattern.replaceAll("ss*", "ss");
        }
        else {
            pattern = pattern.replaceAll("s", "s");
        }

        // Millisecond
        if (pattern.contains("SSS")) {
            pattern = pattern.replaceAll("SSS*", "l");
        }

        //  AM/PM
        if (pattern.contains("a")) {
            pattern = pattern.replaceAll("a+", "TT");
        }

        //  TimeZone
        if (pattern.contains("Z")) {
            pattern = pattern.replaceAll("Z+", "z");
        }
        else if (pattern.contains("XXX")) {
            pattern = pattern.replaceAll("XXX*", "Z");
        }
        else if (pattern.contains("XX")) {
            pattern = pattern.replaceAll("XX", "z");
        }

        return pattern;
    }

}
