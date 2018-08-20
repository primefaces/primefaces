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
        backspaceLabel
    }

    public KeyboardBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getPlaceholder() {
        return (String) getStateHelper().eval(PropertyKeys.placeholder, null);
    }

    public void setPlaceholder(String placeholder) {
        getStateHelper().put(PropertyKeys.placeholder, placeholder);
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public boolean isPassword() {
        return (Boolean) getStateHelper().eval(PropertyKeys.password, false);
    }

    public void setPassword(boolean password) {
        getStateHelper().put(PropertyKeys.password, password);
    }

    public String getShowMode() {
        return (String) getStateHelper().eval(PropertyKeys.showMode, "focus");
    }

    public void setShowMode(String showMode) {
        getStateHelper().put(PropertyKeys.showMode, showMode);
    }

    public String getButtonImage() {
        return (String) getStateHelper().eval(PropertyKeys.buttonImage, null);
    }

    public void setButtonImage(String buttonImage) {
        getStateHelper().put(PropertyKeys.buttonImage, buttonImage);
    }

    public boolean isButtonImageOnly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.buttonImageOnly, false);
    }

    public void setButtonImageOnly(boolean buttonImageOnly) {
        getStateHelper().put(PropertyKeys.buttonImageOnly, buttonImageOnly);
    }

    public String getEffect() {
        return (String) getStateHelper().eval(PropertyKeys.effect, "fadeIn");
    }

    public void setEffect(String effect) {
        getStateHelper().put(PropertyKeys.effect, effect);
    }

    public String getEffectDuration() {
        return (String) getStateHelper().eval(PropertyKeys.effectDuration, null);
    }

    public void setEffectDuration(String effectDuration) {
        getStateHelper().put(PropertyKeys.effectDuration, effectDuration);
    }

    public String getLayout() {
        return (String) getStateHelper().eval(PropertyKeys.layout, "qwerty");
    }

    public void setLayout(String layout) {
        getStateHelper().put(PropertyKeys.layout, layout);
    }

    public String getLayoutTemplate() {
        return (String) getStateHelper().eval(PropertyKeys.layoutTemplate, null);
    }

    public void setLayoutTemplate(String layoutTemplate) {
        getStateHelper().put(PropertyKeys.layoutTemplate, layoutTemplate);
    }

    public boolean isKeypadOnly() {
        return (Boolean) getStateHelper().eval(PropertyKeys.keypadOnly, false);
    }

    public void setKeypadOnly(boolean keypadOnly) {
        getStateHelper().put(PropertyKeys.keypadOnly, keypadOnly);
    }

    public String getPromptLabel() {
        return (String) getStateHelper().eval(PropertyKeys.promptLabel, null);
    }

    public void setPromptLabel(String promptLabel) {
        getStateHelper().put(PropertyKeys.promptLabel, promptLabel);
    }

    public String getCloseLabel() {
        return (String) getStateHelper().eval(PropertyKeys.closeLabel, null);
    }

    public void setCloseLabel(String closeLabel) {
        getStateHelper().put(PropertyKeys.closeLabel, closeLabel);
    }

    public String getClearLabel() {
        return (String) getStateHelper().eval(PropertyKeys.clearLabel, null);
    }

    public void setClearLabel(String clearLabel) {
        getStateHelper().put(PropertyKeys.clearLabel, clearLabel);
    }

    public String getBackspaceLabel() {
        return (String) getStateHelper().eval(PropertyKeys.backspaceLabel, null);
    }

    public void setBackspaceLabel(String backspaceLabel) {
        getStateHelper().put(PropertyKeys.backspaceLabel, backspaceLabel);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}