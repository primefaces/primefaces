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
package org.primefaces.application.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveScriptsToBottomState implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, List<String>> includes;
    private Map<String, List<String>> inlines;
    private int savedInlineTags;

    public MoveScriptsToBottomState() {
        includes = new HashMap<>(1);
        inlines = new HashMap<>(1);
        savedInlineTags = -1;
    }

    public void addInclude(String type, StringBuilder src) {
        if (src.length() > 0) {
            List<String> includeList = includes.computeIfAbsent(type, k -> new ArrayList<>(20));
            includeList.add(src.toString());
        }
    }

    public void addInline(String type, StringBuilder content) {
        if (content.length() > 0) {
            List<String> inlineList = inlines.computeIfAbsent(type, k -> new ArrayList<>(100));
            inlineList.add(content.toString());

            savedInlineTags++;
        }
    }

    public Map<String, List<String>> getIncludes() {
        return includes;
    }

    public Map<String, List<String>> getInlines() {
        return inlines;
    }

    public int getSavedInlineTags() {
        return savedInlineTags;
    }
}
