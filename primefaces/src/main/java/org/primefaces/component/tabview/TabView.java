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
import org.primefaces.util.LangUtils;

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

    /**
     * Returns the key or index of the active tab. Non-String values (e.g. an <code>int</code> bean property) are converted
     * to a String.
     *
     * @return the key or index of the active tab
     */
    @Override
    public String getActive() {
        Object active = getStateHelper().eval(PropertyKeys.active);
        return active == null ? null : active.toString();
    }

    /**
     * Sets the index of the active tab. When called at runtime (e.g. from a listener), it clears a local <code>active</code>
     * value (e.g. decoded from the request), which would otherwise take precedence.
     *
     * @param activeIndex the 0-based index of the active tab
     */
    @Override
    public void setActiveIndex(int activeIndex) {
        super.setActiveIndex(activeIndex);

        // Facelets sets the tag attributes before the component is added to the view; there active takes precedence over
        // activeIndex, regardless of the order in which the attributes are applied
        if (getParent() != null) {
            getStateHelper().remove(PropertyKeys.active);
        }
    }

    protected void resetActive() {
        getStateHelper().remove(PropertyKeys.active);
        getStateHelper().remove(PropertyKeys.activeIndex);
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        super.processUpdates(context);

        ELContext elContext = getFacesContext().getELContext();
        ValueExpression activeExpr = ValueExpressionAnalyzer.getExpression(elContext,
                getValueExpression(PropertyKeys.active.toString()), true);

        // the bean gets the requested index (as posted by the client), not the index of the tab rendered as active,
        // which may differ if the requested tab is not rendered, see #forEachTab - same as before the key support
        if (activeExpr != null) {
            if (!activeExpr.isReadOnly(elContext)) {
                Class<?> type = activeExpr.getType(elContext);
                boolean index = type == int.class || type == Integer.class;
                activeExpr.setValue(elContext, index ? resolveActiveIndex() : getActive());
                resetActive();
            }
        }
        else {
            // Fallback to deprecated activeIndex
            ValueExpression activeIndexExpr = ValueExpressionAnalyzer.getExpression(elContext,
                    getValueExpression(PropertyKeys.activeIndex.toString()), true);
            if (activeIndexExpr != null && !activeIndexExpr.isReadOnly(elContext)) {
                activeIndexExpr.setValue(elContext, resolveActiveIndex());
                resetActive();
            }
        }
    }

    /**
     * Resolves the index of the active tab, either from the key or the index defined via <code>active</code>,
     * falling back to the deprecated <code>activeIndex</code>.
     *
     * @return the 0-based index of the active tab
     */
    public int resolveActiveIndex() {
        String active = getActive();

        // Deprecated
        if (active == null) {
            return getActiveIndex();
        }

        if (LangUtils.isBlank(active)) {
            return 0;
        }

        int index = findTabIndexByKey(active);
        if (index != -1) {
            return index;
        }

        try {
            return Integer.parseInt(active.trim());
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    public void forEachTab(Callbacks.TriConsumer<Tab, Integer, Boolean> callback) {
        forEachTab(resolveActiveIndex(), callback);
    }

    /**
     * Iterates over the rendered tabs, using an already resolved active index (see {@link #resolveActiveIndex()}), so it is
     * resolved only once if the tabs are iterated multiple times, e.g. for the headers and the contents.
     *
     * @param activeIndex the 0-based index of the active tab
     * @param callback the callback receiving the tab, its index and whether it is active
     */
    public void forEachTab(int activeIndex, Callbacks.TriConsumer<Tab, Integer, Boolean> callback) {
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
            setActive(ts.getActive());
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
        setActive(null);
    }
}
