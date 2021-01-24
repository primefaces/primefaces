/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
import org.primefaces.util.WidgetBuilder;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import java.io.IOException;

public class SplitterRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Splitter splitter = (Splitter) component;

        encodeMarkup(context, splitter);
        encodeScript(context, splitter);
    }

    protected void encodeMarkup(FacesContext context, Splitter splitter) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String layout = splitter.getLayout();
        String styleClass = getStyleClassBuilder(context)
                    .add(Splitter.STYLE_CLASS)
                    .add(splitter.getStyleClass())
                    .add("vertical".equals(layout), Splitter.LAYOUT_VERTICAL_CLASS)
                    .add("horizontal".equals(layout), Splitter.LAYOUT_HORIZONTAL_CLASS)
                    .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", splitter.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (splitter.getStyle() != null) {
            writer.writeAttribute("style", splitter.getStyle(), "style");
        }

        int childCount = splitter.getChildCount();
        for (int i = 0; i < childCount; i++) {
            UIComponent component = splitter.getChildren().get(i);

            if (component instanceof SplitterPanel) {
                encodePanel(context, (SplitterPanel) component);

                if (i != childCount - 1) {
                    writer.startElement("div", null);
                    writer.writeAttribute("class", Splitter.GUTTER_CLASS, null);
                    if ("horizontal".equals(layout)) {
                        writer.writeAttribute("style", "width: " + splitter.getGutterSize() + "px", null);
                    }
                    else if ("vertical".equals(layout)) {
                        writer.writeAttribute("style", "height: " + splitter.getGutterSize() + "px", null);
                    }

                    writer.startElement("div", splitter);
                    writer.writeAttribute("class", Splitter.GUTTER_HANDLE_CLASS, null);
                    writer.endElement("div");

                    writer.endElement("div");
                }
            }
        }

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, SplitterPanel splitterPanel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = getStyleClassBuilder(context)
                    .add(SplitterPanel.STYLE_CLASS)
                    .add(splitterPanel.getStyleClass())
                    .add(splitterPanel.isNested(), SplitterPanel.NESTED_CLASS)
                    .build();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, "styleClass");
        writer.writeAttribute("data-size", splitterPanel.getSize(), null);
        writer.writeAttribute("data-minsize", splitterPanel.getMinSize(), null);
        if (splitterPanel.getStyle() != null) {
            writer.writeAttribute("style", splitterPanel.getStyle(), "style");
        }

        splitterPanel.encodeAll(context);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Splitter splitter) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Splitter", splitter)
                .attr("layout", splitter.getLayout())
                .attr("gutterSize", splitter.getGutterSize())
                .attr("stateKey", splitter.getStateKey())
                .attr("stateStorage", splitter.getStateStorage());
        wb.finish();
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeChildren(final FacesContext fc, final UIComponent component) {
        // nothing to do
    }
}
