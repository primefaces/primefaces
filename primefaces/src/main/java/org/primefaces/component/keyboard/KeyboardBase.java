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
package org.primefaces.component.keyboard;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.Widget;

@FacesComponentBase
public abstract class KeyboardBase extends AbstractPrimeHtmlInputText implements Widget {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.KeyboardRenderer";

    public KeyboardBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "When enabled, masks input as password.", defaultValue = "false")
    public abstract boolean isPassword();

    @Property(description = "Mode to show the keyboard. Options: 'focus', 'click', 'button'.", defaultValue = "focus")
    public abstract String getShowMode();

    @Property(description = "Image URL for the keyboard toggle button.")
    public abstract String getButtonImage();

    @Property(description = "When enabled, shows only the button image without text.", defaultValue = "false")
    public abstract boolean isButtonImageOnly();

    @Property(description = "Animation effect when showing/hiding keyboard.", defaultValue = "fadeIn")
    public abstract String getEffect();

    @Property(description = "Duration of the animation effect.")
    public abstract String getEffectDuration();

    @Property(description = "Keyboard layout. Options: 'qwerty', 'alpha', 'international', 'custom'.", defaultValue = "qwerty")
    public abstract String getLayout();

    @Property(description = "Custom layout template for keyboard.")
    public abstract String getLayoutTemplate();

    @Property(description = "When enabled, shows only numeric keypad.", defaultValue = "false")
    public abstract boolean isKeypadOnly();

    @Property(description = "Label for prompt button.")
    public abstract String getPromptLabel();

    @Property(description = "Label for close button.")
    public abstract String getCloseLabel();

    @Property(description = "Label for clear button.")
    public abstract String getClearLabel();

    @Property(description = "Label for backspace button.")
    public abstract String getBackspaceLabel();
}