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
package org.primefaces.component.rating;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

public class RatingRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Rating rating = (Rating) component;
        if (!shouldDecode(rating)) {
            return;
        }

        String clientId = rating.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if (!LangUtils.isValueEmpty(submittedValue)) {
            int submittedStars = Integer.parseInt(submittedValue);
            if (submittedStars < 1 || submittedStars > rating.getStars()) {
                return;
            }
        }

        rating.setSubmittedValue(submittedValue);

        decodeBehaviors(context, rating);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Rating rating = (Rating) component;

        encodeMarkup(context, rating);
        encodeScript(context, rating);
    }

    private void encodeScript(FacesContext context, Rating rating) throws IOException {
        String clientId = rating.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Rating", rating.resolveWidgetVar(context), clientId)
                .callback("onRate", "function(value)", rating.getOnRate())
                .attr("readonly", rating.isReadonly(), false)
                .attr("disabled", rating.isDisabled(), false);

        encodeClientBehaviors(context, rating);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, Rating rating) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = rating.getClientId(context);
        String valueToRender = ComponentUtils.getValueToRender(context, rating);
        Integer value = isValueBlank(valueToRender) ? null : Integer.valueOf(valueToRender);
        int stars = rating.getStars();
        boolean disabled = rating.isDisabled();
        boolean readonly = rating.isReadonly();
        String style = rating.getStyle();
        String styleClass = rating.getStyleClass();
        styleClass = styleClass == null ? Rating.CONTAINER_CLASS : Rating.CONTAINER_CLASS + " " + styleClass;

        if (disabled) {
            styleClass = styleClass + " ui-state-disabled";
        }

        writer.startElement("div", rating);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (rating.isCancel() && !disabled && !readonly) {
            encodeIcon(context, Rating.CANCEL_CLASS);
        }

        for (int i = 0; i < stars; i++) {
            String starClass = (value != null && i < value.intValue()) ? Rating.STAR_ON_CLASS : Rating.STAR_CLASS;
            encodeIcon(context, starClass);
        }

        encodeInput(context, rating, clientId + "_input", valueToRender);

        writer.endElement("div");
    }

    protected void encodeIcon(FacesContext context, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("div", null);
        writer.writeAttribute("class", styleClass, null);

        writer.startElement("a", null);
        writer.endElement("a");

        writer.endElement("div");
    }

    protected void encodeInput(FacesContext context, Rating rating, String id, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("autocomplete", "off", null);
        if (value != null) {
            writer.writeAttribute("value", value, null);
        }
        renderAccessibilityAttributes(context, rating);
        writer.endElement("input");
    }
}
