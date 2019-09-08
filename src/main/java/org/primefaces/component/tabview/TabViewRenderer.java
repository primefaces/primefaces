/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class TabViewRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) component;
        String activeIndexValue = params.get(tabView.getClientId(context) + "_activeIndex");

        if (!LangUtils.isValueBlank(activeIndexValue)) {
            tabView.setActiveIndex(Integer.parseInt(activeIndexValue));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        TabView tabView = (TabView) component;
        String clientId = tabView.getClientId(context);
        String var = tabView.getVar();

        if (tabView.isContentLoadRequest(context)) {
            Tab tabToLoad = null;

            if (var == null) {
                String tabClientId = params.get(clientId + "_newTab");
                tabToLoad = tabView.findTab(tabClientId);

                tabToLoad.encodeAll(context);
                tabToLoad.setLoaded(true);
            }
            else {
                int tabindex = Integer.parseInt(params.get(clientId + "_tabindex"));
                tabView.setIndex(tabindex);
                tabToLoad = (Tab) tabView.getChildren().get(0);
                tabToLoad.encodeAll(context);
                tabView.setIndex(-1);
            }
        }
        else {
            tabView.resetLoadedTabsState();

            encodeMarkup(context, tabView);
            encodeScript(context, tabView);
        }
    }

    protected void encodeScript(FacesContext context, TabView tabView) throws IOException {
        String clientId = tabView.getClientId(context);
        boolean dynamic = tabView.isDynamic();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TabView", tabView.resolveWidgetVar(context), clientId);

        if (dynamic) {
            wb.attr("dynamic", true).attr("cache", tabView.isCache());
        }

        wb.callback("onTabChange", "function(index)", tabView.getOnTabChange())
                .callback("onTabShow", "function(index)", tabView.getOnTabShow())
                .callback("onTabClose", "function(index)", tabView.getOnTabClose());

        wb.attr("effect", tabView.getEffect(), null)
                .attr("effectDuration", tabView.getEffectDuration(), null)
                .attr("scrollable", tabView.isScrollable())
                .attr("tabindex", tabView.getTabindex(), null);

        encodeClientBehaviors(context, tabView);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = tabView.getClientId(context);
        String widgetVar = tabView.resolveWidgetVar(context);
        String orientation = tabView.getOrientation();
        String styleClass = tabView.getStyleClass();
        String defaultStyleClass = TabView.CONTAINER_CLASS + " ui-tabs-" + orientation;
        if (tabView.isScrollable()) {
            defaultStyleClass = defaultStyleClass + " " + TabView.SCROLLABLE_TABS_CLASS;
        }
        styleClass = styleClass == null ? defaultStyleClass : defaultStyleClass + " " + styleClass;

        if (ComponentUtils.isRTL(context, tabView)) {
            styleClass += " ui-tabs-rtl";
        }

        writer.startElement("div", tabView);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");
        if (tabView.getStyle() != null) {
            writer.writeAttribute("style", tabView.getStyle(), "style");
        }

        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);

        if (orientation.equals("bottom")) {
            encodeContents(context, tabView);
            encodeHeaders(context, tabView);
        }
        else {
            encodeHeaders(context, tabView);
            encodeContents(context, tabView);
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
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("value", value, null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.endElement("input");
    }

    protected void encodeHeaders(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = tabView.getVar();
        int activeIndex = tabView.getActiveIndex();
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

        if (var == null) {
            int j = 0;
            for (int i = 0; i < tabView.getChildCount(); i++) {
                UIComponent child = tabView.getChildren().get(i);
                if (child.isRendered() && child instanceof Tab) {
                    encodeTabHeader(context, tabView, (Tab) child, j, (j == activeIndex));
                    j++;
                }
            }
        }
        else {
            int dataCount = tabView.getRowCount();

            //boundary check
            activeIndex = activeIndex >= dataCount ? 0 : activeIndex;

            Tab tab = (Tab) tabView.getChildren().get(0);

            for (int i = 0; i < dataCount; i++) {
                tabView.setIndex(i);

                encodeTabHeader(context, tabView, tab, i, (i == activeIndex));
            }

            tabView.setIndex(-1);
        }

        writer.endElement("ul");

        if (scrollable) {
            writer.endElement("div");
        }
    }

    protected void encodeTabHeader(FacesContext context, TabView tabView, Tab tab, int index, boolean active)
            throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String defaultStyleClass = active ? TabView.ACTIVE_TAB_HEADER_CLASS : TabView.INACTIVE_TAB_HEADER_CLASS;
        defaultStyleClass = defaultStyleClass + " ui-corner-" + tabView.getOrientation();   //cornering
        if (tab.isDisabled()) {
            defaultStyleClass = defaultStyleClass + " ui-state-disabled";
        }
        String styleClass = tab.getTitleStyleClass();
        styleClass = (styleClass == null) ? defaultStyleClass : defaultStyleClass + " " + styleClass;
        UIComponent titleFacet = tab.getFacet("title");
        String tabindex = tab.isDisabled() ? "-1" : tabView.getTabindex();

        //header container
        writer.startElement("li", tab);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "tab", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(active), null);
        writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(active), null);
        writer.writeAttribute(HTML.ARIA_LABEL, tab.getAriaLabel(), null);
        writer.writeAttribute("data-index", index, null);
        if (tab.getTitleStyle() != null) {
            writer.writeAttribute("style", tab.getTitleStyle(), null);
        }
        if (tab.getTitletip() != null) {
            writer.writeAttribute("title", tab.getTitletip(), null);
        }
        if (tabindex != null) {
            writer.writeAttribute("tabindex", tabindex, null);
        }

        //title
        writer.startElement("a", null);
        writer.writeAttribute("href", "#" + tab.getClientId(context), null);
        writer.writeAttribute("tabindex", "-1", null);
        if (titleFacet == null) {
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

        UIComponent actions = tab.getFacet("actions");
        if (actions != null && actions.isRendered()) {
            writer.startElement("li", null);
            writer.writeAttribute("class", "ui-tabs-actions", null);
            writer.writeAttribute(HTML.ARIA_HIDDEN, String.valueOf(!active), null);
            actions.encodeAll(context);
            writer.endElement("li");
        }

        writer.endElement("li");
    }

    protected void encodeContents(FacesContext context, TabView tabView) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String var = tabView.getVar();
        int activeIndex = tabView.getActiveIndex();
        boolean dynamic = tabView.isDynamic();

        writer.startElement("div", null);
        writer.writeAttribute("class", TabView.PANELS_CLASS, null);

        if (var == null) {
            int j = 0;
            for (int i = 0; i < tabView.getChildCount(); i++) {
                UIComponent child = tabView.getChildren().get(i);
                if (child.isRendered() && child instanceof Tab) {
                    encodeTabContent(context, (Tab) child, j, (j == activeIndex), dynamic);
                    j++;
                }
            }
        }
        else {
            int dataCount = tabView.getRowCount();

            //boundary check
            activeIndex = activeIndex >= dataCount ? 0 : activeIndex;

            Tab tab = (Tab) tabView.getChildren().get(0);

            for (int i = 0; i < dataCount; i++) {
                tabView.setIndex(i);

                encodeTabContent(context, tab, i, (i == activeIndex), dynamic);
            }

            tabView.setIndex(-1);
        }

        writer.endElement("div");
    }

    protected void encodeTabContent(FacesContext context, Tab tab, int index, boolean active, boolean dynamic)
            throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = active ? TabView.ACTIVE_TAB_CONTENT_CLASS : TabView.INACTIVE_TAB_CONTENT_CLASS;

        writer.startElement("div", null);
        writer.writeAttribute("id", tab.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "tabpanel", null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, String.valueOf(!active), null);
        writer.writeAttribute("data-index", index, null);

        if (dynamic) {
            if (active) {
                tab.encodeAll(context);
                tab.setLoaded(true);
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
