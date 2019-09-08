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
package org.primefaces.component.tagcloud;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.tagcloud.TagCloudItem;
import org.primefaces.model.tagcloud.TagCloudModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class TagCloudRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        TagCloud tagCloud = (TagCloud) component;

        encodeMarkup(context, tagCloud);
        encodeScript(context, tagCloud);
    }

    protected void encodeMarkup(FacesContext context, TagCloud tagCloud) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        TagCloudModel model = tagCloud.getModel();
        String styleClass = tagCloud.getStyleClass();
        String style = tagCloud.getStyle();
        styleClass = styleClass == null ? TagCloud.STYLE_CLASS : TagCloud.STYLE_CLASS + " " + styleClass;

        writer.startElement("div", tagCloud);
        writer.writeAttribute("id", tagCloud.getClientId(context), "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.startElement("ul", null);

        for (TagCloudItem item : model.getTags()) {
            String url = item.getUrl();
            String href = url == null ? "#" : item.getUrl();

            writer.startElement("li", null);
            writer.writeAttribute("class", "ui-tagcloud-strength-" + item.getStrength(), null);

            writer.startElement("a", null);
            writer.writeAttribute("href", href, null);
            writer.writeText(item.getLabel(), null);
            writer.endElement("a");

            writer.endElement("li");
        }

        writer.endElement("ul");

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, TagCloud tagCloud) throws IOException {
        String clientId = tagCloud.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TagCloud", tagCloud.resolveWidgetVar(context), clientId);

        encodeClientBehaviors(context, tagCloud);

        wb.finish();
    }
}
