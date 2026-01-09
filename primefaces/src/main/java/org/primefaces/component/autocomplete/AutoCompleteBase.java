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
package org.primefaces.component.autocomplete;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.AbstractPrimeHtmlInputText;
import org.primefaces.component.api.InputHolder;
import org.primefaces.component.api.MixedClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.LazyDataModel;

import jakarta.faces.component.UIComponent;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "itemSelect", event = SelectEvent.class, description = "Fires when an item is selected."),
    @FacesBehaviorEvent(name = "itemUnselect", event = UnselectEvent.class, description = "Fires when an item is unselected."),
    @FacesBehaviorEvent(name = "moreTextSelect", event = AjaxBehaviorEvent.class, description = "Fires when a more text is selected."),
    @FacesBehaviorEvent(name = "emptyMessageSelect", event = AjaxBehaviorEvent.class, description = "Fires when an empty message is selected."),
    @FacesBehaviorEvent(name = "clear", event = AjaxBehaviorEvent.class, description = "Fires when the component is cleared."),
    @FacesBehaviorEvent(name = "query", event = AjaxBehaviorEvent.class, description = "Fires when a search query is triggered."),
})
public abstract class AutoCompleteBase extends AbstractPrimeHtmlInputText implements Widget, InputHolder, MixedClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.AutoCompleteRenderer";

    public AutoCompleteBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Facet to render footer.")
    public abstract UIComponent getFooterFacet();

    @Facet(description = "Facet to render item tip.")
    public abstract UIComponent getItemtipFacet();

    @Property(description = "MethodExpression to provide suggestions for autocomplete functionality.")
    public abstract jakarta.el.MethodExpression getCompleteMethod();

    @Property(description = "Name of the iterator variable used in itemLabel and itemValue.")
    public abstract String getVar();

    @Property(description = "Property to use for displaying items.")
    public abstract String getItemLabel();

    @Property(description = "Property to use for item value.")
    public abstract Object getItemValue();

    @Property(description = "CSS class for each item.")
    public abstract String getItemStyleClass();

    @Property(description = "Maximum number of results to display.", defaultValue = "2147483647")
    public abstract int getMaxResults();

    @Property(description = "Minimum number of characters to trigger autocomplete query.", defaultValue = "1")
    public abstract int getMinQueryLength();

    @Property(description = "Delay in milliseconds before triggering autocomplete query.", defaultValue = "300")
    public abstract int getQueryDelay();

    @Property(description = "When enabled, forces selection from suggestions.", defaultValue = "false")
    public abstract boolean isForceSelection();

    @Property(description = "Maximum height in pixels before scrolling is enabled.", defaultValue = "2147483647")
    public abstract int getScrollHeight();

    @Property(description = "When enabled, shows dropdown button.", defaultValue = "false")
    public abstract boolean isDropdown();

    @Property(description = "Inline style for the suggestions panel.")
    public abstract String getPanelStyle();

    @Property(description = "CSS class for the suggestions panel.")
    public abstract String getPanelStyleClass();

    @Property(description = "When enabled, allows multiple selections.", defaultValue = "false")
    public abstract boolean isMultiple();

    @Property(description = "Position of the item tooltip relative to the item.")
    public abstract String getItemtipMyPosition();

    @Property(description = "Position of the item tooltip relative to the target.")
    public abstract String getItemtipAtPosition();

    @Property(description = "When enabled, caches suggestions on client side.", defaultValue = "false")
    public abstract boolean isCache();

    @Property(description = "Timeout in milliseconds for cached suggestions.", defaultValue = "300000")
    public abstract int getCacheTimeout();

    @Property(description = "Search expression to append the panel to.", defaultValue = "@(body)")
    public abstract String getAppendTo();

    @Property(description = "Property to group suggestions by.")
    public abstract Object getGroupBy();

    @Property(description = "Event to trigger autocomplete query.")
    public abstract String getQueryEvent();

    @Property(description = "Dropdown mode. Options: 'current', 'blank'.")
    public abstract String getDropdownMode();

    @Property(description = "When enabled, automatically highlights first suggestion.", defaultValue = "true")
    public abstract boolean isAutoHighlight();

    @Property(description = "Maximum number of items that can be selected.", defaultValue = "2147483647")
    public abstract int getSelectLimit();

    @Property(description = "Inline style for the input element.")
    public abstract String getInputStyle();

    @Property(description = "CSS class for the input element.")
    public abstract String getInputStyleClass();

    @Property(description = "Tooltip text for group headers.")
    public abstract String getGroupByTooltip();

    @Property(description = "Position of the panel relative to the input.")
    public abstract String getMy();

    @Property(description = "Position of the panel relative to the target.")
    public abstract String getAt();

    @Property(description = "When enabled, component is active.", defaultValue = "true")
    public abstract boolean isActive();

    @Property(description = "Text to display when there are more results.", defaultValue = "...")
    public abstract String getMoreText();

    @Property(description = "When enabled, prevents duplicate selections.", defaultValue = "false")
    public abstract boolean isUnique();

    @Property(description = "When enabled, loads suggestions dynamically.", defaultValue = "false")
    public abstract boolean isDynamic();

    @Property(description = "When enabled, automatically selects highlighted suggestion.", defaultValue = "true")
    public abstract boolean isAutoSelection();

    @Property(description = "When enabled, escapes HTML in suggestions.", defaultValue = "true")
    public abstract boolean isEscape();

    @Property(description = "Query mode. Options: 'server', 'client', 'hybrid'.", defaultValue = "server")
    public abstract String getQueryMode();

    @Property(description = "Tabindex for the dropdown button.")
    public abstract String getDropdownTabindex();

    @Property(description = "Endpoint URL for REST-based autocomplete.")
    public abstract String getCompleteEndpoint();

    @Property(description = "LazyDataModel for lazy loading suggestions.")
    public abstract LazyDataModel<?> getLazyModel();

    @Property(description = "Field name for lazy model filtering.")
    public abstract String getLazyField();

    @Property(description = "When enabled, shows empty message when no suggestions.", defaultValue = "true")
    public abstract boolean isShowEmptyMessage();

    @Property(description = "CSS selector for highlighting matched text.")
    public abstract String getHighlightSelector();

    @Property(description = "Message to display when no suggestions are available.")
    public abstract String getEmptyMessage();
}