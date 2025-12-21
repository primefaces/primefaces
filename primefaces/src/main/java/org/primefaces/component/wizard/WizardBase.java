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
package org.primefaces.component.wizard;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponentBase;
import jakarta.faces.component.behavior.ClientBehaviorHolder;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "next", event = AjaxBehaviorEvent.class, description = "When \"next\" is triggered."),
    @FacesBehaviorEvent(name = "back", event = AjaxBehaviorEvent.class, description = "When \"back\" is triggered.")
})
public abstract class WizardBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.WizardRenderer";

    public WizardBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Id of the current step in flow.")
    public abstract String getStep();

    @Property(description = "Style of the main wizard container element.")
    public abstract String getStyle();

    @Property(description = "Style class of the main wizard container element.")
    public abstract String getStyleClass();

    @Property(description = "Server side listener to invoke when wizard attempts to go forward or back.")
    public abstract jakarta.el.MethodExpression getFlowListener();

    @Property(description = "Specifies visibility of default navigator arrows.", defaultValue = "true")
    public abstract boolean isShowNavBar();

    @Property(description = "Specifies visibility of default step title bar.", defaultValue = "true")
    public abstract boolean isShowStepStatus();

    @Property(description = "Javascript event handler to be invoked when flow goes back.")
    public abstract String getOnback();

    @Property(description = "Javascript event handler to be invoked when flow goes forward.")
    public abstract String getOnnext();

    @Property(description = "Label of next navigation button.", defaultValue = "Next")
    public abstract String getNextLabel();

    @Property(description = "Label of back navigation button.", defaultValue = "Back")
    public abstract String getBackLabel();

    @Property(description = "If yes, the model will be updated when the \"Back\" button is clicked.", defaultValue = "false")
    public abstract boolean isUpdateModelOnPrev();

    @Property(description = "Animation effect to use when showing and hiding wizard panel.", implicitDefaultValue = "No animation")
    public abstract String getEffect();

    @Property(description = "Duration of the animation effect in milliseconds.", defaultValue = "400")
    public abstract int getEffectDuration();

    @Property(description = "If true, next and back navigation buttons will be disabled during Ajax requests triggered by them.", defaultValue = "true")
    public abstract boolean isDisableOnAjax();

    @Property(description = "If true, all completed steps are highlighted. If false, only the current step is highlighted.", defaultValue = "false")
    public abstract boolean isHighlightCompletedSteps();
}