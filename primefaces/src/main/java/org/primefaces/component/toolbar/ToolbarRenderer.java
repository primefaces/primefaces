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
package org.primefaces.component.toolbar;

import org.primefaces.model.menu.Separator;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class ToolbarRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Toolbar toolbar = (Toolbar) component;
        if (!shouldBeRendered(context, toolbar)) {
            encodePlaceholder(context, toolbar);
            return;
        }
        ResponseWriter writer = context.getResponseWriter();
        String style = toolbar.getStyle();
        String styleClass = toolbar.getStyleClass();
        styleClass = styleClass == null ? Toolbar.CONTAINER_CLASS : Toolbar.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", toolbar);
        writer.writeAttribute("id", toolbar.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "toolbar", null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (toolbar.getChildCount() > 0) {
            encodeToolbarGroups(context, toolbar);
        }
        else {
            encodeFacet(context, toolbar, "left");
            encodeFacet(context, toolbar, "right");
        }

        writer.endElement("div");
    }

    protected void encodeToolbarGroups(FacesContext context, Toolbar toolbar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        for (UIComponent child : toolbar.getChildren()) {
            if (child.isRendered() && child instanceof ToolbarGroup) {
                ToolbarGroup group = (ToolbarGroup) child;

                if (toolbar.getChildCount() == 1 && "right".equals(group.getAlign())) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", "ui-toolbar-group-left", null);
                    writer.endElement("div");
                }

                String defaultGroupClass = "ui-toolbar-group-" + group.getAlign();
                String groupClass = group.getStyleClass();
                String groupStyle = group.getStyle();
                groupClass = groupClass == null ? defaultGroupClass : defaultGroupClass + " " + groupClass;

                writer.startElement("div", null);
                writer.writeAttribute("class", groupClass, null);
                if (groupStyle != null) {
                    writer.writeAttribute("style", groupStyle, null);
                }

                for (UIComponent groupChild : group.getChildren()) {
                    groupChild.encodeAll(context);
                }

                writer.endElement("div");
            }
        }
    }

    protected void encodeFacet(FacesContext context, Toolbar toolbar, String facetName) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent facet = toolbar.getFacet(facetName);

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-toolbar-group-" + facetName, null);
        if (FacetUtils.shouldRenderFacet(facet)) {
            facet.encodeAll(context);
        }
        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    protected boolean shouldBeRendered(FacesContext facesContext, Toolbar toolbar) {
        if (toolbar.getChildCount() > 0) {
            for (UIComponent child : toolbar.getChildren()) {
                if (child.isRendered() && child instanceof ToolbarGroup) {
                    ToolbarGroup toolbarGroup = (ToolbarGroup) child;
                    return toolbarGroup.getChildren().stream().anyMatch(c -> shouldBeRendered(facesContext, c));
                }
            }
            return false;
        }
        else {
            return FacetUtils.shouldRenderFacet(toolbar.getFacet("left")) || FacetUtils.shouldRenderFacet(toolbar.getFacet("right"));
        }
    }

    /**
     * Check whether toolbar has a render relevant component. Separators themselves will be rendered regularly but if they are the
     * only components there is no need to render the toolbar.
     */
    protected boolean shouldBeRendered(FacesContext facesContext, UIComponent component) {
        if (component instanceof Separator) {
            return false;
        }
        try {
            component.pushComponentToEL(facesContext, component);
            return component.isRendered();
        }
        finally {
            component.popComponentFromEL(facesContext);
        }

    }

    protected void encodePlaceholder(FacesContext context, Toolbar toolbar) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", toolbar);
        writer.writeAttribute("id", toolbar.getClientId(context), "id");
        writer.writeAttribute("class", "ui-toolbar-placeholder", "styleClass");
        writer.endElement("div");
    }
}
