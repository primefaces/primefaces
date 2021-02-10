/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.password;

import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.Widget;

public abstract class PasswordBase extends AbstractPrimeHtmlInputText implements Widget, RTLAware  {

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
        match,
        showEvent,
        hideEvent,
        ignoreLastPass,
        toggleMask
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

    public String getShowEvent() {
        return (String) getStateHelper().eval(PropertyKeys.showEvent, null);
    }

    public void setShowEvent(String showEvent) {
        getStateHelper().put(PropertyKeys.showEvent, showEvent);
    }

    public String getHideEvent() {
        return (String) getStateHelper().eval(PropertyKeys.hideEvent, null);
    }

    public void setHideEvent(String hideEvent) {
        getStateHelper().put(PropertyKeys.hideEvent, hideEvent);
    }

    public boolean isIgnoreLastPass() {
        return (Boolean) getStateHelper().eval(PropertyKeys.ignoreLastPass, false);
    }

    public void setIgnoreLastPass(boolean ignoreLastPass) {
        getStateHelper().put(PropertyKeys.ignoreLastPass, ignoreLastPass);
    }

    public boolean isToggleMask() {
        return (Boolean) getStateHelper().eval(PropertyKeys.toggleMask, false);
    }

    public void setToggleMask(boolean toggleMask) {
        getStateHelper().put(PropertyKeys.toggleMask, toggleMask);
    }
}