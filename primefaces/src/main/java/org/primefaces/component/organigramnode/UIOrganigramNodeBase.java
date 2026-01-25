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
package org.primefaces.component.organigramnode;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
public abstract class UIOrganigramNodeBase extends UIComponentBase implements StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public UIOrganigramNodeBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Type of the node.")
    public abstract String getType();

    @Property(description = "Icon of the node.")
    public abstract String getIcon();

    @Property(description = "Position of the icon, valid values are \"left\" and \"right\".")
    public abstract String getIconPos();

    @Property(description = "Icon to display when node is expanded.")
    public abstract String getExpandedIcon();

    @Property(description = "Icon to display when node is collapsed.")
    public abstract String getCollapsedIcon();

    @Property(defaultValue = "false", description = "When true, skips the default leaf handling.")
    public abstract boolean isSkipLeafHandling();
}