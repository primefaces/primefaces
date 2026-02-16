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
package org.primefaces.component.cascadeselect;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.PrimeSelect;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UISelectOne;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "itemSelect", event = SelectEvent.class, description = "Fires when an item is selected.", defaultEvent = true)
})
public abstract class CascadeSelectBase extends UISelectOne implements Widget, InputHolder, StyleAware, PrimeSelect {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CascadeSelectRenderer";

    public CascadeSelectBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Content of the cascade select.")
    public abstract UIComponent getContentFacet();

    @Property(description = "Name of the iterator variable that references each element in the data set.")
    public abstract String getVar();

    @Property(defaultValue = "@(body)", description = "Appends the overlay to the element defined by search expression.")
    public abstract String getAppendTo();

    @Property(description = "The placeholder attribute specifies a short hint that describes the expected value of an input field.")
    public abstract String getPlaceholder();

    @Property(description = "Position in the tabbing order.")
    public abstract String getTabindex();

    @Property(description = "Disables or enables the component.")
    public abstract boolean isDisabled();

    @Property(description = "Flag indicating that this component will prevent changes by the user.")
    public abstract boolean isReadonly();
}