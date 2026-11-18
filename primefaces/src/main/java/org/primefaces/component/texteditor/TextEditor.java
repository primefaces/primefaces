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
package org.primefaces.component.texteditor;

import org.primefaces.cdk.api.FacesComponentInfo;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;

@FacesComponent(value = TextEditor.COMPONENT_TYPE, namespace = TextEditor.COMPONENT_FAMILY)
@FacesComponentInfo(description = "Editor is an input component with rich text editing capabilities.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "texteditor/texteditor.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "texteditor/texteditor.js")
public class TextEditor extends TextEditorBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.TextEditor";

    public static final String EDITOR_CLASS = "p-texteditor";

    /**
     * The whole component, e.g. as the target of message tooltips; the value input is hidden.
     */
    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext());
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    /**
     * The editing area is a contenteditable created by Quill, not a labelable element, so the label
     * is linked via aria-labelledby only.
     */
    @Override
    public String getLabelClientId() {
        return null;
    }

}