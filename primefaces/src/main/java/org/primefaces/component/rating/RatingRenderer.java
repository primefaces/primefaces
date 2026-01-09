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
package org.primefaces.component.rating;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Rating.DEFAULT_RENDERER, componentFamily = Rating.COMPONENT_FAMILY)
public class RatingRenderer extends InputRenderer<Rating> {

    @Override
    public void decode(FacesContext context, Rating component) {
        if (!shouldDecode(component)) {
            return;
        }

        String clientId = component.getClientId(context);
        String submittedValue = context.getExternalContext().getRequestParameterMap().get(clientId + "_input");

        if (LangUtils.isNotEmpty(submittedValue)) {
            int submittedStars = Integer.parseInt(submittedValue);
            if (submittedStars == 0) {
                submittedValue = Constants.EMPTY_STRING;
            }
            else if (submittedStars < 1 || submittedStars > component.getStars()) {
                // prevent form post of invalid value
                return;
            }
        }

        component.setSubmittedValue(submittedValue);

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Rating component) throws IOException {
        encodeMarkup(context, component);
        encodeScript(context, component);
    }

    private void encodeScript(FacesContext context, Rating rating) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Rating", rating)
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
        String styleClass = createStyleClass(rating, Rating.CONTAINER_CLASS);

        writer.startElement("div", rating);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, null);
        if (LangUtils.isNotEmpty(style)) {
            writer.writeAttribute("style", style, null);
        }

        encodeInput(context, rating, clientId + "_input", valueToRender);

        if (rating.isCancel() && !disabled && !readonly) {
            encodeIcon(context, Rating.CANCEL_CLASS);
        }

        for (int i = 0; i < stars; i++) {
            String starClass = (value != null && i < value) ? Rating.STAR_ON_CLASS : Rating.STAR_CLASS;
            encodeIcon(context, starClass);
        }

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

    protected void encodeInput(FacesContext context, Rating component, String id, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        //input for accessibility
        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "range", null);
        writer.writeAttribute("min", "0", null);
        writer.writeAttribute("max", component.getStars(), null);
        writer.writeAttribute("autocomplete", "off", null);
        writer.writeAttribute("value", LangUtils.defaultIfBlank(value, "0"), null);
        //for keyboard accessibility and ScreenReader
        writer.writeAttribute("tabindex", component.getTabindex(), null);

        if (component.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", null);
        }

        writer.endElement("input");

        writer.endElement("div");
    }
}
