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
package org.primefaces.component.ajaxstatus;

import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIComponentBase;

public abstract class AjaxStatusBase extends UIComponentBase implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.AjaxStatusRenderer";

    public AjaxStatusBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Facet to show content when AJAX request starts.")
    public abstract UIComponent getStartFacet();

    @Facet(description = "Facet to show content when AJAX request completes successfully.")
    public abstract UIComponent getSuccessFacet();

    @Facet(description = "Facet to show content when AJAX request completes (success or error).")
    public abstract UIComponent getCompleteFacet();

    @Facet(description = "Facet to show content when AJAX request fails with error.")
    public abstract UIComponent getErrorFacet();

    @Facet(description = "Default facet to show content when there is no current AJAX activity.")
    public abstract UIComponent getDefaultStatusFacet();

    @Property(description = "Delay in milliseconds before showing the status.", defaultValue = "0")
    public abstract int getDelay();

    @Property(description = "Client-side callback to execute when AJAX request starts.")
    public abstract String getOnstart();

    @Property(description = "Client-side callback to execute when AJAX request completes.")
    public abstract String getOncomplete();

    @Property(description = "Client-side callback to execute when AJAX request succeeds.")
    public abstract String getOnsuccess();

    @Property(description = "Client-side callback to execute when AJAX request fails.")
    public abstract String getOnerror();

    @Property(description = "Inline style of the component.")
    public abstract String getStyle();

    @Property(description = "Style class of the component.")
    public abstract String getStyleClass();
}