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
package org.primefaces.component.overlaypanel;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIPanel;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "show", event = AjaxBehaviorEvent.class, description = "Fires when overlay panel is shown.", defaultEvent = true),
    @FacesBehaviorEvent(name = "hide", event = AjaxBehaviorEvent.class, description = "Fires when overlay panel is hidden."),
    @FacesBehaviorEvent(name = "loadContent", event = AjaxBehaviorEvent.class, description = "Fires when overlay panel content is loaded.")
})
public abstract class OverlayPanelBase extends UIPanel implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.OverlayPanelRenderer";

    public OverlayPanelBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Target component to attach overlay panel to.")
    public abstract String getFor();

    @Property(implicitDefaultValue = "click", description = "Event name to show the overlay panel.")
    public abstract String getShowEvent();

    @Property(implicitDefaultValue = "click", description = "Event name to hide the overlay panel.")
    public abstract String getHideEvent();

    @Property(description = "Component to append overlay panel to.")
    public abstract String getAppendTo();

    @Property(description = "Client-side callback to execute when overlay panel is shown.")
    public abstract String getOnShow();

    @Property(description = "Client-side callback to execute when overlay panel is hidden.")
    public abstract String getOnHide();

    @Property(implicitDefaultValue = "left top", description = "Position of the overlay panel relative to the target.")
    public abstract String getMy();

    @Property(implicitDefaultValue = "left bottom", description = "Position of the target element.")
    public abstract String getAt();

    @Property(implicitDefaultValue = "flip", description = "When the overlay panel collides with the viewport, it will be repositioned.")
    public abstract String getCollision();

    @Property(defaultValue = "false", description = "When true, overlay panel content is loaded dynamically.")
    public abstract boolean isDynamic();

    @Property(defaultValue = "true", description = "When true, overlay panel can be dismissed by clicking outside of it.")
    public abstract boolean isDismissable();

    @Property(defaultValue = "false", description = "When true, displays a close icon.")
    public abstract boolean isShowCloseIcon();

    @Property(defaultValue = "false", description = "When true, overlay panel is displayed as modal.")
    public abstract boolean isModal();

    @Property(defaultValue = "false", description = "When true, blocks scrolling when overlay panel is shown.")
    public abstract boolean isBlockScroll();

    @Property(defaultValue = "0", description = "Delay in milliseconds before showing the overlay panel.")
    public abstract int getShowDelay();

    @Property(defaultValue = "true", description = "When true, overlay panel automatically hides when clicking outside.")
    public abstract boolean isAutoHide();

    @Property(defaultValue = "true", description = "When true, overlay panel content is cached.")
    public abstract boolean isCache();
}