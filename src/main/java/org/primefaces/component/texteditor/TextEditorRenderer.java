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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.WidgetBuilder;

public class TextEditorRenderer extends InputRenderer {

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
