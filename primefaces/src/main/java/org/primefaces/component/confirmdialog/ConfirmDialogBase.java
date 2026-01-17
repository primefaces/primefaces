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
package org.primefaces.component.confirmdialog;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;

@FacesComponentBase
public abstract class ConfirmDialogBase extends UIPanel implements Widget, RTLAware, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ConfirmDialogRenderer";

    public ConfirmDialogBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Allows to place HTML in the header. Alternative to header.")
    public abstract UIComponent getHeaderFacet();

    @Facet(description = "Allows to place HTML in the message area. Alternative to message.")
    public abstract UIComponent getMessageFacet();

    @Property(description = "Message text to display.")
    public abstract String getMessage();

    @Property(description = "Header text.")
    public abstract String getHeader();

    @Property(defaultValue = "alert", description = "Severity of the message, valid values are 'alert' and 'info'.")
    public abstract String getSeverity();

    @Property(description = "Width of the dialog.")
    public abstract String getWidth();

    @Property(description = "Height of the dialog.")
    public abstract String getHeight();

    @Property(defaultValue = "true", description = "Makes dialog closable.")
    public abstract boolean isClosable();

    @Property(implicitDefaultValue = "@(body)", description = "Append dialog to the element with the given identifier.")
    public abstract String getAppendTo();

    @Property(defaultValue = "false", description = "Renders dialog as visible.")
    public abstract boolean isVisible();

    @Property(description = "Show effect to be used when displaying dialog.")
    public abstract String getShowEffect();

    @Property(description = "Hide effect to be used when hiding dialog.")
    public abstract String getHideEffect();

    @Property(defaultValue = "false", description = "Closes dialog when escape key is pressed.")
    public abstract boolean isCloseOnEscape();

    @Property(defaultValue = "false", description = "Closes dialog by clicking on the modal background mask.")
    public abstract boolean isDismissibleMask();

    @Property(defaultValue = "false", description = "When enabled, confirmDialog becomes a shared for other components that require confirmation.")
    public abstract boolean isGlobal();

    @Property(defaultValue = "false", description = "In responsive mode, dialog adjusts itself based on screen width.")
    public abstract boolean isResponsive();
}