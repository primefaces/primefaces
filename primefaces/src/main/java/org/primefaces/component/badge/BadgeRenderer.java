/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.functional.IOBiConsumer;
import org.primefaces.model.badge.BadgeModel;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.LangUtils;

public class BadgeRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Badge badge = (Badge) component;
        boolean hasChildren = badge.getChildCount() > 0;
        encode(context, badge, null, hasChildren);
    }

    public static <T extends UIComponent> void encodeOverlayed(FacesContext context, Object badge,
            IOBiConsumer<FacesContext, T> contentRenderer, T component) throws IOException {
        BadgeModel badgeModel = Badge.getBadgeModel(badge);
        if (badgeModel != null) {
            BadgeRenderer badgeRenderer = Badge.create(context).getRenderer();
            badgeRenderer.encodeOverlayBegin(context, null);
            contentRenderer.accept(context, component);
            badgeRenderer.encode(context, null, badgeModel, false);
            badgeRenderer.encodeOverlayEnd(context);
        }
        else {
            contentRenderer.accept(context, component);
        }
    }

    public static void encode(FacesContext context, Object badge) throws IOException {
        BadgeModel badgeModel = Badge.getBadgeModel(badge);
        if (badgeModel != null) {
            BadgeRenderer badgeRenderer = Badge.create(context).getRenderer();
            badgeRenderer.encode(context, null, badgeModel, false);
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
        String icon = model.getIcon();
        boolean iconEmpty = LangUtils.isEmpty(icon);
        String size = model.getSize();
        String styleClass = getStyleClassBuilder(context)
                    .add(Badge.STYLE_CLASS)
                    .add(model.getStyleClass())
                    .add(!valueEmpty && value.length() == 1, Badge.NO_GUTTER_CLASS)
                    .add(valueEmpty && iconEmpty, Badge.DOT_CLASS)
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

        if (model.getOnclick() != null) {
            writer.writeAttribute("onclick", model.getOnclick(), "onclick");
        }
        else if (renderChildren) {
            // delegate click of badge to its child component
            writer.writeAttribute("onclick", "$(this).next().click();", "onclick");
        }

        encodeValue(context, badge, model);
        writer.endElement("span");

        if (renderChildren) {
            renderChildren(context, badge);
            encodeOverlayEnd(context);
        }
    }

    protected void encodeValue(FacesContext context, Badge badge, BadgeModel model) throws IOException {
        if (!model.isVisible()) {
            return;
        }

        String value = model.getValue();
        boolean valueEmpty = LangUtils.isEmpty(value);
        String icon = model.getIcon();
        boolean iconEmpty = LangUtils.isEmpty(icon);
        String iconPos = model.getIconPos();

        if (iconEmpty) {
            encodeLabel(context, badge, value, valueEmpty);
            return;
        }

        if ("left".equalsIgnoreCase(iconPos)) {
            // left icon
            encodeIcon(context, badge, icon);
            encodeLabel(context, badge, value, valueEmpty);
        }
        else {
            // right icon
            encodeLabel(context, badge, value, valueEmpty);
            encodeIcon(context, badge, icon);
        }
    }

    protected void encodeIcon(FacesContext context, Badge badge, String icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String iconStyleClass = getStyleClassBuilder(context)
                .add(Badge.ICON_CLASS)
                .add(icon)
                .build();
        writer.startElement("span", null);
        writer.writeAttribute("class", iconStyleClass, null);
        writer.endElement("span");
    }

    protected void encodeLabel(FacesContext context, Badge badge, String value, boolean valueEmpty) throws IOException {
        if (!valueEmpty) {
            ResponseWriter writer = context.getResponseWriter();
            writer.startElement("span", null);
            writer.writeAttribute("class", Badge.LABEL_CLASS, null);
            writer.writeText(value, "value");
            writer.endElement("span");
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
