/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class TabViewRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) component;
        String activeIndexValue = params.get(tabView.getClientId(context) + "_activeIndex");

        if (LangUtils.isNotBlank(activeIndexValue)) {
            tabView.setActiveIndex(Integer.parseInt(activeIndexValue));

            if (tabView.isMultiViewState()) {
                TabViewState ts = tabView.getMultiViewState(true);
                ts.setActiveIndex(tabView.getActiveIndex());
            }
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) component;
        String clientId = tabView.getClientId(context);

        if (tabView.isContentLoadRequest(context)) {
            if (tabView.isRepeating()) {
                int index = Integer.parseInt(params.get(clientId + "_tabindex"));
                tabView.setIndex(index);

                Tab tabToLoad = tabView.getDynamicTab();
                tabToLoad.encodeAll(context);

                if (tabView.isDynamic()) {
                    tabToLoad.setLoaded(index, true);
                }

                tabView.setIndex(-1);
            }
            else {
                String tabClientId = params.get(clientId + "_currentTab");
                Tab tabToLoad = tabView.findTab(tabClientId);
                tabToLoad.encodeAll(context);

                if (tabView.isDynamic()) {
                    tabToLoad.setLoaded(true);
                }
            }
        }
        else {
            if (tabView.isMultiViewState()) {
                tabView.restoreMultiViewState();
            }

            tabView.resetLoadedTabsState();

            encodeMarkup(context, tabView);
            encodeScript(context, tabView);
        }
    }

    protected void encodeScript(FacesContext context, TabView tabView) throws IOException {
        boolean dynamic = tabView.isDynamic();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TabView", tabView);

        if (dynamic) {
            wb.attr("dynamic", true).attr("cache", tabView.isCache());
        }

        wb.callback("onTabChange", "function(index)", tabView.getOnTabChange())
                .callback("onTabShow", "function(index)", tabView.getOnTabShow())
                .callback("onTabClose", "function(index)", tabView.getOnTabClose());

        wb.attr("effect", tabView.getEffect(), null)
                .attr("effectDuration", tabView.getEffectDuration(), null)
                .attr("scrollable", tabView.isScrollable())
                .attr("tabindex", tabView.getTabindex(), null)
                .attr("focusOnError", tabView.isFocusOnError(), false)
                .attr("focusOnLastActiveTab", tabView.isFocusOnLastActiveTab(), false)
                .attr("touchable", ComponentUtils.isTouchable(context, tabView),  true)
                .attr("multiViewState", tabView.isMultiViewState(), false);

        encodeClientBehaviors(context, tabView);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tabView.getClientId(context);
        String widgetVar = tabView.resolveWidgetVar(context);
        String orientation = tabView.getOrientation();
        String styleClass = getStyleClassBuilder(context)
                .add(TabView.CONTAINER_CLASS)
                .add("ui-tabs-" + orientation)
                .add(tabView.isScrollable(), TabView.SCROLLABLE_TABS_CLASS)
                .add(tabView.getStyleClass())
                .add(ComponentUtils.isRTL(context, tabView), "ui-tabs-rtl")
                .build();

        writer.startElement("div", tabView);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (tabView.getStyle() != null) {
            writer.writeAttribute("style", tabView.getStyle(), "style");
        }

        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);

        if ("bottom".equals(orientation)) {
            encodeFooter(context, tabView);
            encodeContents(context, tabView);
            encodeHeaders(context, tabView);
        }
        else {
            encodeHeaders(context, tabView);
            encodeContents(context, tabView);
            encodeFooter(context, tabView);
        }

        encodeStateHolder(context, tabView, clientId + "_activeIndex", String.valueOf(tabView.getActiveIndex()));

        if (tabView.isScrollable()) {
            String scrollParam = clientId + "_scrollState";
            String scrollState = context.getExternalContext().getRequestParameterMap().get(scrollParam);
            String scrollValue = scrollState == null ? "0" : scrollState;
            encodeStateHolder(context, tabView, scrollParam, scrollValue);
        }

        writer.endElement("div");
    }

    protected void encodeStateHolder(FacesContext facesContext, TabView tabView, String name, String value) throws IOException {
        renderHiddenInput(facesContext, name, value, false);
    }

    protected void encodeFooter(FacesContext context, TabView tabView) throws IOException {
        UIComponent footerFacet = tabView.getFacet("footer");
        if (FacetUtils.shouldRenderFacet(footerFacet)) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("div", null);
            writer.writeAttribute("class", "ui-tabs-footer", null);
            footerFacet.encodeAll(context);
            writer.endElement("div");
        }
    }

    protected void encodeHeaders(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean scrollable = tabView.isScrollable();

        if (scrollable) {
            writer.startElement("div", null);
            writer.writeAttribute("class", TabView.NAVIGATOR_SCROLLER_CLASS, null);

            encodeScrollerButton(context, tabView, TabView.NAVIGATOR_LEFT_CLASS, TabView.NAVIGATOR_LEFT_ICON_CLASS);
            encodeScrollerButton(context, tabView, TabView.NAVIGATOR_RIGHT_CLASS, TabView.NAVIGATOR_RIGHT_ICON_CLASS);
        }

        writer.startElement("ul", null);
        writer.writeAttribute("class", TabView.NAVIGATOR_CLASS, null);
        writer.writeAttribute("role", "tablist", null);

        AtomicBoolean withActiveFacet = new AtomicBoolean(false);
        tabView.forEachTab((tab, i, active) -> {
            try {
                if (encodeTabHeader(context, tabView, tab, i, active)) {
                    withActiveFacet.set(true);
                }
            }
            catch (IOException ex) {
                throw new FacesException(ex);
            }
        });

        UIComponent actionsFacet = tabView.getFacet("actions");
        if (FacetUtils.shouldRenderFacet(actionsFacet)) {
            writer.startElement("li", null);
            writer.writeAttribute("class", "ui-tabs-actions ui-tabs-actions-global", null);
            writer.writeAttribute(HTML.ARIA_HIDDEN, String.valueOf(withActiveFacet.get()), null);
            actionsFacet.encodeAll(context);
            writer.endElement("li");
        }

        writer.endElement("ul");

        if (scrollable) {
            writer.endElement("div");
        }
    }

    protected boolean encodeTabHeader(FacesContext context, TabView tabView, Tab tab, int index, boolean active)
            throws IOException {
        boolean withFacet = false;
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                .add(active, TabView.ACTIVE_TAB_HEADER_CLASS, TabView.INACTIVE_TAB_HEADER_CLASS)
                .add(tab.isDisabled(), "ui-state-disabled")
                .add(tab.getTitleStyleClass())
                .build();
        UIComponent titleFacet = tab.getFacet("title");
        String tabindex = tab.isDisabled() ? "-1" : tabView.getTabindex();

        //header container
        writer.startElement("li", tab);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "presentation", null);
        writer.writeAttribute("data-index", index, null);
        if (tab.getTitleStyle() != null) {
            writer.writeAttribute("style", tab.getTitleStyle(), null);
        }
        if (tab.getTitletip() != null) {
            writer.writeAttribute("title", tab.getTitletip(), null);
        }

        //title
        String clientId = tab.getClientId(context);
        String tabHeaderId = clientId + "_header";
        writer.startElement("a", null);
        writer.writeAttribute("id", tabHeaderId, null);
        writer.writeAttribute("href", "#" + clientId, null);
        writer.writeAttribute("role", "tab", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(active), null);
        writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(active), null);
        writer.writeAttribute(HTML.ARIA_LABEL, tab.getAriaLabel(), null);
        writer.writeAttribute("data-index", index, null);
        writer.writeAttribute("aria-controls", clientId, null);
        if (tabindex != null) {
            writer.writeAttribute("tabindex", tabindex, null);
        }
        if (!FacetUtils.shouldRenderFacet(titleFacet)) {
            String tabTitle = tab.getTitle();
            if (tabTitle != null) {
                writer.writeText(tabTitle, null);
            }
        }
        else {
            titleFacet.encodeAll(context);
        }
        writer.endElement("a");

        //closable
        if (tab.isClosable()) {
            writer.startElement("span", null);
            writer.writeAttribute("class", "ui-icon ui-icon-close", null);
            writer.endElement("span");
        }

        UIComponent optionsFacet = tab.getFacet("actions");
        if (FacetUtils.shouldRenderFacet(optionsFacet)) {
            withFacet = true;
            writer.startElement("li", null);
            writer.writeAttribute("class", "ui-tabs-actions", null);
            writer.writeAttribute(HTML.ARIA_HIDDEN, String.valueOf(!active), null);
            optionsFacet.encodeAll(context);
            writer.endElement("li");
        }

        writer.endElement("li");
        return active && withFacet;
    }

    protected void encodeContents(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        boolean dynamic = tabView.isDynamic();
        boolean repeating = tabView.isRepeating();

        writer.startElement("div", null);
        writer.writeAttribute("class", TabView.PANELS_CLASS, null);

        tabView.forEachTab((tab, i, active) -> {
            try {
                String tabindex = Boolean.TRUE.equals(active) ? tabView.getTabindex() : "-1";
                encodeTabContent(context, tab, i, active, dynamic, repeating, tabindex);
            }
            catch (IOException ex) {
                throw new FacesException(ex);
            }
        });

        writer.endElement("div");
    }

    protected void encodeTabContent(FacesContext context, Tab tab, int index, boolean active, boolean dynamic, boolean repeating, String tabindex)
            throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = active ? TabView.ACTIVE_TAB_CONTENT_CLASS : TabView.INACTIVE_TAB_CONTENT_CLASS;
        String clientId = tab.getClientId(context);
        String tabHeaderId = clientId + "_header";
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "tabpanel", null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, String.valueOf(!active), null);
        writer.writeAttribute(HTML.ARIA_LABELLEDBY, tabHeaderId, null);
        writer.writeAttribute("data-index", index, null);
        writer.writeAttribute("tabindex", tabindex, null);

        if (dynamic) {
            if (active) {
                tab.encodeAll(context);

                if (repeating) {
                    tab.setLoaded(index, true);
                }
                else {
                    tab.setLoaded(true);
                }
            }
        }
        else {
            tab.encodeAll(context);
        }

        writer.endElement("div");
    }

    protected void encodeScrollerButton(FacesContext context, TabView tabView, String styleClass, String iconClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        writer.endElement("a");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
