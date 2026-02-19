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
package org.primefaces.component.chronoline;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeUIData;
import org.primefaces.component.api.StyleAware;

import jakarta.faces.component.UIComponent;

@FacesComponentBase
public abstract class ChronolineBase extends PrimeUIData implements StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ChronolineRenderer";

    public ChronolineBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Opposite content of the chronoline.")
    public abstract UIComponent getOppositeFacet();

    @Facet(description = "Marker content of the chronoline.")
    public abstract UIComponent getMarkerFacet();

    @Property(description = "Position of the chronoline bar relative to the content. Valid values are \"left\", \"right\" for vertical layout and \"top\", "
            + "\"bottom\" for horizontal layout.", defaultValue = "left")
    public abstract String getAlign();

    @Property(description = "Orientation of the chronoline, valid values are \"vertical\" and \"horizontal\".", defaultValue = "vertical")
    public abstract String getLayout();

    @Override
    @Property(internal = true)
    public abstract String getRowIndexVar();

    @Override
    @Property(internal = true)
    public abstract int getFirst();

    @Override
    @Property(internal = true)
    public abstract boolean isLazy();

    @Override
    @Property(internal = true)
    public abstract int getRows();

    @Override
    @Property(internal = true)
    public abstract boolean isRowStatePreserved();

    @Override
    @Property(description = "Value of the data component.", required = true)
    public Object getValue() {
        return super.getValue();
    }
}
