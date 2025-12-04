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
package org.primefaces.component.accordionpanel;

import org.primefaces.component.menu.Menu;
import org.primefaces.component.panel.Panel;
import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = AccordionPanel.DEFAULT_RENDERER, componentFamily = AccordionPanel.COMPONENT_FAMILY)
public class AccordionPanelRenderer extends CoreRenderer<AccordionPanel> {

    private static final String SB_RESOLVE_ACTIVE_INDEX = AccordionPanelRenderer.class.getName() + "#resolveActive";

    @Override
    public void decode(FacesContext context, AccordionPanel component) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String active = params.get(component.getClientId(context) + "_active");

        if (active != null) {
            if (isValueBlank(active)) {
                // set an empty string instead of null - otherwise the stateHelper will re-evaluate to the default value
                // see GitHub #3140
                component.setActive("");
            }
            else {
                component.setActive(active);
            }

            if (component.isMultiViewState()) {
                AccordionState as = component.getMultiViewState(true);
                String activeState = component.getActive();
                if (activeState == null) {
                    activeState = component.getActiveIndex();
                }

                as.setActive(activeState);
            }
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, AccordionPanel component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        if (component.isContentLoadRequest(context)) {
            String clientId = component.getClientId(context);

            if (component.isRepeating()) {
                int index = Integer.parseInt(params.get(clientId + "_tabindex"));
                component.setIndex(index);

                Tab tabToLoad = component.getDynamicTab();
                tabToLoad.encodeAll(context);

                if (component.isDynamic()) {
                    tabToLoad.setLoaded(index, true);
                }

                component.setIndex(-1);
            }
            else {
                String tabClientId = params.get(clientId + "_currentTab");
                Tab tabToLoad = component.findTab(tabClientId);
                tabToLoad.encodeAll(context);
                tabToLoad.setLoaded(true);
            }
        }
        else {
            component.resetLoadedTabsState();

            if (component.isMultiViewState()) {
                component.restoreMultiViewState();
            }

            encodeMarkup(context, component);
            encodeScript(context, component);
        }
    }

    protected void encodeMarkup(FacesContext context, AccordionPanel component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String widgetVar = component.resolveWidgetVar(context);
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? AccordionPanel.CONTAINER_CLASS : AccordionPanel.CONTAINER_CLASS + " " + styleClass;

        String active = resolveActive(context, component);

        if (ComponentUtils.isRTL(context, component)) {
            styleClass = styleClass + " ui-accordion-rtl";
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }

        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);

        renderDynamicPassThruAttributes(context, component);

        encodeTabs(context, component, active);

        encodeStateHolder(context, component, active);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, AccordionPanel component) throws IOException {
        boolean multiple = component.isMultiple();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("AccordionPanel", component);

        if (component.isDynamic()) {
            wb.attr("dynamic", true).attr("cache", component.isCache());
        }

        wb.attr("multiple", multiple, false)
                .attr("toggleSpeed", component.getToggleSpeed())
                .attr("scrollIntoView", component.getScrollIntoView(), null)
                .callback("onTabChange", "function(panel)", component.getOnTabChange())
                .callback("onTabShow", "function(panel)", component.getOnTabShow())
                .callback("onTabClose", "function(panel)", component.getOnTabClose())
                .attr("multiViewState", component.isMultiViewState(), false);

        if (component.getTabController() != null) {
            wb.attr("controlled", true);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeStateHolder(FacesContext context, AccordionPanel component, String active) throws IOException {
        String clientId = component.getClientId(context);
        String stateHolderId = clientId + "_active";

        renderHiddenInput(context, stateHolderId, active, false);
    }

    protected void encodeTabs(FacesContext context, AccordionPanel component, String active) throws IOException {
        boolean dynamic = component.isDynamic();
        boolean repeating = component.isRepeating();
        boolean rtl = component.getDir().equalsIgnoreCase("rtl");

        List<String> activeList = active == null
                                     ? Collections.emptyList()
                                     : Arrays.asList(active.split(","));

        if (repeating) {
            int dataCount = component.getRowCount();
            Tab tab = component.getDynamicTab();

            for (int i = 0; i < dataCount; i++) {
                component.setIndex(i);
                if (tab.isRendered()) {
                    boolean activated = isActive(tab, activeList, i);
                    encodeTab(context, component, tab, i, activated, dynamic, repeating, rtl);
                }
            }

            component.setIndex(-1);
        }
        else {
            int j = 0;

            for (int i = 0; i < component.getChildCount(); i++) {
                UIComponent child = component.getChildren().get(i);
                if (child.isRendered() && child instanceof Tab) {
                    Tab tab = (Tab) child;
                    boolean activated = isActive(tab, activeList, j);
                    encodeTab(context, component, tab, j, activated, dynamic, repeating, rtl);
                    j++;
                }
            }
        }
    }

    protected boolean isActive(Tab tab, List<String> active, int index) {
        return (active.contains(tab.getKey()) || active.contains(Integer.toString(index))) && !tab.isDisabled();
    }

    protected void encodeTab(FacesContext context, AccordionPanel component, Tab tab, int index, boolean active, boolean dynamic,
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

        UIComponent titleFacet = tab.getTitleFacet();
        String title = tab.getTitle();
        String tabindex = tab.isDisabled() ? "-1" : component.getTabindex();

        //header container
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_header", null);
        writer.writeAttribute("class", headerStyleClass, null);
        writer.writeAttribute(HTML.ARIA_ROLE, "button", null);
        writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(active), null);
        writer.writeAttribute(HTML.ARIA_CONTROLS, clientId, null);
        writer.writeAttribute(HTML.ARIA_LABEL, tab.getAriaLabel(), null);
        writer.writeAttribute("tabindex", tabindex, null);
        if (tab.getKey() != null) {
            writer.writeAttribute("data-key", tab.getKey(), null);
        }
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
        UIComponent actionsFacet = tab.getActionsFacet();
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
        writer.writeAttribute(HTML.ARIA_ROLE, "region", null);
        writer.writeAttribute(HTML.ARIA_LABELLEDBY, clientId + "_header", null);
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
    public void encodeChildren(FacesContext context, AccordionPanel component) throws IOException {
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

    protected String resolveActive(FacesContext context, AccordionPanel accordionPanel) {
        String active = accordionPanel.getActive();

        // Deprecated
        if (active == null) {
            active = accordionPanel.getActiveIndex();
        }

        if ("all".equals(active)) {
            StringBuilder sb = SharedStringBuilder.get(context, SB_RESOLVE_ACTIVE_INDEX);

            if (accordionPanel.getVar() == null) {
                int childIndex = 0;
                for (UIComponent child : accordionPanel.getChildren()) {
                    if (child.isRendered() && child instanceof Tab) {
                        if (childIndex > 0) {
                            sb.append(",");
                        }

                        Tab tab = (Tab) child;
                        sb.append(tab.getKey() != null ? tab.getKey() : Integer.toString(childIndex));

                        childIndex++;
                    }
                }
            }
            else {
                Tab tab = accordionPanel.getDynamicTab();
                for (int i = 0; i < accordionPanel.getRowCount(); i++) {
                    accordionPanel.setIndex(i);
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(tab.getKey() != null ? tab.getKey() : Integer.toString(i));
                }
            }

            active = sb.toString();
        }

        return active;
    }
}
