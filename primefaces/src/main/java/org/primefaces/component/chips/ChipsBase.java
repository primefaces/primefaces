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
package org.primefaces.component.chips;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "itemSelect", event = SelectEvent.class, description = "Fires when an item is selected."),
    @FacesBehaviorEvent(name = "itemUnselect", event = UnselectEvent.class, description = "Fires when an item is unselected.")
})
public abstract class ChipsBase extends AbstractPrimeHtmlInputText implements Widget, InputHolder, MixedClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ChipsRenderer";

    public ChipsBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Maximum number of chips allowed.", defaultValue = "2147483647")
    public abstract int getMax();

    @Property(description = "Inline style for the input element.")
    public abstract String getInputStyle();

    @Property(description = "CSS class for the input element.")
    public abstract String getInputStyleClass();

    @Property(description = "When enabled, adds a chip when input loses focus.", defaultValue = "false")
    public abstract boolean isAddOnBlur();

    @Property(description = "When enabled, adds chips from pasted text.", defaultValue = "false")
    public abstract boolean isAddOnPaste();

    @Property(description = "When enabled, prevents duplicate chips.", defaultValue = "false")
    public abstract boolean isUnique();

    @Property(description = "Character(s) used to separate chips when pasting.")
    public abstract String getSeparator();
}