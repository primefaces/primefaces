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
package org.primefaces.component.roweditor;

import javax.faces.component.UIComponentBase;


abstract class RowEditorBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.RowEditorRenderer";

    public enum PropertyKeys {

        style,
        styleClass,
        editTitle,
        cancelTitle,
        saveTitle;
    }

    public RowEditorBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getStyle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(java.lang.String _style) {
        getStateHelper().put(PropertyKeys.style, _style);
    }

    public java.lang.String getStyleClass() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(java.lang.String _styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, _styleClass);
    }

    public java.lang.String getEditTitle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.editTitle, null);
    }

    public void setEditTitle(java.lang.String _editTitle) {
        getStateHelper().put(PropertyKeys.editTitle, _editTitle);
    }

    public java.lang.String getCancelTitle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.cancelTitle, null);
    }

    public void setCancelTitle(java.lang.String _cancelTitle) {
        getStateHelper().put(PropertyKeys.cancelTitle, _cancelTitle);
    }

    public java.lang.String getSaveTitle() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.saveTitle, null);
    }

    public void setSaveTitle(java.lang.String _saveTitle) {
        getStateHelper().put(PropertyKeys.saveTitle, _saveTitle);
    }

}