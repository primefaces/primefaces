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
package org.primefaces.component.ribbon;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.tabview.Tab;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class RibbonRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Ribbon ribbon = (Ribbon) component;

        encodeMarkup(context, ribbon);
        encodeScript(context, ribbon);
    }

    private void encodeScript(FacesContext context, Ribbon ribbon) throws IOException {
        String clientId = ribbon.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Ribbon", ribbon.resolveWidgetVar(context), clientId);

        encodeClientBehaviors(context, ribbon);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Ribbon ribbon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = ribbon.getClientId(context);
        String style = ribbon.getStyle();
        String styleClass = ribbon.getStyleClass();
        styleClass = (styleClass == null) ? Ribbon.CONTAINER_CLASS : Ribbon.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", ribbon);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("class", style, "style");
        }

        encodeTabHeaders(context, ribbon);
        encodeTabContents(context, ribbon);

        writer.endElement("div");
    }

    protected void encodeTabHeaders(FacesContext context, Ribbon ribbon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int activeIndex = ribbon.getActiveIndex();
        int childCount = ribbon.getChildCount();

        writer.startElement("ul", ribbon);
        writer.writeAttribute("class", Ribbon.NAVIGATOR_CLASS, null);
        writer.writeAttribute("role", "tablist", null);

        if (childCount > 0) {
            List<UIComponent> children = ribbon.getChildren();
            for (int i = 0; i < childCount; i++) {
                UIComponent child = children.get(i);

                if (child instanceof Tab && child.isRendered()) {
                    Tab tab = (Tab) child;
                    String title = tab.getTitle();
                    boolean active = (i == activeIndex);
                    String headerClass = (active) ? Ribbon.ACTIVE_TAB_HEADER_CLASS : Ribbon.INACTIVE_TAB_HEADER_CLASS;

                    // header container
                    writer.startElement("li", null);
                    writer.writeAttribute("class", headerClass, null);
                    writer.writeAttribute("role", "tab", null);
                    writer.writeAttribute(HTML.ARIA_EXPANDED, String.valueOf(active), null);

                    writer.startElement("a", null);
                    writer.writeAttribute("href", tab.getClientId(context), null);
                    if (title != null) {
                        writer.writeText(title, null);
                    }
                    writer.endElement("a");

                    writer.endElement("li");
                }
            }
        }
        writer.endElement("ul");
    }

    protected void encodeTabContents(FacesContext context, Ribbon ribbon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        int activeIndex = ribbon.getActiveIndex();
        int childCount = ribbon.getChildCount();

        writer.startElement("div", ribbon);
        writer.writeAttribute("class", Ribbon.PANELS_CLASS, null);

        if (childCount > 0) {
            List<UIComponent> children = ribbon.getChildren();
            for (int i = 0; i < childCount; i++) {
                UIComponent child = children.get(i);

                if (child instanceof Tab && child.isRendered()) {
                    Tab tab = (Tab) child;
                    encodeTabContent(context, ribbon, tab, (i == activeIndex));
                }
            }
        }

        writer.endElement("div");
    }

    protected void encodeTabContent(FacesContext context, Ribbon ribbon, Tab tab, boolean active) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String contentClass = active ? Ribbon.ACTIVE_TAB_CONTENT_CLASS : Ribbon.INACTIVE_TAB_CONTENT_CLASS;
        int childCount = tab.getChildCount();

        writer.startElement("div", ribbon);
        writer.writeAttribute("id", tab.getClientId(context), null);
        writer.writeAttribute("class", contentClass, null);

        if (childCount > 0) {
            writer.startElement("ul", ribbon);
            writer.writeAttribute("class", Ribbon.GROUPS_CLASS, null);

            List<UIComponent> children = tab.getChildren();
            for (int i = 0; i < childCount; i++) {
                UIComponent child = children.get(i);

                if (child instanceof RibbonGroup && child.isRendered()) {
                    RibbonGroup group = (RibbonGroup) child;
                    group.encodeAll(context);
                }
            }

            writer.endElement("ul");
        }

        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        // Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
