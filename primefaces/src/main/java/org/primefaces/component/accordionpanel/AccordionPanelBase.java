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
package org.primefaces.component.accordionpanel;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.MultiViewStateAware;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.UITabPanelImpl;
import org.primefaces.component.api.Widget;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "tabChange", event = TabChangeEvent.class, description = "Fires when a tab is changed.", defaultEvent = true),
    @FacesBehaviorEvent(name = "tabClose", event = TabCloseEvent.class, description = "Fires when a tab is closed.")
})
public abstract class AccordionPanelBase extends UITabPanelImpl implements Widget, RTLAware, StyleAware,
        MultiViewStateAware<AccordionState> {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.AccordionPanelRenderer";

    public AccordionPanelBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Id of the active tab.")
    public abstract String getActive();

    @Property(defaultValue = "0", description = "Index of the active tab.")
    public abstract String getActiveIndex();

    @Property(description = "Client side callback to execute when a tab is changed.")
    public abstract String getOnTabChange();

    @Property(description = "Client side callback to execute when a tab is shown.")
    public abstract String getOnTabShow();

    @Property(description = "Client side callback to execute when a tab is closed.")
    public abstract String getOnTabClose();

    @Property(defaultValue = "true", description = "When enabled, dynamically loaded tab contents are cached.")
    public abstract boolean isCache();

    @Property(description = "Allows multiple tabs to be active at the same time.")
    public abstract boolean isMultiple();

    @Property(defaultValue = "0", description = "Position of the element in the tabbing order.")
    public abstract String getTabindex();

    @Property(description = "MethodExpression to control tab access.")
    public abstract jakarta.el.MethodExpression getTabController();

    @Property(defaultValue = "500", description = "Speed of toggling in milliseconds.")
    public abstract int getToggleSpeed();

    @Property(description = "Scrolls the active tab into view. Valid values are start, center, end, nearest.")
    public abstract String getScrollIntoView();

    @Override
    @Property(hide = true)
    public int getOffset() {
        return super.getOffset();
    }

    @Override
    @Property(hide = true)
    public int getSize() {
        return super.getSize();
    }

    @Override
    @Property(hide = true)
    public int getStep() {
        return super.getStep();
    }
}