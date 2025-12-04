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
package org.primefaces.component.inputmask;

import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.Widget;

public abstract class InputMaskBase extends AbstractPrimeHtmlInputText implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InputMaskRenderer";

    public InputMaskBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Mask pattern to apply to the input.")
    public abstract String getMask();

    @Property(description = "Character to display in empty mask slots.", defaultValue = "_")
    public abstract String getSlotChar();

    @Property(description = "When enabled, clears the input when it doesn't match the mask.", defaultValue = "true")
    public abstract boolean isAutoClear();

    @Property(description = "When enabled, validates input against the mask.", defaultValue = "true")
    public abstract boolean isValidateMask();

    @Property(description = "When enabled, shows the mask when input receives focus.", defaultValue = "true")
    public abstract boolean isShowMaskOnFocus();

    @Property(description = "When enabled, shows the mask when hovering over the input.", defaultValue = "true")
    public abstract boolean isShowMaskOnHover();
}