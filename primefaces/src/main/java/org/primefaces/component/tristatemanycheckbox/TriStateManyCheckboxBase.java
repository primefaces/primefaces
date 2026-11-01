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
package org.primefaces.component.tristatemanycheckbox;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeSelect;
import org.primefaces.component.api.PrimeUIInput;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class, description = "Fires when the state of an item was changed.",
            defaultEvent = true)
})
public abstract class TriStateManyCheckboxBase extends PrimeUIInput implements Widget, StyleAware, PrimeSelect {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.TriStateManyCheckboxRenderer";

    public TriStateManyCheckboxBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "lineDirection", description = "Layout of the items: lineDirection (in a row) or pageDirection (one per line).")
    public abstract String getLayout();

    @Property(description = "Icon (style class) of the first state, by default the box is empty.")
    public abstract String getStateOneIcon();

    @Property(description = "Icon (style class) of the second state, by default the checked icon of the theme.")
    public abstract String getStateTwoIcon();

    @Property(description = "Icon (style class) of the third state, by default the crossed icon of the theme.")
    public abstract String getStateThreeIcon();

    @Property(description = "Title/tooltip of the first state.")
    public abstract String getStateOneTitle();

    @Property(description = "Title/tooltip of the second state.")
    public abstract String getStateTwoTitle();

    @Property(description = "Title/tooltip of the third state.")
    public abstract String getStateThreeTitle();

    @Property(description = "User presentable name, used in the validation messages.")
    public abstract String getLabel();

    @Property(defaultValue = "false", description = "Disables the component.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "false", description = "Prevents changes by the user.")
    public abstract boolean isReadonly();

    @Property(description = "Position of the items in the tabbing order.")
    public abstract String getTabindex();

    @Property(description = "Client side callback to execute when the state of an item was changed.")
    public abstract String getOnchange();
}
