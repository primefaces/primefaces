/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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

    @Property(description = "Desired symbol or unit to display, e.g. '$' or ' kg'.", implicitDefaultValue = "none")
    public abstract String getSymbol();

    @Property(description = "Placement of the negative/positive sign relative to the symbolPosition option."
            + " The sign is placed on either side of the symbolPosition, which can be placed on either side of the numbers."
            + " Options: 'p' for prefix, 's' for suffix, 'l' for left, 'r' for right.")
    public abstract String getSignPosition();

    @Property(description = "Position of the symbol. Options: 'p' for prefix, 's' for suffix.", implicitDefaultValue = "p")
    public abstract String getSymbolPosition();

    @Property(description = "Minimum value allowed. WARNING: if minValue is greater than 0, you effectively prevent your users from entirely"
            + " deleting the content of their input.", implicitDefaultValue = "-10000000000000")
    public abstract String getMinValue();

    @Property(description = "Maximum value allowed. WARNING: if maxValue is lower than 0, you effectively prevent your users from entirely"
            + " deleting the content of their input.", implicitDefaultValue = "10000000000000")
    public abstract String getMaxValue();

    @Property(description = "Controls the rounding method. Options: 'S' for round-half-up symmetric, 'A' for round-half-up asymmetric,"
            + " 's' for round-half-down symmetric, 'a' for round-half-down asymmetric, 'B' for round-half-even (banker's rounding),"
            + " 'U' for round up (away from zero), 'D' for round down (toward zero), 'C' for round to ceiling (toward positive infinity),"
            + " 'F' for round to floor (toward negative infinity), 'N05' or 'CHF' for round to the nearest 0.05,"
            + " 'U05' for round up to the next 0.05, 'D05' for round down to the next 0.05.",
        defaultValue = "S")
    public abstract String getRoundMethod();

    @Property(description = "Number of decimal places. If the value is a Byte/Short/Integer/Long/BigInteger it defaults to 0.",
        implicitDefaultValue = "2")
    public abstract String getDecimalPlaces();

    @Property(description = "Specifies the number of decimal places to retain for the raw value, while decimalPlaces is used for the displayed"
            + " value. If left as null, the decimalPlaces value is used. Note: setting this to fewer decimal places than those displayed may"
            + " cause user confusion.")
    public abstract Integer getDecimalPlacesRawValue();

    @Property(description = "Decimal separator character. Defaults to locale-specific separator if not specified.")
    public abstract String getDecimalSeparator();

    @Property(description = "Thousand separator character. Defaults to locale-specific separator if not specified.")
    public abstract String getThousandSeparator();

    @Property(description = "Defines what to display when the input value is empty. Options: 'empty' (or null) to display nothing,"
            + " 'focus' to display the symbol on focus, 'press' to display the symbol while a key is pressed, 'always' to always display"
            + " the symbol, 'min' or 'max' to display the minValue/maxValue, 'zero' to display zero, or a string representing a number.",
        defaultValue = "focus")
    public abstract String getEmptyValue();

    @Property(description = "Inline style of the input element.")
    public abstract String getInputStyle();

    @Property(description = "Style class of the input element.")
    public abstract String getInputStyleClass();

    @Property(description = "Allow padding the decimal places with zeros. If set to 'true' it will always pad the decimal places with zeros,"
            + " and never if set to 'false'. If set to 'floats', padding is only done when there are some decimals (up to the number of decimal"
            + " places from the decimalPlaces attribute). If set to an integer greater than 0, padding will use that number for adding the"
            + " zeros; 0 is not a valid value.",
        defaultValue = "true")
    public abstract String getPadControl();

    @Property(description = "Controls leading zero behavior. Options: 'allow' to allow the leading zero while typing and remove it on focus"
            + " out, 'deny' to prevent the leading zero from being entered, 'keep' to allow and keep the leading zero.",
        defaultValue = "allow")
    public abstract String getLeadingZero();

    @Property(description = "Allows declaring an alternative decimal separator which is automatically replaced by decimalSeparator when typed.")
    public abstract String getDecimalSeparatorAlternative();

    @Property(description = "Allows the user to increment or decrement the element value with the mouse wheel.", defaultValue = "true")
    public abstract boolean isModifyValueOnWheel();

    @Property(description = "Allows the user to increment or decrement the element value with the up and down arrow keys.", defaultValue = "true")
    public abstract boolean isModifyValueOnUpDownArrow();

    @Property(description = "Defines if the element value should be selected on focus.", defaultValue = "true")
    public abstract boolean isSelectOnFocus();

    @Property(description = "Defines where the caret should be positioned on focus."
            + " Options: 'start', 'end', 'decimalLeft', 'decimalRight'.")
    public abstract String getCaretPositionOnFocus();
}
