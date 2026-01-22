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
package org.primefaces.component.spinner;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.Widget;

@FacesComponentBase
public abstract class SpinnerBase extends AbstractPrimeHtmlInputText implements Widget, InputHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SpinnerRenderer";

    public static final String BUTTONS_STACKED = "stacked";
    public static final String BUTTONS_HORIZONTAL = "horizontal";
    public static final String BUTTONS_HORIZONTAL_AFTER = "horizontal-after";
    public static final String BUTTONS_VERTICAL = "vertical";
    public static final Double MAX_VALUE = Double.MAX_VALUE;
    public static final Double MIN_VALUE = -Double.MAX_VALUE;

    public SpinnerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Step factor for increment/decrement operations.", defaultValue = "1.0")
    public abstract double getStepFactor();

    @Property(description = "Minimum value allowed.", defaultValue = "-1.7976931348623157E308")
    public abstract double getMin();

    @Property(description = "Maximum value allowed.", defaultValue = "1.7976931348623157E308")
    public abstract double getMax();

    @Property(description = "Prefix text to display before the value.")
    public abstract String getPrefix();

    @Property(description = "Suffix text to display after the value.")
    public abstract String getSuffix();

    @Property(description = "Number of decimal places to display.")
    public abstract String getDecimalPlaces();

    @Property(description = "Decimal separator character. Defaults to locale-specific separator if not specified.")
    public abstract String getDecimalSeparator();

    @Property(description = "Thousand separator character. Defaults to locale-specific separator if not specified.")
    public abstract String getThousandSeparator();

    @Property(description = "When enabled, values rotate from max to min when incrementing past max.", defaultValue = "false")
    public abstract boolean isRotate();

    @Property(description = "When enabled, values are rounded to the nearest step.", defaultValue = "false")
    public abstract boolean isRound();

    @Property(description = "Layout of the spinner buttons.", defaultValue = "stacked")
    public abstract String getButtons();

    @Property(description = "Icon for the up/increment button.")
    public abstract String getUpIcon();

    @Property(description = "Icon for the down/decrement button.")
    public abstract String getDownIcon();

    @Property(description = "CSS class for the up/increment button.")
    public abstract String getUpButtonStyleClass();

    @Property(description = "CSS class for the down/decrement button.")
    public abstract String getDownButtonStyleClass();

    @Property(description = "When enabled, mouse wheel modifies the value.", defaultValue = "true")
    public abstract boolean isModifyValueOnWheel();

}