/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class AccordionPanelRenderer extends CoreRenderer {

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
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        AccordionPanel acco = (AccordionPanel) component;

        if (acco.isContentLoadRequest(context)) {
            String var = acco.getVar();
            String clientId = acco.getClientId(context);

            if (var == null) {
                String tabClientId = params.get(clientId + "_newTab");
                Tab tabToLoad = acco.findTab(tabClientId);
                tabToLoad.encodeAll(context);
                tabToLoad.setLoaded(true);
            }
            else {
                int index = Integer.parseInt(params.get(clientId + "_tabindex"));
                acco.setIndex(index);
                acco.getChildren().get(0).encodeAll(context);
                acco.setIndex(-1);
            }
        }
        else {
            acco.resetLoadedTabsState();

            encodeMarkup(context, acco);
            encodeScript(context, acco);
        }
    }

    protected void encodeMarkup(FacesContext context, AccordionPanel acco) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = acco.getClientId(context);
        String widgetVar = acco.resolveWidgetVar();
        String styleClass = acco.getStyleClass();
        styleClass = styleClass == null ? AccordionPanel.CONTAINER_CLASS : AccordionPanel.CONTAINER_CLASS + " " + styleClass;

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

        encodeTabs(context, acco);

        encodeStateHolder(context, acco);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, AccordionPanel acco) throws IOException {
        String clientId = acco.getClientId(context);
        boolean multiple = acco.isMultiple();

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("AccordionPanel", acco.resolveWidgetVar(), clientId);

        if (acco.isDynamic()) {
            wb.attr("dynamic", true).attr("cache", acco.isCache());
        }

        wb.attr("multiple", multiple, false)
                .callback("onTabChange", "function(panel)", acco.getOnTabChange())
                .callback("onTabShow", "function(panel)", acco.getOnTabShow())
                .callback("onTabClose", "function(panel)", acco.getOnTabClose());

        if (acco.getTabController() != null) {
            wb.attr("controlled", true);
        }

        encodeClientBehaviors(context, acco);

        wb.finish();
    }

    protected void encodeStateHolder(FacesContext context, AccordionPanel accordionPanel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = accordionPanel.getClientId(context);
        String stateHolderId = clientId + "_active";

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", stateHolderId, null);
        writer.writeAttribute("name", stateHolderId, null);
        writer.writeAttribute("value", accordionPanel.getActiveIndex(), null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.endElement("input");
    }

    protected void encodeTabs(FacesContext context, AccordionPanel acco) throws IOException {
        boolean dynamic = acco.isDynamic();
        String var = acco.getVar();
        boolean rtl = acco.getDir().equalsIgnoreCase("rtl");

        String activeIndex = acco.getActiveIndex();
        List<String> activeIndexes = activeIndex == null
                                     ? Collections.<String>emptyList()
                                     : Arrays.asList(activeIndex.split(","));

        if (var == null) {
            int j = 0;

            for (int i = 0; i < acco.getChildCount(); i++) {
                UIComponent child = acco.getChildren().get(i);
                if (child.isRendered() && child instanceof Tab) {
                    boolean active = activeIndexes.indexOf(Integer.toString(j)) != -1;

                    encodeTab(context, acco, (Tab) child, active, dynamic, rtl);

                    j++;
                }
            }
        }
        else {
            int dataCount = acco.getRowCount();
            Tab tab = (Tab) acco.getChildren().get(0);

            for (int i = 0; i < dataCount; i++) {
                acco.setIndex(i);
                boolean active = activeIndexes.indexOf(Integer.toString(i)) != -1;

                encodeTab(context, acco, tab, active, dynamic, rtl);
            }

            acco.setIndex(-1);
        }
    }

    protected void encodeTab(FacesContext context, AccordionPanel accordionPanel, Tab tab, boolean active, boolean dynamic,
                             boolean rtl) throws IOException {

        ResponseWriter writer = context.getResponseWriter();

        String headerClass = active ? AccordionPanel.ACTIVE_TAB_HEADER_CLASS : AccordionPanel.TAB_HEADER_CLASS;
        headerClass = tab.isDisabled() ? headerClass + " ui-state-disabled" : headerClass;
        headerClass = tab.getTitleStyleClass() == null ? headerClass : headerClass + " " + tab.getTitleStyleClass();
        String iconClass = active
                           ? AccordionPanel.ACTIVE_TAB_HEADER_ICON_CLASS
                           : (rtl ? AccordionPanel.TAB_HEADER_ICON_RTL_CLASS : AccordionPanel.TAB_HEADER_ICON_CLASS);
        String contentClass = active
                              ? AccordionPanel.ACTIVE_TAB_CONTENT_CLASS
                              : AccordionPanel.INACTIVE_TAB_CONTENT_CLASS;
        UIComponent titleFacet = tab.getFacet("title");
        String title = tab.getTitle();
        String tabindex = tab.isDisabled() ? "-1" : accordionPanel.getTabindex();

        //header container
        writer.startElement("div", null);
        writer.writeAttribute("class", headerClass, null);
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
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        if (titleFacet != null) {
            titleFacet.encodeAll(context);
        }
        else if (title != null) {
            writer.writeText(title, null);
        }
        else {
            writer.write("&nbsp;");
        }

        writer.endElement("div");

        //content
        writer.startElement("div", null);
        writer.writeAttribute("id", tab.getClientId(context), null);
        writer.writeAttribute("class", contentClass, null);
        writer.writeAttribute("role", "tabpanel", null);
        writer.writeAttribute(HTML.ARIA_HIDDEN, String.valueOf(!active), null);

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

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
