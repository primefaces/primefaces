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
package org.primefaces.component.sidebar;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.CloseEvent;

import jakarta.faces.component.UIComponentBase;
import jakarta.faces.event.AjaxBehaviorEvent;


@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "close", event = CloseEvent.class, defaultEvent = true, description = "Fires when the sidebar is closed."),
    @FacesBehaviorEvent(name = "open", event = AjaxBehaviorEvent.class, description = "Fires when the sidebar is opened."),
    @FacesBehaviorEvent(name = "loadContent", event = AjaxBehaviorEvent.class, description = "Fires when the content of the sidebar is loaded."),
})
public abstract class SidebarBase extends UIComponentBase implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SidebarRenderer";

    public SidebarBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Specifies the visibility of the sidebar.")
    public abstract boolean isVisible();

    @Property(defaultValue = "left", description = "Position of the sidebar.")
    public abstract String getPosition();

    @Property(description = "When enabled, sidebar is displayed in full screen mode.")
    public abstract boolean isFullScreen();

    @Property(description = "Whether to block scrolling of the document when sidebar is active.")
    public abstract boolean isBlockScroll();

    @Property(defaultValue = "0", description = "Base zIndex value to use in layering. "
            + "Only use this attribute if you are having issues with your sidebar displaying as "
            + "this may cause issues with overlay components inside the sidebar.")
    public abstract int getBaseZIndex();

    @Property(description = "Appends the sidebar to the given search expression.")
    public abstract String getAppendTo();

    @Property(description = "Defines if dynamic loading is enabled for the element's panel. "
            + "If the value is \"true\", the overlay is not rendered on page load to improve performance.")
    public abstract boolean isDynamic();

    @Property(description = "Client side callback to execute when sidebar is displayed.")
    public abstract String getOnShow();

    @Property(description = "Client side callback to execute when sidebar is hidden.")
    public abstract String getOnHide();

    @Property(defaultValue = "true", description = "Boolean value that specifies whether the document should be shielded "
            + "with a partially transparent mask to require the user to close the Panel before being able to activate any elements in the document.")
    public abstract boolean isModal();

    @Property(defaultValue = "true", description = "Displays a close icon to hide the overlay.")
    public abstract boolean isShowCloseIcon();
}