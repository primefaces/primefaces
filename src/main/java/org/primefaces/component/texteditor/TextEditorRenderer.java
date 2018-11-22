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
package org.primefaces.component.texteditor;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class TextEditorRenderer extends InputRenderer {

    private static final PolicyFactory HTML_IMAGES_SANITIZER = new HtmlPolicyBuilder()
            .allowUrlProtocols("data", "http", "https")
            .allowElements("img")
            .allowAttributes("src")
            .matching(Pattern.compile("^(data:image/(gif|png|jpeg)[,;]|http|https|mailto|//).+", Pattern.CASE_INSENSITIVE))
            .onElements("img")
            .toFactory();
    private static final PolicyFactory HTML_LINKS_SANITIZER = Sanitizers.LINKS
            .and(new HtmlPolicyBuilder()
            .allowElements("a")
            .allowAttributes("target")
            .onElements("a")
            .toFactory());
    private static final PolicyFactory HTML_STYLES_SANITIZER = Sanitizers.STYLES
            .and(new HtmlPolicyBuilder()
            .allowElements("span")
            .allowAttributes("class")
            .onElements("span")
            .toFactory());
    private static final PolicyFactory HTML_DENY_ALL_SANITIZER = new HtmlPolicyBuilder().toFactory();

    protected String sanitizeHtml(String value, TextEditor editor) {
        PolicyFactory sanitizer = HTML_DENY_ALL_SANITIZER;
        if (editor.isAllowBlocks()) {
            sanitizer = sanitizer.and(Sanitizers.BLOCKS);
        }
        if (editor.isAllowFormatting()) {
            sanitizer = sanitizer.and(Sanitizers.FORMATTING);
        }
        if (editor.isAllowLinks()) {
            sanitizer = sanitizer.and(HTML_LINKS_SANITIZER);
        }
        if (editor.isAllowStyles()) {
            sanitizer = sanitizer.and(HTML_STYLES_SANITIZER);
        }
        if (editor.isAllowImages()) {
            sanitizer = sanitizer.and(HTML_IMAGES_SANITIZER);
        }
        return value == null ? null : sanitizer.sanitize(value);
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        TextEditor editor = (TextEditor) component;

        if (!shouldDecode(editor)) {
            return;
        }

        decodeBehaviors(context, editor);

        String inputParam = editor.getClientId(context) + "_input";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String value = sanitizeHtml(params.get(inputParam), editor);

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
        wb.init("TextEditor", editor.resolveWidgetVar(), clientId)
                .attr("toolbarVisible", editor.isToolbarVisible())
                .attr("readOnly", editor.isReadonly(), false)
                .attr("placeholder", editor.getPlaceholder(), null)
                .attr("height", editor.getHeight(), Integer.MIN_VALUE);
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
