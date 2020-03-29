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
package org.primefaces.component.signature;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.WidgetBuilder;

public class SignatureRenderer extends InputRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Signature signature = (Signature) component;
        if (!shouldDecode(signature)) {
            return;
        }
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String value = params.get(signature.getClientId(context) + "_value");
        String base64Value = params.get(signature.getClientId(context) + "_base64");
        signature.setSubmittedValue(value);

        if (base64Value != null) {
            signature.setBase64Value(base64Value);
        }
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
        String styleClass = signature.getStyleClass();
        String defaultStyle = signature.resolveStyleClass();
        styleClass = styleClass == null ? defaultStyle : defaultStyle + " " + styleClass;

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, null);
        }

        encodeInputField(context, signature, clientId + "_value", signature.getValue());

        if (signature.getValueExpression(Signature.PropertyKeys.base64Value.toString()) != null) {
            encodeInputField(context, signature, clientId + "_base64", null);
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Signature signature) throws IOException {
        String clientId = signature.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Signature", signature.resolveWidgetVar(context), clientId)
                .attr("background", signature.getBackgroundColor(), null)
                .attr("color", signature.getColor(), null)
                .attr("thickness", signature.getThickness(), 2)
                .attr("readonly", signature.isReadonly(), false)
                .attr("guideline", signature.isGuideline(), false)
                .attr("guidelineColor", signature.getGuidelineColor(), null)
                .attr("guidelineOffset", signature.getGuidelineOffset(), 25)
                .attr("guidelineIndent", signature.getGuidelineIndent(), 10)
                .callback("onchange", "function()", signature.getOnchange());

        if (signature.getValueExpression(Signature.PropertyKeys.base64Value.toString()) != null) {
            wb.attr("base64", true);
        }

        wb.finish();
    }

    protected void encodeInputField(FacesContext context, Signature signature, String name, Object value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("id", name, null);
        writer.writeAttribute("name", name, null);
        writer.writeAttribute("autocomplete", "off", null);
        if (value != null) {
            writer.writeAttribute("value", value, null);
        }
        renderAccessibilityAttributes(context, signature);
        writer.endElement("input");
    }
}
