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
package org.primefaces.component.texteditor;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.HtmlSanitizer;
import org.primefaces.util.LangUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = TextEditor.DEFAULT_RENDERER, componentFamily = TextEditor.COMPONENT_FAMILY)
public class TextEditorRenderer extends InputRenderer<TextEditor> {

    private static final Logger LOGGER = Logger.getLogger(TextEditorRenderer.class.getName());

    @Override
    public void decode(FacesContext context, TextEditor component) {
        if (!shouldDecode(component)) {
            return;
        }

        decodeBehaviors(context, component);

        String inputParam = component.getClientId(context) + "_input";
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String value = sanitizeHtml(context, component, params.get(inputParam));

        if ("<br/>".equals(value)) {
            value = Constants.EMPTY_STRING;
        }

        component.setSubmittedValue(value);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, TextEditor component) throws IOException {
        // #5163 fail rendering if insecure
        checkSecurity(facesContext, component);

        encodeMarkup(facesContext, component);
        encodeScript(facesContext, component);
    }

    protected void encodeMarkup(FacesContext context, TextEditor component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);
        String valueToRender = sanitizeHtml(context, component, ComponentUtils.getValueToRender(context, component));
        String inputId = clientId + "_input";
        String editorId = clientId + "_editor";
        UIComponent toolbar = component.getToolbarFacet();

        String style = getStyleBuilder(context)
                .add(component.getStyle())
                .build();

        String styleClass = createStyleClass(component, TextEditor.EDITOR_CLASS);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute(HTML.ARIA_ROLE, HTML.ARIA_ROLE_TEXTBOX, null);
        writer.writeAttribute("class", styleClass, null);
        if (LangUtils.isNotBlank(style)) {
            writer.writeAttribute("style", style, null);
        }

        renderARIARequired(context, component);
        renderARIAInvalid(context, component);

        if (component.isToolbarVisible() && FacetUtils.shouldRenderFacet(toolbar)) {
            writer.startElement("div", component);
            writer.writeAttribute("id", clientId + "_toolbar", null);
            writer.writeAttribute("class", "ui-editor-toolbar", null);
            toolbar.encodeAll(context);
            writer.endElement("div");
        }

        String innerStyle = getStyleBuilder(context)
                .add("height", component.getHeight())
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", editorId, null);
        if (LangUtils.isNotBlank(innerStyle)) {
            writer.writeAttribute("style", innerStyle, null);
        }
        if (valueToRender != null) {
            writer.write(valueToRender);
        }
        writer.endElement("div");

        renderHiddenInput(context, inputId, valueToRender, component.isDisabled());

        writer.endElement("div");
    }

    private void encodeScript(FacesContext context, TextEditor component) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("TextEditor", component)
                .attr("toolbarVisible", component.isToolbarVisible())
                .attr("readOnly", component.isReadonly(), false)
                .attr("disabled", component.isDisabled(), false)
                .attr("placeholder", component.getPlaceholder(), null);

        List formats = component.getFormats();
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

        encodeClientBehaviors(context, component);
        wb.finish();
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return ComponentUtils.getConvertedValue(context, component, submittedValue);
    }

    /**
     * Enforce security by default requiring the OWASP sanitizer on the classpath.  Only if a user marks the editor
     * with secure="false" will they opt-out of security.
     *
     * @param context the FacesContext
     * @param component the editor to check for security
     */
    private void checkSecurity(FacesContext context, TextEditor component) {
        boolean sanitizerAvailable = PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isHtmlSanitizerAvailable();
        if (component.isSecure() && !sanitizerAvailable) {
            throw new FacesException("TextEditor component is marked secure='true' but the HTML Sanitizer was not found on the classpath. "
                        + "Either add the HTML sanitizer to the classpath per the documentation"
                        + " or mark secure='false' if you would like to use the component without the sanitizer.");
        }
    }

    /**
     * If security is enabled sanitize the HTML string to prevent XSS.
     *
     * @param context the FacesContext
     * @param component the TextEditor instance
     * @param value the value to sanitize
     * @return the sanitized value
     */
    private String sanitizeHtml(FacesContext context, TextEditor component, String value) {
        String result = value;
        if (component.isSecure() && PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isHtmlSanitizerAvailable()) {
            result = HtmlSanitizer.sanitizeHtml(value,
                    component.isAllowBlocks(), component.isAllowFormatting(),
                    component.isAllowLinks(), component.isAllowStyles(), component.isAllowImages());
        }
        else {
            if (!component.isAllowBlocks() || !component.isAllowFormatting()
                    || !component.isAllowLinks() || !component.isAllowStyles() || !component.isAllowImages()) {
                LOGGER.warning("HTML sanitizer not available - skip sanitizing....");
            }
        }
        return result;
    }
}
