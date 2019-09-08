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
package org.primefaces.component.editor;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.application.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.AgentUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class EditorRenderer extends InputRenderer {

    private static final Logger LOGGER = Logger.getLogger(EditorRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Editor editor = (Editor) component;

        if (!shouldDecode(editor)) {
            return;
        }

        decodeBehaviors(context, editor);

        String inputParam = editor.getClientId(context) + "_input";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String value = params.get(inputParam);

        if (value != null && value.equals("<br/>")) {
            value = "";
        }

        editor.setSubmittedValue(value);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        Editor editor = (Editor) component;

        encodeMarkup(facesContext, editor);
        encodeScript(facesContext, editor);
    }

    protected void encodeMarkup(FacesContext context, Editor editor) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = editor.getClientId(context);
        String valueToRender = ComponentUtils.getValueToRender(context, editor);
        String inputId = clientId + "_input";

        String style = editor.getStyle();
        style = style == null ? "visibility:hidden" : "visibility:hidden;" + style;

        writer.startElement("div", editor);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("style", style, null);
        if (editor.getStyleClass() != null) {
            writer.writeAttribute("class", editor.getStyleClass(), null);
        }

        writer.startElement("textarea", null);
        writer.writeAttribute("id", inputId, null);
        writer.writeAttribute("name", inputId, null);

        if (valueToRender != null) {
            writer.write(valueToRender);
        }

        writer.endElement("textarea");

        writer.endElement("div");
    }

    private void encodeScript(FacesContext context, Editor editor) throws IOException {
        String clientId = editor.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Editor", editor.resolveWidgetVar(context), clientId)
                .attr("disabled", editor.isDisabled(), false)
                .attr("invalid", editor.isValid(), true)
                .attr("controls", editor.getControls(), null)
                .attr("width", editor.getWidth(), Integer.MIN_VALUE)
                .attr("height", editor.getHeight(), Integer.MIN_VALUE)
                .attr("maxlength", editor.getMaxlength(), Integer.MAX_VALUE)
                .callback("change", "function(e)", editor.getOnchange());

        if (AgentUtils.isIE(context)) {
            Resource resource = context.getApplication().getResourceHandler().createResource("editor/editor-ie.css", "primefaces");
            wb.attr("docCSSFile", resource.getRequestPath());
        }

        if (editor.getMaxlength() != Integer.MAX_VALUE) {
            LOGGER.info("Maxlength option is deprecated and will be removed in a future version.");
        }

        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        Editor editor = (Editor) component;
        String value = (String) submittedValue;
        Converter converter = ComponentUtils.getConverter(context, component);

        if (converter != null) {
            return converter.getAsObject(context, editor, value);
        }

        return value;
    }
}
