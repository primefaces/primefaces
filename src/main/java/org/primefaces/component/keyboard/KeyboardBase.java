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
package org.primefaces.component.keyboard;

import javax.faces.component.html.HtmlInputText;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class KeyboardBase extends HtmlInputText implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.KeyboardRenderer";

    public enum PropertyKeys {

        placeholder,
        widgetVar,
        password,
        showMode,
        buttonImage,
        buttonImageOnly,
        effect,
        effectDuration,
        layout,
        layoutTemplate,
        keypadOnly,
        promptLabel,
        closeLabel,
        clearLabel,
        backspaceLabel;
    }

    public KeyboardBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public java.lang.String getPlaceholder() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(java.lang.String _placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, _placeholder);
    }

    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(java.lang.String _widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
    }

    public boolean isPassword() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.password, false);
    }

    public void setPassword(boolean _password) {
        getStateHelper().put(PropertyKeys.password, _password);
    }

    public java.lang.String getShowMode() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.showMode, "focus");
    }

    public void setShowMode(java.lang.String _showMode) {
        getStateHelper().put(PropertyKeys.showMode, _showMode);
    }

    public java.lang.String getButtonImage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.buttonImage, null);
    }

    public void setButtonImage(java.lang.String _buttonImage) {
        getStateHelper().put(PropertyKeys.buttonImage, _buttonImage);
    }

    public boolean isButtonImageOnly() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.buttonImageOnly, false);
    }

    public void setButtonImageOnly(boolean _buttonImageOnly) {
        getStateHelper().put(PropertyKeys.buttonImageOnly, _buttonImageOnly);
    }

    public java.lang.String getEffect() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effect, "fadeIn");
    }

    public void setEffect(java.lang.String _effect) {
        getStateHelper().put(PropertyKeys.effect, _effect);
    }

    public java.lang.String getEffectDuration() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.effectDuration, null);
    }

    public void setEffectDuration(java.lang.String _effectDuration) {
        getStateHelper().put(PropertyKeys.effectDuration, _effectDuration);
    }

    public java.lang.String getLayout() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.layout, "qwerty");
    }

    public void setLayout(java.lang.String _layout) {
        getStateHelper().put(PropertyKeys.layout, _layout);
    }

    public java.lang.String getLayoutTemplate() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.layoutTemplate, null);
    }

    public void setLayoutTemplate(java.lang.String _layoutTemplate) {
        getStateHelper().put(PropertyKeys.layoutTemplate, _layoutTemplate);
    }

    public boolean isKeypadOnly() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.keypadOnly, false);
    }

    public void setKeypadOnly(boolean _keypadOnly) {
        getStateHelper().put(PropertyKeys.keypadOnly, _keypadOnly);
    }

    public java.lang.String getPromptLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.promptLabel, null);
    }

    public void setPromptLabel(java.lang.String _promptLabel) {
        getStateHelper().put(PropertyKeys.promptLabel, _promptLabel);
    }

    public java.lang.String getCloseLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.closeLabel, null);
    }

    public void setCloseLabel(java.lang.String _closeLabel) {
        getStateHelper().put(PropertyKeys.closeLabel, _closeLabel);
    }

    public java.lang.String getClearLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.clearLabel, null);
    }

    public void setClearLabel(java.lang.String _clearLabel) {
        getStateHelper().put(PropertyKeys.clearLabel, _clearLabel);
    }

    public java.lang.String getBackspaceLabel() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.backspaceLabel, null);
    }

    public void setBackspaceLabel(java.lang.String _backspaceLabel) {
        getStateHelper().put(PropertyKeys.backspaceLabel, _backspaceLabel);
    }

    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}