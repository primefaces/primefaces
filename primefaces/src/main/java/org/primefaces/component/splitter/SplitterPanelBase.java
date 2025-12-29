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
package org.primefaces.component.splitter;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;

import jakarta.faces.component.UIPanel;

@FacesComponentBase
public abstract class SplitterPanelBase extends UIPanel implements StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public SplitterPanelBase() {
        setRendererType(null);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "0", description = "Size of the element relative to 100%.")
    public abstract Integer getSize();

    @Property(defaultValue = "0", description = "Minimum size of the element relative to 100%.")
    public abstract Integer getMinSize();

    @Property(description = "Splitter handle ARIA label for screenreader support.")
    public abstract String getAriaLabel();

    @Property(description = "Establishes relationships between the splitter and panel label element IDs.")
    public abstract String getAriaLabelledBy();

    @Property(defaultValue = "0", description = "Position in the tabbing order.")
    public abstract String getTabindex();
}
