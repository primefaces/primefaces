/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.colorpicker;

import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.RTLAware;
import org.primefaces.component.api.Widget;

public abstract class ColorPickerBase extends AbstractPrimeHtmlInputText implements Widget, PrimeClientBehaviorHolder, RTLAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ColorPickerRenderer";

    public static final String CLEAR_LABEL = "primefaces.colorpicker.CLEAR";
    public static final String CLOSE_LABEL = "primefaces.colorpicker.CLOSE";

    public enum PropertyKeys {
        widgetVar,
        mode,
        theme,
        themeMode,
        format,
        formatToggle,
        alpha,
        forceAlpha,
        swatchesOnly,
        focusInput,
        selectInput,
        clearButton,
        closeButton,
        swatches;
    }

    public ColorPickerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getWidgetVar() {
        return (String) getStateHelper().eval(PropertyKeys.widgetVar, null);
    }

    public void setWidgetVar(String widgetVar) {
        getStateHelper().put(PropertyKeys.widgetVar, widgetVar);
    }

    public String getMode() {
        return (String) getStateHelper().eval(PropertyKeys.mode, "popup");
    }

    public void setMode(String mode) {
        getStateHelper().put(PropertyKeys.mode, mode);
    }

    public String getTheme() {
        return (String) getStateHelper().eval(PropertyKeys.theme, "default");
    }

    public void setTheme(String theme) {
        getStateHelper().put(PropertyKeys.theme, theme);
    }

    public String getThemeMode() {
        return (String) getStateHelper().eval(PropertyKeys.themeMode, "auto");
    }

    public void setThemeMode(String themeMode) {
        getStateHelper().put(PropertyKeys.themeMode, themeMode);
    }

    public String getFormat() {
        return (String) getStateHelper().eval(PropertyKeys.format, "hex");
    }

    public void setFormat(String format) {
        getStateHelper().put(PropertyKeys.format, format);
    }

    public boolean isFormatToggle() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.formatToggle, false);
    }

    public void setFormatToggle(boolean formatToggle) {
        getStateHelper().put(PropertyKeys.formatToggle, formatToggle);
    }

    public boolean isAlpha() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.alpha, true);
    }

    public void setAlpha(boolean alpha) {
        getStateHelper().put(PropertyKeys.alpha, alpha);
    }

    public boolean isForceAlpha() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.forceAlpha, false);
    }

    public void setForceAlpha(boolean forceAlpha) {
        getStateHelper().put(PropertyKeys.forceAlpha, forceAlpha);
    }

    public boolean isSwatchesOnly() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.swatchesOnly, false);
    }

    public void setSwatchesOnly(boolean swatchesOnly) {
        getStateHelper().put(PropertyKeys.swatchesOnly, swatchesOnly);
    }

    public boolean isFocusInput() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.focusInput, true);
    }

    public void setFocusInput(boolean focusInput) {
        getStateHelper().put(PropertyKeys.focusInput, focusInput);
    }

    public boolean isSelectInput() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.selectInput, false);
    }

    public void setSelectInput(boolean selectInput) {
        getStateHelper().put(PropertyKeys.selectInput, selectInput);
    }

    public boolean isClearButton() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.clearButton, false);
    }

    public void setClearButton(boolean clearButton) {
        getStateHelper().put(PropertyKeys.clearButton, clearButton);
    }

    public boolean isCloseButton() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.closeButton, false);
    }

    public void setCloseButton(boolean closeButton) {
        getStateHelper().put(PropertyKeys.closeButton, closeButton);
    }

    public String getSwatches() {
        return (String) getStateHelper().eval(PropertyKeys.swatches, null);
    }

    public void setSwatches(String swatches) {
        getStateHelper().put(PropertyKeys.swatches, swatches);
    }
}