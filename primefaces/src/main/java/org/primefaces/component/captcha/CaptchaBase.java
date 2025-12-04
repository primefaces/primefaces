/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.captcha;

import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIInput;

public abstract class CaptchaBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CaptchaRenderer";

    public enum PropertyKeys {

        type,
        theme,
        language,
        tabindex,
        label,
        callback,
        expired,
        size,
        executor,
        sourceUrl,
        verifyUrl,
        widgetVar
    }

    public CaptchaBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getTheme() {
        return (String) getStateHelper().eval(PropertyKeys.theme, "auto");
    }

    public void setTheme(String theme) {
        getStateHelper().put(PropertyKeys.theme, theme);
    }

    public String getLanguage() {
        return (String) getStateHelper().eval(PropertyKeys.language, "en");
    }

    public void setLanguage(String language) {
        getStateHelper().put(PropertyKeys.language, language);
    }

    public int getTabindex() {
        return (Integer) getStateHelper().eval(PropertyKeys.tabindex, 0);
    }

    public void setTabindex(int tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

    public String getLabel() {
        return (String) getStateHelper().eval(PropertyKeys.label, null);
    }

    public void setLabel(String label) {
        getStateHelper().put(PropertyKeys.label, label);
    }

    public String getCallback() {
        return (String) getStateHelper().eval(PropertyKeys.callback, null);
    }

    public void setCallback(String callback) {
        getStateHelper().put(PropertyKeys.callback, callback);
    }

    public String getExpired() {
        return (String) getStateHelper().eval(PropertyKeys.expired, null);
    }

    public void setExpired(String expired) {
        getStateHelper().put(PropertyKeys.expired, expired);
    }

    public String getSize() {
        return (String) getStateHelper().eval(PropertyKeys.size, null);
    }

    public void setSize(String size) {
        getStateHelper().put(PropertyKeys.size, size);
    }

    public String getType() {
        return (String) getStateHelper().eval(PropertyKeys.type, Captcha.RECAPTCHA);
    }

    public void setType(String type) {
        getStateHelper().put(PropertyKeys.type, type);
    }

    @Override
    public java.lang.String getWidgetVar() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar);
    }

    public void setWidgetVar(java.lang.String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getExecutor() {
        return (String) getStateHelper().eval(PropertyKeys.executor, () -> {
            String type = this.getType();
            switch (type) {
                case Captcha.RECAPTCHA:
                    return "grecaptcha";
                case Captcha.HCAPTCHA:
                    return "hcaptcha";
                default:
                    return null;
            }
        });
    }

    public void setExecutor(String executor) {
        getStateHelper().put(PropertyKeys.executor, executor);
    }

    public String getSourceUrl() {
        return (String) getStateHelper().eval(PropertyKeys.sourceUrl, () -> {
            String type = this.getType();
            switch (type) {
                case Captcha.RECAPTCHA:
                    return "https://www.google.com/recaptcha/api.js";
                case Captcha.HCAPTCHA:
                    return "https://js.hcaptcha.com/1/api.js";
                default:
                    return null;
            }
        });
    }

    public void setSourceUrl(String sourceUrl) {
        getStateHelper().put(PropertyKeys.sourceUrl, sourceUrl);
    }

    public String getVerifyUrl() {
        return (String) getStateHelper().eval(PropertyKeys.verifyUrl, () -> {
            String type = this.getType();
            switch (type) {
                case Captcha.RECAPTCHA:
                    return "https://www.google.com/recaptcha/api/siteverify";
                case Captcha.HCAPTCHA:
                    return "https://api.hcaptcha.com/siteverify";
                default:
                    return null;
            }
        });
    }

    public void setVerifyUrl(String verifyUrl) {
        getStateHelper().put(PropertyKeys.verifyUrl, verifyUrl);
    }
}