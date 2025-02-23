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
package org.primefaces.component.signature;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Map;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

public class SignatureRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Signature signature = (Signature) component;
        if (!shouldDecode(signature)) {
            return;
        }
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        // JSON value
        String value = params.get(signature.getClientId(context) + "_value");
        signature.setSubmittedValue(value);

        // printed text value
        String textValue = params.get(signature.getClientId(context) + "_text");
        if (LangUtils.isNotBlank(textValue)) {
            signature.setTextValue(textValue);
        }

        // base64 image value
        String base64Value = params.get(signature.getClientId(context) + "_base64");
        if (base64Value != null) {
            signature.setBase64Value(base64Value);
        }

        decodeBehaviors(context, signature);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        Signature signature = (Signature) component;

        encodeMarkup(facesContext, signature);
        encodeScript(facesContext, signature);
    }

    protected void encodeMarkup(FacesContext context, Signature signature) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = signature.getClientId(context);
        String style = signature.getStyle();
        String styleClass = getStyleClassBuilder(context)
                .add(Signature.STYLE_CLASS)
                .add(signature.getStyleClass())
                .add(signature.isReadonly(), Signature.READONLY_STYLE_CLASS)
                .add(signature.isDisabled(), "ui-state-disabled")
                .add(!signature.isValid(), "ui-state-error")
                .build();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeInputField(context, signature, clientId + "_value", signature.getValue());
        encodeInputField(context, signature, clientId + "_text", signature.getTextValue());

        if (signature.getValueExpression(Signature.PropertyKeys.base64Value.toString()) != null) {
            encodeInputField(context, signature, clientId + "_base64", null);
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Signature signature) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Signature", signature)
                .attr("background", signature.getBackgroundColor(), null)
                .attr("color", signature.getColor(), null)
                .attr("thickness", signature.getThickness(), 2)
                .attr("readonly", signature.isReadonly(), false)
                .attr("guideline", signature.isGuideline(), false)
                .attr("guidelineColor", signature.getGuidelineColor(), null)
                .attr("guidelineOffset", signature.getGuidelineOffset(), 25)
                .attr("guidelineIndent", signature.getGuidelineIndent(), 10)
                .attr("fontFamily", signature.getFontFamily(), null)
                .attr("fontSize", signature.getFontSize(), 40)
                .attr("ariaLabel", signature.getAriaLabel(), null)
                .attr("ariaLabelledBy", signature.getLabelledBy(), null)
                .attr("tabindex", signature.getTabindex(), "0")
                .callback("onchange", "function()", signature.getOnchange());

        if (signature.getValueExpression(Signature.PropertyKeys.base64Value.toString()) != null) {
            wb.attr("base64", true);
        }

        wb.finish();
    }

    protected void encodeInputField(FacesContext context, Signature signature, String name, Object value) throws IOException {
        String valueToRender = null;
        if (value != null) {
            valueToRender = value.toString();
        }
        renderHiddenInput(context, name, valueToRender, isDisabled(signature));
    }
}
