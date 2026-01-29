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
package org.primefaces.component.toggleswitch;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIInput;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class, description = "Fired when the value changes.", defaultEvent = true)
})
public abstract class ToggleSwitchBase extends UIInput implements Widget, StyleAware, InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ToggleSwitchRenderer";

    public ToggleSwitchBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "User presentable name.")
    public abstract String getLabel();

    @Property(description = "The aria-label attribute is used to define a string that labels the current element for accessibility.")
    public abstract String getAriaLabel();

    @Property(defaultValue = "false", description = "Disables or enables the switch.")
    public abstract boolean isDisabled();

    @Property(description = "Client side callback to execute on value change event.")
    public abstract String getOnchange();

    @Property(description = "The tabindex attribute specifies the tab order of an element when the \"tab\" button is used for navigating.")
    public abstract String getTabindex();

    @Property(description = "Client side callback to execute when component receives focus.")
    public abstract String getOnfocus();

    @Property(description = "Client side callback to execute when component loses focus.")
    public abstract String getOnblur();

    @Property(description = "The icon representing the \"On\" position.")
    public abstract String getOnIcon();

    @Property(description = "The icon representing the \"Off\" position.")
    public abstract String getOffIcon();

    @Property(defaultValue = "false", description = "Flag indicating that this component will prevent changes by the user.")
    public abstract boolean isReadonly();

    @Override
    public String getInputClientId() {
        return this.getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return this.getInputClientId();
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    @Override
    public String getAriaDescribedBy() {
        return (String) getStateHelper().get("ariaDescribedBy");
    }

    @Override
    public void setAriaDescribedBy(String ariaDescribedBy) {
        getStateHelper().put("ariaDescribedBy", ariaDescribedBy);
    }
}