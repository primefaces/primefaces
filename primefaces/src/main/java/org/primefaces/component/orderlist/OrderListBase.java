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
package org.primefaces.component.orderlist;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.FlexAware;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "select", event = SelectEvent.class, description = "Fires when an item is selected."),
    @FacesBehaviorEvent(name = "unselect", event = UnselectEvent.class, description = "Fires when an item is unselected."),
    @FacesBehaviorEvent(name = "reorder", event = AjaxBehaviorEvent.class, description = "Fires when items are reordered.")
})
public abstract class OrderListBase extends UIInput implements Widget, PrimeClientBehaviorHolder, FlexAware, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.OrderListRenderer";

    public OrderListBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Caption of the list.")
    public abstract UIComponent getCaptionFacet();

    @Property(description = "Name of the iterator variable used to reference each data in the list.")
    public abstract String getVar();

    @Property(description = "Label of the item.")
    public abstract String getItemLabel();

    @Property(description = "Value of the item.")
    public abstract Object getItemValue();

    @Property(defaultValue = "false", description = "Disables the component.")
    public abstract boolean isDisabled();

    @Property(description = "Effect to use when moving items.")
    public abstract String getEffect();

    @Property(defaultValue = "Move Up", description = "Label for move up button.")
    public abstract String getMoveUpLabel();

    @Property(defaultValue = "Move Top", description = "Label for move top button.")
    public abstract String getMoveTopLabel();

    @Property(defaultValue = "Move Down", description = "Label for move down button.")
    public abstract String getMoveDownLabel();

    @Property(defaultValue = "Move Bottom", description = "Label for move bottom button.")
    public abstract String getMoveBottomLabel();

    @Property(defaultValue = "left", description = "Location of the controls, valid values are \"left\" and \"right\".")
    public abstract String getControlsLocation();

    @Property(defaultValue = "false", description = "In responsive mode, the layout of OrderList is optimized for smaller screens.")
    public abstract boolean isResponsive();

}