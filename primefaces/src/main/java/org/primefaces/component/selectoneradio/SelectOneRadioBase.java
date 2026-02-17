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
package org.primefaces.component.selectoneradio;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.FlexAware;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.PrimeSelect;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.html.HtmlSelectOneRadio;

@FacesComponentBase
public abstract class SelectOneRadioBase extends HtmlSelectOneRadio implements Widget, FlexAware, InputHolder, PrimeSelect {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectOneRadioRenderer";

    public SelectOneRadioBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Custom content of the selectoneradio.")
    public abstract UIComponent getCustomFacet();

    @Property(defaultValue = "12", description = "Defines the number of columns in grid layout.")
    public abstract int getColumns();

    @Property(defaultValue = "false", description = "Unselectable mode when true clicking a radio again will clear the selection.")
    public abstract boolean isUnselectable();

    @Property(description = "Comma separated list of column style classes.")
    public abstract String getColumnClasses();


}