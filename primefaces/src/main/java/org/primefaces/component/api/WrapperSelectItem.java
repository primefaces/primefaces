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
package org.primefaces.component.api;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UISelectItem;
import jakarta.faces.model.SelectItem;

/**
 * Wraps a SelectItem so its {@code <f:selectitem>} passthrough attributes can be used.
 */
public class WrapperSelectItem extends SelectItem {

    private static final long serialVersionUID = 1L;

    /**
     * Owning component like `{@code <f:selectitem>} or {@code <f:selectitems>}
     */
    private transient UIComponent component;

    public WrapperSelectItem() {
        super();
    }

    public WrapperSelectItem(UISelectItem component) {
        super(component.getItemValue(),
                component.getItemLabel(),
                component.getItemDescription(),
                component.isItemDisabled(),
                component.isItemEscaped(),
                component.isNoSelectionOption());
        setComponent(component);
    }

    public WrapperSelectItem(Object value, String label, String description, boolean disabled, boolean escape,
            boolean noSelectionOption) {
        super(value, label, description, disabled, escape, noSelectionOption);
    }


    public UIComponent getComponent() {
        return component;
    }

    public void setComponent(UIComponent component) {
        this.component = component;
    }

}
