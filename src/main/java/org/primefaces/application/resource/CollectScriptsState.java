/**
 * Copyright 2009-2017 PrimeTek.
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
import java.util.List;

public class CollectScriptsState implements Serializable {
    
    private List<String> includes;
    private StringBuilder inline;
    private int savedInlineTags;

    public CollectScriptsState() {
        includes = new ArrayList<String>(20);
        inline = new StringBuilder(750);
        savedInlineTags = 0;
    }
    
    public void addInclude(StringBuilder src) {
        if (src.length() > 0) {
            includes.add(src.toString());
        }
    }
    
    public void addInline(StringBuilder content) {
        if (content.length() > 0) {
            inline.append(content.toString());
            inline.append(';');
            savedInlineTags++;
        }
    }

    public List<String> getIncludes() {
        return includes;
    }

    public StringBuilder getInline() {
        return inline;
    }

    public int getSavedInlineTags() {
        return savedInlineTags;
    }
}
