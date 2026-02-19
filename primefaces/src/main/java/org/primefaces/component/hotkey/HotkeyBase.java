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
package org.primefaces.component.hotkey;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AjaxSource;

import jakarta.el.MethodExpression;
import jakarta.faces.component.UICommand;
import jakarta.faces.event.ActionListener;

@FacesComponentBase
public abstract class HotkeyBase extends UICommand implements AjaxSource {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.HotkeyRenderer";

    public HotkeyBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "A method expression or a string outcome to process when command is executed.", skipAccessors = true)
    public MethodExpression getAction() {
        return super.getActionExpression();
    }

    @Property(description = "An action listener to process when command is executed.", skipAccessors = true)
    public ActionListener getActionListener() {
        return super.getActionListeners()[0];
    }

    @Property(required = true, description = "The key binding.")
    public abstract String getBind();

    @Property(description = "Javascript event handler to be executed when the key binding is pressed.")
    public abstract String getHandler();

    @Property(defaultValue = "false", description = "Disables the hotKey binding.")
    public abstract boolean isDisabled();

    @Property(description = "An alternative key binding for macOS.")
    public abstract String getBindMac();
}