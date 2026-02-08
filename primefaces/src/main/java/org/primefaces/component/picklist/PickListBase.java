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
package org.primefaces.component.picklist;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "transfer", event = TransferEvent.class, description = "Fires when items are transferred between lists.", defaultEvent = true),
    @FacesBehaviorEvent(name = "select", event = SelectEvent.class, description = "Fires when an item is selected."),
    @FacesBehaviorEvent(name = "unselect", event = UnselectEvent.class, description = "Fires when an item is unselected."),
    @FacesBehaviorEvent(name = "reorder", event = AjaxBehaviorEvent.class, description = "Fires when items are reordered.")
})
public abstract class PickListBase extends UIInput implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.PickListRenderer";

    public PickListBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Custom content for the source list caption.")
    public abstract UIComponent getSourceCaptionFacet();

    @Facet(description = "Custom content for the target list caption.")
    public abstract UIComponent getTargetCaptionFacet();

    @Property(description = "Name of the iterator variable used to reference each data.")
    public abstract String getVar();

    @Property(description = "Label of the item.")
    public abstract String getItemLabel();

    @Property(description = "Value of the item.")
    public abstract Object getItemValue();

    @Property(defaultValue = "false", description = "Disables the component.")
    public abstract boolean isDisabled();

    @Property(defaultValue = "fade", description = "Effect to use when transferring items. Valid values are 'fade', 'slide' and 'none'.")
    public abstract String getEffect();

    @Property(defaultValue = "fast", description = "Speed of the effect. Valid values are 'slow', 'normal' and 'fast'.")
    public abstract String getEffectSpeed();

    @Property(defaultValue = "false", description = "Shows reorder controls for source list.")
    public abstract boolean isShowSourceControls();

    @Property(defaultValue = "false", description = "Shows reorder controls for target list.")
    public abstract boolean isShowTargetControls();

    @Property(description = "Client side callback to execute when items are transferred.")
    public abstract String getOnTransfer();

    @Property(description = "Label for the component.")
    public abstract String getLabel();

    @Property(defaultValue = "false", description = "Defines if an item is disabled.")
    public abstract boolean isItemDisabled();

    @Property(defaultValue = "false", description = "Shows filter input for source list.")
    public abstract boolean isShowSourceFilter();

    @Property(defaultValue = "false", description = "Shows filter input for target list.")
    public abstract boolean isShowTargetFilter();

    @Property(description = "Placeholder text for source filter input.")
    public abstract String getSourceFilterPlaceholder();

    @Property(description = "Placeholder text for target filter input.")
    public abstract String getTargetFilterPlaceholder();

    @Property(description = "Match mode for filtering, " +
        "valid values are 'startsWith', 'endsWith', 'contains', 'exact', 'lt', 'lte', 'gt', 'gte', 'equals', 'in' and 'custom'.")
    public abstract String getFilterMatchMode();

    @Property(description = "Client side function to use for custom filtering.")
    public abstract String getFilterFunction();

    @Property(defaultValue = "false", description = "Shows checkbox for each item.")
    public abstract boolean isShowCheckbox();

    @Property(defaultValue = "tooltip", description = "Defines how labels are displayed. Valid values are 'tooltip' and 'inline'.")
    public abstract String getLabelDisplay();

    @Property(defaultValue = "horizontal", description = "Defines layout orientation. Valid values are 'horizontal' and 'vertical'.")
    public abstract String getOrientation();

    @Property(defaultValue = "false", description = "In responsive mode, picklist adjusts itself based on screen size.")
    public abstract boolean isResponsive();

    @Property(defaultValue = "0", description = "Position of the element in the tabbing order.")
    public abstract String getTabindex();

    @Property(description = "Event to trigger filtering. Valid values are 'keyup', 'keydown' and 'keypress'.")
    public abstract String getFilterEvent();

    @Property(defaultValue = "Integer.MAX_VALUE", description = "Delay in milliseconds before sending each filter query.")
    public abstract int getFilterDelay();

    @Property(defaultValue = "true", description = "Escapes special HTML characters in item labels.")
    public abstract boolean isEscape();

    @Property(defaultValue = "true", description = "Escapes special HTML characters in item values.")
    public abstract boolean isEscapeValue();

    @Property(defaultValue = "true", description = "Transfers items on double click.")
    public abstract boolean isTransferOnDblclick();

    @Property(defaultValue = "false", description = "Transfers items when checkbox is clicked.")
    public abstract boolean isTransferOnCheckboxClick();

    @Property(defaultValue = "false", description = "Normalizes the filter input by removing accents.")
    public abstract boolean isFilterNormalize();

    @Property(defaultValue = "true", description = "Enables drag and drop functionality.")
    public abstract boolean isDragDrop();
}