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
package org.primefaces.component.megamenu;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

import jakarta.faces.component.UIComponent;

@FacesComponentBase
public abstract class MegaMenuBase extends AbstractMenu implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.MegaMenuRenderer";

    public MegaMenuBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Allows custom HTML in the start of the menu.")
    public abstract UIComponent getStartFacet();

    @Facet(description = "Allows custom HTML in the end of the menu.")
    public abstract UIComponent getEndFacet();

    @Facet(description = "Allows custom HTML in the options of the menu.")
    public abstract UIComponent getOptionsFacet();

    @Override
    @Property(description = "MenuModel instance to create menus programmatically")
    public abstract org.primefaces.model.menu.MenuModel getModel();

    @Property(defaultValue = "0", description = "Delay in milliseconds before displaying the submenu.")
    public abstract int getDelay();

    @Property(description = "Index of the active root menu to display as highlighted. By default no root is highlighted.")
    public abstract int getActiveIndex();

    @Property(defaultValue = "horizontal", description = "Defines the orientation of the root menuitems, valid values are \"horizontal\" and \"vertical\".")
    public abstract String getOrientation();

}
