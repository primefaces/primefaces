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
package org.primefaces.component.divider;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.model.menu.Separator;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
public abstract class DividerBase extends UIComponentBase implements Separator {
    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DividerRenderer";

    public DividerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Alignment of the content, options are 'left', 'center', 'right' for horizontal and 'top', 'center', 'bottom' for vertical.")
    public abstract String getAlign();

    @Property(defaultValue = "horizontal", description = "Specifies the orientation, valid values are \"horizontal\" and \"vertical\".")
    public abstract String getLayout();

    @Property(defaultValue = "solid", description = "Border style type, \"solid\", \"dashed\" and \"dotted\".")
    public abstract String getType();
}
