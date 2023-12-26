/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.accordionpanel;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.menu.Menu;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

public class AccordionPanelRenderer extends CoreRenderer {

    private static final String SB_RESOLVE_ACTIVE_INDEX = AccordionPanelRenderer.class.getName() + "#resolveActiveIndex";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        AccordionPanel acco = (AccordionPanel) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String active = params.get(acco.getClientId(context) + "_active");

        if (active != null) {
            if (isValueBlank(active)) {
                // set an empty string instead of null - otherwise the stateHelper will re-evaluate to the default value
                // see GitHub #3140
                acco.setActiveIndex("");
            }
            else {
                acco.setActiveIndex(active);
            }

            if (acco.isMultiViewState()) {
                AccordionState as = acco.getMultiViewState(true);
                as.setActiveIndex(acco.getActiveIndex());
            }
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        AccordionPanel acco = (AccordionPanel) component;

        if (acco.isContentLoadRequest(context)) {
            String clientId = acco.getClientId(context);

            if (acco.isRepeating()) {
                int index = Integer.parseInt(params.get(clientId + "_tabindex"));
                acco.setIndex(index);

                Tab tabToLoad = acco.getDynamicTab();
                tabToLoad.encodeAll(context);

                if (acco.isDynamic()) {
                    tabToLoad.setLoaded(index, true);
                }

                acco.setIndex(-1);
            }
            else {
                String tabClientId = params.get(clientId + "_newTab");
                Tab tabToLoad = acco.findTab(tabClientId);
                tabToLoad.encodeAll(context);
                tabToLoad.setLoaded(true);
            }
        }
        else {
            acco.resetLoadedTabsState();

            if (acco.isMultiViewState()) {
                acco.restoreMultiViewState();
            }

            encodeMarkup(context, acco);
            encodeScript(context, acco);
        }
    }

    protected void encodeMarkup(FacesContext context, AccordionPanel acco) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = acco.getClientId(context);
        String widgetVar = acco.resolveWidgetVar(context);
        String styleClass = acco.getStyleClass();
        styleClass = styleClass == null ? AccordionPanel.CONTAINER_CLASS : AccordionPanel.CONTAINER_CLASS + " " + styleClass;

        String activeIndex = resolveActiveIndex(context, acco);

        if (ComponentUtils.isRTL(context, acco)) {
            styleClass = styleClass + " ui-accordion-rtl";
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (acco.getStyle() != null) {
            writer.writeAttribute("style", acco.getStyle(), null);
        }

        writer.writeAttribute("role", "tablist", null);

        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);

        renderDynamicPassThruAttributes(context, acco);

        encodeTabs(context, acco, activeIndex);

        encodeStateHolder(context, acco, activeIndex);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, AccordionPanel acco) throws IOException {
        boolean multiple = acco.isMultiple();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("AccordionPanel", acco);

        if (acco.isDynamic()) {
            wb.attr("dynamic", true).attr("cache", acco.isCache());
        }

        wb.attr("multiple", multiple, false)
                .attr("toggleSpeed", acco.getToggleSpeed())
                .attr("scrollIntoView", acco.getScrollIntoView(), null)
                .callback("onTabChange", "function(panel)", acco.getOnTabChange())
                .callback("onTabShow", "function(panel)", acco.getOnTabShow())
                .callback("onTabClose", "function(panel)", acco.getOnTabClose())
                .attr("multiViewState", acco.isMultiViewState(), false);

        if (acco.getTabController() != null) {
            wb.attr("controlled", true);
        }

        encodeClientBehaviors(context, acco);

        wb.finish();
    }

    protected void encodeStateHolder(FacesContext context, AccordionPanel accordionPanel, String activeIndex) throws IOException {
        String clientId = accordionPanel.getClientId(context);
        String stateHolderId = clientId + "_active";

        renderHiddenInput(context, stateHolderId, activeIndex, false);
    }

    protected void encodeTabs(FacesContext context, AccordionPanel acco, String activeIndex) throws IOException {
        boolean dynamic = acco.isDynamic();
        boolean repeating = acco.isRepeating();
        boolean rtl = acco.getDir().equalsIgnoreCase("rtl");

        List<String> activeIndexes = activeIndex == null
                                     ? Collections.<String>emptyList()
                                     : Arrays.asList(activeIndex.split(","));

        if (repeating) {
            int dataCount = acco.getRowCount();
            Tab tab = acco.getDynamicTab();

            for (int i = 0; i < dataCount; i++) {
                acco.setIndex(i);
                boolean active = isActive(tab, activeIndexes, i);
                encodeTab(context, acco, tab, i, active, dynamic, repeating, rtl);
            }

            acco.setIndex(-1);
        }
        else {
            int j = 0;

            for (int i = 0; i < acco.getChildCount(); i++) {
                UIComponent child = acco.getChildren().get(i);
                if (child.isRendered() && child instanceof Tab) {
                    Tab tab = (Tab) child;
                    boolean active = isActive(tab, activeIndexes, j);
                    encodeTab(context, acco, tab, j, active, dynamic, repeating, rtl);
                    j++;
                }
            }
        }
    }

    protected boolean isActive(Tab tab, List<String> activeIndexes, int index) {
        boolean active = activeIndexes.indexOf(Integer.toString(index)) != -1;
        return active && !tab.isDisabled();
    }

    protected void encodeTab(FacesContext context, AccordionPanel accordionPanel, Tab tab, int index, boolean active, boolean dynamic,
            boolean repeating, boolean rtl) throws IOException {

        ResponseWriter writer = context.getResponseWriter();
        String clientId = tab.getClientId(context);
        Menu optionsMenu = tab.getOptionsMenu();

        String headerStyleClass = getStyleClassBuilder(context)
            .add(active, AccordionPanel.ACTIVE_TAB_HEADER_CLASS, AccordionPanel.TAB_HEADER_CLASS)
            .add(tab.isDisabled(), "ui-state-disabled")
            .add(tab.getTitleStyleClass())
            .build();

        String iconStyleClass = getStyleClassBuilder(context)
            .add(active, AccordionPanel.ACTIVE_TAB_HEADER_ICON_CLASS)
            .add(!active && rtl, AccordionPanel.TAB_HEADER_ICON_RTL_CLASS)
            .add(!active && !rtl, AccordionPanel.TAB_HEADER_ICON_CLASS)
            .build();

        String contentStyleClass = getStyleClassBuilder(context)
            .add(active, AccordionPanel.ACTIVE_TAB_CONTENT_CLASS, AccordionPanel.INACTIVE_TAB_CONTENT_CLASS)
            .build();

        UIComponent titleFacet = tab.getFacet("title");
        String title = tab.getTitle();
        String tabindex = tab.isDisabled() ? "-1" : accordionPanel.getTabindex();

        //header container
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_header", null);
        writer.writeAttribute("class", headerStyleClass, null);
        writer.writeAttribute("role", "tab", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(active), null);
        writer.writeAttribute(HTML.ARIA_SELECTED, String.valueOf(active), null);
        writer.writeAttribute(HTML.ARIA_LABEL, tab.getAriaLabel(), null);
        writer.writeAttribute("tabindex", tabindex, null);
        if (tab.getTitleStyle() != null) {
            writer.writeAttribute("style", tab.getTitleStyle(), null);
        }
        if (tab.getTitletip() != null) {
            writer.writeAttribute("title", tab.getTitletip(), null);
        }

        //icon
        writer.startElement("span", null);
        writer.writeAttribute("class", iconStyleClass, null);
        writer.endElement("span");

        if (FacetUtils.shouldRenderFacet(titleFacet)) {
            titleFacet.encodeAll(context);
        }
        else if (title != null) {
            writer.writeText(title, null);
        }
        else {
            writer.write("&nbsp;");
        }

        //options menu trigger
        if (optionsMenu != null) {
            encodeIcon(context, tab, "ui-icon-gear", clientId + "_menu", tab.getMenuTitle(), null);
        }

        //actions
        UIComponent actionsFacet = tab.getFacet("actions");
        if (FacetUtils.shouldRenderFacet(actionsFacet)) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Panel.PANEL_ACTIONS_CLASS, null);
            writer.writeAttribute("onclick", "event.stopPropagation()", null);
            actionsFacet.encodeAll(context);
            writer.endElement("div");
        }

        writer.endElement("div");

        //content
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", contentStyleClass, null);
        writer.writeAttribute("role", "tabpanel", null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, String.valueOf(!active), null);

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

        //options menu
        if (optionsMenu != null) {
            optionsMenu.setOverlay(true);
            optionsMenu.setTrigger("@(#" + ComponentUtils.escapeSelector(clientId) + "_menu)");
            optionsMenu.setMy("left top");
            optionsMenu.setAt("left bottom");

            optionsMenu.encodeAll(context);
        }

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected void encodeIcon(FacesContext context, Tab tab, String iconClass, String id, String title, String ariaLabel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("a", null);
        if (id != null) {
            writer.writeAttribute("id", id, null);
        }
        writer.writeAttribute("href", "#", null);
        writer.writeAttribute("class", Panel.PANEL_TITLE_ICON_CLASS, null);
        if (title != null) {
            writer.writeAttribute("title", title, null);
        }

        if (ariaLabel != null) {
            writer.writeAttribute(HTML.ARIA_LABEL, ariaLabel, null);
        }

        writer.writeAttribute("onclick", "event.stopPropagation()", null);

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon " + iconClass, null);
        writer.endElement("span");

        writer.endElement("a");
    }

    protected String resolveActiveIndex(FacesContext context, AccordionPanel accordionPanel) {

        String activeIndex = accordionPanel.getActiveIndex();
        if ("all".equals(activeIndex)) {
            int childCount = 0;

            String var = accordionPanel.getVar();
            if (var == null) {
                for (UIComponent child : accordionPanel.getChildren()) {
                    if (child.isRendered() && child instanceof Tab) {
                        childCount++;
                    }
                }
            }
            else {
                childCount = accordionPanel.getRowCount();

                // add some puffer for dynamic added tabs
                childCount += childCount * 2;
            }

            StringBuilder sb = SharedStringBuilder.get(context, SB_RESOLVE_ACTIVE_INDEX);
            for (int i = 0; i < childCount; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(i);
            }

            activeIndex = sb.toString();
        }

        return activeIndex;
    }
}
