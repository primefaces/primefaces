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
package org.primefaces.component.panelmenu;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

@FacesComponentBase
public abstract class PanelMenuBase extends AbstractMenu implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PanelMenuRenderer";

    public PanelMenuBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    @Property(description = "MenuModel instance to build menu dynamically.")
    public abstract org.primefaces.model.menu.MenuModel getModel();

    @Property(defaultValue = "true", description = "Allows multiple root submenus to be expanded at the same time.")
    public abstract boolean isMultiple();

    @Property(defaultValue = "true", description = "Menu state is saved in HTML5 LocalStorage.")
    public abstract boolean isStateful();

    @Property(defaultValue = "false",
        description = "When enabled, menu state is saved globally across pages. If disabled then state is stored per view/page.")
    public abstract boolean isStatefulGlobal();

}