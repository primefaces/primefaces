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
package org.primefaces.component.selectonebutton;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UISelectOne;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class, description = "Fires when the value of the button changes.", defaultEvent = true)
})
public abstract class SelectOneButtonBase extends UISelectOne implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectOneButtonRenderer";

    public SelectOneButtonBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Disables or enables the component.",
            defaultValue = "false")
    public abstract boolean isDisabled();

    @Property(description = "Label of the button.")
    public abstract String getLabel();

    @Property(description = "Layout of the selectOneButton, valid values are 'custom' or 'default'.")
    public abstract String getLayout();

    @Property(description = "Javascript to execute when the value of the button changes.")
    public abstract String getOnchange();

    @Property(description = "Position of the element in the tabbing order.",
            defaultValue = "0")
    public abstract String getTabindex();

    @Property(description = "Unselectable mode when true allows the button to be unselected.",
            defaultValue = "true")
    public abstract boolean isUnselectable();
}