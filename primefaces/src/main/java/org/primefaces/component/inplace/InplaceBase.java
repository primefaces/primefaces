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
package org.primefaces.component.inplace;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIComponentBase;
import jakarta.faces.event.BehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "save", event = BehaviorEvent.class, description = "Fires when inplace is saved."),
    @FacesBehaviorEvent(name = "cancel", event = BehaviorEvent.class, description = "Fires when inplace is cancelled.")
})
public abstract class InplaceBase extends UIComponentBase implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InplaceRenderer";

    public static final String MODE_OUTPUT = "output";
    public static final String MODE_INPUT = "input";

    public InplaceBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Custom content for the display area. Alternative to label and value.")
    public abstract UIComponent getOutputFacet();

    @Facet(description = "Custom content for the input area. Alternative to children.")
    public abstract UIComponent getInputFacet();

    @Property(description = "Label to display.")
    public abstract String getLabel();

    @Property(description = "Label to display when value is empty.")
    public abstract String getEmptyLabel();

    @Property(defaultValue = "fade", description = "Effect to use when toggling, valid values are \"fade\", \"slide\" and \"none\".")
    public abstract String getEffect();

    @Property(defaultValue = "normal", description = "Speed of the effect, valid values are \"slow\", \"normal\" and \"fast\".")
    public abstract String getEffectSpeed();

    @Property(defaultValue = "false", description = "Disables the component.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "false", description = "Displays save and cancel buttons when enabled.")
    public abstract boolean isEditor();

    @Property(defaultValue = "Save", description = "Label of the save button in editor mode.")
    public abstract String getSaveLabel();

    @Property(defaultValue = "Cancel", description = "Label of the cancel button in editor mode.")
    public abstract String getCancelLabel();

    @Property(defaultValue = "click", description = "Client side event to trigger display of inline content.")
    public abstract String getEvent();

    @Property(defaultValue = "true", description = "Makes the component toggleable.")
    public abstract boolean isToggleable();

    @Property(defaultValue = "output", description = "Mode of the component, valid values are \"output\" and \"input\".")
    public abstract String getMode();

    @Property(defaultValue = "0", description = "Position of the element in the tabbing order.")
    public abstract String getTabindex();
}