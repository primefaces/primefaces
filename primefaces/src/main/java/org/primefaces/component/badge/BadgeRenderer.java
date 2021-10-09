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
package org.primefaces.component.badge;

import org.primefaces.functional.IOBiConsumer;
import org.primefaces.model.badge.BadgeModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class BadgeRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Badge badge = (Badge) component;
        boolean hasChildren = badge.getChildCount() > 0;
        encode(context, badge, null, hasChildren);
    }

    public static <T extends UIComponent> void encode(FacesContext context, Object badge,
            IOBiConsumer<FacesContext, T> contentRenderer, T component) throws IOException {
        BadgeModel badgeModel = Badge.getBadgeModel(badge);
        if (badgeModel != null) {
            BadgeRenderer badgeRenderer = new BadgeRenderer();
            badgeRenderer.encodeOverlayBegin(context, null);
            contentRenderer.accept(context, component);
            badgeRenderer.encode(context, null, badgeModel, false);
            badgeRenderer.encodeOverlayEnd(context);
        }
        else {
            contentRenderer.accept(context, component);
        }
    }

    protected void encodeOverlayBegin(FacesContext context, String clientId) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", null);
        if (clientId != null) {
            writer.writeAttribute("id", clientId, "id");
        }
        writer.writeAttribute("class", Badge.OVERLAY_CLASS, "styleClass");
    }

    protected void encodeOverlayEnd(FacesContext context) throws IOException {
        context.getResponseWriter().endElement("div");
    }

    protected void encode(FacesContext context, Badge badge, BadgeModel badgeModel, boolean renderChildren) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BadgeModel model = badgeModel;
        if (model == null) {
            model = badge.toBadgeModel();
        }
        String clientId = badge == null ? null : badge.getClientId(context);
        String value = model.getValue();
        boolean valueEmpty = LangUtils.isEmpty(value);
        String severity = model.getSeverity();
        String size = model.getSize();
        String styleClass = getStyleClassBuilder(context)
                    .add(Badge.STYLE_CLASS)
                    .add(model.getStyleClass())
                    .add(!valueEmpty && value.length() == 1, Badge.NO_GUTTER_CLASS)
                    .add(valueEmpty, Badge.DOT_CLASS)
                    .add(!model.isVisible(), "ui-state-hidden")
                    .add("large".equals(size), Badge.SIZE_LARGE_CLASS)
                    .add("xlarge".equals(size), Badge.SIZE_XLARGE_CLASS)
                    .add("info".equals(severity), Badge.SEVERITY_INFO_CLASS)
                    .add("success".equals(severity), Badge.SEVERITY_SUCCESS_CLASS)
                    .add("warning".equals(severity), Badge.SEVERITY_WARNING_CLASS)
                    .add("danger".equals(severity), Badge.SEVERITY_DANGER_CLASS)
                    .build();

        if (renderChildren) {
            encodeOverlayBegin(context, clientId);
        }

        writer.startElement("span", null);
        if (!renderChildren && clientId != null) {
            writer.writeAttribute("id", clientId, "id");
        }
        writer.writeAttribute("class", styleClass, "styleClass");
        if (model.getStyle() != null) {
            writer.writeAttribute("style", model.getStyle(), "style");
        }

        if (!valueEmpty && model.isVisible()) {
            writer.write(value);
        }
        writer.endElement("span");

        if (renderChildren) {
            renderChildren(context, badge);
            encodeOverlayEnd(context);
        }
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
