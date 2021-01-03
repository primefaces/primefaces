/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.model.rest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AutoComplete REST-endpoint response.
 */
public class AutoCompleteSuggestionResponse implements Serializable {

    private static final long serialVersionUID = 1940299384032248495L;

    private List<AutoCompleteSuggestion> suggestions = new ArrayList<>();
    private Boolean moreAvailable = false;

    public AutoCompleteSuggestionResponse(List<AutoCompleteSuggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public void addSuggestion(AutoCompleteSuggestion suggestion) {
        suggestions.add(suggestion);
    }

    public List<AutoCompleteSuggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<AutoCompleteSuggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public Boolean getMoreAvailable() {
        return moreAvailable;
    }

    public void setMoreAvailable(Boolean moreAvailable) {
        this.moreAvailable = moreAvailable;
    }
}
