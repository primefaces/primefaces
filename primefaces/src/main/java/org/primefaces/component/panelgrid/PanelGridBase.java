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
package org.primefaces.component.panelgrid;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;

@FacesComponentBase
public abstract class PanelGridBase extends UIPanel implements StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PanelGridRenderer";

    public static final String LAYOUT_TABULAR = "tabular";
    public static final String LAYOUT_GRID = "grid";
    public static final String LAYOUT_FLEX = "flex";
    public static final String LAYOUT_TAILWIND = "tailwind";

    public PanelGridBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Allows custom HTML in the header.")
    public abstract UIComponent getHeaderFacet();

    @Facet(description = "Allows custom HTML in the footer.")
    public abstract UIComponent getFooterFacet();

    @Property(defaultValue = "12", description = "Number of columns to display.")
    public abstract int getColumns();

    @Property(description = "Inline style of the content area.")
    public abstract String getContentStyle();

    @Property(description = "Style class of the content area.")
    public abstract String getContentStyleClass();

    @Property(description = "Comma separated list of CSS classes to apply to columns.")
    public abstract String getColumnClasses();

    @Property(defaultValue = "grid", description = "Layout mode, valid values are \"grid\", \"tabular\", \"flex\" and \"tailwind\".")
    public abstract String getLayout();

    @Property(defaultValue = "grid", description = "ARIA role attribute.")
    public abstract String getRole();
}