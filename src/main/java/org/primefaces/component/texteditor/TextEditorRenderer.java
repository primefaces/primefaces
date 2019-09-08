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
package org.primefaces.component.texteditor;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.context.PrimeApplicationContext;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.HtmlSanitizer;
import org.primefaces.util.WidgetBuilder;

public class TextEditorRenderer extends InputRenderer {

    private static final Logger LOGGER = Logger.getLogger(TextEditorRenderer.class.getName());

    @Override
    public void decode(FacesContext context, UIComponent component) {
        TextEditor editor = (TextEditor) component;

        if (!shouldDecode(editor)) {
            return;
        }

        decodeBehaviors(context, editor);

        String inputParam = editor.getClientId(context) + "_input";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String value = params.get(inputParam);

        if (PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isHtmlSanitizerAvailable()) {
            value = HtmlSanitizer.sanitizeHtml(value,
                    editor.isAllowBlocks(), editor.isAllowFormatting(),
                    editor.isAllowLinks(), editor.isAllowStyles(), editor.isAllowImages());
        }
        else {
            if (!editor.isAllowBlocks() || !editor.isAllowFormatting()
                    || !editor.isAllowLinks() || !editor.isAllowStyles() || !editor.isAllowImages()) {
                LOGGER.warning("HTML sanitizer not available - skip sanitizing....");
            }
        }

        if (value != null && value.equals("<br/>")) {
            value = "";
        }

        editor.setSubmittedValue(value);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        TextEditor editor = (TextEditor) component;

        encodeMarkup(facesContext, editor);
        encodeScript(facesContext, editor);
    }

    protected void encodeMarkup(FacesContext context, TextEditor editor) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = editor.getClientId(context);
        String valueToRender = ComponentUtils.getValueToRender(context, editor);
        String inputId = clientId + "_input";
        String editorId = clientId + "_editor";
        UIComponent toolbar = editor.getFacet("toolbar");

        String style = editor.getStyle();
        String styleClass = editor.getStyleClass();
        styleClass = (styleClass != null) ? TextEditor.EDITOR_CLASS + " " + styleClass : TextEditor.EDITOR_CLASS;

        writer.startElement("div", editor);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if (style != null) {
            writer.writeAttribute("style", style, null);
        }

        if (toolbar != null && editor.isToolbarVisible()) {
            writer.startElement("div", editor);
            writer.writeAttribute("id", clientId + "_toolbar", null);
            writer.writeAttribute("class", "ui-editor-toolbar", null);
            toolbar.encodeAll(context);
            writer.endElement("div");
        }

        writer.startElement("div", editor);
        writer.writeAttribute("id", editorId, null);
        if (valueToRender != null) {
            writer.write(valueToRender);
        }
        writer.endElement("div");

        writer.startElement("input", null);
        writer.writeAttribute("type", "hidden", null);
        writer.writeAttribute("name", inputId, null);
        // #2905
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }
        writer.endElement("input");

        writer.endElement("div");
    }

    private void encodeScript(FacesContext context, TextEditor editor) throws IOException {
        String clientId = editor.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TextEditor", editor.resolveWidgetVar(context), clientId)
                .attr("toolbarVisible", editor.isToolbarVisible())
                .attr("readOnly", editor.isReadonly(), false)
                .attr("placeholder", editor.getPlaceholder(), null)
                .attr("height", editor.getHeight(), Integer.MIN_VALUE);

        List formats = editor.getFormats();
        if (formats != null) {
            wb.append(",formats:[");
            for (int i = 0; i < formats.size(); i++) {
                if (i != 0) {
                    wb.append(",");
                }

                wb.append("\"" + EscapeUtils.forJavaScript((String) formats.get(i)) + "\"");
            }
            wb.append("]");
        }

        encodeClientBehaviors(context, editor);
        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        TextEditor editor = (TextEditor) component;
        String value = (String) submittedValue;
        Converter converter = ComponentUtils.getConverter(context, component);

        if (converter != null) {
            return converter.getAsObject(context, editor, value);
        }

        return value;
    }
}
