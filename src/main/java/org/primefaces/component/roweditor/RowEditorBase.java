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

    public String getStyle() {
        return (String) getStateHelper().eval(PropertyKeys.style, null);
    }

    public void setStyle(String style) {
        getStateHelper().put(PropertyKeys.style, style);
    }

    public String getStyleClass() {
        return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
    }

    public void setStyleClass(String styleClass) {
        getStateHelper().put(PropertyKeys.styleClass, styleClass);
    }

    public String getEditTitle() {
        return (String) getStateHelper().eval(PropertyKeys.editTitle, null);
    }

    public void setEditTitle(String editTitle) {
        getStateHelper().put(PropertyKeys.editTitle, editTitle);
    }

    public String getCancelTitle() {
        return (String) getStateHelper().eval(PropertyKeys.cancelTitle, null);
    }

    public void setCancelTitle(String cancelTitle) {
        getStateHelper().put(PropertyKeys.cancelTitle, cancelTitle);
    }

    public String getSaveTitle() {
        return (String) getStateHelper().eval(PropertyKeys.saveTitle, null);
    }

    public void setSaveTitle(String saveTitle) {
        getStateHelper().put(PropertyKeys.saveTitle, saveTitle);
    }

}