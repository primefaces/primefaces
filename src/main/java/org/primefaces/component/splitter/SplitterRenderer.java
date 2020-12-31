/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
        String styleClass = splitter.getStyleClass();
        styleClass = styleClass == null ? Splitter.DEFAULT_STYLE_CLASS : Splitter.DEFAULT_STYLE_CLASS + " " + styleClass;
        String layout = splitter.getLayout();
        int childCount = splitter.getChildCount();

        if (layout.equals("vertical")) {
            styleClass += " " + Splitter.LAYOUT_VERTICAL_CLASS;
        }
        else if (layout.equals("horizontal")) {
            styleClass += " " + Splitter.LAYOUT_HORIZONTAL_CLASS;
        }

        writer.startElement("div", splitter);
        writer.writeAttribute("id", splitter.getClientId(context), "id");
        writer.writeAttribute("tabindex", "0", "tabindex");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (splitter.getStyle() != null) {
            writer.writeAttribute("style", splitter.getStyle(), "style");
        }

        for (int i = 0; i < childCount; i++) {
            UIComponent component = splitter.getChildren().get(i);

            if (component instanceof SplitterPanel) {
                encodePanel(context, (SplitterPanel) component);

                if (i + 1 != childCount) {
                    writer.startElement("div", splitter);
                    writer.writeAttribute("class", Splitter.GUTTER_CLASS, "styleClass");
                    if (layout.equals("horizontal")) {
                        writer.writeAttribute("style", "width: " + splitter.getGutterSize() + "px", "style");
                    }
                    else if (layout.equals("vertical")) {
                        writer.writeAttribute("style", "height: " + splitter.getGutterSize() + "px", "style");
                    }
                    writer.startElement("div", splitter);
                    writer.writeAttribute("class", Splitter.GUTTER_HANDLE_CLASS, "styleClass");
                    writer.endElement("div");
                    writer.endElement("div");
                }
            }

            else {
                renderChildren(context, splitter);
            }
        }

        writer.endElement("div");
    }

    protected void encodePanel(FacesContext context, SplitterPanel splitterPanel) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String styleClass = splitterPanel.getStyleClass();
        styleClass = styleClass == null ? SplitterPanel.DEFAULT_STYLE_CLASS : SplitterPanel.DEFAULT_STYLE_CLASS + " " + styleClass;
        boolean isNested = false;
        for (UIComponent child: splitterPanel.getChildren()) {
            if (child instanceof Splitter) {
                isNested = true;
                break;
            }
        }
        if (isNested) {
            styleClass += " " + SplitterPanel.NESTED_CLASS;
        }

        writer.startElement("div", splitterPanel);
        writer.writeAttribute("class", styleClass, "styleClass");
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
