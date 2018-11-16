/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.application.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoveScriptsToBottomState implements Serializable {

    private HashMap<String, ArrayList<String>> includes;
    private HashMap<String, ArrayList<String>> inlines;
    private int savedInlineTags;

    public MoveScriptsToBottomState() {
        includes = new HashMap<>(1);
        inlines = new HashMap<>(1);
        savedInlineTags = -1;
    }

    public void addInclude(String type, StringBuilder src) {
        if (src.length() > 0) {
            ArrayList<String> includeList = includes.get(type);
            if (includeList == null) {
                includeList = new ArrayList<>(20);
                includes.put(type, includeList);
            }
            includeList.add(src.toString());
        }
    }

    public void addInline(String type, StringBuilder content) {
        if (content.length() > 0) {
            ArrayList<String> inlineList = inlines.get(type);
            if (inlineList == null) {
                inlineList = new ArrayList<>(100);
                inlines.put(type, inlineList);
            }
            inlineList.add(content.toString());

            savedInlineTags++;
        }
    }

    public Map<String, ArrayList<String>> getIncludes() {
        return includes;
    }

    public Map<String, ArrayList<String>> getInlines() {
        return inlines;
    }

    public int getSavedInlineTags() {
        return savedInlineTags;
    }
}
