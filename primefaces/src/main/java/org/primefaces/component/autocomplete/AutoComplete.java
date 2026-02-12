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

import org.primefaces.cdk.api.FacesComponentHandler;
import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.cdk.api.PrimeClientBehaviorEventKeys;
import org.primefaces.component.api.UIPageableData;
import org.primefaces.component.column.Column;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.MatchMode;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.el.MethodExpression;
import jakarta.faces.FacesException;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = AutoComplete.COMPONENT_TYPE, namespace = AutoComplete.COMPONENT_FAMILY)
@FacesComponentInfo(description = "AutoComplete is a component that provides live suggestions while an input is being typed.")
@FacesComponentHandler(AutoCompleteHandler.class)
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class AutoComplete extends AutoCompleteBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.AutoComplete";
    public static final String STYLE_CLASS = "ui-autocomplete";
    public static final String MULTIPLE_STYLE_CLASS = "ui-autocomplete ui-autocomplete-multiple";
    public static final String DROPDOWN_SYLE_CLASS = "ui-autocomplete-dd";
    public static final String INPUT_CLASS = "ui-autocomplete-input ui-inputfield ui-widget ui-state-default";
    public static final String INPUT_WITH_DROPDOWN_CLASS = "ui-autocomplete-input ui-autocomplete-dd-input ui-inputfield ui-widget ui-state-default";
    public static final String DROPDOWN_CLASS = "ui-autocomplete-dropdown ui-button ui-widget ui-state-default ui-button-icon-only";
    public static final String PANEL_CLASS = "ui-autocomplete-panel ui-widget-content ui-helper-hidden ui-shadow ui-input-overlay";
    public static final String LIST_CLASS = "ui-autocomplete-items ui-autocomplete-list ui-widget-content ui-widget ui-helper-reset";
    public static final String TABLE_CLASS = "ui-autocomplete-items ui-autocomplete-table ui-widget-content ui-widget ui-helper-reset";
    public static final String ITEM_CLASS = "ui-autocomplete-item ui-autocomplete-list-item";
    public static final String ROW_CLASS = "ui-autocomplete-item ui-autocomplete-row ui-widget-content";
    public static final String TOKEN_DISPLAY_CLASS = "ui-autocomplete-token ui-state-active";
    public static final String TOKEN_LABEL_CLASS = "ui-autocomplete-token-label";
    public static final String TOKEN_LABEL_DISABLED_CLASS = "ui-autocomplete-token-label-disabled";
    public static final String TOKEN_ICON_CLASS = "ui-autocomplete-token-icon ui-icon ui-icon-close";
    public static final String TOKEN_INPUT_CLASS = "ui-autocomplete-input-token";
    public static final String MULTIPLE_CONTAINER_CLASS = "ui-autocomplete-multiple-container ui-widget ui-inputfield ui-state-default";
    public static final String MULTIPLE_CONTAINER_WITH_DROPDOWN_CLASS = "ui-autocomplete-multiple-container ui-autocomplete-dd-multiple-container ui-widget ui-inputfield ui-state-default";
    public static final String ITEMTIP_CONTENT_CLASS = "ui-autocomplete-itemtip-content";
    public static final String MORE_TEXT_LIST_CLASS = "ui-autocomplete-item ui-autocomplete-moretext";
    public static final String MORE_TEXT_TABLE_CLASS = "ui-autocomplete-item ui-autocomplete-moretext ui-widget-content";

    protected static final List<PrimeClientBehaviorEventKeys> UNOBTRUSIVE_EVENT_KEYS = LangUtils.unmodifiableList(
            ClientBehaviorEventKeys.itemSelect, ClientBehaviorEventKeys.itemUnselect,
            ClientBehaviorEventKeys.query, ClientBehaviorEventKeys.moreTextSelect,
            ClientBehaviorEventKeys.emptyMessageSelect, ClientBehaviorEventKeys.clear);

    private Object suggestions;

    private Integer suggestionsCount;

    @Override
    public Collection<PrimeClientBehaviorEventKeys> getUnobtrusiveClientBehaviorEventKeys() {
        return UNOBTRUSIVE_EVENT_KEYS;
    }

    public boolean isMoreTextRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_moreText");
    }

    public boolean isDynamicLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_dynamicload");
    }

    public boolean isClientCacheRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_clientCache");
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = event.getFacesContext();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (isAjaxBehaviorEventSource(event)) {
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemSelect)) {
                Object selectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(getClientId(context) + "_itemSelect"));
                SelectEvent<?> selectEvent = new SelectEvent<>(this, ajaxBehaviorEvent.getBehavior(), selectedItemValue);
                selectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(selectEvent);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemUnselect)) {
                Object unselectedItemValue = ComponentUtils.getConvertedValue(context, this, params.get(getClientId(context) + "_itemUnselect"));
                UnselectEvent<?> unselectEvent = new UnselectEvent<>(this, ajaxBehaviorEvent.getBehavior(), unselectedItemValue);
                unselectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(unselectEvent);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.moreTextSelect)
                || isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.emptyMessageSelect)
                || isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.clear)) {
                ajaxBehaviorEvent.setPhaseId(event.getPhaseId());
                super.queueEvent(ajaxBehaviorEvent);
            }
            else {
                //e.g. blur, focus, change
                super.queueEvent(event);
            }
        }
        else {
            //e.g. valueChange, autoCompleteEvent
            super.queueEvent(event);
        }
    }

    @Override
    public void broadcast(jakarta.faces.event.FacesEvent event) throws jakarta.faces.event.AbortProcessingException {
        super.broadcast(event);

        if (!(event instanceof org.primefaces.event.AutoCompleteEvent)) {
            return;
        }

        String query = ((org.primefaces.event.AutoCompleteEvent) event).getQuery();
        LazyDataModel<?> lazyModel = getLazyModel();
        if (lazyModel != null) {
            String field = getLazyField();
            if (LangUtils.isEmpty(field)) {
                throw new FacesException("lazyField is required with lazyModel");
            }
            Map<String, FilterMeta> searchFilter = new HashMap<>();
            searchFilter.put(field,
                    FilterMeta.builder()
                            .field(field)
                            .filterValue(query)
                            .matchMode(MatchMode.CONTAINS)
                            .build());
            Map<String, SortMeta> sortBy = new HashMap<>();
            sortBy.put(field,
                    SortMeta.builder()
                            .field(field)
                            .order(SortOrder.ASCENDING)
                            .build());
            suggestions = lazyModel.load(0, getMaxResults(), sortBy, searchFilter);
            suggestionsCount = lazyModel.count(searchFilter);
        }
        else {
            FacesContext facesContext = getFacesContext();
            MethodExpression me = getCompleteMethod();

            if (me != null) {
                suggestions = me.invoke(facesContext.getELContext(), new Object[]{query});

                if (suggestions == null) {
                    suggestions = isServerQueryMode() ? Collections.emptyList() : Collections.emptyMap();
                }

                facesContext.renderResponse();
            }
        }
    }

    protected boolean hasMoreSuggestions() {
        int count = suggestionsCount != null ? suggestionsCount : ((List<?>) getSuggestions()).size();
        return count > getMaxResults();
    }

    public List<Column> getColums() {
        List<Column> columns = new ArrayList<>();

        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child instanceof Column) {
                columns.add((Column) child);
            }
        }

        return columns;
    }

    public Object getSuggestions() {
        return suggestions;
    }

    public boolean isServerQueryMode() {
        return "server".equals(getQueryMode());
    }

    public boolean isClientQueryMode() {
        return "client".equals(getQueryMode());
    }

    public boolean isHybridQueryMode() {
        return "hybrid".equals(getQueryMode());
    }

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getInputClientId();
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    @Override
    public String getAriaDescribedBy() {
        return (String) getStateHelper().get("ariaDescribedBy");
    }

    @Override
    public void setAriaDescribedBy(String ariaDescribedBy) {
        getStateHelper().put("ariaDescribedBy", ariaDescribedBy);
    }

    @Override
    public String getEmptyMessage() {
        return (String) getStateHelper().eval(PropertyKeys.emptyMessage,
            () -> MessageFactory.getMessage(getFacesContext(), UIPageableData.EMPTY_MESSAGE));
    }

    @Override
    public Object saveState(FacesContext context) {
        // reset component for MyFaces view pooling
        suggestions = null;
        suggestionsCount = null;

        return super.saveState(context);
    }
}