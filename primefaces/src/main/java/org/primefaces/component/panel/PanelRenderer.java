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
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Panel.DEFAULT_RENDERER, componentFamily = Panel.COMPONENT_FAMILY)
public class PanelRenderer extends CoreRenderer<Panel> {

    @Override
    public void decode(FacesContext context, Panel component) {
        String clientId = component.getClientId(context);
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        //Restore toggle state
        String collapsedParam = params.get(clientId + "_collapsed");
        if (collapsedParam != null) {
            component.setCollapsed(Boolean.parseBoolean(collapsedParam));

            if (component.isMultiViewState()) {
                PanelState ps = component.getMultiViewState(true);
                ps.setCollapsed(component.isCollapsed());
            }
        }

        //Restore visibility state
        String visibleParam = params.get(clientId + "_visible");
        if (visibleParam != null) {
            component.setVisible(Boolean.parseBoolean(visibleParam));
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, Panel component) throws IOException {
        Menu optionsMenu = component.getOptionsMenu();

        if (component.isMultiViewState()) {
            component.restoreMultiViewState();
        }

        encodeMarkup(facesContext, component, optionsMenu);
        encodeScript(facesContext, component, optionsMenu);
    }

    protected void encodeScript(FacesContext context, Panel component, Menu optionsMenu) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Panel", component);

        if (component.isToggleable()) {
            wb.attr("toggleable", true)
                    .attr("toggleSpeed", component.getToggleSpeed())
                    .attr("collapsed", component.isCollapsed())
                    .attr("toggleOrientation", component.getToggleOrientation())
                    .attr("toggleableHeader", component.isToggleableHeader())
                    .attr("multiViewState", component.isMultiViewState(), false);
        }

        if (component.isClosable()) {
            wb.attr("closable", true)
                    .attr("closeSpeed", component.getCloseSpeed());
        }

        if (optionsMenu != null) {
            wb.attr("hasMenu", true);
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Panel component, Menu optionsMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String widgetVar = component.resolveWidgetVar(context);
        boolean collapsed = component.isCollapsed();
        boolean visible = component.isVisible();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        String styleClass = component.getStyleClass() == null ? Panel.PANEL_CLASS : Panel.PANEL_CLASS + " " + component.getStyleClass();

        if (collapsed) {
            styleClass += " ui-hidden-container";

            if (component.getToggleOrientation().equals("horizontal")) {
                styleClass += " ui-panel-collapsed-h";
            }
        }

        if (!visible) {
            styleClass += " ui-helper-hidden";
        }

        writer.writeAttribute("class", styleClass, "styleClass");

        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        writer.writeAttribute(HTML.WIDGET_VAR, widgetVar, null);
        writer.writeAttribute(HTML.ARIA_ROLE, "region", null);

        boolean isRenderHeader = shouldRenderHeader(context, component);
        if (isRenderHeader) {
            writer.writeAttribute(HTML.ARIA_LABELLEDBY, clientId + "_header", null);
        }

        renderDynamicPassThruAttributes(context, component);

        if (isRenderHeader) {
            encodeHeader(context, component, optionsMenu);
        }
        encodeContent(context, component);
        encodeFooter(context, component);

        if (component.isToggleable()) {
            encodeStateHolder(context, component, clientId + "_collapsed", String.valueOf(collapsed));
        }

        if (component.isClosable()) {
            encodeStateHolder(context, component, clientId + "_visible", String.valueOf(visible));
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

    protected boolean shouldRenderHeader(FacesContext context, Panel component) throws IOException {
        UIComponent header = component.getHeaderFacet();
        String headerText = component.getHeader();
        return headerText != null || FacetUtils.shouldRenderFacet(header, component.isRenderEmptyFacets());
    }

    protected void encodeHeader(FacesContext context, Panel component, Menu optionsMenu) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent headerFacet = component.getHeaderFacet();
        String headerText = component.getHeader();
        String clientId = component.getClientId(context);
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(headerFacet, component.isRenderEmptyFacets());

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context) + "_header", null);
        writer.writeAttribute("class", Panel.PANEL_TITLEBAR_CLASS, null);
        if (component.isToggleable()) {
            writer.writeAttribute(HTML.ARIA_ROLE, "button", null);
            writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(!component.isCollapsed()), null);
            writer.writeAttribute(HTML.ARIA_CONTROLS, clientId + "_content", null);
        }
        renderPassThruAttributes(context, headerFacet);

        //Title
        writer.startElement("span", null);
        writer.writeAttribute("class", Panel.PANEL_TITLE_CLASS, null);

        if (shouldRenderFacet) {
            renderFacet(context, headerFacet);
        }
        else {
            writer.writeText(headerText, null);
        }

        writer.endElement("span");

        //Options
        if (component.isClosable()) {
            encodeIcon(context, component, "ui-icon-closethick", clientId + "_closer", component.getCloseTitle(), null);
        }

        if (component.isToggleable()) {
            String icon = component.isCollapsed() ? "ui-icon-plusthick" : "ui-icon-minusthick";
            encodeIcon(context, component, icon, clientId + "_toggler", component.getToggleTitle(), null);
        }

        if (optionsMenu != null) {
            encodeIcon(context, component, "ui-icon-gear", clientId + "_menu", component.getMenuTitle(), null);
        }

        //Actions
        UIComponent actionsFacet = component.getActionsFacet();
        if (FacetUtils.shouldRenderFacet(actionsFacet)) {
            writer.startElement("div", null);
            writer.writeAttribute("class", Panel.PANEL_ACTIONS_CLASS, null);
            actionsFacet.encodeAll(context);
            writer.endElement("div");
        }

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext context, Panel component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context) + "_content", null);
        writer.writeAttribute("class", Panel.PANEL_CONTENT_CLASS, null);
        if (component.isCollapsed()) {
            writer.writeAttribute("style", "display:none", null);
        }

        renderChildren(context, component);

        writer.endElement("div");
    }

    protected void encodeFooter(FacesContext context, Panel component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent footer = component.getFooterFacet();
        String footerText = component.getFooter();
        boolean shouldRenderFacet = FacetUtils.shouldRenderFacet(footer, component.isRenderEmptyFacets());

        if (footerText == null && !shouldRenderFacet) {
            return;
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context) + "_footer", null);
        writer.writeAttribute("class", Panel.PANEL_FOOTER_CLASS, null);

        if (shouldRenderFacet) {
            renderChild(context, footer);
        }
        else {
            writer.writeText(footerText, null);
        }

        writer.endElement("div");
    }

    protected void encodeIcon(FacesContext context, Panel component, String iconClass, String id, String title, String ariaLabel) throws IOException {
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
    public void encodeChildren(FacesContext context, Panel component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
