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
package org.primefaces.component.password;

import javax.faces.component.html.HtmlInputText;

import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;


abstract class PasswordBase extends HtmlInputText implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PasswordRenderer";

    public enum PropertyKeys {

        placeholder,
        widgetVar,
        feedback,
        inline,
        promptLabel,
        weakLabel,
        goodLabel,
        strongLabel,
        redisplay,
        match
    }

    public PasswordBase() {
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

    public boolean isFeedback() {
        return (Boolean) getStateHelper().eval(PropertyKeys.feedback, false);
    }

    public void setFeedback(boolean feedback) {
        getStateHelper().put(PropertyKeys.feedback, feedback);
    }

    public boolean isInline() {
        return (Boolean) getStateHelper().eval(PropertyKeys.inline, false);
    }

    public void setInline(boolean inline) {
        getStateHelper().put(PropertyKeys.inline, inline);
    }

    public String getPromptLabel() {
        return (String) getStateHelper().eval(PropertyKeys.promptLabel, "Please enter a password");
    }

    public void setPromptLabel(String promptLabel) {
        getStateHelper().put(PropertyKeys.promptLabel, promptLabel);
    }

    public String getWeakLabel() {
        return (String) getStateHelper().eval(PropertyKeys.weakLabel, "Weak");
    }

    public void setWeakLabel(String weakLabel) {
        getStateHelper().put(PropertyKeys.weakLabel, weakLabel);
    }

    public String getGoodLabel() {
        return (String) getStateHelper().eval(PropertyKeys.goodLabel, "Good");
    }

    public void setGoodLabel(String goodLabel) {
        getStateHelper().put(PropertyKeys.goodLabel, goodLabel);
    }

    public String getStrongLabel() {
        return (String) getStateHelper().eval(PropertyKeys.strongLabel, "Strong");
    }

    public void setStrongLabel(String strongLabel) {
        getStateHelper().put(PropertyKeys.strongLabel, strongLabel);
    }

    public boolean isRedisplay() {
        return (Boolean) getStateHelper().eval(PropertyKeys.redisplay, false);
    }

    public void setRedisplay(boolean redisplay) {
        getStateHelper().put(PropertyKeys.redisplay, redisplay);
    }

    public String getMatch() {
        return (String) getStateHelper().eval(PropertyKeys.match, null);
    }

    public void setMatch(String match) {
        getStateHelper().put(PropertyKeys.match, match);
    }

    @Override
    public String resolveWidgetVar() {
        return ComponentUtils.resolveWidgetVar(getFacesContext(), this);
    }
}