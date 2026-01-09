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
package org.primefaces.component.dialog;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.MoveEvent;
import org.primefaces.event.ResizeEvent;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "close", event = CloseEvent.class, description = "Fires when dialog is closed.", defaultEvent = true),
    @FacesBehaviorEvent(name = "minimize", event = AjaxBehaviorEvent.class, description = "Fires when dialog is minimized."),
    @FacesBehaviorEvent(name = "maximize", event = AjaxBehaviorEvent.class, description = "Fires when dialog is maximized."),
    @FacesBehaviorEvent(name = "move", event = MoveEvent.class, description = "Fires when dialog is moved."),
    @FacesBehaviorEvent(name = "restoreMinimize", event = AjaxBehaviorEvent.class, description = "Fires when dialog is restored from minimized state."),
    @FacesBehaviorEvent(name = "restoreMaximize", event = AjaxBehaviorEvent.class, description = "Fires when dialog is restored from maximized state."),
    @FacesBehaviorEvent(name = "open", event = AjaxBehaviorEvent.class, description = "Fires when dialog is opened."),
    @FacesBehaviorEvent(name = "loadContent", event = AjaxBehaviorEvent.class, description = "Fires when dialog content is loaded."),
    @FacesBehaviorEvent(name = "resizeStart", event = ResizeEvent.class, description = "Fires when dialog resize starts."),
    @FacesBehaviorEvent(name = "resizeStop", event = ResizeEvent.class, description = "Fires when dialog resize stops.")
})
public abstract class DialogBase extends UIPanel implements Widget, RTLAware, StyleAware, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DialogRenderer";

    public DialogBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Allows to place HTML in the header. Alternative to header.")
    public abstract UIComponent getHeaderFacet();

    @Facet(description = "Allows to place HTML in the footer. Alternative to footer.")
    public abstract UIComponent getFooterFacet();

    @Facet(description = "Allows to add custom action to the titlebar.")
    public abstract UIComponent getActionsFacet();

    @Property(description = "Header text.")
    public abstract String getHeader();

    @Property(defaultValue = "true", description = "Makes dialog draggable.")
    public abstract boolean isDraggable();

    @Property(defaultValue = "true", description = "Makes dialog resizable.")
    public abstract boolean isResizable();

    @Property(defaultValue = "false", description = "Makes dialog modal.")
    public abstract boolean isModal();

    @Property(defaultValue = "false", description = "Blocks scrolling of the document when dialog is modal.")
    public abstract boolean isBlockScroll();

    @Property(defaultValue = "false", description = "Renders dialog as visible.")
    public abstract boolean isVisible();

    @Property(description = "Width of the dialog.")
    public abstract String getWidth();

    @Property(description = "Height of the dialog.")
    public abstract String getHeight();

    @Property(defaultValue = "Integer.MIN_VALUE", description = "Minimum width in pixels.")
    public abstract int getMinWidth();

    @Property(defaultValue = "Integer.MIN_VALUE", description = "Minimum height in pixels.")
    public abstract int getMinHeight();

    @Property(description = "Show effect to be used when displaying dialog.")
    public abstract String getShowEffect();

    @Property(description = "Hide effect to be used when hiding dialog.")
    public abstract String getHideEffect();

    @Property(description = "Position of the dialog relative to the target element.")
    public abstract String getMy();

    @Property(description = "Position of the target element relative to the dialog.")
    public abstract String getPosition();

    @Property(defaultValue = "true", description = "Makes dialog closable.")
    public abstract boolean isClosable();

    @Property(description = "Client-side callback to execute when dialog is hidden.")
    public abstract String getOnHide();

    @Property(description = "Client-side callback to execute when dialog is shown.")
    public abstract String getOnShow();

    @Property(implicitDefaultValue = "@(body)", description = "Append dialog to the element with the given identifier.")
    public abstract String getAppendTo();

    @Property(defaultValue = "true", description = "Renders dialog header.")
    public abstract boolean isShowHeader();

    @Property(description = "Footer text.")
    public abstract String getFooter();

    @Property(defaultValue = "false", description = "Enables lazy loading of dialog content.")
    public abstract boolean isDynamic();

    @Property(defaultValue = "false", description = "Makes dialog minimizable.")
    public abstract boolean isMinimizable();

    @Property(defaultValue = "false", description = "Makes dialog maximizable.")
    public abstract boolean isMaximizable();

    @Property(defaultValue = "false", description = "Closes dialog when escape key is pressed.")
    public abstract boolean isCloseOnEscape();

    @Property(description = "Component to receive focus when dialog is opened.")
    public abstract String getFocus();

    @Property(defaultValue = "false", description = "Fits dialog in the viewport.")
    public abstract boolean isFitViewport();

    @Property(defaultValue = "fixed", description = "Position type of the dialog, valid values are 'fixed' and 'absolute'.")
    public abstract String getPositionType();

    @Property(defaultValue = "true", description = "In responsive mode, dialog adjusts itself based on screen width.")
    public abstract boolean isResponsive();

    @Property(defaultValue = "true", description = "Caches the content when dynamic loading is enabled.")
    public abstract boolean isCache();
}