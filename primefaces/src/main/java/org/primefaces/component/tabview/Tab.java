/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import javax.faces.context.FacesContext;

public class Tab extends TabBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Tab";

    public boolean isLoaded() {
        Object value = getStateHelper().get("loaded");
        return Boolean.TRUE.equals(value);
    }

    public void setLoaded(boolean value) {
        if (value == false) {
            getStateHelper().remove("loaded");
        }
        else {
            getStateHelper().put("loaded", value);
        }
    }

    /**
     * In case of a repeating parent (var=...), we need to store the state on row basis.
     * This is actually a workaround because neither UITabPanel, not UIRepeat has a rowStatePreserved attribute.
     *
     * @param index The tab index.
     * @return if loaded or not.
     */
    public boolean isLoaded(int index) {
        Object value = getStateHelper().get("loaded_" + index);
        return Boolean.TRUE.equals(value);
    }

    /**
     * In case of a repeating parent (var=...), we need to store the state on row basis.
     * This is actually a workaround because neither UITabPanel, not UIRepeat has a rowStatePreserved attribute.
     *
     * @param index The tab index.
     * @param value The loaded state.
     */
    public void setLoaded(int index, boolean value) {
        if (value == false) {
            getStateHelper().remove("loaded_" + index);
        }
        else {
            getStateHelper().put("loaded_" + index, value);
        }
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (!isRendered() || isDisabled()) {
            return;
        }
        super.processDecodes(context);
    }
}