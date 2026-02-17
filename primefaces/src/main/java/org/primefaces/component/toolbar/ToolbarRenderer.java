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
package org.primefaces.component.toolbar;

import org.primefaces.model.menu.Separator;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.FacetUtils;

import java.io.IOException;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Toolbar.DEFAULT_RENDERER, componentFamily = Toolbar.COMPONENT_FAMILY)
public class ToolbarRenderer extends CoreRenderer<Toolbar> {

    @Override
    public void encodeEnd(FacesContext context, Toolbar component) throws IOException {
        if (!shouldBeRendered(context, component)) {
            encodePlaceholder(context, component);
            return;
        }
        ResponseWriter writer = context.getResponseWriter();
        String style = component.getStyle();
        String styleClass = component.getStyleClass();
        styleClass = styleClass == null ? Toolbar.CONTAINER_CLASS : Toolbar.CONTAINER_CLASS + " " + styleClass;

        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(context), null);
        writer.writeAttribute("class", styleClass, null);
        writer.writeAttribute("role", "toolbar", null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (component.getChildCount() > 0) {
            encodeToolbarGroups(context, component);
        }
        else {
            encodeFacet(context, component, "left");
            encodeFacet(context, component, "right");
        }

        writer.endElement("div");
    }

    protected void encodeToolbarGroups(FacesContext context, Toolbar component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        for (UIComponent child : component.getChildren()) {
            if (child.isRendered() && child instanceof ToolbarGroup) {
                ToolbarGroup group = (ToolbarGroup) child;

                if (component.getChildCount() == 1 && "right".equals(group.getAlign())) {
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

    protected void encodeFacet(FacesContext context, Toolbar component, String facetName) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        UIComponent facet = component.getFacet(facetName);

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-toolbar-group-" + facetName, null);
        if (FacetUtils.shouldRenderFacet(facet)) {
            facet.encodeAll(context);
        }
        writer.endElement("div");
    }

    @Override
    public void encodeChildren(FacesContext facesContext, Toolbar component) throws IOException {
        //Do nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    /**
     * Determines whether a toolbar should be rendered based on its contents.
     * <p>
     * For toolbars with children, checks if any ToolbarGroup child contains renderable components.
     * For toolbars without children, checks if either the left or right facet should be rendered.
     *
     * @param facesContext The current FacesContext
     * @param component The toolbar component to check
     * @return true if the toolbar has renderable content, false otherwise
     */
    protected boolean shouldBeRendered(FacesContext facesContext, Toolbar component) {
        if (component.getChildCount() > 0) {
            for (UIComponent child : component.getChildren()) {
                if (child.isRendered() && child instanceof ToolbarGroup) {
                    ToolbarGroup toolbarGroup = (ToolbarGroup) child;
                    if (toolbarGroup.getChildren().stream().anyMatch(c -> shouldBeRendered(facesContext, c))) {
                        return true;
                    }
                }
            }
            return false;
        }
        else {
            return FacetUtils.shouldRenderFacet(component.getLeftFacet()) || FacetUtils.shouldRenderFacet(component.getRightFacet());
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

    protected void encodePlaceholder(FacesContext context, Toolbar component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", component);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", "ui-toolbar-placeholder", "styleClass");
        writer.endElement("div");
    }
}
