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
package org.primefaces.component.panel;

import org.primefaces.component.menu.Menu;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class PanelRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Panel panel = (Panel) component;
        String clientId = panel.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        //Restore toggle state
        String collapsedParam = params.get(clientId + "_collapsed");
        if (collapsedParam != null) {
            panel.setCollapsed(Boolean.parseBoolean(collapsedParam));

            if (panel.isMultiViewState()) {
                PanelState ps = panel.getMultiViewState(true);
                ps.setCollapsed(panel.isCollapsed());
            }
        }

        //Restore visibility state
        String visibleParam = params.get(clientId + "_visible");
        if (visibleParam != null) {
            panel.setVisible(Boolean.parseBoolean(visibleParam));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        Panel panel = (Panel) component;
        Menu optionsMenu = panel.getOptionsMenu();

        if (panel.isMultiViewState()) {
            panel.restoreMultiViewState();
        }

        encodeMarkup(facesContext, panel, optionsMenu);
        encodeScript(facesContext, panel, optionsMenu);
    }

    protected void encodeScript(FacesContext context, Panel panel, Menu optionsMenu) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Panel", panel);

        if (panel.isToggleable()) {
            wb.attr("toggleable", true)
                    .attr("toggleSpeed", panel.getToggleSpeed())
                    .attr("collapsed", panel.isCollapsed())
                    .attr("toggleOrientation", panel.getToggleOrientation())
                    .attr("toggleableHeader", panel.isToggleableHeader())
                    .attr("multiViewState", panel.isMultiViewState(), false);
        }

        if (panel.isClosable()) {
            wb.attr("closable", true)
                    .attr("closeSpeed", panel.getCloseSpeed());
        }

        if (optionsMenu != null) {
            wb.attr("hasMenu", true);
        }

        encodeClientBehaviors(context, panel);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Panel panel, Menu optionsMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = panel.getClientId(context);
        String widgetVar = panel.resolveWidgetVar(context);
        boolean collapsed = panel.isCollapsed();
        boolean visible = panel.isVisible();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        String styleClass = panel.getStyleClass() == null ? Panel.PANEL_CLASS : Panel.PANEL_CLASS + " " + panel.getStyleClass();

        if (collapsed) {
            styleClass += " ui-hidden-container";

            if (panel.getToggleOrientation().equals("horizontal")) {
                styleClass += " ui-panel-collapsed-h";
            }
        }

        if (!visible) {
            styleClass += " ui-helper-hidden";
        }

        writer.writeAttribute("class", styleClass, "styleClass");

        if (panel.getStyle() != null) {
            writer.writeAttribute("style", panel.getStyle(), "style");
        }

        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);
        writer.writeAttribute(HTML.ARIA_ROLE, "region", null);

        boolean isRenderHeader = shouldRenderHeader(context, panel);
        if (isRenderHeader) {
            writer.writeAttribute(HTML.ARIA_LABELLEDBY, clientId + "_header", null);
        }

        renderDynamicPassThruAttributes(context, panel);

        if (isRenderHeader) {
            encodeHeader(context, panel, optionsMenu);
        }
        encodeContent(context, panel);
        encodeFooter(context, panel);

        if (panel.isToggleable()) {
            encodeStateHolder(context, panel, clientId + "_collapsed", String.valueOf(collapsed));
        }

        if (panel.isClosable()) {
            encodeStateHolder(context, panel, clientId + "_visible", String.valueOf(visible));
        }

        if (optionsMenu != null) {
            optionsMenu.setOverlay(true);
            optionsMenu.setTrigger("@(#" + ComponentUtils.escapeSelector(clientId) + "_menu)");
            optionsMenu.setMy("left top");
            optionsMenu.setAt("left bottom");

            optionsMenu.encodeAll(context);
        }

        writer.endElement("div");
    }

    protected boolean shouldRenderHeader(FacesContext context, Panel panel) throws IOException {
        UIComponent header = panel.getFacet("header");
        String headerText = panel.getHeader();
        return headerText != null || FacetUtils.shouldRenderFacet(header, panel.isRenderEmptyFacets());
    }

    protected void encodeHeader(FacesContext context, Panel panel, Menu optionsMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent header = panel.getFacet("header");
        String headerText = panel.getHeader();
        String clientId = panel.getClientId(context);
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(header, panel.isRenderEmptyFacets());

        writer.startElement("div", null);
        writer.writeAttribute("id", panel.getClientId(context) + "_header", null);
        writer.writeAttribute("class", Panel.PANEL_TITLEBAR_CLASS, null);
        if (panel.isToggleable()) {
            writer.writeAttribute(HTML.ARIA_ROLE, "button", null);
            writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(!panel.isCollapsed()), null);
            writer.writeAttribute(HTML.ARIA_CONTROLS, clientId + "_content", null);
        }

        //Title
        writer.startElement("span", null);
        writer.writeAttribute("class", Panel.PANEL_TITLE_CLASS, null);

        if (shouldRenderFacet) {
            renderChild(context, header);
        }
        else {
            writer.writeText(headerText, null);
        }

        writer.endElement("span");

        //Options
        if (panel.isClosable()) {
            encodeIcon(context, panel, "ui-icon-closethick", clientId + "_closer", panel.getCloseTitle(), null);
        }

        if (panel.isToggleable()) {
            String icon = panel.isCollapsed() ? "ui-icon-plusthick" : "ui-icon-minusthick";
            encodeIcon(context, panel, icon, clientId + "_toggler", panel.getToggleTitle(), null);
        }

        if (optionsMenu != null) {
            encodeIcon(context, panel, "ui-icon-gear", clientId + "_menu", panel.getMenuTitle(), null);
        }

        //Actions
        UIComponent actionsFacet = panel.getFacet("actions");
        if (FacetUtils.shouldRenderFacet(actionsFacet)) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Panel.PANEL_ACTIONS_CLASS, null);
            actionsFacet.encodeAll(context);
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, Panel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", panel.getClientId(context) + "_content", null);
        writer.writeAttribute("class", Panel.PANEL_CONTENT_CLASS, null);
        if (panel.isCollapsed()) {
            writer.writeAttribute("style", "display:none", null);
        }

        renderChildren(context, panel);

        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext context, Panel panel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent footer = panel.getFacet("footer");
        String footerText = panel.getFooter();
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(footer, panel.isRenderEmptyFacets());

        if (footerText == null && !shouldRenderFacet) {
            return;
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", panel.getClientId(context) + "_footer", null);
        writer.writeAttribute("class", Panel.PANEL_FOOTER_CLASS, null);

        if (shouldRenderFacet) {
            renderChild(context, footer);
        }
        else {
            writer.writeText(footerText, null);
        }

        writer.endElement("div");
    }

    protected void encodeIcon(FacesContext context, Panel panel, String iconClass, String id, String title, String ariaLabel) throws IOException {
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

        writer.startElement("span", null);
        writer.writeAttribute("class", "ui-icon " + iconClass, null);
        writer.endElement("span");

        writer.endElement("a");
    }

    protected void encodeStateHolder(FacesContext context, Panel panel, String name, String value) throws IOException {
        renderHiddenInput(context, name, value, false);
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
