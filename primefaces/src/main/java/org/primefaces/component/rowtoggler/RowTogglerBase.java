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
package org.primefaces.component.rowtoggler;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
public abstract class RowTogglerBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.RowTogglerRenderer";

    public RowTogglerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Expand text to display instead of icon to.")
    public abstract String getExpandLabel();

    @Property(description = "Collapse text to display instead of icon.")
    public abstract String getCollapseLabel();

    @Property(description = "Icon to expand rowExpansion",
            defaultValue = "ui-icon-circle-triangle-e")
    public abstract String getExpandIcon();

    @Property(description = "Icon to collapse rowExpansion",
            defaultValue = "ui-icon-circle-triangle-s")
    public abstract String getCollapseIcon();

    @Property(description = "Position of the element in the tabbing order.",
            defaultValue = "0")
    public abstract String getTabindex();
}