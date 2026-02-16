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
package org.primefaces.component.dataview;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.FlexAware;
import org.primefaces.component.api.MultiViewStateAware;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.component.api.Widget;
import org.primefaces.event.data.PageEvent;

import jakarta.faces.component.UIComponent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "page", event = PageEvent.class, description = "Fired on pagination.", defaultEvent = true)
})
public abstract class DataViewBase extends UIPageableData
        implements Widget, StyleAware, MultiViewStateAware<DataViewState>, FlexAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataViewRenderer";

    public DataViewBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Header content of the dataview.")
    public abstract UIComponent getHeaderFacet();

    @Facet(description = "Empty message content of the dataview.")
    public abstract UIComponent getEmptyMessageFacet();

    @Property(defaultValue = "list", description = "Layout of the items, valid values are \"list\" and \"grid\".")
    public abstract String getLayout();

    @Property(description = "Icon to display for grid layout toggle button.")
    public abstract String getGridIcon();

    @Property(description = "Icon to display for list layout toggle button.")
    public abstract String getListIcon();

    @Property(description = "Inline style of each row (grid-cell) in grid layout.")
    public abstract String getGridRowStyle();

    @Property(description = "Style class of each row (grid-cell) in grid layout.")
    public abstract String getGridRowStyleClass();

    @Property(description = "Title for each row.")
    public abstract String getRowTitle();
}
