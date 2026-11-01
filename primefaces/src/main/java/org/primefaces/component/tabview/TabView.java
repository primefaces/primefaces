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
package org.primefaces.component.tabview;

import org.primefaces.PrimeFaces;
import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.event.TabEvent;
import org.primefaces.util.Callbacks;
import org.primefaces.util.ComponentUtils;

import java.util.Map;

import jakarta.el.ELContext;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = TabView.COMPONENT_TYPE, namespace = TabView.COMPONENT_FAMILY)
@FacesComponentInfo(description = "TabView is a tabbed panel component featuring client side tabs, dynamic content loading with AJAX and transition effects.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
public class TabView extends TabViewBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.TabView";

    public static final String CONTAINER_CLASS = "p-tabs p-widget p-widget-content p-hidden-container";
    public static final String NAVIGATOR_CLASS = "p-tabs-nav p-widget-header";
    public static final String INACTIVE_TAB_HEADER_CLASS = "p-tabs-header p-state-default";
    public static final String ACTIVE_TAB_HEADER_CLASS = "p-tabs-header p-state-default p-tabs-selected p-state-active";
    public static final String PANELS_CLASS = "p-tabs-panels";
    public static final String ACTIVE_TAB_CONTENT_CLASS = "p-tabs-panel p-widget-content";
    public static final String INACTIVE_TAB_CONTENT_CLASS = "p-tabs-panel p-widget-content p-hidden";
    public static final String NAVIGATOR_SCROLLER_CLASS = "p-tabs-navscroller";
    public static final String NAVIGATOR_LEFT_CLASS = "p-tabs-navscroller-btn p-tabs-navscroller-btn-left p-state-default";
    public static final String NAVIGATOR_RIGHT_CLASS = "p-tabs-navscroller-btn p-tabs-navscroller-btn-right p-state-default";
    public static final String NAVIGATOR_LEFT_ICON_CLASS = "p-icon p-prev-icon p-tabs-prev-icon";
    public static final String NAVIGATOR_RIGHT_ICON_CLASS = "p-icon p-next-icon p-tabs-next-icon";
    public static final String CLOSE_ICON_CLASS = "p-icon p-close-icon p-tabs-close-icon";
    public static final String SCROLLABLE_TABS_CLASS = "p-tabs-scrollable";

    public boolean isContentLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_contentLoad");
    }

    public Tab findTab(String tabClientId) {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.getClientId().equals(tabClientId)) {
                return (Tab) child;
            }
        }

        return null;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if (isAjaxBehaviorEventSource(event)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = getClientId(context);
            boolean repeating = isRepeating();
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            String tabClientId = params.get(clientId + "_currentTab");
            int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));

            if (repeating) {
                setIndex(tabindex);
            }

            Object data = repeating ? getIndexData() : null;
            Tab tab = repeating ? getDynamicTab() : findTab(tabClientId);

            TabEvent<?> changeEvent;
            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.tabChange)) {
                changeEvent = new TabChangeEvent<>(this, behaviorEvent.getBehavior(), tab, data, ClientBehaviorEventKeys.tabChange.getName(), tabindex);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.tabClose)) {
                changeEvent = new TabCloseEvent<>(this, behaviorEvent.getBehavior(), tab, data, ClientBehaviorEventKeys.tabClose.getName(), tabindex);
            }
            else {
                throw new FacesException("Unsupported event");
            }

            changeEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(changeEvent);

            if (repeating) {
                setIndex(-1);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    protected void resetActiveIndex() {
        getStateHelper().remove(PropertyKeys.activeIndex);
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        super.processUpdates(context);

        ELContext elContext = getFacesContext().getELContext();
        ValueExpression expr = ValueExpressionAnalyzer.getExpression(elContext,
                getValueExpression(PropertyKeys.activeIndex.toString()), true);
        if (expr != null && !expr.isReadOnly(elContext)) {
            expr.setValue(elContext, getActiveIndex());
            resetActiveIndex();
        }
    }

    public void forEachTab(Callbacks.TriConsumer<Tab, Integer, Boolean> callback) {
        int activeIndex = getActiveIndex();
        boolean activeTabRendered = false;

        if (isRepeating()) {
            int dataCount = getRowCount();

            //boundary check
            activeIndex = activeIndex >= dataCount ? 0 : activeIndex;

            Tab tab = getDynamicTab();

            for (int i = 0; i < dataCount; i++) {
                setIndex(i);

                if (tab.isRendered()) {
                    boolean tabActive = i == activeIndex;

                    // e.g. if the first tab is not rendered and the activeIndex=0
                    //- > we should render the first rendered tab as active
                    if (!activeTabRendered && i > activeIndex) {
                        tabActive = true;
                    }
                    if (tabActive) {
                        activeTabRendered = true;
                    }

                    callback.accept(tab, i, tabActive);
                }
            }

            setIndex(-1);
        }
        else {
            int renderedChildCount = ComponentUtils.getRenderedChildCount(this);

            //boundary check
            activeIndex = activeIndex >= renderedChildCount ? 0 : activeIndex;

            int j = 0;
            for (int i = 0; i < getChildCount(); i++) {
                UIComponent child = getChildren().get(i);
                if (child.isRendered() && child instanceof Tab tab) {
                    boolean tabActive = j == activeIndex;

                    // e.g. if the first tab is not rendered and the activeIndex=0
                    //- > we should render the first rendered tab as active
                    if (!activeTabRendered && j > activeIndex) {
                        tabActive = true;
                    }
                    if (tabActive) {
                        activeTabRendered = true;
                    }

                    callback.accept(tab, j, tabActive);
                    j++;
                }
            }
        }
    }

    @Override
    public void restoreMultiViewState() {
        TabViewState ts = getMultiViewState(false);
        if (ts != null) {
            setActiveIndex(ts.getActiveIndex());
        }
    }

    @Override
    public TabViewState getMultiViewState(boolean create) {
        FacesContext fc = getFacesContext();
        String viewId = fc.getViewRoot().getViewId();

        return PrimeFaces.current().multiViewState()
                .get(viewId, getClientId(fc), create, TabViewState::new);
    }

    @Override
    public void resetMultiViewState() {
        setActiveIndex(0);
    }
}
