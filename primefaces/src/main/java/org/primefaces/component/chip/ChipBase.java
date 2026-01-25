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
package org.primefaces.component.chip;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.UIComponentBase;
import jakarta.faces.component.behavior.ClientBehaviorHolder;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "select", event = AjaxBehaviorEvent.class, description = "Fires when the chip gets selected.", defaultEvent = true),
    @FacesBehaviorEvent(name = "close", event = AjaxBehaviorEvent.class, description = "Fires when the chip gets closed."),
})
public abstract class ChipBase extends UIComponentBase implements Widget, ClientBehaviorHolder, PrimeClientBehaviorHolder, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.ChipRenderer";

    public ChipBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Defines the text to display.")
    public abstract String getLabel();

    @Property(description = "Defines the icon to display.")
    public abstract String getIcon();

    @Property(description = "Defines the image to display.")
    public abstract String getImage();

    @Property(description = "Whether to display a remove icon.", defaultValue = "false")
    public abstract boolean isRemovable();

    @Property(description = "Icon of the remove element.", defaultValue = "pi pi-times-circle")
    public abstract String getRemoveIcon();
}
