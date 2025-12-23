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
package org.primefaces.component.colorpicker;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;

import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class, description = "Fires when the color is changed.", defaultEvent = true),
    @FacesBehaviorEvent(name = "open", event = AjaxBehaviorEvent.class, description = "Fires when the color picker is opened."),
    @FacesBehaviorEvent(name = "close", event = AjaxBehaviorEvent.class, description = "Fires when the color picker is closed.")
})
public abstract class ColorPickerBase extends AbstractPrimeHtmlInputText implements Widget, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ColorPickerRenderer";

    public ColorPickerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Display mode. Options: 'popup', 'inline'.", defaultValue = "popup")
    public abstract String getMode();

    @Property(description = "Color picker theme.", defaultValue = "default")
    public abstract String getTheme();

    @Property(description = "Theme mode. Options: 'auto', 'light', 'dark'.", defaultValue = "auto")
    public abstract String getThemeMode();

    @Property(description = "Color format. Options: 'hex', 'rgb', 'hsl', 'hsv'.", defaultValue = "hex")
    public abstract String getFormat();

    @Property(description = "When enabled, allows toggling between color formats.", defaultValue = "false")
    public abstract boolean isFormatToggle();

    @Property(description = "When enabled, includes alpha channel (transparency).", defaultValue = "true")
    public abstract boolean isAlpha();

    @Property(description = "When enabled, forces alpha channel to be always visible.", defaultValue = "false")
    public abstract boolean isForceAlpha();

    @Property(description = "When enabled, shows only color swatches.", defaultValue = "false")
    public abstract boolean isSwatchesOnly();

    @Property(description = "When enabled, focuses the input field.", defaultValue = "true")
    public abstract boolean isFocusInput();

    @Property(description = "When enabled, selects the input text on focus.", defaultValue = "false")
    public abstract boolean isSelectInput();

    @Property(description = "When enabled, shows clear button.", defaultValue = "false")
    public abstract boolean isClearButton();

    @Property(description = "When enabled, shows close button.", defaultValue = "false")
    public abstract boolean isCloseButton();

    @Property(description = "Comma-separated list of color swatches to display.")
    public abstract String getSwatches();

    @Property(description = "Locale for the color picker.")
    public abstract Object getLocale();
}