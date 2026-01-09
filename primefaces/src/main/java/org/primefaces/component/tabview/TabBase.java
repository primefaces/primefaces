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
package org.primefaces.component.tabview;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;

@FacesComponentBase
public abstract class TabBase extends UIPanel {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public TabBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Allows to add custom action to the tab header.")
    public abstract UIComponent getActionsFacet();

    @Facet(description = "Allows to add custom options to the tab header.")
    public abstract UIComponent getOptionsFacet();

    @Facet(description = "Allows to place HTML in the title. Alternative to title.")
    public abstract UIComponent getTitleFacet();

    @Property(description = "Title text of the tab.")
    public abstract String getTitle();

    @Property(description = "Inline style of the tab title.")
    public abstract String getTitleStyle();

    @Property(description = "Style class of the tab title.")
    public abstract String getTitleStyleClass();

    @Property(defaultValue = "false", description = "When true, tab cannot be activated.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "false", description = "When true, tab can be closed.")
    public abstract boolean isClosable();

    @Property(description = "Tooltip text of the tab title.")
    public abstract String getTitletip();

    @Property(description = "Label for screen readers.")
    public abstract String getAriaLabel();

    @Property(description = "Title text for the menu.")
    public abstract String getMenuTitle();

    @Property(description = "Unique key to identify the tab.")
    public abstract String getKey();

}