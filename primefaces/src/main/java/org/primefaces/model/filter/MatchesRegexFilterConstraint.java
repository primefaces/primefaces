/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.model.filter;

import java.util.Locale;
import java.util.regex.PatternSyntaxException;

import jakarta.faces.context.FacesContext;

/**
 * Matches when the field value, as a string, fully matches the filter value interpreted as a regular
 * expression (e.g., {@code ^INV-\d+$}). An invalid regex (e.g., while the user is still typing it) is treated
 * as not matching rather than throwing.
 */
public class MatchesRegexFilterConstraint implements FilterConstraint {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isMatching(FacesContext ctxt, Object value, Object filter, Locale locale) {
        if (value == null || filter == null) {
            return false;
        }

        try {
            String patternText = filter.toString();
            java.util.regex.Pattern pattern;
            if (ctxt != null) {
                String key = MatchesRegexFilterConstraint.class.getName() + ".pattern." + patternText;
                Object cached = ctxt.getExternalContext().getRequestMap().get(key);
                pattern = cached instanceof java.util.regex.Pattern
                        ? (java.util.regex.Pattern) cached
                        : java.util.regex.Pattern.compile(patternText);
                if (!(cached instanceof java.util.regex.Pattern)) {
                    ctxt.getExternalContext().getRequestMap().put(key, pattern);
                }
            }
            else {
                pattern = java.util.regex.Pattern.compile(patternText);
            }
            return pattern.matcher(value.toString()).matches();
        }
        catch (PatternSyntaxException e) {
            return false;
        }
    }
}
