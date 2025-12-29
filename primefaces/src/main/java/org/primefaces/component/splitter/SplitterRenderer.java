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
package org.primefaces.component.splitter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Objects;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Splitter.DEFAULT_RENDERER, componentFamily = Splitter.COMPONENT_FAMILY)
public class SplitterRenderer extends CoreRenderer<Splitter> {

    @Override
    public void decode(FacesContext context, Splitter component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Splitter component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    protected void encodeMarkup(FacesContext context, Splitter component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String layout = component.getLayout();
        boolean isVertical = "vertical".equals(layout);
        String styleClass = getStyleClassBuilder(context)
                    .add(Splitter.STYLE_CLASS)
                    .add(component.getStyleClass())
                    .add(isVertical, Splitter.LAYOUT_VERTICAL_CLASS)
                    .add("horizontal".equals(layout), Splitter.LAYOUT_HORIZONTAL_CLASS)
                    .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", component.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        int childCount = component.getChildCount();
        for (int i = 0; i < childCount; i++) {
            UIComponent child = component.getChildren().get(i);

            if (child instanceof SplitterPanel) {
                SplitterPanel panel = (SplitterPanel) child;
                String panelId = encodePanel(context, panel, i);
                String valueNow = panel.getSize() == null ? "0" : panel.getSize().toString();
                String minSize = panel.getMinSize() == null ? "0" : panel.getMinSize().toString();

                if (i != childCount - 1) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", Splitter.GUTTER_CLASS, null);
                    if ("horizontal".equals(layout)) {
                        writer.writeAttribute("style", "width: " + component.getGutterSize() + "px", null);
                    }
                    else if ("vertical".equals(layout)) {
                        writer.writeAttribute("style", "height: " + component.getGutterSize() + "px", null);
                    }

                    writer.startElement("div", component);
                    writer.writeAttribute("class", Splitter.GUTTER_HANDLE_CLASS, null);
                    writer.writeAttribute("tabindex", Objects.toString(panel.getTabindex(), "0"), null);
                    writer.writeAttribute(HTML.ARIA_ROLE, "separator", null);
                    writer.writeAttribute(HTML.ARIA_CONTROLS, panelId, null);
                    writer.writeAttribute(HTML.ARIA_ORIENTATION, isVertical ? "horizontal" : "vertical", null);
                    writer.writeAttribute(HTML.ARIA_CONTROLS, panelId, null);
                    writer.writeAttribute(HTML.ARIA_VALUE_MIN, minSize, null);
                    writer.writeAttribute(HTML.ARIA_VALUE_MAX, "100", null);
                    writer.writeAttribute(HTML.ARIA_VALUE_NOW, valueNow, null);
                    writer.writeAttribute(HTML.ARIA_VALUE_TEXT, valueNow + "%", null);

                    if (LangUtils.isNotBlank(panel.getAriaLabel())) {
                        writer.writeAttribute(HTML.ARIA_LABEL, panel.getAriaLabel(), null);
                    }
                    if (LangUtils.isNotBlank(panel.getAriaLabelledBy())) {
                        writer.writeAttribute(HTML.ARIA_LABELLEDBY, panel.getAriaLabelledBy(), null);
                    }

                    writer.endElement("div");

                    writer.endElement("div");
                }
            }
        }

        writer.endElement("div");
    }

    protected String encodePanel(FacesContext context, SplitterPanel component, int index) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String id = component.getClientId(context) + "_" + index;
        String styleClass = getStyleClassBuilder(context)
                    .add(SplitterPanel.STYLE_CLASS)
                    .add(component.getStyleClass())
                    .add(component.isNested(), SplitterPanel.NESTED_CLASS)
                    .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", id, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        writer.writeAttribute(HTML.ARIA_ROLE, "presentation", null);
        writer.writeAttribute("data-size", component.getSize(), null);
        writer.writeAttribute("data-minsize", component.getMinSize(), null);
        if (LangUtils.isNotBlank(component.getStyle())) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        component.encodeAll(context);

        writer.endElement("div");

        return id;
    }

    protected void encodeScript(FacesContext context, Splitter component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Splitter", component)
                .attr("layout", component.getLayout())
                .attr("gutterSize", component.getGutterSize())
                .attr("step", component.getStep())
                .attr("stateKey", component.getStateKey())
                .attr("stateStorage", component.getStateStorage());

        wb.callback("onResizeEnd", "function(panelSizes)", component.getOnResizeEnd());
        encodeClientBehaviors(context, component);

        wb.finish();
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(FacesContext fc, Splitter component) {
        // nothing to do
    }
}
