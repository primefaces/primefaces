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
package org.primefaces.component.inplace;

import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorHolder;

import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class InplaceBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InplaceRenderer";

    public enum PropertyKeys {

        widgetVar,
        label,
        emptyLabel,
        effect,
        effectSpeed,
        disabled,
        style,
        styleClass,
        editor,
        saveLabel,
        cancelLabel,
        event,
        toggleable
    }

    public InplaceBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    public String getEmptyLabel() {
        return (String) getStateHelper().eval(PropertyKeys.emptyLabel, null);
    }

    public void setEmptyLabel(String emptyLabel) {
        getStateHelper().put(PropertyKeys.emptyLabel, emptyLabel);
    }

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, "fade");
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public String getEffectSpeed() {
        return (String) getStateHelper().eval(PropertyKeys.effectSpeed, "normal");
    }

    public void setEffectSpeed(String effectSpeed) {
        getStateHelper().put(PropertyKeys.effectSpeed, effectSpeed);
    }

    public boolean isDisabled() {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        getStateHelper().put(PropertyKeys.disabled, disabled);
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

    public boolean isEditor() {
        return (Boolean) getStateHelper().eval(PropertyKeys.editor, false);
    }

    public void setEditor(boolean editor) {
        getStateHelper().put(PropertyKeys.editor, editor);
    }

    public String getSaveLabel() {
        return (String) getStateHelper().eval(PropertyKeys.saveLabel, "Save");
    }

    public void setSaveLabel(String saveLabel) {
        getStateHelper().put(PropertyKeys.saveLabel, saveLabel);
    }

    public String getCancelLabel() {
        return (String) getStateHelper().eval(PropertyKeys.cancelLabel, "Cancel");
    }

    public void setCancelLabel(String cancelLabel) {
        getStateHelper().put(PropertyKeys.cancelLabel, cancelLabel);
    }

    public String getEvent() {
        return (String) getStateHelper().eval(PropertyKeys.event, "click");
    }

    public void setEvent(String event) {
        getStateHelper().put(PropertyKeys.event, event);
    }

    public boolean isToggleable() {
        return (Boolean) getStateHelper().eval(PropertyKeys.toggleable, true);
    }

    public void setToggleable(boolean toggleable) {
        getStateHelper().put(PropertyKeys.toggleable, toggleable);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}