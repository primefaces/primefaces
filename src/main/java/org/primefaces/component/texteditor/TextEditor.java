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
package org.primefaces.component.texteditor;

import java.util.Collection;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

import org.primefaces.util.LangUtils;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "texteditor/texteditor.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "texteditor/texteditor.js")
})
public class TextEditor extends TextEditorBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.TextEditor";

    public static final String EDITOR_CLASS = "ui-texteditor";

    private static final Collection<String> EVENT_NAMES = LangUtils.unmodifiableList("blur", "change", "click", "dblclick", "focus", "keydown",
            "keypress", "keyup", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "select");

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return "change";
    }
}