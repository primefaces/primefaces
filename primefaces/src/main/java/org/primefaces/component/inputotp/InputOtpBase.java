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
package org.primefaces.component.inputotp;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.PrimeUIInput;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class,
            description = "Fires when the focus leaves the component and the value was changed.", defaultEvent = true),
    @FacesBehaviorEvent(name = "complete", event = AjaxBehaviorEvent.class, description = "Fires when all characters are entered.")
})
public abstract class InputOtpBase extends PrimeUIInput implements Widget, StyleAware, InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InputOtpRenderer";

    public InputOtpBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "4", description = "Number of characters, one input per character.")
    public abstract int getLength();

    @Property(defaultValue = "false", description = "Accepts digits only and shows the numeric keyboard on mobile devices.")
    public abstract boolean isIntegerOnly();

    @Property(defaultValue = "false", description = "Masks the characters like a password.")
    public abstract boolean isMask();

    @Property(defaultValue = "false", description = "With mask: renders the value, otherwise the inputs are empty like a password.")
    public abstract boolean isRedisplay();

    @Property(description = "Text displayed between the inputs, the separator facet takes precedence.")
    public abstract String getSeparator();

    @Property(description = "Placeholder of the inputs: one character for all inputs, or one character per input.")
    public abstract String getPlaceholder();

    @Property(defaultValue = "one-time-code",
            description = "Autocomplete of the first input, the default lets mobile browsers fill in a code received by SMS.")
    public abstract String getAutocomplete();

    @Property(description = "Label of each input for screen readers, {0} is replaced by the position of the input."
            + " Defaults to the otpLabel of the locale.")
    public abstract String getAriaLabel();

    @Property(description = "User presentable name, used in the validation messages.")
    public abstract String getLabel();

    @Property(defaultValue = "false", description = "Disables the component.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "false", description = "Prevents changes by the user.")
    public abstract boolean isReadonly();

    @Property(description = "Position of the inputs in the tabbing order.")
    public abstract String getTabindex();

    @Property(description = "Inline style of the inputs.")
    public abstract String getInputStyle();

    @Property(description = "Style class of the inputs, e.g. p-inputtext-sm or p-inputtext-lg.")
    public abstract String getInputStyleClass();

    @Property(description = "Client side callback to execute when the value changes, the new value is passed as argument.")
    public abstract String getOnchange();

    @Facet(description = "Content displayed between the inputs, e.g. an icon.")
    public abstract UIComponent getSeparatorFacet();
}
