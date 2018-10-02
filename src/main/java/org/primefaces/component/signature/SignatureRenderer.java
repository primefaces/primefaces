/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        wb.init("Signature", signature.resolveWidgetVar(), clientId)
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
