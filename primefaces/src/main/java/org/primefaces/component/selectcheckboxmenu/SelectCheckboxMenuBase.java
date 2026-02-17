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
package org.primefaces.component.selectcheckboxmenu;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.PrimeSelect;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;

import jakarta.faces.component.html.HtmlSelectManyCheckbox;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "toggleSelect", event = ToggleSelectEvent.class, description = "Fires when the selectAll checkbox is toggled."),
    @FacesBehaviorEvent(name = "change", event = AjaxBehaviorEvent.class, description = "Fires when the element's value changes.", defaultEvent = true),
    @FacesBehaviorEvent(name = "itemSelect", event = SelectEvent.class, description = "Fires when an item is selected."),
    @FacesBehaviorEvent(name = "itemUnselect", event = UnselectEvent.class, description = "Fires when an item is unselected.")
})
public abstract class SelectCheckboxMenuBase extends HtmlSelectManyCheckbox implements Widget, InputHolder, StyleAware, PrimeSelect {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.SelectCheckboxMenuRenderer";

    public SelectCheckboxMenuBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "Defines the maximum height of the scrollable area.",
            defaultValue = "200")
    public abstract String getScrollHeight();

    @Property(description = "Client side callback to execute when overlay is displayed.")
    public abstract String getOnShow();

    @Property(description = "Client side callback to execute when overlay is hidden.")
    public abstract String getOnHide();

    @Property(description = "Renders an input field as a filter when enabled.",
            defaultValue = "false")
    public abstract boolean isFilter();

    @Property(description = "Match mode for filtering, valid values are startsWith (default), contains, endsWith and custom.")
    public abstract String getFilterMatchMode();

    @Property(description = "Client side function to use in custom filterMatchMode.")
    public abstract String getFilterFunction();

    @Property(description = "Placeholder text to show when filter input is empty.")
    public abstract String getFilterPlaceholder();

    @Property(description = "Defines if filtering would be case sensitive.",
            defaultValue = "false")
    public abstract boolean isCaseSensitive();

    @Property(description = "Defines if filtering would be done using normalized values (accents will be removed from characters).",
            defaultValue = "false")
    public abstract boolean isFilterNormalize();

    @Property(description = "Inline style of the dropdown panel container element.")
    public abstract String getPanelStyle();

    @Property(description = "Style class of the dropdown panel container element.")
    public abstract String getPanelStyleClass();

    @Property(description = "Name of iterator to be used in custom content display.")
    public abstract String getVar();

    @Property(description = "Appends the overlay to the element defined by search expression.",
            defaultValue = "@(body)")
    public abstract String getAppendTo();

    @Override
    @Property(description = "Position of the element in the tabbing order.")
    public abstract String getTabindex();

    @Override
    @Property(description = "Advisory tooltip information.")
    public abstract String getTitle();

    @Property(description = "When enabled, the header of panel is displayed.",
            defaultValue = "true")
    public abstract boolean isShowHeader();

    @Property(description = "When enabled, the \"Select All\" checkbox option is displayed.",
            defaultValue = "true")
    public abstract boolean isShowSelectAll();

    @Property(description = "When enabled, the label is updated on every change, else it statically displays the 'selectedLabel'.",
            defaultValue = "false")
    public abstract boolean isUpdateLabel();

    @Property(description = "Enables multiple selection mode.",
            defaultValue = "false")
    public abstract boolean isMultiple();

    @Property(description = "Defines if dynamic loading is enabled for the element's panel."
            + " If the value is \"true\", the overlay is not rendered on page load to improve performance.",
            defaultValue = "false")
    public abstract boolean isDynamic();

    @Property(description = "Separator for joining item lables if updateLabel is set to true.",
            defaultValue = ", ")
    public abstract String getLabelSeparator();

    @Property(description = "Label to be shown in updateLabel mode when no item is selected. If not set the label is shown.")
    public abstract String getEmptyLabel();

    @Property(description = "Label to be shown in updateLabel mode when one or more items are selected. If not set the label is shown.")
    public abstract String getSelectedLabel();
}