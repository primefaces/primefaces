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
package org.primefaces.component.inputnumber;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.Widget;

@FacesComponentBase
public abstract class InputNumberBase extends AbstractPrimeHtmlInputText implements Widget, InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.InputNumberRenderer";

    public InputNumberBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Currency symbol to display.")
    public abstract String getSymbol();

    @Property(description = "Position of the sign. Options: 'p' for prefix, 's' for suffix.")
    public abstract String getSignPosition();

    @Property(description = "Position of the symbol. Options: 'p' for prefix, 's' for suffix.")
    public abstract String getSymbolPosition();

    @Property(description = "Minimum value allowed.")
    public abstract String getMinValue();

    @Property(description = "Maximum value allowed.")
    public abstract String getMaxValue();

    @Property(description = "Rounding method. Options: 'S' for standard, 'A' for away from zero, 'C' for ceiling, 'F' for floor, 'D' for down, 'U' for up.",
        defaultValue = "S")
    public abstract String getRoundMethod();

    @Property(description = "Number of decimal places to display.")
    public abstract String getDecimalPlaces();

    @Property(description = "Raw value for decimal places.")
    public abstract Integer getDecimalPlacesRawValue();

    @Property(description = "Decimal separator character. Defaults to locale-specific separator if not specified.")
    public abstract String getDecimalSeparator();

    @Property(description = "Thousand separator character. Defaults to locale-specific separator if not specified.")
    public abstract String getThousandSeparator();

    @Property(description = "Empty value behavior. Options: 'focus', 'blur', 'always', 'never'.", defaultValue = "focus")
    public abstract String getEmptyValue();

    @Property(description = "Inline style for the input element.")
    public abstract String getInputStyle();

    @Property(description = "CSS class for the input element.")
    public abstract String getInputStyleClass();

    @Property(description = "Padding control. Options: 'true', 'false', 'floats'.", defaultValue = "true")
    public abstract String getPadControl();

    @Property(description = "Leading zero behavior. Options: 'allow', 'deny', 'keep'.", defaultValue = "allow")
    public abstract String getLeadingZero();

    @Property(description = "Alternative decimal separator character.")
    public abstract String getDecimalSeparatorAlternative();

    @Property(description = "When enabled, mouse wheel modifies the value.", defaultValue = "true")
    public abstract boolean isModifyValueOnWheel();

    @Property(description = "When enabled, up/down arrow keys modify the value.", defaultValue = "true")
    public abstract boolean isModifyValueOnUpDownArrow();

    @Property(description = "When enabled, selects all text on focus.", defaultValue = "true")
    public abstract boolean isSelectOnFocus();

    @Property(description = "Caret position on focus. Options: 'start', 'end', 'min', 'max'.")
    public abstract String getCaretPositionOnFocus();
}