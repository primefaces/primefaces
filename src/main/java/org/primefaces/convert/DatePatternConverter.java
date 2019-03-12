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

public class DatePatternConverter implements PatternConverter {

    @Override
    public String convert(String pattern) {

        //year
        if (pattern.contains("yyyy")) {
            pattern = pattern.replaceAll("yyyy*", "yy");
        }
        else {
            pattern = pattern.replaceAll("yy", "y");
        }

        //month
        if (pattern.contains("MMMM")) { // name
            pattern = pattern.replaceAll("MMMM*", "MM");
        }
        else if (pattern.contains("MMM")) {
            pattern = pattern.replaceAll("MMM", "M");
        }
        else if (pattern.contains("MM")) { // number
            pattern = pattern.replaceAll("MM", "mm");
        }
        else {
            pattern = pattern.replaceAll("M", "m");
        }

        //day of Year
        if (pattern.contains("DDD")) {
            pattern = pattern.replaceAll("DDD*", "oo");
        }
        else {
            pattern = pattern.replaceAll("D", "o");
        }

        //day of Week
        if (pattern.contains("EEEE")) {
            pattern = pattern.replaceAll("EEEE*", "DD");
        }
        else {
            pattern = pattern.replaceAll("EEE", "D");
        }

        return pattern;
    }

}
