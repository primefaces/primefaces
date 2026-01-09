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
package org.primefaces.component.datagrid;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.FlexAware;
import org.primefaces.component.api.MultiViewStateAware;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.component.api.Widget;
import org.primefaces.event.data.PageEvent;

import jakarta.faces.component.behavior.ClientBehaviorHolder;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "page", event = PageEvent.class, description = "Fired on pagination.")
})
public abstract class DataGridBase extends UIPageableData
        implements Widget, StyleAware, ClientBehaviorHolder, PrimeClientBehaviorHolder, FlexAware, MultiViewStateAware<DataGridState> {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataGridRenderer";

    public DataGridBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "3", description = "Number of columns of grid.")
    public abstract int getColumns();

    @Property(description = "Inline style of each row (grid-cell).")
    public abstract String getRowStyle();

    @Property(description = "Style class of each row (grid-cell).")
    public abstract String getRowStyleClass();

    @Property(description = "Title for each row.")
    public abstract String getRowTitle();

}