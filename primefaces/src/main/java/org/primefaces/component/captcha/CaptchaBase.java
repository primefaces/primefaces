/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIInput;

@FacesComponentBase
public abstract class CaptchaBase extends UIInput implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CaptchaRenderer";

    public CaptchaBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "g-recaptcha", description = "Which captcha to use, either 'g-recaptcha' for Google reCaptcha or 'h-captcha` for hCaptcha.")
    public abstract String getType();

    @Property(defaultValue = "auto", description = "Theme of the captcha either 'light', 'dark', or 'auto'.")
    public abstract String getTheme();

    @Property(defaultValue = "en", description = "Key of the supported languages.")
    public abstract String getLanguage();

    @Property(defaultValue = "0", description = "Position of the input element in the tabbing order.")
    public abstract int getTabindex();

    @Property(description = "A localized user presentable name.")
    public abstract String getLabel();

    @Property(description = "Callback executed when the user submits a successful captcha response.")
    public abstract String getCallback();

    @Property(description = "Callback executed when the captcha response expires and the user needs to solve a new captcha.")
    public abstract String getExpired();

    @Property(description = "The size of the widget.")
    public abstract String getSize();

    @Property(implicitDefaultValue = "grecaptcha", description = "JavaScript global executor for the captcha.")
    public abstract String getExecutor();

    @Property(description = "URL for the ReCaptcha JavaScript file. Some countries do not have access to Google.")
    public abstract String getSourceUrl();

    @Property(description = "URL for the verification of the captcha.")
    public abstract String getVerifyUrl();
}