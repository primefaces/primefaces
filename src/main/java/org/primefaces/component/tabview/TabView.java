/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import java.util.Collection;
import java.util.Map;
import javax.el.ELContext;

import javax.el.ValueExpression;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.PrimeFaces;
import org.primefaces.el.ValueExpressionAnalyzer;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.ConsumerThree;
import org.primefaces.util.MapBuilder;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
public class TabView extends TabViewBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.TabView";


    public static final String CONTAINER_CLASS = "ui-tabs ui-widget ui-widget-content ui-corner-all ui-hidden-container";
    public static final String NAVIGATOR_CLASS = "ui-tabs-nav ui-helper-reset ui-widget-header ui-corner-all";
    public static final String INACTIVE_TAB_HEADER_CLASS = "ui-tabs-header ui-state-default";
    public static final String ACTIVE_TAB_HEADER_CLASS = "ui-tabs-header ui-state-default ui-tabs-selected ui-state-active";
    public static final String PANELS_CLASS = "ui-tabs-panels";
    public static final String ACTIVE_TAB_CONTENT_CLASS = "ui-tabs-panel ui-widget-content ui-corner-bottom";
    public static final String INACTIVE_TAB_CONTENT_CLASS = "ui-tabs-panel ui-widget-content ui-corner-bottom ui-helper-hidden";
    public static final String NAVIGATOR_SCROLLER_CLASS = "ui-tabs-navscroller";
    public static final String NAVIGATOR_LEFT_CLASS = "ui-tabs-navscroller-btn ui-tabs-navscroller-btn-left ui-state-default ui-corner-right";
    public static final String NAVIGATOR_RIGHT_CLASS = "ui-tabs-navscroller-btn ui-tabs-navscroller-btn-right ui-state-default ui-corner-left";
    public static final String NAVIGATOR_LEFT_ICON_CLASS = "ui-icon ui-icon-carat-1-w";
    public static final String NAVIGATOR_RIGHT_ICON_CLASS = "ui-icon ui-icon-carat-1-e";
    public static final String SCROLLABLE_TABS_CLASS = "ui-tabs-scrollable";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("tabChange", TabChangeEvent.class)
            .put("tabClose", TabCloseEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

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

        if (ComponentUtils.isRequestSource(this, context) && event instanceof AjaxBehaviorEvent) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);
            boolean repeating = isRepeating();
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if ("tabChange".equals(eventName)) {
                String tabClientId = params.get(clientId + "_newTab");
                TabChangeEvent changeEvent = new TabChangeEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId));

                if (repeating) {
                    int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setIndex(tabindex);
                    changeEvent.setData(getIndexData());
                    changeEvent.setTab((Tab) getChildren().get(0));
                }

                changeEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(changeEvent);

                if (repeating) {
                    setIndex(-1);
                }
            }
            else if ("tabClose".equals(eventName)) {
                String tabClientId = params.get(clientId + "_closeTab");
                TabCloseEvent closeEvent = new TabCloseEvent(this, behaviorEvent.getBehavior(), findTab(tabClientId));

                if (repeating) {
                    int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));
                    setIndex(tabindex);
                    closeEvent.setData(getIndexData());
                    closeEvent.setTab((Tab) getChildren().get(0));
                }

                closeEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(closeEvent);

                if (repeating) {
                    setIndex(-1);
                }
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
                getValueExpression(PropertyKeys.activeIndex.toString()));
        if (expr != null && !expr.isReadOnly(elContext)) {
            expr.setValue(elContext, getActiveIndex());
            resetActiveIndex();
        }
    }

    public void forEachTab(ConsumerThree<Tab, Integer, Boolean> callback) {
        String var = getVar();
        int activeIndex = getActiveIndex();

        if (var == null) {
            int j = 0;
            for (int i = 0; i < getChildCount(); i++) {
                UIComponent child = getChildren().get(i);
                if (child.isRendered() && child instanceof Tab) {
                    callback.accept((Tab) child, j, (j == activeIndex));
                    j++;
                }
            }
        }
        else {
            int dataCount = getRowCount();

            //boundary check
            activeIndex = activeIndex >= dataCount ? 0 : activeIndex;
            boolean activeTabRendered = false;

            Tab tab = (Tab) getChildren().get(0);

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