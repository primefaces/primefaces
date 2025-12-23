/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.dock;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;

@FacesComponentBase
public abstract class DockBase extends AbstractMenu implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DockRenderer";

    public DockBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    @Property(description = "MenuModel instance to create menus programmatically")
    public abstract org.primefaces.model.menu.MenuModel getModel();

    @Property(defaultValue = "bottom", description = "Position of the dock, bottom or top.")
    public abstract String getPosition();

    @Property(defaultValue = "center", description = "Horizontal alignment. left, center, or right")
    public abstract String getHalign();

    @Property(defaultValue = "false", description = "Whether to block scrolling of the document.")
    public abstract boolean isBlockScroll();

    @Property(defaultValue = "true", description = "Whether to animate the OSX bounce effect when clicking an item.")
    public abstract boolean isAnimate();

    @Property(defaultValue = "1600", description = "How long in milliseconds to animate the bounce effect.")
    public abstract int getAnimationDuration();

}